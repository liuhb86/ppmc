package org.net9.simplex.ppmc.checker;

import java.util.BitSet;
import org.net9.simplex.ppmc.core.SimpleDTMC;
import org.net9.simplex.ppmc.prop.*;
import org.net9.simplex.ppmc.solver.Solver;
import org.net9.simplex.ppmc.solver.logic.*;

public class SimplePCTLChecker implements StatePropVisitor {
	SimpleDTMC model;
	
	final Solver sTrue, sFalse;
	
	//checker internal state
	int initState;
	boolean isNested;
	Solver result;
	
	public SimplePCTLChecker (SimpleDTMC model) {
		this.model = model;
		this.sTrue = new TrueSolver(model.size());
		this.sFalse = new FalseSolver(model.size());
	}
	
	public Solver check(StateProp p){
		initState = model.currentState;
		isNested = false;
		p.accept((StatePropVisitor)this);
		return this.result;
	}

	@Override
	public void visit(StateProp p) {assert(false);}

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
		this.check(p.p1);
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
		AndSolver ss = new AndSolver();
		for (StateProp sp: p.item){
			this.check(sp);
			Solver s = this.result;
			if (s==sFalse) {
				this.result = sFalse;
				return;
			}
			if (s==sTrue) continue;
			if (s instanceof SetSolver) 
				ss.item.add(s);
			else
				sc.item.add(s);
		}
		switch(ss.item.size()){
		case 0: break;
		case 1: sc.item.add(ss.item.getFirst()); break;
		default: 
			Solver s = this.getCombinedSolver(ss);
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
		OrSolver ss = new OrSolver();
		for (StateProp sp: p.item){
			this.check(sp);
			Solver s = this.result;
			if (s==sTrue) {
				this.result = sTrue;
				return;
			}
			if (s==sFalse) continue;
			if (s instanceof SetSolver) 
				ss.item.add(s);
			else
				sc.item.add(s);
		}
		switch(ss.item.size()){
		case 0: break;
		case 1: sc.item.add(ss.item.getFirst()); break;
		default: 
			Solver s = this.getCombinedSolver(ss);
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
	
	Solver getConstSolver(boolean b) {
		return b? sTrue: sFalse;
	}
	
	Solver getCombinedSolver(Solver s) {
		BitSet bs = s.solveSet(null);
		if (bs.cardinality()== model.size()) return sTrue;
		else if (bs.cardinality()== 0) return sFalse;
		return new SetSolver(bs);
	}
	
}
