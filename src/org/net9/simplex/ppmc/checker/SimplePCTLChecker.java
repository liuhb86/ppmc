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
	public Object visit(PropEventually p) {
		
		Solver s = s(p.p1.accept(this));
		if (!s.isConstant()){
			//TODO : run time solver
			throw new UnsupportedOperationException();
		}
		BitSet bs = s.solveSet(null);
		
		if (p.parent.isNested) {
			// TODO : return set solver
			throw new UnsupportedOperationException();
		} else {
			return solveEventually(this.initState, bs);
		}
	}	

	@Override
	public Object visit(PropUntil p) {
		
		Solver s1 = s(p.p1.accept(this));
		if (!s1.isConstant()){
			//TODO : run time solver
			throw new UnsupportedOperationException();
		}
		BitSet bs1 = s1.solveSet(null);
		if (bs1.cardinality() == model.size()){
			PropEventually eventually = 
					new PropEventually(p.p2);
			return eventually.accept(this);
		}
				
		Solver s2 = s(p.p2.accept(this));
		if (!s2.isConstant()){
			//TODO : run time solver
			throw new UnsupportedOperationException();
		}
		BitSet bs2 = s2.solveSet(null);
		BitSet bs = (BitSet) bs1.clone();
		bs.or(bs2);
		
		if (bs.isEmpty()){
			return this.getConstSolver(false);
		} else if (bs.cardinality() == bs.size()){
			PropEventually eventually = 
					new PropEventually(p.p2);
			return eventually.accept(this);		
		}
		
		if (p.parent.isNested) {
			// TODO : return set solver
			throw new UnsupportedOperationException();
		} else {
			Node exp = UntilChecker.check(specificModel, bs1, bs2, this.initState);
			return new ExpressionSolver(exp);
		}
	}
}
