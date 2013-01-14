package org.net9.simplex.ppmc.solver;

import java.util.BitSet;
import java.util.HashMap;

public interface Solver {
	public boolean solve(HashMap<String, Double> val);
	public BitSet solveSet(HashMap<String, Double> val);
}
