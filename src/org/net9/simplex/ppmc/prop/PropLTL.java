package org.net9.simplex.ppmc.prop;

public class PropLTL extends PathProperty {
	public StateProperty p1;
	boolean isOnlyState;
	
	public PropLTL(StateProperty p) {
		this(p, false);
	}
	
	public PropLTL(StateProperty p,boolean isOnlyState) {
		this.p1 = p;
		this.isOnlyState = isOnlyState;
	}

	@Override
	public Object accept(PropertyVisitor visitor) {
		return visitor.visit(this);
	}

}
