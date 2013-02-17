package org.net9.simplex.ppmc.checker;

import org.net9.simplex.ppmc.prop.PropertyVisitor;
import org.net9.simplex.ppmc.prop.StateProperty;
import org.net9.simplex.ppmc.solver.Solver;

public interface ModelChecker extends PropertyVisitor{
	public Solver check(StateProperty p);
	public Solver getResult();
}
