package org.net9.simplex.ppmc.solver.logic;

import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedList;

import org.net9.simplex.ppmc.solver.Solver;

public class AndSolver extends Solver {

	public LinkedList<Solver> item = new LinkedList<Solver>();
	
	public AndSolver(Solver s1, Solver s2) {
		item.add(s1);
		item.add(s2);
	}
	public AndSolver() {
	}
	
	@Override
	public boolean solve(HashMap<String, Double> val) {
		for (Solver s:item){
			if (!s.solve(val)) return false;
		}
		return true;
	}

	@Override
	public BitSet solveSet(HashMap<String, Double> val) {
		BitSet bs = new BitSet();
		for (Solver s:item)
		bs.and(s.solveSet(val));
		return bs;
	}
}
