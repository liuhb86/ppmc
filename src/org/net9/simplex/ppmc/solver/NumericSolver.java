package org.net9.simplex.ppmc.solver;

import java.io.PrintWriter;

import org.net9.simplex.ppmc.core.Assignment;
import org.net9.simplex.ppmc.util.BinaryPredicator;

public interface NumericSolver {
	public double solveNumeric(Assignment val);
	public double solveNumeric(Assignment val, int index);
	public boolean isSingle();
	public void complement();
	public void writeTo(PrintWriter writer);
	void setConstraints(String strPred, BinaryPredicator<Double, Double> pred,
			double value);
}