package org.net9.simplex.ppmc.checker;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.net9.simplex.ppmc.core.DTMC;
import org.net9.simplex.ppmc.core.GeneralDTMC;
import org.net9.simplex.ppmc.ltl.SimpleLTLConverter;
import org.net9.simplex.ppmc.prop.PropLTL;
import org.net9.simplex.ppmc.prop.StateProperty;
import org.net9.simplex.ppmc.solver.Solver;
import org.prismmodelchecker.jltl2ba.APSet;
import org.prismmodelchecker.jltl2ba.SimpleLTL;
import org.prismmodelchecker.jltl2dstar.DRA;
import org.prismmodelchecker.jltl2dstar.LTL2Rabin;
import org.prismmodelchecker.prism.PrismException;

public class LTLChecker {
	
	DTMC model;
	ModelChecker mc;
	
	public LTLChecker (DTMC model, ModelChecker mc) {
		this.model = model;
		this.mc = mc;
	}
	
	public Solver check(PropLTL prop) {
		SimpleLTLConverter slc = new SimpleLTLConverter();
		SimpleLTL ltl = slc.convert(prop);
		APSet apSet = slc.getAPSet();
		Map<String, BitSet> apMap = slc.getAPMap();
		Map<String, StateProperty> subFormulaMap = slc.getSubFormula();
		for (Entry<String, StateProperty> e:subFormulaMap.entrySet()) {
			Solver s = (Solver) e.getValue().accept(this.mc);
			if (!s.isConstant()) throw new UnsupportedOperationException();
			apMap.put(e.getKey(), s.solveSet(null));
		}
		DRA dra;
		try {
			dra = LTL2Rabin.ltl2rabin(ltl, apSet);
			dra.print(System.out);
		} catch (PrismException e1) {
			throw new IllegalArgumentException("Cannot convert LTL to DRA.");
		}
		//TODO: 
		return null;
	}
	
	GeneralDTMC constructProductMC(DTMC dtmc, DRA dra, Map<String, BitSet> apMap) {
		//TODO:
		return null;
	}
}
