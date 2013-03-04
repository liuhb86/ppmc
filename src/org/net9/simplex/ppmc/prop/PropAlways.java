package org.net9.simplex.ppmc.prop;

public class PropAlways extends PathProperty {
	public StateProperty p1;
	public PropAlways(StateProperty p1) {
		this.p1 = p1;
	}
	@Override
	public Object accept(PropertyVisitor visitor) {
		return visitor.visit(this);
	}

}
