package org.net9.simplex.ppmc.prop;

public class PropTrue extends StateProperty {
	public void print(){
		System.out.println("True");
	}
	@Override
	public void accept(StatePropertyVisitor visitor) {
		visitor.visit(this);
	}
}
