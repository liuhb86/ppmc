package org.net9.simplex.ppmc.core;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.text.ParseException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.net9.simplex.ppmc.mat.SmartMatrix;
import org.net9.simplex.ppmc.mat.SparseMatrix;


/**
 * @author Hongbo Liu <liuhongbo@pku.edu.cn, liuhb86@gmail.com>
 * @author Antonio Filieri 
 */
public class SimpleDTMC implements DTMC {
	SmartMatrix trans;
	int currentState;
	public int numTransients;
	HashMap<String, BitSet> ap;

	SimpleDTMC(){	
	}
	public SimpleDTMC(double[][] transitionMatrix,int numTransients){
		init(transitionMatrix, new SparseMatrix<String>(), numTransients,0, new HashMap<String, BitSet>());
	}
	
	public SimpleDTMC(double[][] transitionMatrix, SparseMatrix<String> var,int numTransients,int initial, HashMap<String, BitSet> ap){
		init(transitionMatrix, var, numTransients, initial, ap);
	}
	
	void init(double[][] transitionMatrix, SparseMatrix<String> var,int numTransients,int initial, HashMap<String, BitSet> ap){
		this.currentState=initial;
		this.numTransients=numTransients;
		this.trans = new SmartMatrix(transitionMatrix,var);
		this.ap = ap;
	}
	
	public static SimpleDTMC loadFrom(Reader in) throws ParseException{
		Scanner s = new Scanner(in);
		String str;
		Pattern ptnBracket = Pattern.compile("\\[.*"),
				ptnParamValue =Pattern.compile("\\s*(.*)"),
				ptnInt = Pattern.compile("\\d+");
		str = s.next();
		if (!"[Model]".equals(str)) throw new ParseException("Missing Section: Model",0);
		int size =-1, init = -1, absorbing = -1;
		while(s.hasNext() && !s.hasNext(ptnBracket)) {
			String param = 	s.next();
			str = s.next();
			if (!"=".equals(str)) throw new ParseException("Bad assigment: "+param,0);
			s.findInLine(ptnParamValue);
			MatchResult r=s.match();
			if (param.equals("size")) size = Integer.parseInt(r.group(1));
			else if (param.equals("init")) init = Integer.parseInt(r.group(1));
			else if (param.equals("absorbing")) absorbing = Integer.parseInt(r.group(1));
			else throw new ParseException("Unknown parameter: "+param,0);
		}
		if (size<0) throw new ParseException("Missing parameter: size",0);
		if (init<0) throw new ParseException("Missing parameter: init",0);
		if (absorbing<0) absorbing=0;
		
		str = s.next();
		if (!"[Transition]".equals(str)) throw new ParseException("Missing Section: Transition",0);
		double[][] trans = new double[size][size];
		SparseMatrix<String> sm = new SparseMatrix<String>();
		s.useDelimiter("[\\s&&[^ ]]+");
		for(int i=0;i<size;++i){
			for(int j=0;j<size;++j){
				if (s.hasNextDouble()) trans[i][j]=s.nextDouble();
				else {
					if (!s.hasNext()) throw new ParseException("Missing Transition Entry" + (i*size+j),0);
					trans[i][j]=-1;
					sm.put(i, j, s.next());
				}
			}
		}
		s.useDelimiter("\\s+");
		if (s.hasNext() && !s.hasNext(ptnBracket)) throw new ParseException("Extra Transition Entry: "+s.next(), 0);
		
		str = s.next();
		if (!"[AP]".equals(str)) throw new ParseException("Missing Section: AP",0);
		HashMap<String, BitSet> ap = new HashMap<String, BitSet>();
		
		while(s.hasNext() && !s.hasNext(ptnBracket)) {
			String param = 	s.next();
			str = s.next();
			if (!"=".equals(str)) throw new ParseException("Bad assigment: "+param,0);
			BitSet bs = new BitSet(size);
			while(true) {
				str = s.findInLine(ptnInt);
				if (str==null) break;
				bs.set(Integer.parseInt(str));
			}
			ap.put(param, bs);
		}
		s.close();
		return new SimpleDTMC(trans, sm, size-absorbing, init, ap);
	}
	
	public void writeTo(Writer writer) {
		PrintWriter printer = new PrintWriter(writer);
		printer.println("[Model]");
		printer.print("size = ");
		printer.println(trans.getDim());
		printer.print("init = ");
		printer.println(this.currentState);
		printer.print("absorbing = ");
		printer.println(trans.getDim()-this.numTransients);
		printer.println("[Transition]");
		trans.writeTo(printer);
		printer.println("[AP]");
		for (Entry<String, BitSet> e : ap.entrySet()){
			printer.print(e.getKey());
			printer.print(" = ");
			BitSet bs = e.getValue();
			for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i+1)) {
			     printer.print(' ');
			     printer.print(i);
			}
			printer.println();
		}
		//printer.close();
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
	
	public boolean isAbsorbingState(int s) {
		return s>=this.numTransients;
	}
	
	public int size() {
		return trans.getDim();
	}
	
	@Override
	public SmartMatrix getTrans() {
		return this.trans;
	}
	@Override
	public int getInitState() {
		return this.currentState;
	}
	@Override
	public HashMap<String, BitSet> getAP() {
		return this.ap;
	}
}
