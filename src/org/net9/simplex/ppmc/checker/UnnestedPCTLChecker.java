package org.net9.simplex.ppmc.checker;

import java.util.BitSet;

import org.net9.simplex.ppmc.core.GeneralDTMC;
import org.net9.simplex.ppmc.core.ReorderedAbsorbingDTMC;
import org.net9.simplex.ppmc.prop.*;
import org.net9.simplex.ppmc.solver.Solver;

public class UnnestedPCTLChecker extends BasePCTLChecker {

	GeneralDTMC specificModel;
	SimplePCTLChecker absorbingChecker;
	SimpleReachabilityChecker reachChecker;

	public UnnestedPCTLChecker (GeneralDTMC model) {
		super(model);
		this.specificModel = model;
		this.absorbingChecker = new SimplePCTLChecker(model.absorbingTrans);
	}
	
	public Solver solveEventually(int from, BitSet to) {
		BitSet bs = new BitSet(specificModel.absorbingTrans.size());
		for(int i=to.nextSetBit(0);i>=0;i=to.nextSetBit(i+1)){
			bs.set(specificModel.reorderMap[i]);
		}
		if (bs.isEmpty()){
			return this.getConstSolver(false);
		} else {
			return fromAbsorbingChecker(
				absorbingChecker.solveEventually(from, bs));
		}
	}
	
	@Override
	public Object visit(PropEventually p) {
		
		Solver s = s(p.p1.accept(this));
		if (!s.isConstant()){
			//TODO : run time solver
			throw new UnsupportedOperationException();
		}
		BitSet pbs = s.solveSet(null);
		
		if (p.parent.isNested) {
			// TODO : return set solver
			throw new UnsupportedOperationException();
		} else {
			return solveEventually(this.initState, pbs);
		}
		
	}
	
	Solver fromAbsorbingChecker(Solver s){
		//TODO: draft
		if (s==absorbingChecker.sTrue) return this.sTrue;
		if (s==absorbingChecker.sFalse) return this.sFalse;
		return s;
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
			BitSet bsEmpty = new BitSet(model.size());
			bs.flip(0, model.size());
			ReorderedAbsorbingDTMC filteredDTMC = 
					new ReorderedAbsorbingDTMC(model, bsEmpty, bs, false);
			GeneralDTMC dtmc = 
					new GeneralDTMC(filteredDTMC.getTrans(),filteredDTMC.getInitState(),filteredDTMC.getAP());
			UnnestedPCTLChecker checker = new UnnestedPCTLChecker(dtmc);
			return checker.solveEventually(dtmc.getInitState(), bs2);
		}
	}
}
