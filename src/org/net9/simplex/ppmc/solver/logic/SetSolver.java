package org.net9.simplex.ppmc.solver.logic;

import java.util.BitSet;
import java.util.HashMap;

import org.net9.simplex.ppmc.solver.Solver;

public class SetSolver implements Solver {

	BitSet item;
	public SetSolver(BitSet item){
		this.item = item;
	}
	
	public SetSolver(Iterable<Integer> item, int size) {
		this.item = new BitSet(size);
		for (int i:item){
			this.item.set(i);
		}
	}
	
	@Override
	public boolean solve(HashMap<String, Double> val) {
		assert(false);
		return false;
	}

	@Override
	public BitSet solveSet(HashMap<String, Double> val) {
		return (BitSet) item.clone();
	}
}
