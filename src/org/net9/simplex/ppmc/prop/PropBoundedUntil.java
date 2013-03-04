package org.net9.simplex.ppmc.prop;

public class PropBoundedUntil extends PathProperty {
	public StateProperty p1,p2;
	public int bound;
	public PropBoundedUntil(StateProperty p1, StateProperty p2, int bound) {
		this.p1 = p1;
		this.p2 = p2;
		this.bound = bound;
	}

	@Override
	public Object accept(PropertyVisitor visitor) {
		return visitor.visit(this);
	}

}
