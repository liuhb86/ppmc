package org.net9.simplex.ppmc.solver.logic;

import java.util.BitSet;
import org.net9.simplex.ppmc.core.Assignment;
import org.net9.simplex.ppmc.solver.Solver;

public class NotSolver extends Solver {

	Solver c1;
	int size;
	
	public NotSolver(Solver c1, int size) {
		this.c1 = c1;
		this.size = size;
	}
	
	@Override
	public boolean solve(Assignment val) {
		return !c1.solve(val);
	}

	@Override
	public BitSet solveSet(Assignment val) {
		BitSet bs = c1.solveSet(val);
		bs.flip(0, this.size);
		return bs;
	}


}
