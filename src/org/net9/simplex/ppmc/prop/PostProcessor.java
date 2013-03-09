package org.net9.simplex.ppmc.prop;

import java.text.ParseException;

public class PostProcessor implements PropertyVisitor {
	
	boolean hasProb;
	boolean isLTL;
	boolean isOnlyState;
	
	void reset() {
		hasProb = false;
		isLTL = false;
		isOnlyState = true;
	}
	
	StateProperty postProcess(StateProperty p) throws ParseException{
		reset();
		boolean b = b(p.accept(this));
		if (b) throw new ParseException("Unexpected Path Operator", 0);
		return p;
	}
	
	public PathProperty getPathFormula(StateProperty p) throws ParseException {
		reset();
		p.accept(this);
		if (hasProb) throw new ParseException("Nested Probability Operator", 0);
		if (isLTL) return new PropLTL(p);
		if (isOnlyState) return new PropLTL(p);
		return ((PropLTLPathWrapper) p).p1;
		
	}
	
	boolean b(Object o) {
		return (Boolean) o;
	}
	
	@Override
	public Object visit(PropTrue p) {
		return false;
	}

	@Override
	public Object visit(PropAtom p) {
		return false;
	}

	@Override
	public Object visit(PropNot p) {
		boolean b = b(p.p1.accept(this));
		if (b) isLTL = true;
		return b;
	}

	@Override
	public Object visit(PropAnd p) {
		boolean hasPath = false;
		for (StateProperty i: p.item){
			hasPath |= b(i.accept(this));
		}
		if (hasPath) isLTL = true;
		return hasPath;
	}

	@Override
	public Object visit(PropOr p) {
		boolean hasPath = false;
		for (StateProperty i: p.item){
			hasPath |= b(i.accept(this));
		}
		if (hasPath) isLTL = true;
		return hasPath;
	}

	@Override
	public Object visit(PropSet p) {
		return false;
	}

	@Override
	public Object visit(PropProb p) {
		isOnlyState = false;
		hasProb = true;
		return false;
	}

	@Override
	public Object visit(PropEventually p) {
		isOnlyState = false;
		boolean b = b(p.p1.accept(this));
		if (b) isLTL = true;
		return null;
	}

	@Override
	public Object visit(PropAlways p) {
		isOnlyState = false;
		boolean b = b(p.p1.accept(this));
		if (b) isLTL = true;
		return null;
	}

	@Override
	public Object visit(PropNext p) {
		isOnlyState = false;
		boolean b = b(p.p1.accept(this));
		if (b) isLTL = true;
		return null;
	}

	@Override
	public Object visit(PropUntil p) {
		isOnlyState = false;
		boolean b = b(p.p1.accept(this));
		b |= b(p.p2.accept(this));
		if (b) isLTL = true;
		return null;
	}

	@Override
	public Object visit(PropBoundedUntil p) {
		isOnlyState = false;
		boolean b = b(p.p1.accept(this));
		b |= b(p.p2.accept(this));
		if (b) isLTL = true;
		return null;
	}

	@Override
	public Object visit(PropLTLPathWrapper p) {
		p.p1.accept(this);
		isOnlyState = false;
		return true;
	}

	@Override
	public Object visit(PropLTL p) {
		throw new IllegalArgumentException();
	}

}
