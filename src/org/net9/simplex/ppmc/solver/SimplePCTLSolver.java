package org.net9.simplex.ppmc.solver;

import java.util.Stack;

import org.net9.simplex.ppmc.core.SimpleDTMC;
import org.net9.simplex.ppmc.prop.*;
import org.net9.simplex.ppmc.runtime.RuntimeChecker;
import org.net9.simplex.ppmc.runtime.logic.*;

public class SimplePCTLSolver implements StatePropVisitor {
	SimpleDTMC model;
	Stack<RuntimeChecker> stack;
	
	final RuntimeChecker sTrue, sFalse;
	
	public SimplePCTLSolver (SimpleDTMC model) {
		this.model = model;
		this.sTrue = new TrueChecker(model.size());
		this.sFalse = new FalseChecker(model.size());
	}
	
	public RuntimeChecker solve(StateProp p){
		stack = new Stack<RuntimeChecker>();
		p.tranverse((StatePropVisitor)this);
		return stack.pop();
	}

	@Override
	public void visit(StateProp p) {assert(false);}

	@Override
	public void visit(PropTrue p) {
		stack.push(new TrueChecker(model.size()));
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
	
	RuntimeChecker getConstChecker(boolean b) {
		return b? sTrue: sFalse;
	}
	
}
