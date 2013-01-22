package org.net9.simplex.ppmc.checker;

import java.util.BitSet;

import org.net9.simplex.ppmc.core.ReorderedAbsorbingDTMC;
import org.net9.simplex.ppmc.core.SimpleDTMC;
import org.nfunk.jep.Node;

public class UntilChecker {
	public static Node check(SimpleDTMC model, BitSet bs1, BitSet bs2, int from) {
		BitSet accept = bs2;
		BitSet reject = (BitSet) bs1.clone();
		reject.or(bs1);
		reject.flip(0,model.size());
		ReorderedAbsorbingDTMC dtmc = 
				new ReorderedAbsorbingDTMC(model,accept,reject);
		SimpleReachabilityChecker checker = 
				new SimpleReachabilityChecker(dtmc);
		from = dtmc.getState(from);
		return checker.check(from, dtmc.getAcceptState());
		
	}
}
