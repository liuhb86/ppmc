/**
 * 
 */
package mat;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author anfi
 *
 */
public class SparseMatrix {
	private String name;
	private int size;
	private Set<MatrixEntry> entries;
	
	public SparseMatrix(String name, int size){
		this.name=name;
		this.size=size;
		this.entries=new TreeSet<MatrixEntry>();
	}
	
	public void addEntry(int x,int y,double val){
		MatrixEntry m = new MatrixEntry(x, y, val);
		this.entries.add(m);
	}
	
	public void addEntry(MatrixEntry m){
		this.entries.add(m);
	}
	
	public Set<MatrixEntry> getEntries(){
		return this.entries;
	}
	
	public String getName(){
		return this.name+"";
	}
	
	public double[][] getAsArray(){
		double[][] ret=new double[this.size][this.size];
		for(MatrixEntry e : this.entries){
			if(e.getValue()!=0){
				ret[e.getX()-1][e.getY()-1]=e.getValue();
			}
		}
		return ret;
	}
	
	public String getMatlab(){
		String mat=this.name+"=sparse("+this.size+","+this.size+");\n";
		for(MatrixEntry e : this.entries){
			if(e.getValue()!=0){
				mat=mat+this.name+"("+e.getX()+","+e.getY()+")="+e.getValue()+";\n";
			}
		}
		//mat=mat+"diary D"+this.name+"\n";
		mat=mat+"d"+this.name+"=det("+this.name+")\n";
		//mat=mat+"diary off\n";
		return mat;
	}
	
	public class MatrixEntry implements Comparable{
		private int x;
		private int y;
		private double value;

		public MatrixEntry(int x, int y, double value){
			this.x=x;
			this.y=y;
			this.value=value;
		}
		
		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}

		public double getValue() {
			return value;
		}

		public void setValue(double value) {
			this.value = value;
		}

		public int compareTo(Object arg0) {
			if(!(arg0 instanceof MatrixEntry)){
				return -1;
			}
			int ax=((MatrixEntry) arg0).getX();
			int ay=((MatrixEntry) arg0).getY();
			double avalue=((MatrixEntry) arg0).getValue();
			if(ax==this.x && ay==this.y && avalue==this.value){
				return 0;
			}else{
				return 1;
			}
		}
		
	}
}
