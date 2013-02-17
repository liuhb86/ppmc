package org.net9.simplex.ppmc.checker;

import java.util.BitSet;

import org.net9.simplex.ppmc.core.DTMC;
import org.net9.simplex.ppmc.prop.*;
import org.net9.simplex.ppmc.solver.*;
import org.net9.simplex.ppmc.solver.logic.*;
import org.nfunk.jep.Node;

public abstract class BasePCTLChecker implements ModelChecker {

	DTMC model;
	
	//checker internal state
	int initState;
	boolean isNested;
	public final Solver sTrue, sFalse;
	Solver result;
	NextChecker nextChecker;
	
	public BasePCTLChecker (DTMC model) {
		this.model = model;
		this.sTrue = new TrueSolver(model.size());
		this.sFalse = new FalseSolver(model.size());
	}
	
	NextChecker getNextChecker() {
		if (this.nextChecker==null)
			this.nextChecker = new NextChecker(model.getTrans());
		return this.nextChecker;
		
	}
	
	@Override
	public Solver check(StateProperty p) 
			throws IllegalArgumentException, 
				UnsupportedOperationException {
		initState = model.getInitState();
		isNested = false;
		p.accept(this);
		return this.result;
	}
	
	@Override
	public Solver getResult() {
		return this.result;
	}
	
	Solver getConstSolver(boolean b) {
		return b? sTrue: sFalse;
	}
	
	Solver getSetSolver(Solver s) {
		if (s==sTrue || s==sFalse) return s;
		BitSet bs = s.solveSet(null);
		if (bs.cardinality()== model.size()) return sTrue;
		else if (bs.cardinality()== 0) return sFalse;
		return new SetSolver(bs);
	}

	@Override
	public void visit(PropTrue p) {
		this.result = sTrue;
	}

	@Override
	public void visit(PropNot p) {
		p.p1.accept(this);
		Solver s = this.result;
		if (s==sTrue) this.result = sFalse;
		else if (s==sFalse) this.result = sTrue;
		else {
			this.result = new NotSolver(s, model.size());
			if (s.isConstant()) 
				this.result = this.getSetSolver(this.result);
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
			Solver s = this.getSetSolver(constSolver);
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
			Solver s = this.getSetSolver(constSolver);
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
	public void visit(PropAtom p) {
		BitSet ap = model.getAP().get(p.atom); 
		if (ap == null) throw new IllegalArgumentException("No Atom Proposition: "+p.atom);
		if(!isNested){
			this.result = this.getConstSolver(ap.get(initState));
		} else {
			this.result = new SetSolver(ap);
		}
	}
	
	@Override
	public void visit(PropSet p) {
		if (isNested){
			this.result = new SetSolver(p.item, model.size());
		} else {
			this.result = this.getConstSolver(p.item.contains(initState));
		}
	}
	
	@Override
	public void visit(PropProb p) {
		p.isNested = this.isNested;
		this.isNested = true;
		p.p1.accept(this);
		ExpressionSolver s = (ExpressionSolver) this.result;
		s.setConstraints(p.strComparator, p.comparator, p.prob);
		if (s.isConstant()){
			if (s.isSingle()) {
				this.result = this.getConstSolver(s.solve(null));
			} else {
				this.result = this.getSetSolver(s);
			}
		}
		this.isNested = p.isNested;
	}

	@Override
	public abstract void visit(PropEventually p);

	@Override
	public void visit(PropAlways p) {
		PropEventually eventually = 
				new PropEventually(new PropNot(p.p1));
		eventually.accept(this);
		((NumericSolver) this.result).complement();
	}

	@Override
	public void visit(PropNext p) {
		p.p1.accept(this);
		Solver s = this.result;
		if (!s.isConstant()){
			//TODO : run time solver
			throw new UnsupportedOperationException();
		}
		
		if (p.parent.isNested) {
			// TODO : return set solver
			throw new UnsupportedOperationException();
		} else {
			NextChecker nextChecker = this.getNextChecker();
			Node exp = nextChecker.check(this.initState, s.solveSet(null));
			this.result = new ExpressionSolver(exp);
		}
	}

	@Override
	public abstract void visit(PropUntil p);

	@Override
	public void visit(PropBoundedUntil p) {
		p.p1.accept(this);
		Solver s1 = this.result;
		p.p2.accept(this);
		Solver s2 = this.result;
		if (!s1.isConstant() || !s2.isConstant()){
			//TODO : run time solver
			throw new UnsupportedOperationException();
		}
		
		BitSet bs1 = s1.solveSet(null);
		BitSet bs2 = s2.solveSet(null);
		if (p.parent.isNested) {
			// TODO : return set solver
			throw new UnsupportedOperationException();
		} else {
			Node r = BoundedUntilChecker.check(model,p.bound, bs1, bs2, this.initState);
			this.result = new ExpressionSolver(r);
		}
	}
}
