package org.net9.simplex.ppmc.runtime.logic;

import java.util.BitSet;
import java.util.HashMap;

import org.net9.simplex.ppmc.runtime.RuntimeChecker;

public class AndChecker implements RuntimeChecker {

	RuntimeChecker rc1,rc2;
	
	public AndChecker(RuntimeChecker rc1, RuntimeChecker rc2) {
		this.rc1 = rc1;
		this.rc2 = rc2;
	}
	
	@Override
	public boolean check(HashMap<String, Double> val) {
		return rc1.check(val) && rc2.check(val);
	}

	@Override
	public BitSet checkState(HashMap<String, Double> val) {
		BitSet bs = rc1.checkState(val);
		bs.and(rc2.checkState(val));
		return bs;
	}

}
