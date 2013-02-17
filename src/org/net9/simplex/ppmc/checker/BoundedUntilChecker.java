package org.net9.simplex.ppmc.checker;

import java.util.BitSet;

import org.lsmp.djep.matrixJep.nodeTypes.MatrixNodeI;
import org.net9.simplex.ppmc.core.DTMC;
import org.net9.simplex.ppmc.core.ReorderedAbsorbingDTMC;
import org.net9.simplex.ppmc.mat.JepMatrix;
import org.nfunk.jep.Node;

public class BoundedUntilChecker {
	public static Node check(DTMC model, int bound,BitSet bs1, BitSet bs2, int from) {
		BitSet accept = bs2;
		BitSet reject = (BitSet) bs1.clone();
		reject.or(bs1);
		reject.flip(0,model.size());
		ReorderedAbsorbingDTMC dtmc = 
				new ReorderedAbsorbingDTMC(model,accept,reject, false);
		MatrixNodeI trans = JepMatrix.valueOf(dtmc.getTrans());
		MatrixNodeI matrix = JepMatrix.power(trans, bound);
		from = dtmc.getState(from);
		return JepMatrix.get(matrix, from, dtmc.getAcceptState());
		
	}
}
