package org.net9.simplex.ppmc.runtime;

import java.util.BitSet;
import java.util.HashMap;

public interface RuntimeChecker {
	public boolean check(HashMap<String, Double> val);
	public BitSet checkState(HashMap<String, Double> val);
}
