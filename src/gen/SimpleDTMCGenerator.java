package gen;

import util.Random;
import core.SimpleDTMC;

public class SimpleDTMCGenerator {
	public double mean;
	public double stddev;
	Random rand;
	public SimpleDTMCGenerator (double mean, double stddev, long seed) {
		this.mean=mean;
		this.stddev=stddev;
		this.rand = new Random(seed);
	}
	public SimpleDTMC generate(int numStates, int numAbsorbing){
		return generate(numStates, numAbsorbing, rand.nextLong());
	}
	public SimpleDTMC generate(int numStates, int numAbsorbing, long seed){
        double[][] process=new double[numStates][numStates];
		double sum=0.0;
		int count=0;
		Random rgen = new Random(seed);
        for(int a=0;a<process.length-numAbsorbing;a++){
			sum=0.0;
			count=0;
			int numTrans=0;
			while(numTrans<=1 || numTrans>numStates){
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
        for(int i=0;i<numAbsorbing;i++){
        	process[numStates-1-i][numStates-1-i]=1;
        }
        SimpleDTMC ret=new SimpleDTMC(process,numStates-numAbsorbing,0);
        return ret;
	}
}
