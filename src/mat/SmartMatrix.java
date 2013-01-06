package mat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import Jama.Matrix;

public class SmartMatrix  extends Thread{
	public static final int MIN_SPLIT_SIZE=Integer.MAX_VALUE;
	public static final int MIN_NUM_SPLIT_SIZE=Integer.MAX_VALUE;
	public static final int MIN_MATLAB_SIZE=Integer.MAX_VALUE;
	private double[][] base;
	private int[] vars;
	private String[][] indexes;
	private String det="";
	private String prefix="";
	private String postfix="";
	private String matlabAssign="";
	/**
	 * @param args
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public static void main(String[] args) throws NumberFormatException, IOException{
		double error=0;
		Random r=new Random();
		double[][] smb={{0.1,0.1,0.1},{0.43,0.62,0.87},{0.92,0.53,0.82}};
		SmartMatrix sm1=new SmartMatrix(smb,0,1,2);
		//System.out.println(sm1.determinant());
		//System.out.println(sm1.getMatlabAssign());
		
		for(int i=1;false && i<2;i++){
			int size=1000;//r.nextInt(999)+1;
			int avg=15;//Math.max((int)size/2, r.nextInt(size+1));
			int stddev=5;//r.nextInt(Math.max(1, (int)(size/2)));
			System.out.println("AVG: "+avg+"\tSTDDEV: "+stddev+"\tDENSITY: "+((double)avg/size));
			double[][] test1=SmartMatrix.randomSparseSquaredMatrix01Normal(size, avg, stddev);
			Matrix m=new Matrix(test1);
			SmartMatrix sm= new SmartMatrix(test1);
			long start=System.currentTimeMillis();
			double detI=Double.parseDouble(sm.determinant(null));
			long stop=System.currentTimeMillis();
			System.out.println("Internal computation ("+size+"): "+(stop-start));
			System.out.println("Internal result: "+detI);
			start=System.currentTimeMillis();
			double detM=sm.solveMatlab();
			stop=System.currentTimeMillis();
			System.out.println("Matlab computation ("+size+"): "+(stop-start));
			System.out.println("Results:\n"+detI+"\t"+detM+"\nError: "+Math.abs(detI-detM)+"\nJama: "+m.det());
			error+=Math.abs(detI-detM);
		}
		System.out.println("average error: "+(error/1000));
	}
	
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
	
	public SmartMatrix(double[][] base,int ... vars){
		this("","",base,vars);
	}
	
	public SmartMatrix(String prefix,String postfix,double[][] base,int ... vars){
		this.indexes=new String[base.length][base.length];
		String alph="abcdefghijklmnopqrstuvwxyz";
		for(int i=0;i<base.length;i++){
			for(int j=0;j<base[i].length;j++){
				this.indexes[i][j]="v"+(i*base.length+j);
				//System.out.print(this.indexes[i][j]+"\t");
			}
		}
		this.base=base;
		Arrays.sort(vars);
		/*System.out.println("VARS:");
		for(int i=0;i<vars.length;i++){
			System.out.print(vars[i]+"\t");
		}*/
		if(vars.length>0 && vars[vars.length-1]>this.base.length){
			System.out.println("NONDEVEESSERECOSI "+this.base.length);
		}
		this.vars=vars;
		
