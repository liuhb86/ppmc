package org.net9.simplex.ppmc.solver;

import java.util.BitSet;
import java.util.HashMap;

public abstract class Solver {
	boolean isConstant = false;
	public abstract boolean solve(HashMap<String, Double> val);
	public abstract BitSet solveSet(HashMap<String, Double> val);
	public boolean isConstant(){
		return isConstant;
	}
	public void setConstant(boolean c){
		this.isConstant = c;
	}
}
