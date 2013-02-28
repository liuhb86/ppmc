package org.net9.simplex.ppmc.checker;

import java.util.BitSet;

import org.net9.simplex.ppmc.core.SimpleDTMC;
import org.net9.simplex.ppmc.prop.*;
import org.net9.simplex.ppmc.solver.*;
import org.nfunk.jep.Node;

public class SimplePCTLChecker extends BasePCTLChecker {
	SimpleDTMC specificModel;
	SimpleReachabilityChecker reachChecker;
	
	public SimplePCTLChecker (SimpleDTMC model) {
		super(model);
		this.specificModel = model;
	}
	
	SimpleReachabilityChecker getReachabilityChecker(){
		if (this.reachChecker==null)
			this.reachChecker = new SimpleReachabilityChecker(specificModel);
		return this.reachChecker;
	}
	
	public Solver solveEventually(int from, BitSet to) {
		SimpleReachabilityChecker rc;
		int destState;
		if (to.isEmpty()){
			return this.getConstSolver(false);
		} else if (to.cardinality()==1) {
			rc = this.getReachabilityChecker();
			destState = to.nextSetBit(0);
			Node exp = rc.check(from, destState);
			return new ExpressionSolver(exp);
		} else {
			BitSet bs1 = this.getConstSolver(true).solveSet(null);
			Node exp = UntilChecker.check(specificModel, bs1, to, from);
			return new ExpressionSolver(exp);
		}
	}
	
	@Override
	public void visit(PropEventually p) {
		p.p1.accept(this);
		Solver s = this.result;
		if (!s.isConstant()){
			//TODO : run time solver
			throw new UnsupportedOperationException();
		}
		BitSet bs = s.solveSet(null);
		
		if (p.parent.isNested) {
			// TODO : return set solver
			throw new UnsupportedOperationException();
		} else {
			this.result = solveEventually(this.initState, bs);
		}
	}	

	@Override
	public void visit(PropUntil p) {
		p.p1.accept(this);
		Solver s1 = this.result;
		if (!s1.isConstant()){
			//TODO : run time solver
			throw new UnsupportedOperationException();
		}
		BitSet bs1 = s1.solveSet(null);
		if (bs1.cardinality() == model.size()){
			PropEventually eventually = 
					new PropEventually(p.p2);
			eventually.accept(this);
			return;
		}
		
		p.p2.accept(this);
		Solver s2 = this.result;
		if (!s2.isConstant()){
			//TODO : run time solver
			throw new UnsupportedOperationException();
		}
		BitSet bs2 = s2.solveSet(null);
		BitSet bs = (BitSet) bs1.clone();
		bs.or(bs2);
		
		if (bs.isEmpty()){
			this.result = this.getConstSolver(false);
			return;
		} else if (bs.cardinality() == bs.size()){
			PropEventually eventually = 
					new PropEventually(p.p2);
			eventually.accept(this);
			return;
		}
		
		if (p.parent.isNested) {
			// TODO : return set solver
			throw new UnsupportedOperationException();
		} else {
			Node exp = UntilChecker.check(specificModel, bs1, bs2, this.initState);
			this.result = new ExpressionSolver(exp);
			return;
		}
	}
}
