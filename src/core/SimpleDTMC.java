/**
 * 
 */
package core;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;
import java.io.DataOutputStream;

import org.lsmp.djep.djep.DJep;
import org.lsmp.djep.xjep.XJep;
import org.nfunk.jep.ASTConstant;
import org.nfunk.jep.ASTFunNode;
import org.nfunk.jep.ASTVarNode;
import org.nfunk.jep.JEP;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.SimpleNode;
import org.nfunk.jep.SymbolTable;

import util.Utils;

import mat.MatrixIndex;
import mat.SmartMatrix;
import mat.SparseMatrix;

/**
 * @author Antonio Filieri
 *
 */
public class SimpleDTMC implements DTMC {
	SmartMatrix trans;
	int currentState;
	int numTransients;
	private SmartMatrix M=null;
	
	public SimpleDTMC(double[][] transitionMatrix,int numTransients){
		this(transitionMatrix, new SparseMatrix<String>(), numTransients,0);
	}
	
	public SimpleDTMC(double[][] transitionMatrix, SparseMatrix<String> var,int numTransients,int initial){
		this.currentState=initial;
		this.numTransients=numTransients;
		this.trans = new SmartMatrix(transitionMatrix,var);
	}
	
	/* (non-Javadoc)
	 * @see core.DTMC#getCurrentNode()
	 */
	@Override
	public Node getCurrentNode() {
		return new Node("n"+this.currentState,this.currentState);
	}

	/* (non-Javadoc)
	 * @see core.DTMC#isAbsorbed()
	 */
	@Override
	public boolean isAbsorbed() {
		return (this.currentState>=this.numTransients);
	}

	/* (non-Javadoc)
	 * @see core.DTMC#move(double)
	 */
	@Override
	public void move(double dice) {
		if(this.isAbsorbed()) return;
		double cum=0.0;
		for(int a=0;a<trans.getDimC();a++){
			if(dice<cum+trans.getNumericEntry(currentState, a)){
				this.currentState=a;
				break;
			}
			cum+=trans.getNumericEntry(currentState, a);
		}
	}

	/* (non-Javadoc)
	 * @see core.DTMC#reset()
	 */
	@Override
	public void reset() {
		this.currentState=0;
	}

	@Override
	public Node[] getNodeSet() {
		ArrayList<Node> ret = new ArrayList<Node>();
		for(int i=0;i<trans.getDim();i++){
			ret.add(new Node("n"+i,i));
		}
		Node[] n =new Node[ret.size()];
		return ret.toArray(n);
	}

	@Override
	public void loadFile(String path) throws IOException {
		DataInputStream fIn = new DataInputStream(new FileInputStream(path));
		char c = fIn.readChar();
		if(c!='S') throw new IOException(path+" does not contain a SimpleDTMC");
		this.currentState=fIn.readInt();
		this.numTransients=fIn.readInt();
		int size=fIn.readInt();
		this.trans = new SmartMatrix(new double[size][size]);
		for(int i=0;i<size;i++){
			for(int j=0;j<size;j++){
				trans.getBase()[i][j]=fIn.readDouble();
			}
		}
		fIn.close();
	}

	@Override
	public void toFile(String path) throws IOException {
		DataOutputStream fOut=new DataOutputStream(new FileOutputStream(path));
		fOut.writeChar('S');
		fOut.writeInt(this.currentState);
		fOut.writeInt(this.numTransients);
		fOut.writeInt(trans.getDim());
		for(int i=0;i<trans.getDimR();i++){
			for(int j=0;j<trans.getDimC();j++){
				fOut.writeDouble(trans.getNumericEntry(i, j));
			}
		}
		fOut.close();	
	}
	
	public void writeTo(Object printer) {
		PrintWriter writer = Utils.getWriter(printer);
		if (writer == null) return;
		writer.println("Transitions:");
		trans.writeTo(writer);
		writer.println("Initial:");
		writer.print('\t');
		writer.println(this.currentState);
		writer.flush();
	}
	
	public SmartMatrix getM(){
		if(this.M==null){
			double smd[][]=new double[numTransients][numTransients];
			SparseMatrix<String> vars = trans.getVars().clone();
			for(int i=0;i<numTransients;i++){
				for(int j=0;j<numTransients;j++){
					if (trans.isSymbolicEntry(i, j)) {
						vars.put(i, j, ((i==j)? "1":"") + "-(" +trans.getEntry(i, j, true)+")");
						smd[i][j] = -1;
					} else {
						smd[i][j]=((i==j)?1:0)-trans.getNumericEntry(i, j);
					}
				}
			}
			this.M=new SmartMatrix(smd,vars);
		}
		return this.M;
	}
	
