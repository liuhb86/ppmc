package org.net9.simplex.ppmc.prop;


public class PropNext extends PathProperty {

	public StateProperty p1;
	
	public PropNext(StateProperty p1) {
		this.p1 = p1;
	}

	@Override
	public void accept(PropertyVisitor visitor) {
		visitor.visit(this);
	}

}
