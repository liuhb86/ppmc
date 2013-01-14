package org.net9.simplex.ppmc.solver.logic;

import java.util.BitSet;
import java.util.HashMap;

import org.net9.simplex.ppmc.solver.Solver;

public class AndSolver implements Solver {

	Solver c1,c2;
	
	public AndSolver(Solver rc1, Solver rc2) {
		this.c1 = rc1;
		this.c2 = rc2;
	}
	
	@Override
	public boolean solve(HashMap<String, Double> val) {
		return c1.solve(val) && c2.solve(val);
	}

	@Override
	public BitSet solveSet(HashMap<String, Double> val) {
		BitSet bs = c1.solveSet(val);
		bs.and(c2.solveSet(val));
		return bs;
	}

}
