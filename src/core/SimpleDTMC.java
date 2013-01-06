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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeMap;
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

import mat.MatlabBatch;
import mat.SmartMatrix;

/**
 * @author Antonio Filieri
 *
 */
public class SimpleDTMC implements DTMC {
	double[][] transitionMatrix;
	int currentState;
	int numTransients;
	private SmartMatrix smartMatrix=null;
	
	public SimpleDTMC(double[][] transitionMatrix,int numTransients){
		this(transitionMatrix,numTransients,0);
	}
	
	public SimpleDTMC(double[][] transitionMatrix,int numTransients,int initial){
		this.currentState=initial;
		this.transitionMatrix=transitionMatrix;
		this.numTransients=numTransients;
	}
	
	public SimpleDTMC(double[][] transientMatrix,double[][] absorbingMatrix){
		this(transientMatrix,absorbingMatrix,0);
	}
	
	public SimpleDTMC(double[][] transientMatrix,double[][] absorbingMatrix,int initial){
		this.currentState=initial;
		this.numTransients=transientMatrix.length;
		this.transitionMatrix=new double[transientMatrix.length+absorbingMatrix[0].length][transientMatrix.length+absorbingMatrix[0].length];
		for(int i=0;i<transientMatrix.length+absorbingMatrix[0].length;i++){
			for(int j=0;j<transientMatrix.length+absorbingMatrix[0].length;j++){
				if(i<transientMatrix.length){
					if(j<transientMatrix.length){
						this.transitionMatrix[i][j]=transientMatrix[i][j];
					}else{
						this.transitionMatrix[i][j]=absorbingMatrix[i][j-transientMatrix.length];
					}
				}else{
					if(i==j){
						this.transitionMatrix[i][j]=1.0;
					}else{
						this.transitionMatrix[i][j]=0.0;
					}
				}
			}
		}
	}
	
	
	/* (non-Javadoc)
	 * @see core.DTMC#getCurrentNode()
	 */
	@Override
	public Node getCurrentNode() {
		return new Node("n"+this.currentState,this.currentState);
	}

