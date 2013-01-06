/**
 * 
 */
package gen;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import mat.SmartMatrix;


import core.Node;
import core.SimpleDTMC;
/**
 * @author Antonio Filieri
 *
 */
public class SimpleGeneratorS implements Callable<String>{
	public static final double MAXERROR=10e-10;
	public static final int NTHREDS=2;
	private boolean USEDB=false;
	private boolean JUSTGENERATE=true;
	public static final int NUMRTSAMPLE=10;
	private SimpleDTMC res;
	private int numStates;
	private int numAbsorbing;
	private double mean;
	private double stddev;
	private long seed;
	private int numRows;
	private int[] rws=null;
	private String runID;
	private String prismPath="path to prism";
	private String mrmcPath="path to mrmc";
	private String matlabPath="path matlab";
	private int from;
	private int to;
	private int[] completed;
	
	
	
	public SimpleGeneratorS(String runId,int[] completed,int numStates,int numAbsorbing,double mean, double stddev,int from, int to,int numRows,long seed,boolean usedb,boolean generate){
		this.USEDB=usedb;
		this.JUSTGENERATE=generate;
		this.runID=runId;
		this.completed=completed;
		this.numStates=numStates;
		this.numAbsorbing=numAbsorbing;
		this.mean=mean;
		this.stddev=stddev;
		this.seed=seed;
		this.from=from;
		this.to=to;
		this.numRows=numRows;
		this.res = this.generate();
	}
	
	public SimpleDTMC getRes(){
		return this.res;
	}
	
	public void setRes(SimpleDTMC res){
		this.res=res;
	}

	public int[] getVars(){
		return this.rws;
	}
	
	public void setVars(int[] vars){
		this.rws=vars;
	}
	