		this.prefix=prefix;
		this.postfix=postfix;
		this.matlabAssign="";
	}
	
	public SmartMatrix(String[][] indexes,double[][] base,int ... vars){
		this("","",indexes,base,vars);
	}
	
	public SmartMatrix(String prefix,String postfix,String[][] indexes,double[][] base,int ... vars){
		this.indexes=indexes;
		this.base=base;
		Arrays.sort(vars);
		if(vars.length>0 && vars[vars.length-1]>this.base.length){
			System.out.println("NONDEVEESSERECOSI "+this.base.length);
		}
		this.vars=vars;
		this.prefix=prefix;
		this.postfix=postfix;
		this.matlabAssign="";
	}
	
	public void run(){
		String temp="";
		try {
			temp = this.determinant(null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		synchronized(this.det){
			this.det=temp;
		}
	}
	
	public String getDeterminant(){
		String temp="";
		synchronized(this.det){
			temp=this.det;
		}
		return temp;
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
	
	public int[] getVars(){
		return this.vars;
	}
	
	public String[][] getIndexes(){
		return this.indexes;
	}
	
	public String getIndex(int r,int c){
		if(r<this.indexes.length && c<this.indexes.length && c>=0 && r>=0){
			return this.indexes[r][c];
		}
		return null;
	}
	
	//TODO rendere efficiente
	public double valFromIndex(String index){
		for(int i=0;i<this.indexes.length;i++){
			for(int j=0;j<this.indexes[i].length;j++){
				if(this.indexes[i][j].equals(index)){
					return this.base[i][j];
				}
			}
		}
		return -1;
	}
	
	public String determinant(MatlabBatch mb) throws IOException{
		//System.out.println("Computing det. Size: "+this.base.length);
		int row=this.getSymbolicLine();
		if(row>=this.base.length){
			//System.out.println("MANNAGGIALAMORTE");
		}
		if(row==-1){
			if(this.base.length==0){
				return "(1)";
			}else{
				if(mb==null){
					//System.out.println("Computing determinant internally");
					Matrix m=new Matrix(this.base);
					return "("+m.det()+")";
				}else{
					String placeholder=mb.getUniqueId();
					//System.out.println("Adding determinant computation to Matlab "+placeholder);
					mb.addMatrix(placeholder, this.base);
					return "("+placeholder+")";
				}
			}
		}else{
			String ret="";
			for(int i=0;i<this.base[row].length;i++){
				//if(this.base[row][i]==0) continue;
				int coef=(int)Math.pow(-1, i+row);
				SmartMatrix mino=this.minor(row, i);
				if(this.base[row][i]==1){
					ret=ret+"("+coef+")*("+mino.determinant(mb)+")+";
				}else{
					//System.out.println(row);
					if(this.base[row][i]==0){
						//ret=ret+"0*("+mino.determinant()+")+";
					}else{
						ret=ret+"("+coef+")*("+this.indexes[row][i]+")*("+mino.determinant(mb)+")+";
					}
				}
			}
			return (ret.length()>0)?ret.substring(0,(ret.charAt(ret.length()-1)=='+')?ret.length()-1:ret.length()):"0";
		}
	}
	
	public String getMatlabAssign(){
		return ""+this.matlabAssign;
	}
	
	public String determinant1() throws IOException{
		//System.out.println("Computing det. Size: "+this.base.length);
		int row=this.getSymbolicLine();
		if(row==-1){
			if(this.base.length==0){
				return "1";
			}else{
				//check
				if(true || this.base.length<SmartMatrix.MIN_NUM_SPLIT_SIZE){
					if(this.base.length<this.MIN_MATLAB_SIZE){
						Matrix m=new Matrix(this.base);
						return ""+m.det();
					}else{
						return ""+this.solveMatlab();
					}
				}else{
					ArrayList<SmartMatrix> workers = new ArrayList<SmartMatrix>();
					ArrayList<Double> coefs = new ArrayList<Double>();
					for(int i=0;i<this.base[0].length;i++){
						if(this.base[0][i]==0) continue;
						int coef=(int)Math.pow(-1, i);
						coefs.add(new Double(this.base[0][i]*coef));
						SmartMatrix temp = this.minor(0, i);
						temp.start();
						workers.add(temp);
					}
					double result=0.0;
					while(!workers.isEmpty()){
						SmartMatrix temp = workers.get(0);
						if(!temp.getDeterminant().equals("")){
							result=result+Double.parseDouble(temp.getDeterminant())*coefs.get(0).doubleValue();
						}else{
							try {
								temp.join();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					return ""+result;
				}
			}
		}else{
			String ret="";
			if(this.base.length<SmartMatrix.MIN_SPLIT_SIZE){
				//SERIAL EXECUTION
				for(int i=0;i<this.base[row].length;i++){
					if(this.base[row][i]==0) continue;
					int coef=(int)Math.pow(-1, i+row);
					if(coef<0) ret=ret+"("+coef+")*";				
					if(this.base[row][i]==1){
						ret=ret+"("+this.minor(row, i).determinant(null)+")";
					}else{
						ret=ret+this.indexes[row][i]+"*("+this.minor(row, i).determinant(null)+")+";
					}
				}
			}else{
				//PARALLELIZE 
				ArrayList<SmartMatrix> workers = new ArrayList<SmartMatrix>();
				for(int i=0;i<this.base[row].length;i++){
					if(this.base[row][i]==0) continue;
					int coef=(int)Math.pow(-1, i+row);
					System.out.println("XXXXXXXXXX");
					String pref=this.indexes[row][i]+"*(";
					if(coef<0) pref="("+coef+")*"+pref;
					SmartMatrix temp = this.minor(row, i);
					temp.setPrefix(pref);
					temp.setPostfix(")");
					temp.start();
					workers.add(temp);
				}
				while(!workers.isEmpty()){
					SmartMatrix temp = workers.get(0);
					if(!temp.getDeterminant().equals("")){
						ret=ret+temp.getPrefix()+temp.getDeterminant()+temp.getPostfix()+"+";
						workers.remove(0);
					}else{
						try {
							temp.join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
			return (ret.length()>0)?ret.substring(0,ret.length()-1):"0";
		}
	}
	
	
	public SmartMatrix minor(int r,int c){
		double[][] par=new double[this.base.length-1][this.base.length-1];
		String[][] ind=new String[this.base.length-1][this.base.length-1];
		int nr=0,nc=0;
		for(int i=0;i<this.base.length;i++){
			for(int j=0;j<this.base[i].length;j++){
				if(i==r || j==c) continue;
				nr=(i<r)?i:i-1;
				nc=(j<c)?j:j-1;
				double t=this.base[i][j];
				par[nr][nc]=t;
				ind[nr][nc]=this.indexes[i][j];
			}
		}
		int position=-1;
		for(int i=0;i<this.vars.length;i++){
			if(this.vars[i]==r){
				position=i;
				break;
			}
		}
		int[] v;
		nr=0;
		if(position!=-1){
			v=new int[this.vars.length-1];
			boolean mod=false;
			for(int i=0;i<this.vars.length;i++){
				if(i==position) continue;
				v[nr++]=(this.vars[i]<r)?this.vars[i]:this.vars[i]-1;
				mod=true;
			}
			if(!mod){
				v=new int[0];
			}
		}else{
			v=this.vars;
		}
		return new SmartMatrix(ind,par, v);
	}

	
	public int getSymbolicLine(){
		if(this.vars==null) return -1;
		if(this.vars.length==0) return -1;
		if(this.vars[0]>=this.base.length){
			//System.out.println("Riga sbagliata "+this.vars[0]+"\t"+this.base.length);
			return -1;
		}
		return this.vars[0];
	}
	
	public double solveMatlab(){
		PrintWriter fOut;
		//System.out.println("matlab");
		try {
			String temp="dsma"+Math.abs((int)(System.currentTimeMillis()*System.nanoTime()))+".m";
			fOut = new PrintWriter(new FileOutputStream(temp));
			fOut.println("format long;");
			fOut.println("Q=sparse("+this.base.length+","+this.base.length+");");
			for(int i=0;i<this.base.length;i++){
				for(int j=0;j<this.base[i].length;j++){
					if(this.base[i][j]!=0)
						fOut.println("Q("+(i+1)+","+(j+1)+")="+this.base[i][j]+";");
				}
			}
			fOut.println("result=det(Q)");
			fOut.println("clear all;");
			fOut.println("quit");
			fOut.close();

			String launcher="launcher"+((int)Math.random())+System.currentTimeMillis()+".m";
			PrintWriter fOut1=new PrintWriter(new FileOutputStream(launcher));
			fOut1.println(temp.substring(0,temp.length()-2));
			fOut1.close();
			Process p = Runtime.getRuntime().exec("matlab < "+launcher);
			BufferedReader input =new BufferedReader(new InputStreamReader(p.getInputStream()));
			StringBuffer sb=new StringBuffer();
			String line;
			double resultd=-1;
			int state=0;//1->wait for result; 3->wait for extime
			while ((line = input.readLine()) != null) {
				//System.out.println(line);
				if(line.trim().startsWith("result =") && state==0){
					state=1;
					continue;
				}
				if(state==1  && line.trim().length()>0){
					String result=line.trim();
					resultd=Double.parseDouble(result);
					state=2;
					continue;
				}
			}
			(new File(launcher)).delete();
			(new File(temp)).delete();
			//System.out.println("matlabComplete");
			return resultd;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
}
