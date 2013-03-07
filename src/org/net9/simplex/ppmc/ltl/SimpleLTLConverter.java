package org.net9.simplex.ppmc.ltl;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.net9.simplex.ppmc.prop.*;
import org.prismmodelchecker.jltl2ba.APSet;
import org.prismmodelchecker.jltl2ba.SimpleLTL;

public class SimpleLTLConverter implements PropertyVisitor {

	APSet apSet;
	Map<String, BitSet > apMap;
	Map<String, StateProperty> subFormulaMap;
	
	int label;
	public static final String SET_LABEL_PREFIX ="{}";
	
	public SimpleLTL convert(PropLTL prop) {
		label = 0;
		apSet = new APSet();
		apMap = new HashMap<String, BitSet>();
		subFormulaMap = new TreeMap<String, StateProperty> ();
		return p(prop.p1.accept(this));
	}
	
	public APSet getAPSet() {
		return this.apSet;
	}
	
	public Map<String, BitSet> getAPMap() {
		return this.apMap;
	}
	
	public Map<String, StateProperty> getSubFormula() {
		return this.subFormulaMap;
	}
	
	SimpleLTL p(Object o) {
		return (SimpleLTL) o;
	}
	
	@Override
	public Object visit(PropTrue p) {
		return new SimpleLTL(true);
	}
	
	@Override
	public Object visit(PropAtom p) {
		String ap = p.atom;
		if (!apMap.containsKey(p)) {
			apMap.put(ap, null);
			apSet.addAP(ap);
		}
		return new SimpleLTL(ap);
	}

	@Override
	public Object visit(PropNot p) {
		return new SimpleLTL(SimpleLTL.LTLType.NOT, p(p.p1.accept(this)));
	}

	@Override
	public Object visit(PropAnd p) {
		if (p.item.size()==0) return new SimpleLTL(true);
		SimpleLTL ltl = p(p.item.get(0).accept(this));
		for(int i=1;i<p.item.size();++i) {
			ltl = new SimpleLTL(SimpleLTL.LTLType.AND, 
					ltl, p(p.item.get(i).accept(this)));
		}
		return ltl;
	}

	@Override
	public Object visit(PropOr p) {
		if (p.item.size()==0) return new SimpleLTL(false);
		SimpleLTL ltl = p(p.item.get(0).accept(this));
		for(int i=1;i<p.item.size();++i) {
			ltl = new SimpleLTL(SimpleLTL.LTLType.OR, 
					ltl, p(p.item.get(i).accept(this)));
		}
		return ltl;
	}

	@Override
	public Object visit(PropSet p) {
		String ap = SET_LABEL_PREFIX + label;
		++label;
		apSet.addAP(ap);
		apMap.put(ap, p.item);
		return new SimpleLTL(ap);
	}

	@Override
	public Object visit(PropProb p) {
		String ap = SET_LABEL_PREFIX + label;
		++label;
		apSet.addAP(ap);
		apMap.put(ap, null);
		subFormulaMap.put(ap, p);
		return new SimpleLTL(ap);
	}

	@Override
	public Object visit(PropEventually p) {
		return new SimpleLTL(SimpleLTL.LTLType.FINALLY, p(p.p1.accept(this)));
	}

	@Override
	public Object visit(PropAlways p) {
		return new SimpleLTL(SimpleLTL.LTLType.GLOBALLY, p(p.p1.accept(this)));
	}

	@Override
	public Object visit(PropNext p) {
		return new SimpleLTL(SimpleLTL.LTLType.NEXT, p(p.p1.accept(this)));
	}

	@Override
	public Object visit(PropUntil p) {
		return new SimpleLTL(SimpleLTL.LTLType.UNTIL, 
				p(p.p1.accept(this)),p(p.p2.accept(this)));
	}

	@Override
	public Object visit(PropBoundedUntil p) {
		throw new IllegalArgumentException();
	}

	@Override
	public Object visit(PropLTLPathWrapper p) {
		return p.p1.accept(this);
	}

	@Override
	public Object visit(PropLTL p) {
		throw new IllegalArgumentException();
	}

}
