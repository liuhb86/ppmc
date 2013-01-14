package org.net9.simplex.ppmc.runtime.logic;

import java.util.BitSet;
import java.util.HashMap;

import org.net9.simplex.ppmc.runtime.RuntimeChecker;

public class TrueChecker implements RuntimeChecker {

	int size;
	
	public TrueChecker(int size){
		this.size = size;
	}
	@Override
	public boolean check(HashMap<String, Double> val) {
		return true;
	}

	@Override
	public BitSet checkState(HashMap<String, Double> val) {
		BitSet bs = new BitSet(size);
		bs.set(0, size);
		return bs;
	}

}
