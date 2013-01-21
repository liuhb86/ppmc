package org.net9.simplex.ppmc.prop;

public class PropUntil extends PathProperty {
	public StateProperty p1,p2;
	public PropUntil(StateProperty p1, StateProperty p2) {
		this.p1 = p1;
		this.p2 = p2;
	}

	@Override
	public void accept(PropertyVisitor visitor) {
		visitor.visit(this);
	}

}
