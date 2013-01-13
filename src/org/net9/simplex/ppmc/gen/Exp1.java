package org.net9.simplex.ppmc.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Exp1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int numAbsorbing=2;
		int completed[]=new int[1];
		completed[0]=0;
		
		
		long start=System.currentTimeMillis();
		System.out.println("Loading workers for "+SimpleGeneratorS.NTHREDS+" processors.");
		ExecutorService executor = Executors.newFixedThreadPool(SimpleGeneratorS.NTHREDS);
		List<Future<String>> list = new ArrayList<Future<String>>();
		
		//esperimento1
		int i=0;
		Random seedGen=new Random(6597524);
		for(int avg=4;avg<=4;avg+=2){
			//for(int numComponents=50;numComponents<=1000;){
			for(int numComponents=10;numComponents<=10;){
				for(int sample=0;sample<1;sample++){
					Callable<String> worker = new SimpleGeneratorS("exp1c"+numComponents+"s"+sample, completed, numComponents, numAbsorbing, avg, 2, 0, numComponents-numAbsorbing,3, seedGen.nextInt(),false,true);
					i++;
					Future<String> submit = executor.submit(worker);
					list.add(submit);
				}
				int step=0;
				if(numComponents<100){
					step=25;
				}else if(numComponents<500){
					step=50;
				}else{
					step=100;
				}
				numComponents+=step;
			}
		}
		
		System.out.println("Workers loaded: "+i);
		String res="";
		// Now retrieve the result
		for (Future<String> future : list) {
			try {
				res=res+"\n"+future.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		System.out.println(res);
		executor.shutdownNow();
		System.out.println("Done in "+(System.currentTimeMillis()-start)+" millis.");
	}

}
