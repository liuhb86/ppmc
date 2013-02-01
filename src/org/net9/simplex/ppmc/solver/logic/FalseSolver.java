package org.net9.simplex.ppmc.solver.logic;

import java.io.PrintWriter;
import java.util.BitSet;

import org.net9.simplex.ppmc.core.Assignment;
import org.net9.simplex.ppmc.solver.Solver;

public class FalseSolver extends Solver {

	int size;
	
	public FalseSolver(int size){
		this.size = size;
	}
	@Override
	public boolean solve(Assignment val) {
		return false;
	}

	@Override
	public BitSet solveSet(Assignment val) {
		BitSet bs = new BitSet(size);
		return bs;
	}
	
	@Override
	public boolean isConstant(){
		return true;
	}
	
	@Override
	public void writeTo(PrintWriter writer){
		writer.println("FALSE");
	}
}