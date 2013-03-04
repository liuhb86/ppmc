package org.net9.simplex.ppmc.prop;

public class PropEventually extends PathProperty {
	public StateProperty p1;
	public PropEventually(StateProperty p1) {
		this.p1 = p1;
	}
	@Override
	public Object accept(PropertyVisitor visitor) {
		return visitor.visit(this);
	}
}
