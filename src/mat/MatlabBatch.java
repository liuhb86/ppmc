/**
 * 
 */
package mat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;

import Jama.Matrix;

import mat.SparseMatrix.MatrixEntry;

/**
 * @author anfi
 *
 */
public class MatlabBatch {
	public static final String MATLABPATH="matlab";
	public static final int CRITICALSIZE=1000;
	private HashMap<String,SparseMatrix> matrices;
	private HashMap<String,Double> results;
	private int counter=0;

	public MatlabBatch(){
		this.matrices=new HashMap<String,SparseMatrix>();
		this.results=new HashMap<String,Double>();
	}
	
	public synchronized void addMatrix(String name,double[][] m) throws IOException{
		SparseMatrix sm = new SparseMatrix(name, m.length);
		for(int i=0;i<m.length;i++){
			for(int j=0;j<m[i].length;j++){
				if(m[i][j]!=0)
					sm.addEntry(i+1, j+1, m[i][j]);
			}
		}
		this.matrices.put(name,sm);
		if(this.matrices.size()>=CRITICALSIZE){
			this.compute();
		}
	}

	public synchronized String getUniqueId(){
		String ret="m"+this.counter;
		this.counter++;
		return ret;
	}
	
	public synchronized void addMatrix(String name,SparseMatrix sm) throws IOException{
		this.matrices.put(name, sm);
		if(this.matrices.size()>=CRITICALSIZE){
			this.compute();
		}
	}
	
	public synchronized int getCounter(){
		return this.counter;
	}

	public synchronized void computeJ() throws IOException{
		for(String nextName : this.matrices.keySet()){
			SparseMatrix sm=this.matrices.get(nextName);
			Matrix toSolve = new Matrix(sm.getAsArray());
			this.results.put(nextName, toSolve.det());
		}
		this.matrices.clear();
	}
	
	public synchronized void compute() throws IOException{
		this.computeJ();
	}
	
	public synchronized void computeM() throws IOException{
		String module="m"+System.currentTimeMillis()+".m";
		PrintWriter fOut=new PrintWriter(new FileOutputStream(module));
		fOut.println("format long");
		for(SparseMatrix sm : this.matrices.values()){
			fOut.println(sm.getMatlab());
		}
		int newMat=this.matrices.size();
		this.matrices.clear();
		fOut.flush();
		fOut.close();
		String launch="l"+module;
		fOut=new PrintWriter(new FileOutputStream(launch));
		fOut.println(module.substring(0, module.length()-2));
		fOut.flush();
		fOut.close();
		//System.out.println("MatlabBatch: launching matlab "+this.counter);
		Process p = Runtime.getRuntime().exec(MATLABPATH+" < "+launch);
		BufferedReader input =new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		String nextVar="";
		double nextVal;
		int oldResSize=this.results.size();
		int state=0; //0=wait,1=get value
		//synchronized(this.results){
			while ((line = input.readLine()) != null) {
				if(line.trim().startsWith("d") && state==0){
					state=1;
					nextVar=line.substring(1,line.indexOf(" ="));
					continue;
				}
				if(state==1  && line.trim().length()>0){
					String result=line.trim();
					nextVal=Double.parseDouble(result);
					state=0;
					this.results.put(nextVar, nextVal);
					continue;
				}
			}
		//}
		if(this.results.size()-oldResSize!=newMat){
			System.out.println("Problema");
		}
		File fdel=new File(module);
		fdel.delete();
		fdel=new File(launch);
		fdel.delete();
	}
	
	public synchronized HashMap<String,Double> getResults(){
		return this.results;
	}
	
}
