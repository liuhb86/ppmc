package org.net9.simplex.ppmc.solver.logic;

import java.util.BitSet;
import java.util.HashMap;

import org.net9.simplex.ppmc.solver.Solver;

public class TrueSolver implements Solver {

	int size;
	
	public TrueSolver(int size){
		this.size = size;
	}
	@Override
	public boolean solve(HashMap<String, Double> val) {
		return true;
	}

	@Override
	public BitSet solveSet(HashMap<String, Double> val) {
		BitSet bs = new BitSet(size);
		bs.set(0, size);
		return bs;
	}

}
