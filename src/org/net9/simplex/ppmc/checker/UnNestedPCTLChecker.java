package org.net9.simplex.ppmc.checker;

import org.net9.simplex.ppmc.core.GeneralDTMC;
import org.net9.simplex.ppmc.prop.*;

public class UnNestedPCTLChecker extends BasePCTLChecker {

	GeneralDTMC specificModel;
	SimplePCTLChecker absorbingChecker;

	public UnNestedPCTLChecker (GeneralDTMC model) {
		super(model);
		this.specificModel = model;
		this.absorbingChecker = new SimplePCTLChecker(model.absorbingTrans);
	}
	

	@Override
	public void visit(PropEventually p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(PropUntil propUntil) {
		// TODO Auto-generated method stub

	}
}
