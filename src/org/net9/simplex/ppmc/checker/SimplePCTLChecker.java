package org.net9.simplex.ppmc.checker;

import java.util.BitSet;
import org.net9.simplex.ppmc.core.SimpleDTMC;
import org.net9.simplex.ppmc.prop.*;
import org.net9.simplex.ppmc.solver.Solver;
import org.net9.simplex.ppmc.solver.logic.*;

public class SimplePCTLChecker implements PropertyVisitor {
	SimpleDTMC model;
	
	final Solver sTrue, sFalse;
	
	//checker internal state
	int initState;
	boolean isNested;
	Solver result;
	SimpleReachabilityChecker reachChecker;
	
	public SimplePCTLChecker (SimpleDTMC model) {
		this.model = model;
		this.sTrue = new TrueSolver(model.size());
		this.sFalse = new FalseSolver(model.size());
	}
	
	public Solver check(StateProperty p){
		initState = model.currentState;
		isNested = false;
		p.accept(this);
		return this.result;
	}
	
	@Override
	public void visit(PropTrue p) {
		this.result = sTrue;
	}

	@Override
	public void visit(PropAtom p) {
		BitSet ap = model.ap.get(p.atom); 
		if(!isNested){
			this.result = this.getConstSolver(ap.get(initState));
		} else {
			this.result = new SetSolver(ap);
		}
	}

	@Override
	public void visit(PropNot p) {
		p.p1.accept(this);
		p.p1.accept(this);
		Solver s = this.result;
		if (s==sTrue) this.result = sFalse;
		else if (s==sFalse) this.result = sTrue;
		else {
			this.result = new NotSolver(s, model.size());
			if (s instanceof SetSolver) 
				this.result = this.getCombinedSolver(this.result);
		}
		
	}

	@Override
	public void visit(PropAnd p) {
		AndSolver sc = new AndSolver();
		AndSolver constSolver = new AndSolver();
		for (StateProperty sp: p.item){
			sp.accept(this);
			Solver s = this.result;
			if (s==sFalse) {
				this.result = sFalse;
				return;
			}
			if (s==sTrue) continue;
			if (s.isConstant()) 
				constSolver.item.add(s);
			else
				sc.item.add(s);
		}
		switch(constSolver.item.size()){
		case 0: break;
		case 1: sc.item.add(constSolver.item.getFirst()); break;
		default:
			Solver s = this.getCombinedSolver(constSolver);
			if (s==sFalse) {
				this.result = sFalse;
				return;
			}
			if (s!=sTrue) sc.item.add(s);
		}
		switch(sc.item.size()){
		case 0: this.result = sTrue; break;
		case 1: this.result = sc.item.getFirst(); break;
		default: this.result = sc;
		}
	}

	@Override
	public void visit(PropOr p) {
		OrSolver sc = new OrSolver();
		OrSolver constSolver = new OrSolver();
		for (StateProperty sp: p.item){
			sp.accept(this);
			Solver s = this.result;
			if (s==sTrue) {
				this.result = sTrue;
				return;
			}
			if (s==sFalse) continue;
			if (s.isConstant()) 
				constSolver.item.add(s);
			else
				sc.item.add(s);
		}
		switch(constSolver.item.size()){
		case 0: break;
		case 1: sc.item.add(constSolver.item.getFirst()); break;
		default: 
			Solver s = this.getCombinedSolver(constSolver);
			if (s==sTrue) {
				this.result = sTrue;
				return;
			}
			if (s!=sFalse) sc.item.add(s);
		}
		switch(sc.item.size()){
		case 0: this.result = sFalse; break;
		case 1: this.result = sc.item.getFirst(); break;
		default: this.result = sc;
		}		
	}
	
	@Override
	public void visit(PropSet p) {
		if (isNested){
			this.result = new SetSolver(p.item, model.size());
		} else {
			this.result = this.getConstSolver(p.item.contains(model.currentState));
		}
	}
	
	@Override
	public void visit(PropProb p) {
		p.isNested = this.isNested;
		this.isNested = true;
		p.p1.accept(this);
		this.isNested = p.isNested;
	}

	@Override
	public void visit(PropEventually p) {
		p.p1.accept(this);
		Solver s = this.result;
		if (!s.isConstant()){
			//TODO : run time solver
			this.result = this.getConstSolver(false);
		}
		BitSet bs = s.solveSet(null);
		SimpleReachabilityChecker rc;
		int destState;
		if (bs.cardinality()==1) {
			rc = this.getReachabilityChecker();
			destState = bs.nextSetBit(0);
		} else {
			// TODO: build new model and checker
			rc = this.getReachabilityChecker();
			destState = bs.nextSetBit(0);
		}
		if (p.parent.isNested) {
			// TODO : return set solver
		}
	}	
	
	Solver getConstSolver(boolean b) {
		return b? sTrue: sFalse;
	}
	
	Solver getCombinedSolver(Solver s) {
		BitSet bs = s.solveSet(null);
		if (bs.cardinality()== model.size()) return sTrue;
		else if (bs.cardinality()== 0) return sFalse;
		return new SetSolver(bs);
	}
	
	SimpleReachabilityChecker getReachabilityChecker(){
		if (this.reachChecker==null)
			this.reachChecker = new SimpleReachabilityChecker(model);
		return this.reachChecker;
	}

}
