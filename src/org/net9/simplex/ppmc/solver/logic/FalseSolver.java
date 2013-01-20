package org.net9.simplex.ppmc.solver.logic;

import java.util.BitSet;
import java.util.HashMap;

import org.net9.simplex.ppmc.solver.Solver;

public class FalseSolver extends Solver {

	int size;
	
	public FalseSolver(int size){
		this.size = size;
	}
	@Override
	public boolean solve(HashMap<String, Double> val) {
		return false;
	}

	@Override
	public BitSet solveSet(HashMap<String, Double> val) {
		BitSet bs = new BitSet(size);
		return bs;
	}
	
	@Override
	public boolean isConstant(){
		return true;
	}
}