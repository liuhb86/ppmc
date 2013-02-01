package org.net9.simplex.ppmc.solver.logic;

import java.io.PrintWriter;
import java.util.BitSet;

import org.net9.simplex.ppmc.core.Assignment;
import org.net9.simplex.ppmc.solver.Solver;

public class SetSolver extends Solver {

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
	public boolean solve(Assignment val) {
		assert(false);
		return false;
	}

	@Override
	public BitSet solveSet(Assignment val) {
		return (BitSet) item.clone();
	}
	
	@Override
	public boolean isConstant(){
		return true;
	}
	
	@Override
	public void writeTo(PrintWriter writer){
		writer.print("STATES: ");
		writer.print(item);
	}
}
