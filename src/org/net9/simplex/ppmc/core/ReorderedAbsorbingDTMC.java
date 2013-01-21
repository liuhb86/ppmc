package org.net9.simplex.ppmc.core;

import java.util.BitSet;
import java.util.HashMap;

import org.net9.simplex.ppmc.mat.SmartMatrix;

public class ReorderedAbsorbingDTMC extends SimpleDTMC{

	HashMap<Integer,Integer> reorderMap = new HashMap<Integer,Integer>();
	
	public ReorderedAbsorbingDTMC(SimpleDTMC model, BitSet accept, BitSet reject) {
		SmartMatrix sm = model.trans.increaseSize(2, 2);
		int newSize = sm.getDim();
		int nTransient = model.numTransients;
		final int sAccept = model.size();
		final int sReject = model.size()+1;
		BitSet union = (BitSet) accept.clone();
		union.or(reject);
		for(int k=union.nextSetBit(0);k>=0;k=union.nextSetBit(k+1)){
			for (int p=0;p<newSize;++p)
				sm.setEntry(k, p, 0);
			if (accept.get(k))
				sm.setEntry(k, sAccept, 1);
			else
				sm.setEntry(k, sReject, 1);
		}
		int i= model.numTransients;
		int j= model.size()-1;

		while(i<j){
			if (accept.get(i) || reject.get(i)){
				++i;
				++nTransient;
				continue;
			}
			if (!(accept.get(i) || reject.get(i))) {
				--j;
				continue;
			}
			++nTransient;
			reorderMap.put(i, j);
			reorderMap.put(j, i);
		}
		if (i==j && (accept.get(i) || reject.get(i))) {++nTransient;}

		// TODO: unfinished
	}

}
