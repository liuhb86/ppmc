package org.net9.simplex.ppmc.solver.logic;

import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedList;

import org.net9.simplex.ppmc.solver.Solver;

public class OrSolver extends Solver{
	public LinkedList<Solver> item = new LinkedList<Solver>();
	
	public OrSolver(Solver s1, Solver s2) {
		item.add(s1);
		item.add(s2);
	}
	public OrSolver() {
	}
	
	@Override
	public boolean solve(HashMap<String, Double> val) {
		for (Solver s:item){
			if (s.solve(val)) return true;
		}
		return false;
	}

	@Override
	public BitSet solveSet(HashMap<String, Double> val) {
		BitSet bs = new BitSet();
		for (Solver s:item)
		bs.or(s.solveSet(val));
		return bs;
	}
}