	/* (non-Javadoc)
	 * @see core.DTMC#getOutgoingTrans(core.Node)
	 */
	@Override
	public Transition[] getOutgoingTrans(Node n) {
		int pos=n.getId();
		ArrayList<Transition> out = new ArrayList<Transition>();
		for(int i=0;i<this.transitionMatrix.length;i++){
			if(this.transitionMatrix[pos][i]>0){
				Node[] from=new Node[1];
				from[0]=new Node("n"+pos,pos);
				out.add(new Transition(from,new Node("n"+i,i),this.transitionMatrix[pos][i]));
			}
		}
		Transition[] t = new Transition[out.size()];
		return out.toArray(t);
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
		for(int a=0;a<this.transitionMatrix[this.currentState].length;a++){
			if(dice<cum+this.transitionMatrix[this.currentState][a]){
				this.currentState=a;
				break;
			}
			cum+=this.transitionMatrix[this.currentState][a];
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
		for(int i=0;i<this.transitionMatrix.length;i++){
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
		this.transitionMatrix=new double[size][size];
		for(int i=0;i<size;i++){
			for(int j=0;j<size;j++){
				this.transitionMatrix[i][j]=fIn.readDouble();
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
		fOut.writeInt(this.transitionMatrix.length);
		for(int i=0;i<this.transitionMatrix.length;i++){
			for(int j=0;j<this.transitionMatrix[i].length;j++){
				fOut.writeDouble(this.transitionMatrix[i][j]);
			}
		}
		fOut.close();	
	}
	

	
	public SmartMatrix getSmartMatrix(){
		if(this.smartMatrix==null){
			double smd[][]=new double[this.transitionMatrix.length][this.transitionMatrix.length];
			for(int i=0;i<this.transitionMatrix.length;i++){
				for(int j=0;j<this.transitionMatrix.length;j++){
					smd[i][j]=(i==j)?1-this.transitionMatrix[i][j]:-1*this.transitionMatrix[i][j];
				}
			}
			this.smartMatrix=new SmartMatrix(smd);
		}
		return this.smartMatrix;
	}
	
	public int unsimplified(String fileName,double comparison,int from, int to, int ... variableRows) throws IOException{
		assert(to>=this.numTransients);

		double smd[][]=new double[this.transitionMatrix.length][this.transitionMatrix.length];
		double smd2[][]=new double[this.transitionMatrix.length][this.transitionMatrix.length];
		for(int i=0;i<this.transitionMatrix.length;i++){
			for(int j=0;j<this.transitionMatrix.length;j++){
				if(i<this.numTransients && j<this.numTransients){
					smd[i][j]=(i==j)?1-this.transitionMatrix[i][j]:-1*this.transitionMatrix[i][j];
					smd2[i][j]=(i==j)?1-this.transitionMatrix[i][j]:-1*this.transitionMatrix[i][j];
				}else{
					smd[i][j]=this.transitionMatrix[i][j];
					smd2[i][j]=this.transitionMatrix[i][j];
				}
				//System.out.print(smd[i][j]+"\t");
			}
			//System.out.println();
		}
		SmartMatrix sm=new SmartMatrix(smd,variableRows);
		SmartMatrix sm2=new SmartMatrix(smd2,variableRows);
		for(int i=0;i<this.transitionMatrix.length-this.numTransients;i++){
			sm=sm.minor(this.transitionMatrix.length-i-1, this.transitionMatrix.length-i-1);
			System.out.println(Arrays.deepToString(sm.getBase()));
		}
		MatlabBatch mb = new MatlabBatch();
		String deta=sm.determinant(mb);
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
			//if(this.transitionMatrix[from][i]==0) continue;
			int coef=(int)Math.pow(-1, from+i);
			boolean variable=false;
			for(int k=0;k<variableRows.length;k++){
				if(variableRows[k]==i){
					variable=true;
					break;
				}
			}
			SmartMatrix mino=sm.minor(i, from);
			String print="";
			String invComp=(i==to)?"1-":"(-1)*";
			if(!variable || (this.transitionMatrix[i][to]==0)){
				if(this.transitionMatrix[i][to]!=0){
					num=num+"+"+"("+coef+")*("+mino.determinant(mb)+")*("+this.transitionMatrix[i][to]+")";
				}
			}else{
				//System.out.println("USATO NUMERICO BAH");
				num=num+"+"+"("+coef+")*("+mino.determinant(mb)+")*("+sm2.getIndex(i, to)+")";
			}
		}
		String both="("+num.substring(1)+")/("+deta+")";

		/*System.out.println("STARTING MATLAB COMPUTATION");
		long timeS = System.currentTimeMillis();*/
		mb.compute();
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
			double val=sm2.valFromIndex(variable);
			if(!varsRemapping.containsKey(variable)){
				varsRemapping.put(variable, "x["+count+"]");
				varsAssignment.put("x["+count+"]", val);
				count++;
			}
			function=function.substring(0,vpos)+varsRemapping.get(variable)+function.substring(endpos);
		}

		HashMap<String,Double> mc = mb.getResults();
		/*System.out.println(function);
		for(String k1 : mc.keySet()){
			System.out.println(k1+" = "+mc.get(k1));
		}*/
		System.out.println("LAST COUNTER: "+mb.getCounter()+"\t\tCURRENT SIZE: "+mc.size());
		while(function.contains("m")){
			int vpos=function.indexOf("m");
			int endpos=vpos+1;
			while(function.charAt(endpos)<='9' && function.charAt(endpos)>='0'){
				endpos++;
			}
			//endpos--;
			String variable=function.substring(vpos,endpos);
			System.out.println(variable);
			double val=mc.get(variable);
			
			function=function.substring(0,vpos)+val+function.substring(endpos);
		}
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

	public void setNumTransitionRow(int row,int numTrans,int minCol, int maxCol,int seed){
		double sum=0.0;
		int count=0;
		Random rgen;
		if(seed==-1){
			rgen=new Random();
		}else{
			rgen=new Random(seed);
		}
		for(int o=0;o<this.transitionMatrix[row].length;o++){
			this.transitionMatrix[row][o]=0;
		}
		while(sum<=0.0 || count<numTrans){
			int next=Math.max(0, minCol)+rgen.nextInt(Math.min(this.transitionMatrix[row].length,maxCol)-Math.max(0, minCol));	
			if(this.transitionMatrix[row][next]==0){
				double x=rgen.nextDouble();
				if(x<0 || x>1){
					//throw new Exception("Porca l'oca");
				}
				while(x<=0 || (x==1 && row==next)){
					x=rgen.nextDouble();
				}
				this.transitionMatrix[row][next]=x;
				count++;
				sum+=x;
			}
		}
		for(int o=0;o<this.transitionMatrix[row].length;o++){
			this.transitionMatrix[row][o]=this.transitionMatrix[row][o]/sum;
			//System.out.println(this.transitionMatrix[row][o]);
		}
	}
	
	public void toTransTable(OutputStream os) {
		PrintWriter fOut=new PrintWriter(os);
		for(int i=0;i<this.transitionMatrix.length;i++){
			for(int j=0;j<this.transitionMatrix[i].length;j++){
				fOut.print(this.transitionMatrix[i][j]+"\t");
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
