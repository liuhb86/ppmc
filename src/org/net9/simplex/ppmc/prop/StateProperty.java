package org.net9.simplex.ppmc.prop;

public abstract class StateProperty {
	public boolean isNested = false;
	public void print(){
		System.out.println(this.getClass().toString());
	}
	public void accept(StatePropertyVisitor visitor){
		visitor.visit(this);
	}
}