	@Override
	public String call() throws Exception {
		long globalInit=System.currentTimeMillis();
		double testRes[]=new double[3];
		String ret=this.runID;
		PreparedStatement ps;
		Connection dbCon;
		if(USEDB){
			// Load the driver class
			Class.forName( "" );
			// Define the data source for the driver
			String dbURL = "";
			// Create a connection through the DriverManager
			dbCon = DriverManager.getConnection( dbURL, "" , "" );
			dbCon.setAutoCommit( false );
			ps = dbCon.prepareStatement( "insert into runsdatamit (\"numStates\",\"numObserved\",\"numAbsorbing\",\"seed\",\"mean\",\"stddev\",\"mlNumericVal\",\"mlNumericTime\",\"mlNumericSpawn\",\"mlSymbolicVal\",\"mlSymbolicTime\",\"mlSymbolicSpawn\",\"javaVal\",\"javaTime\",\"javaSpawn\",\"prismVal\",\"prismTime\",\"prismSpawn\",\"mrmcVal\",\"mrmcTime\",\"mrmcSpawn\") values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		}
		try{

			Random r=new Random(this.seed);
			if(this.rws==null){
				rws=new int[this.numRows];
				for(int i=0;i<this.numRows;i++){
					rws[i]=-1;
				}
				int count=0;
				while(count<this.numRows){
					int nextPlace=r.nextInt(this.numStates-this.numAbsorbing);
					boolean ok=true;
					for(int i=0;i<count;i++){
						if(rws[i]==nextPlace){
							ok=false;
							break;
						}
					}
					if(ok){
						rws[count]=nextPlace;
						count++;
					}
				}
			}

			res.mathematica(this.runID, 0, from, to, rws);
			if(1==2-1){
				System.out.println("Finito");
				return "";
			}
			
			long letSee=System.currentTimeMillis();
			res.unsimplified(this.runID+"Du",0,from, to, rws);
			long designTime=(System.currentTimeMillis()-letSee)/1000;
			PrintWriter fOut=new PrintWriter(new FileOutputStream(this.runID+"DT.log"));
			try{
				fOut.println("NumStates:    "+this.numStates);
				fOut.println("NumAbsorbing: "+this.numAbsorbing);
				fOut.println("NumVarRows:   "+this.numRows);
				fOut.println("AvgNumTrans:  "+this.mean);
				fOut.println("Stddev:       "+this.stddev);
				fOut.println("Seed:         "+this.seed);
				fOut.println("Desgin Time: "+designTime);
				fOut.println();
				if(USEDB){
					ps.setInt(1, this.numStates);
					ps.setInt(3, this.numAbsorbing);
					ps.setLong(4, this.seed);
					ps.setDouble(5, this.mean);
					ps.setDouble(6, this.stddev);
				}
				fOut.flush();
				fOut.close();
				if(!USEDB){
					return "done";
				}
				double[] temp=new double[3];

				testRes[0]=0;
				testRes[1]=0;
				testRes[2]=0;
				for(int it=0;it<NUMRTSAMPLE;it++){
					temp=this.basicMatlab(this.matlabPath, this.runID+"_b.m");
					testRes[0]+=temp[0];
					testRes[1]+=temp[1];
					testRes[2]+=temp[2];
				}
				testRes[0]=testRes[0]/NUMRTSAMPLE;
				testRes[1]=testRes[1]/NUMRTSAMPLE;
				testRes[2]=testRes[2]/NUMRTSAMPLE;
				
				fOut.println("Matlab numeric:\t"+testRes[0]+"\t"+testRes[1]+"\t"+testRes[2]);
				fOut.flush();
				if(USEDB){
					ps.setDouble(7, testRes[0]);
					ps.setDouble(8, testRes[1]);
					ps.setDouble(9, testRes[2]);
				}
				double val=testRes[0];
				long st=System.currentTimeMillis();

				
				if(USEDB){
					ps.setDouble(10, 0);
					ps.setDouble(11, 0);
					ps.setDouble(12, 0);
				}
				
				if(USEDB){
					ps.setInt(2, this.numRows);
				}

				testRes[0]=0;
				testRes[1]=0;
				testRes[2]=0;
				for(int it=0;it<NUMRTSAMPLE;it++){
					temp=this.cRun(this.runID+"Du");
					testRes[0]+=temp[0];
					testRes[1]+=temp[1];
					testRes[2]+=temp[2];
				}
				testRes[0]=testRes[0]/NUMRTSAMPLE;
				testRes[1]=testRes[1]/NUMRTSAMPLE;
				testRes[2]=testRes[2]/NUMRTSAMPLE;
				
				fOut.println("C:\t\t"+testRes[0]+"\t"+testRes[1]+"\t"+testRes[2]);
				fOut.flush();
				if(USEDB){
					ps.setDouble(13, testRes[0]);
					ps.setDouble(14, testRes[1]);
					ps.setDouble(15, testRes[2]);
				}
				if(Math.abs(testRes[0]-val)>SimpleGeneratorS.MAXERROR){
					fOut.println("MAXIMUM ERROR EXCEEDED");
					System.out.println("MAXIMUM ERROR EXCEEDED");
					ret=ret+"MAXIMUM ERROR EXCEEDED";
				}
				

				
				testRes[0]=0;
				testRes[1]=0;
				testRes[2]=0;
				for(int it=0;it<NUMRTSAMPLE;it++){
					temp=this.prismAnalisis(this.prismPath, this.runID+".pm", this.runID+".pctl");
					testRes[0]+=temp[0];
					testRes[1]+=temp[1];
					testRes[2]+=temp[2];
				}
				testRes[0]=testRes[0]/NUMRTSAMPLE;
				testRes[1]=testRes[1]/NUMRTSAMPLE;
				testRes[2]=testRes[2]/NUMRTSAMPLE;
				

				fOut.println("Prism:\t\t"+testRes[0]+"\t"+testRes[1]+"\t"+testRes[2]);
				if(Math.abs(testRes[0]-val)>SimpleGeneratorS.MAXERROR){
					fOut.println("MAXIMUM ERROR EXCEEDED");
					System.out.println("MAXIMUM ERROR EXCEEDED");
					ret=ret+"MAXIMUM ERROR EXCEEDED";
				}
				fOut.flush();
				if(USEDB){
					ps.setDouble(16, testRes[0]);
					ps.setDouble(17, testRes[1]);
					ps.setDouble(18, testRes[2]);
				}
				
				
				
				testRes[0]=0;
				testRes[1]=0;
				testRes[2]=0;
				for(int it=0;it<NUMRTSAMPLE;it++){
					temp=this.mrmcAnalisis(this.mrmcPath,this.runID);
					testRes[0]+=temp[0];
					testRes[1]+=temp[1];
					testRes[2]+=temp[2];
				}
				testRes[0]=testRes[0]/NUMRTSAMPLE;
				testRes[1]=testRes[1]/NUMRTSAMPLE;
				testRes[2]=testRes[2]/NUMRTSAMPLE;
				
				
				fOut.println("MRMC:\t\t"+testRes[0]+"\t"+testRes[1]+"\t"+testRes[2]);
				fOut.flush();
				if(USEDB){
					ps.setDouble(19, testRes[0]);
					ps.setDouble(20, testRes[1]);
					ps.setDouble(21, testRes[2]);
				}
				if(Math.abs(testRes[0]-val)>SimpleGeneratorS.MAXERROR){
					fOut.println("MAXIMUM ERROR EXCEEDED");
					System.out.println("MAXIMUM ERROR EXCEEDED");
					ret=ret+"MAXIMUM ERROR EXCEEDED";
				}

				fOut.flush();
				fOut.close();

				if(USEDB){
					ps.executeUpdate();
				    ps.close();
				    dbCon.commit();
				    dbCon.close();
				}
				System.out.println("Done "+this.runID+" in "+Math.round((System.currentTimeMillis()-globalInit)/1000)+" s");

				synchronized (this.completed) {
					this.completed[0]++;
				}
			}catch(Exception e){
				fOut.println("Something went wrong:\n"+e.getMessage());
				System.out.println("Exception. Seed: "+this.seed+"\n"+e.getMessage()+"\n"+e.getClass());
				ret=ret+"Exception. Seed: "+this.seed+"\n"+e.getMessage()+"\n"+e.getClass()+"\nEXCEPTION";
				e.printStackTrace();
			}finally{
				synchronized (this.completed) {
					this.completed[0]++;
				}
				fOut.close();
			}
		}catch(IOException ex){
			System.err.println("Unable to run:\n"+ex.getMessage());
			ret=ret+"Unable to run:\n"+ex.getMessage()+"EXCEPTION";
			ex.printStackTrace();
		}
		return ret;
	}
	
	
	//returns double[0] result, double[1] exectime,double[2] process execution time (nanos)
	public double[] prismAnalisis(String prismPath, String model, String properties) throws IOException{
		Process p = Runtime.getRuntime().exec(prismPath+" "+model+" "+properties);
		BufferedReader input =new BufferedReader(new InputStreamReader(p.getInputStream()));
		double[] ret=new double[3];
		ret[0]=ret[1]=ret[2]=-1;
		StringBuffer sb=new StringBuffer();
		String line;
		long start=System.nanoTime();
		while ((line = input.readLine()) != null) {
			if(line.startsWith("Time for model checking:")){
				String time=line.substring(line.indexOf(": ")+2,line.indexOf("seconds")-1);
				ret[1]=Double.parseDouble(time)*1.0e6;
			}
			if(line.startsWith("Result (pro")){
				String prob=line.substring(line.indexOf(": ")+2);
				ret[0]=Double.parseDouble(prob);
			}
		}
		long end=System.nanoTime();
		ret[2]=(double)(end-start);
		input.close();
		return ret;
	}

	//returns double[0] result, double[1] exectime,double[2] process execution time (nanos)
	public double[] cRun(String source) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException{
		long begin=System.nanoTime();
		String launcher="launcher"+((int)Math.random())+System.currentTimeMillis()+".sh";
		PrintWriter fOut=new PrintWriter(new FileOutputStream(launcher));
		fOut.println("#!/bin/bash -u");
		//rimosso -O3
		fOut.println("gcc -std=c99 -w -o "+source+" "+source+".c");
		fOut.println("./"+source);
		fOut.close();
		double[] ret=new double[3];
		ret[0]=ret[1]=ret[2]=-1;
		Process p = Runtime.getRuntime().exec("sh "+launcher);
		BufferedReader input =new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		while ((line = input.readLine()) != null){
			if(line.startsWith("TIME ")){
				ret[1]=Double.parseDouble(line.substring("TIME ".length()));
			}
			if(line.startsWith("VALUE ")){
				ret[0]=Double.parseDouble(line.substring("VALUE ".length()));
			}
		}
		long end=System.nanoTime();
		File delL=new File(launcher);
		//delL.delete();
		ret[2]=(double)(end-begin);
		input.close();
		return ret;
	}
	
	//returns double[0] result, double[1] exectime,double[2] process execution time (nanos)
	public double[] mrmcAnalisis(String mrmcPath, String model) throws IOException, InterruptedException{
		String line;
		long start=System.nanoTime();
		String launcher="launcher"+((int)Math.random())+System.currentTimeMillis()+".sh";
		PrintWriter fOut=new PrintWriter(new FileOutputStream(launcher));
		fOut.println("#!/bin/bash -u");
		fOut.println(mrmcPath+" dtmc "+model+".tra "+model+".lab < "+model+".input > "+model+".output");
		fOut.close();
		Process p = Runtime.getRuntime().exec("sh "+launcher);
		BufferedReader input =new BufferedReader(new InputStreamReader(p.getInputStream()));
		while ((line = input.readLine()) != null){}
		input.close();
		double[] ret=new double[3];
		ret[0]=ret[1]=ret[2]=-1;
		input=new BufferedReader(new FileReader(model+".output"));
		while ((line = input.readLine()) != null) {
			if(line.startsWith("The Total Elapsed Model-Checking Time is ")){
				String time=line.substring(line.indexOf("is ")+3,line.indexOf(" milli sec"));
				ret[1]=Double.parseDouble(time)*1.0e3;//millis
			}
			if(line.startsWith(">>$RESULT[")){
				String prob=line.substring(line.indexOf("= ")+2);
				ret[0]=Double.parseDouble(prob);
			}
		}
		long end=System.nanoTime();
		File delL=new File(launcher);
		delL.delete();
		ret[2]=(double)(end-start);
		input.close();
		return ret;
	}
	
	//returns double[0] result, double[1] exectime,double[2] process execution time (nanos)
	public double[] basicMatlab(String matlabPath, String model) throws IOException{
		String launcher="launcher"+((int)Math.random())+System.currentTimeMillis()+".m";
		PrintWriter fOut=new PrintWriter(new FileOutputStream(launcher));
		fOut.println(model.substring(0,model.length()-2));
		fOut.close();
		Process p = Runtime.getRuntime().exec(matlabPath+" < "+launcher);
		BufferedReader input =new BufferedReader(new InputStreamReader(p.getInputStream()));
		double[] ret=new double[3];
		ret[0]=ret[1]=ret[2]=-1;
		StringBuffer sb=new StringBuffer();
		String line;
		long start=System.nanoTime();
		int state=0;//1->wait for result; 3->wait for extime
		while ((line = input.readLine()) != null) {
			if(line.trim().startsWith("result =") && state==0){
				state=1;
				continue;
			}
			if(state==1  && line.trim().length()>0){
				String result=line.trim();
				ret[0]=Double.parseDouble(result);
				state=2;
				continue;
			}
			if(line.trim().startsWith("exTime =") && state==2){
				state=3;
				continue;
			}
			if(state==3 && line.trim().length()>0){
				String extime=line.trim();
				ret[1]=Double.parseDouble(extime)*1.0e6;
				state=4;
				continue;
			}
		}
		long end=System.nanoTime();
		ret[2]=(double)(end-start);
		input.close();
		File delL=new File(launcher);
		delL.delete();
		return ret;
	}
	
	
	public SimpleDTMC generate(){
        double[][] process=new double[this.numStates][this.numStates];
        long se;
        if(this.seed!=-1){
        	se=this.seed;
        }else{
        	se=System.nanoTime()*System.currentTimeMillis();
        }
        Random rgen=new Random(se);
		double sum=0.0;
		int count=0;
		
        for(int a=0;a<process.length-this.numAbsorbing;a++){
			sum=0.0;
			count=0;
			int numTrans=0;
			while(numTrans<=1 || numTrans>this.numStates){
				numTrans=(int) (rgen.nextGaussian()*this.stddev+this.mean);
			}
			while(sum<=0.0 || count<numTrans){
				int next=rgen.nextInt(process[a].length);
				if(process[a][next]==0){
					double x=rgen.nextDouble();
					if(x<0 || x>1){
						//throw new Exception("Porca l'oca");
					}
					while(x<=0 || (x==1 && a==next)){
						x=rgen.nextDouble();
					}
					process[a][next]=x;
					count++;
					sum+=x;
				}
			}
			for(int o=0;o<process[a].length;o++){
				process[a][o]=process[a][o]/sum;
			}
        }
        for(int i=0;i<this.numAbsorbing;i++){
        	process[this.numStates-1-i][this.numStates-1-i]=1;
        }
        SimpleDTMC ret=new SimpleDTMC(process,this.numStates-this.numAbsorbing,0);
        return ret;
	}
	
	public void zipResults() throws IOException{
		File d = new File(".");
		String[] entries = d.list(new PrefixFilter(this.runID));
		/*byte[] buffer = new byte[4096];
		int bytesRead;
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(this.runID+".zip"));
		for (int i = 0; i < entries.length; i++) {
			File f = new File(d, entries[i]);
			FileInputStream in = new FileInputStream(f); // Stream to read file
			ZipEntry entry = new ZipEntry(f.getPath()); // Make a ZipEntry
			out.putNextEntry(entry); // Store entry
			while ((bytesRead = in.read(buffer)) != -1)
				out.write(buffer, 0, bytesRead);
			in.close(); 
		}
		out.close();*/
		for (int i = 0; i < entries.length; i++) {
			File f = new File(d, entries[i]);
			if(!f.getName().endsWith(".log")){
				//f.delete();
			}
		}
	}

}
