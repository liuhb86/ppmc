package org.net9.simplex.ppmc.core;

import java.util.BitSet;
import java.util.HashMap;

import org.net9.simplex.ppmc.mat.SmartMatrix;

// TODO : this class architecture is awful.
public class ReorderedAbsorbingDTMC extends SimpleDTMC{

	HashMap<Integer,Integer> reorderMap = new HashMap<Integer,Integer>();
	final int sAccept;
	final int sReject;
	public boolean isAbsorbing;
	
	public ReorderedAbsorbingDTMC(DTMC model, BitSet accept, BitSet reject, boolean reorder) {
		SmartMatrix sm = model.getTrans().increaseSize(2, 2);
		int newSize = sm.getDim();
		this.sAccept = model.size();
		this.sReject = model.size()+1;
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
		this.trans = sm;
		this.numTransients = 2;
		this.currentState = model.getInitState();
		this.ap = model.getAP();
		if (reorder){
			this.reorder((SimpleDTMC) model, accept, reject);
		} else {
			this.isAbsorbing = false;
		}
	}
	public ReorderedAbsorbingDTMC(SimpleDTMC model, BitSet accept, BitSet reject) {
		this(model,accept,reject,true);
	}
	
	void reorder(SimpleDTMC model, BitSet accept, BitSet reject){
		int nTransient = model.numTransients;
		
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
		this.trans.exchange(reorderMap, reorderMap);
		this.numTransients = nTransient;
	}
	
	public int getState(int index) {
		Integer r = reorderMap.get(index);
		if (r==null) return index;
		return r;
	}
	
	public int getAcceptState() {
		return this.sAccept;
	}
	
	public int getRejectState() {
		return this.sReject;
	}
}