	public int unsimplified(String fileName,double comparison,int from, int to) throws IOException{
		assert(to>=this.numTransients);

		SmartMatrix sm= this.getM();
		String deta=sm.determinant();
		PrintWriter fOut=new PrintWriter(new FileOutputStream(fileName+".c"));
		fOut.println("#include <stdio.h>");
		fOut.println("#include <time.h>");
		fOut.println("#include <sys/time.h>");
		fOut.println("#include <math.h>");

		fOut.println("struct timezone {");
		fOut.println("\tint tz_minuteswest;");
		fOut.println("\tint tz_dsttime;");
		fOut.println("};");
;

		String num="";

		for(int i=0;i<this.numTransients;i++){	
			if(trans.getNumericEntry(i, to)!=0){
				char coef=(from+i)%2==0?'+':'-';
				SmartMatrix mino=sm.minor(i, from);
				num=num+coef+"("+mino.determinant()+")*"+trans.getEntry(i, to, true);
			}
		}
		String both="("+num+")/("+deta+")";
		System.out.println(both);
		/*System.out.println("STARTING MATLAB COMPUTATION");
		long timeS = System.currentTimeMillis();*/
		//System.out.println("MATLAB COMPUTATION took "+(System.currentTimeMillis()-timeS)+" millis.");
		
		
		XJep myParser = new XJep();
		myParser.setAllowUndeclared(true);
		String simpl="-1";
		try {
			org.nfunk.jep.Node a0 = myParser.parse(both);//"x*(a+(b*(c+1)))+0-x^1+0-x*1+(a+b*c)*0");
			org.nfunk.jep.Node ap = myParser.preprocess(a0);
			org.nfunk.jep.Node a= myParser.simplify(ap);
			simpl=myParser.toString(a);
			System.out.println(simpl);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		SymbolTable st = myParser.getSymbolTable();		
		//System.out.println("FUNC before ("+both.length()+"): "+both+"\n");
		//System.out.println("FUNC after:  ("+simpl.length()+"): "+simpl+"\n");
		
		String function= simpl+"";
		TreeMap<String,String> varsRemapping = new TreeMap<String,String>();
		TreeMap<String,Double> varsAssignment = new TreeMap<String,Double>();
		int count=0;
		while(function.contains("v")){
			int vpos=function.indexOf("v");
			int endpos=vpos+1;
			while(function.charAt(endpos)<='9' && function.charAt(endpos)>='0'){
				endpos++;
			}
			//endpos--;
			String variable=function.substring(vpos,endpos);
			if(!varsRemapping.containsKey(variable)){
				varsRemapping.put(variable, "x["+count+"]");
				varsAssignment.put("x["+count+"]", 0.0);
				count++;
			}
			function=function.substring(0,vpos)+varsRemapping.get(variable)+function.substring(endpos);
		}

		/*System.out.println(function);
		for(String k1 : mc.keySet()){
			System.out.println(k1+" = "+mc.get(k1));
		}*/
		
		function = function.replace("+-", "-");
		function = function.replace("-+", "-");
		function = function.replace("++", "+");
		function = function.replace("--", "+");
		fOut.println("\ndouble compute(double x[]){");
		fOut.println("\t return "+function+";\n}");
		fOut.println();
		fOut.println("int main(){");
		fOut.println("\tdouble x["+varsAssignment.size()+"];");
		for(String k: varsAssignment.keySet()){
			fOut.println("\t"+k+"="+varsAssignment.get(k)+";");
		}
		fOut.println("\tstruct timeval start,end;");
		fOut.println("\tstruct timezone tzp;"); 
		fOut.println("\tgettimeofday (&start, &tzp);");
		fOut.println("\tdouble v = compute(x);");
		fOut.println("\tgettimeofday (&end, &tzp);"); 
		fOut.println("\tprintf(\"TIME %d\\n\",end.tv_usec - start.tv_usec);");
		fOut.println("\tprintf(\"VALUE %.16f\\n\",v);");
		fOut.println("\treturn 0;");
		fOut.println("}");
		fOut.println();
		fOut.close();
		return st.size();
	}
	
	public String simplify(String in){
		XJep myParser = new XJep();
		myParser.setAllowUndeclared(true);
		String ret="";
		try {
			org.nfunk.jep.Node a0 = myParser.parse(in);//"x*(a+(b*(c+1)))+0-x^1+0-x*1+(a+b*c)*0");
			org.nfunk.jep.Node ap = myParser.preprocess(a0);
			org.nfunk.jep.Node a= myParser.simplify(ap);
			ret=myParser.toString(a);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		SymbolTable st = myParser.getSymbolTable();
		System.out.println("Num variables: "+st.size());
		
		return ret;
	}
	
	private String jepToString(SimpleNode n){
		
		if(n.jjtGetNumChildren()==0){
			String ret="";
			if(n instanceof ASTConstant){
				ret=ret+((ASTConstant) n).getValue();
			}else if(n instanceof ASTVarNode){
				ret=ret+((ASTVarNode) n).getName();
			}else if(n instanceof ASTFunNode){
				ret=ret+((ASTFunNode) n).getName();
			}else{
				ret=ret+"XXX";
			}
			return ret;
		}else if(n.jjtGetNumChildren()==1){
			String ret="";
			if(n instanceof ASTConstant){
				ret=ret+"c"+((ASTConstant) n).getValue();
			}else if(n instanceof ASTVarNode){
				ret=ret+"v"+((ASTVarNode) n).getName();
			}else if(n instanceof ASTFunNode){
				ret=ret+((ASTFunNode) n).getOperator().getSymbol();
			}else{
				ret=ret+"XXX";
			}
			return ret+jepToString((SimpleNode)n.jjtGetChild(0));
		}else if(n.jjtGetNumChildren()==2){
			String ret="";
			if(n instanceof ASTConstant){
				ret=ret+((ASTConstant) n).getValue();
			}else if(n instanceof ASTVarNode){
				ret=ret+((ASTVarNode) n).getName();
			}else if(n instanceof ASTFunNode){
				ret=ret+((ASTFunNode) n).getName();
			}else{
				ret=ret+"XXX";
			}
			return jepToString((SimpleNode)n.jjtGetChild(0))+ret+jepToString((SimpleNode)n.jjtGetChild(1));
		}else{
			return "MANNAGGIALIGUAI";
		}
	}
	
	public void toTransTable(OutputStream os) {
		PrintWriter fOut=new PrintWriter(os);
		for(int i=0;i<trans.getDimR();i++){
			for(int j=0;j<trans.getDimC();j++){
				fOut.print(trans.getEntry(i, j)+"\t");
			}
			fOut.println();
		}
		fOut.close();
	}
	public void toTransTable(String path) throws FileNotFoundException{
		FileOutputStream fOut = new FileOutputStream(path);
		toTransTable(fOut);
		
	}
}
