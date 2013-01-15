package org.net9.simplex.ppmc.solver.logic;

import java.util.BitSet;
import java.util.HashMap;

import org.net9.simplex.ppmc.solver.Solver;

public class NotSolver implements Solver {

	Solver c1;
	int size;
	
	public NotSolver(Solver c1, int size) {
		this.c1 = c1;
		this.size = size;
	}
	
	@Override
	public boolean solve(HashMap<String, Double> val) {
		return !c1.solve(val);
	}

	@Override
	public BitSet solveSet(HashMap<String, Double> val) {
		BitSet bs = c1.solveSet(val);
		bs.flip(0, this.size);
		return bs;
	}


}
