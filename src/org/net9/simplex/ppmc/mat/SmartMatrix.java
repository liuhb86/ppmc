package org.net9.simplex.ppmc.mat;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map.Entry;
import java.util.Random;

import org.net9.simplex.ppmc.util.Utils;


import Jama.Matrix;

public class SmartMatrix {
	public static final int MIN_SPLIT_SIZE=Integer.MAX_VALUE;
	public static final int MIN_NUM_SPLIT_SIZE=Integer.MAX_VALUE;
	public static final int MIN_MATLAB_SIZE=Integer.MAX_VALUE;
	private double[][] base;
	private SparseMatrix<String> vars;
	private String det = null;
	private String prefix="";
	private String postfix="";
	
	public static double[][] randomSquaredMatrix01(int size){
		double[][] ret = new double[size][size];
		Random r= new Random();
		for(int i=0;i<size;i++){
			for(int j=0;j<size;j++){
				ret[i][j]=r.nextDouble();
			}
		}
		return ret;
	}

	public static double[][] randomZeroDet01(int size){
		double[][] ret;
		if(size>1){
			ret = randomSquaredMatrix01(size);
			for(int i=0;i<size;i++){
				ret[1][i]=ret[0][i];
			}
		}else{
			ret=new double[1][1];
		}
		return ret;
	}
	
	public static double[][] identity(int size){
		double[][] ret = new double[size][size];
		for(int i=0;i<size;i++){
				ret[i][i]=1;
		}
		return ret;
	}
	
	public static double[][] randomSparseSquaredMatrix01Normal(int size,int avg, int stdev){
		double[][] ret = new double[size][size];
		Random r= new Random();
		for(int i=0;i<size;i++){
			int numThisRow=(int)Math.round(r.nextGaussian()*stdev+avg);
			if(numThisRow>size){
				numThisRow=size;
			}
			if(numThisRow<1){
				numThisRow=1;
			}
			int count=0;
			while(count<numThisRow){
				int pos=r.nextInt(size);
				if(ret[i][pos]==0){
					do{
						ret[i][pos]=r.nextDouble();
					}while(ret[i][pos]==0);
					count++;
				}
			}
		}
		return ret;
	}
	
	public SmartMatrix(double[][] base){
		this("","",base,new SparseMatrix<String>());
	}
	public SmartMatrix(double[][] base,SparseMatrix<String> vars){
		this("","",base,vars);
	}
	
	public SmartMatrix(String prefix,String postfix,double[][] base,SparseMatrix<String> vars){
		
		this.base=base;
		/*System.out.println("VARS:");
		for(int i=0;i<vars.length;i++){
			System.out.print(vars[i]+"\t");
		}*/

		this.vars=vars;

		this.prefix=prefix;
		this.postfix=postfix;
	}
	
	public void setPrefix(String p){
		synchronized(this.prefix){
			this.prefix=p;
		}
	}
	
	public String getPrefix(){
		String temp="";
		synchronized(this.prefix){
			temp=this.prefix;
		}
		return temp;
	}

	public String getPostfix(){
		String temp="";
		synchronized(this.postfix){
			temp=this.postfix;
		}
		return temp;
	}
	
	public void setPostfix(String p){
		synchronized(this.postfix){
			this.postfix=p;
		}
	}
	
	public double[][] getBase(){
		return this.base;
	}
	
	public SparseMatrix<String> getVars(){
		return this.vars;
	}
	public int getDim() {
		return this.base.length;
	}
	public int getDimR() {
		return this.base.length;
	}
	public int getDimC() {
		return this.base[0].length;
	}
	
	public String determinant() {
		return determinant(false);
	}
	public String determinant(boolean recalc) {
		//System.out.println("Computing det. Size: "+this.base.length);
		if (this.det != null && !recalc) return this.det; 
		int row=this.getSymbolicLine();
		if(row>=this.base.length){
			//System.out.println("MANNAGGIALAMORTE");
		}
		if(row==-1){
			if(this.base.length==0){
				this.det = "1";
			}else{
					//System.out.println("Computing determinant internally");
					Matrix m=new Matrix(this.base);
					double det = m.det();
					this.det = det==0.0?"0":Double.toString(det);
			}
		}else{
			String ret="";
			for(int i=0;i<this.base[row].length;i++){
				//if(this.base[row][i]==0) continue;
				String coef=(i+row)%2==0?"+":"-";
				SmartMatrix mino=this.minor(row, i);
					if(this.base[row][i]==0){
						//ret=ret+"0*("+mino.determinant()+")+";
					}else{
						String det = mino.determinant();
						if (!det.equals("0"))
							ret=ret+coef+this.getEntry(row, i, true)+"*("+mino.determinant()+")";
					}
			}
			this.det = (ret.length()>0)?ret:"0";
		}
		return this.det;
	}
	
	public SmartMatrix minor(int r,int c){
		double[][] par=new double[this.base.length-1][this.base.length-1];
		SparseMatrix<String> var=new SparseMatrix<String>();
		for(int i=0,i1=0;i<this.base.length;i++){
			if (i==r) continue;
			for(int j=0,j1=0;j<this.base[i].length;j++){
				if(j==c) continue;
				par[i1][j1]=this.base[i][j];
				++j1;
			}
			++i1;
		}
		
		for (Entry<MatrixIndex,String> i: this.vars.getMap().entrySet()){
			int row = i.getKey().row;
			if (row==r) continue;
			int col = i.getKey().col;
			if (col==c) continue;
			if (row>r) --row;
			if (col>c) --col;
			var.put(row, col, i.getValue());
		}
		return new SmartMatrix(par, var);
	}

	
	public int getSymbolicLine(){
		if(this.vars==null) return -1;
		if(this.vars.size() ==0) return -1;
		return this.vars.getMap().firstKey().row;
	}
	
	public String getEntry(int r,int c) {
		return getEntry(r,c,false);
	}
	public String getEntry(int r, int c, boolean bracket){
		String s = getSymbolicEntry(r,c);
		if (s!= null) {
			return bracket? "("+s+")" : s;
		}
		else return Double.toString(base[r][c]);
	}
	public double getNumericEntry(int r,int c){
		return base[r][c];
	}
	public String getSymbolicEntry(int r,int c){
		return vars.getN(r, c);
	}
	public boolean isSymbolicEntry(int r, int c){
		return (base[r][c]<0) && vars.getN(r, c)!=null;
	}
	public String toString(){
		StringWriter writer = new StringWriter();
		this.writeTo(new PrintWriter(writer));
		return writer.toString();
	}
	public void writeTo(Object printer) {
		PrintWriter writer = Utils.getWriter(printer);
		if (writer == null) return;
		for(int i=0;i<this.getDimR(); ++i) {
			for(int j=0; j<this.getDimC(); ++j) {
				writer.print(this.getEntry(i, j));
				writer.print('\t');
			}
			writer.println();
		}
		writer.flush();
	}
}