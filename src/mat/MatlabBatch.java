/**
 * 
 */
package mat;

import java.io.IOException;
import java.util.HashMap;

import Jama.Matrix;

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
	
	public synchronized HashMap<String,Double> getResults(){
		return this.results;
	}
	
}
