package org.net9.simplex.ppmc.checker;

import org.net9.simplex.ppmc.core.DTMC;
import org.net9.simplex.ppmc.ltl.SimpleLTLConverter;
import org.net9.simplex.ppmc.prop.PropLTL;
import org.net9.simplex.ppmc.solver.Solver;

public class LTLChecker {
	
	DTMC model;
	LTLChecker (DTMC model) {
		this.model = model;
	}
	
	Solver check(PropLTL prop) {
		SimpleLTLConverter slc = new SimpleLTLConverter();
		
		
	}
}
