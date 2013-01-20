package org.net9.simplex.ppmc.solver;

import org.net9.simplex.ppmc.core.Assignment;
import org.net9.simplex.ppmc.util.BinaryPredicator;

public interface NumericSolver {
	public double solveNumeric(Assignment val);
	public double solveNumeric(Assignment val, int index);
	public void setConstraints(BinaryPredicator<Double,Double> pred, double value);
	public boolean isSingle();
}