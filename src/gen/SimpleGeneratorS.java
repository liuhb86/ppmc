/**
 * 
 */
package gen;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.concurrent.Callable;
import util.Random;

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

	private int numVars;
	private String runID;
	private String prismPath="path to prism";
	private String mrmcPath="path to mrmc";
	private String matlabPath="path matlab";
	private int from;
	private int to;
	private int[] completed;
	private SimpleDTMCGenerator generator;
	private Random rand;
	
	
	
	public SimpleGeneratorS(String runId,int[] completed,int numStates,int numAbsorbing,double mean, double stddev,int from, int to,int numVars,int seed ,boolean usedb,boolean generate){
		this.USEDB=usedb;
		this.JUSTGENERATE=generate;
		this.runID=runId;
		this.completed=completed;
		this.numStates=numStates;
		this.numAbsorbing=numAbsorbing;
		this.from=from;
		this.to=to;
		this.numVars=numVars;
		this.rand = new Random(seed);
		if (seed == 0) {
			this.generator = new SimpleDTMCGenerator(mean,stddev, Random.RANDOM_SEED);
		} else {
			this.generator = new SimpleDTMCGenerator(mean,stddev, rand.nextLong());
		}
		this.res = this.generator.generate(numStates, numAbsorbing, numVars);
		this.res.writeTo(System.out);
	}
	
	public SimpleDTMC getRes(){
		return this.res;
	}
	
	public void setRes(SimpleDTMC res){
		this.res=res;
	}
	
	@Override
	public String call() throws Exception {
		long globalInit=System.currentTimeMillis();
		String ret=this.runID;
		
			long letSee=System.currentTimeMillis();
			res.unsimplified(this.runID+"Du",0,from, to);
			long designTime=(System.currentTimeMillis()-letSee)/1000;
			PrintWriter fOut=new PrintWriter(new FileOutputStream(this.runID+"DT.log"));
			try{
				fOut.println("NumStates:    "+this.numStates);
				fOut.println("NumAbsorbing: "+this.numAbsorbing);
				fOut.println("NumVarRows:   "+this.numVars);
				fOut.println("Desgin Time: "+designTime);
				fOut.println();
				
				System.out.println("Done "+this.runID+" in "+Math.round((System.currentTimeMillis()-globalInit)/1000)+" s");
			}catch(Exception e){
				fOut.println("Something went wrong:\n"+e.getMessage());
				System.out.println("Exception. Seed: "+this.rand.getOriginalSeed()+"\n"+e.getMessage()+"\n"+e.getClass());
				ret=ret+"Exception. Seed: "+this.rand.getOriginalSeed()+"\n"+e.getMessage()+"\n"+e.getClass()+"\nEXCEPTION";
				e.printStackTrace();
			}finally{
				synchronized (this.completed) {
					this.completed[0]++;
				}
				fOut.close();
			}
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

}
