package gen;

import java.util.BitSet;
import java.util.HashMap;

import mat.MatrixIndex;
import mat.SparseMatrix;
import util.Random;
import core.SimpleDTMC;

/**
 * @author Hongbo Liu <liuhongbo@pku.edu.cn, liuhb86@gmail.com>
 */
public class SimpleDTMCGenerator {
	public double mean;
	public double stddev;
	Random rand;
	public SimpleDTMCGenerator (double mean, double stddev, long seed) {
		this.mean=mean;
		this.stddev=stddev;
		this.rand = new Random(seed);
	}
	public SimpleDTMC generate(int numStates, int numAbsorbing, int numVars){
		return generate(numStates, numAbsorbing, numVars, rand.nextLong());
	}
	
	//TODO: seed not fixed.
	public SimpleDTMC generate(int numStates, int numAbsorbing, int numVars, long seed){
        double[][] process=new double[numStates][numStates];
        SparseMatrix<String> vars = new SparseMatrix<String>();
		double sum=0.0;
		int count=0;
		Random rgen = new Random(seed);
		int nTransient = numStates-numAbsorbing;
		int nVarsT=0;
        for(int a=0;a<nTransient ;a++){
			sum=0.0;
			count=0;
			int numTrans=0;
			int nVars = 0;
			while(numTrans<=1 || numTrans>numStates){
				numTrans=(int) (rgen.nextGaussian()*this.stddev+this.mean);
			}
			while(sum<=0.0 || count<numTrans){
				int next=rgen.nextInt(process[a].length);
				if(process[a][next]==0){
					
					// generate variable transitions 
					//TODO: seems biased
					int transLeftState = numTrans - count;
					int transLeft = transLeftState + (int)(mean*(nTransient-a-1));
					if (a+1 == nTransient && nVars + numVars ==1 ) ++numVars;
					int varLeft;
					if (nVars ==1 && transLeft>transLeftState*numVars) {
						varLeft = 1;
						transLeft = transLeftState;
					} else if (nVars==0 && transLeftState==1) { 
						varLeft = 0;
					}else {
						varLeft = numVars;
					}
					//System.out.printf("%d %d\n", varLeft,transLeft);
					int r = rgen.nextInt(transLeft);
					if (r<varLeft) {
						vars.put(a, next, "v"+nVarsT);
						++nVars;
						++nVarsT;
						--numVars;
					}
					
					double x;
					x= 1- rgen.nextDouble();
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
        for (MatrixIndex i : vars.getMap().keySet()) {
        	process[i.row][i.col] = -1;
        }
        SimpleDTMC ret=new SimpleDTMC(process,vars, nTransient,0, new HashMap<String, BitSet>());
        return ret;
	}
}
