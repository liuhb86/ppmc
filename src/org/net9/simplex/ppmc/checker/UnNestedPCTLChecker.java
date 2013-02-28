package org.net9.simplex.ppmc.checker;

import java.util.BitSet;

import org.net9.simplex.ppmc.core.GeneralDTMC;
import org.net9.simplex.ppmc.core.SimpleDTMC;
import org.net9.simplex.ppmc.prop.*;
import org.net9.simplex.ppmc.solver.ExpressionSolver;
import org.net9.simplex.ppmc.solver.Solver;
import org.nfunk.jep.Node;

public class UnnestedPCTLChecker extends BasePCTLChecker {

	GeneralDTMC specificModel;
	SimplePCTLChecker absorbingChecker;
	SimpleReachabilityChecker reachChecker;

	public UnnestedPCTLChecker (GeneralDTMC model) {
		super(model);
		this.specificModel = model;
		this.absorbingChecker = new SimplePCTLChecker(model.absorbingTrans);
	}
	

	@Override
	public void visit(PropEventually p) {
		p.p1.accept(this);
		Solver s = this.result;
		if (!s.isConstant()){
			//TODO : run time solver
			throw new UnsupportedOperationException();
		}
		BitSet pbs = s.solveSet(null);
		BitSet bs = new BitSet(specificModel.absorbingTrans.size());
		for(int i=pbs.nextSetBit(0);i>=0;i=pbs.nextSetBit(i+1)){
			bs.set(specificModel.reorderMap[i]);
		}
		
		if (bs.isEmpty()){
			this.result = this.getConstSolver(false);
		} else {
			if (p.parent.isNested) {
				// TODO : return set solver
				throw new UnsupportedOperationException();
			} else {
				this.result = fromAbsorbingChecker(
						absorbingChecker.solveEventually(this.initState, bs));
			}
		}
		
	}
	
	Solver fromAbsorbingChecker(Solver s){
		//TODO: draft
		if (s==absorbingChecker.sTrue) return this.sTrue;
		if (s==absorbingChecker.sFalse) return this.sFalse;
		return s;
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
			// TODO: build new model and checker
			Node exp = UntilChecker.check(specificModel, bs1, bs2, this.initState);
			this.result = new ExpressionSolver(exp);
			return;
		}
	}
	}
}
