package org.net9.simplex.ppmc.checker;

import java.util.Stack;

import org.net9.simplex.ppmc.core.SimpleDTMC;
import org.net9.simplex.ppmc.prop.*;
import org.net9.simplex.ppmc.solver.Solver;
import org.net9.simplex.ppmc.solver.logic.*;

public class SimplePCTLChecker implements StatePropVisitor {
	SimpleDTMC model;
	Stack<Solver> stack;
	
	final Solver sTrue, sFalse;
	
	public SimplePCTLChecker (SimpleDTMC model) {
		this.model = model;
		this.sTrue = new TrueSolver(model.size());
		this.sFalse = new FalseSolver(model.size());
	}
	
	public Solver check(StateProp p){
		stack = new Stack<Solver>();
		p.tranverse((StatePropVisitor)this);
		return stack.pop();
	}

	@Override
	public void visit(StateProp p) {assert(false);}

	@Override
	public void visit(PropTrue p) {
		stack.push(new TrueSolver(model.size()));
	}

	@Override
	public void visit(PropAtom p) {
		if(!p.isNested){
			
		}
	}

	@Override
	public void visit(PropNot p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(PropAnd p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(PropOr p) {
		// TODO Auto-generated method stub
		
	}
	
	Solver getConstChecker(boolean b) {
		return b? sTrue: sFalse;
	}
	
}
