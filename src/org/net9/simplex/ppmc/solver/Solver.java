package org.net9.simplex.ppmc.solver;

import java.io.PrintWriter;
import java.util.BitSet;
import org.net9.simplex.ppmc.core.Assignment;

public abstract class Solver {
	boolean isConstant = false;
	public abstract boolean solve(Assignment val);
	public abstract BitSet solveSet(Assignment val);
	public boolean isConstant(){
		return isConstant;
	}
	public void setConstant(boolean c){
		this.isConstant = c;
	}
	public void writeTo(PrintWriter writer){
		writer.println(this.toString());
	}
}
