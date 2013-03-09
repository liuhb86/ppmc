package org.net9.simplex.ppmc.checker;

import org.net9.simplex.ppmc.core.GeneralDTMC;
import org.net9.simplex.ppmc.prop.PropLTL;

public class UnnestedPCTLStarChecker extends UnnestedPCTLChecker {

	public UnnestedPCTLStarChecker(GeneralDTMC model) {
		super(model);
	}
	
	@Override
	public Object visit(PropLTL p) {
		if (p.parent.isNested) {
			// TODO : return set solver
			throw new UnsupportedOperationException();
		} else {
			LTLChecker checker = new LTLChecker(this.model, this);
			return checker.check(p);
		}
	}
}
