package org.net9.simplex.ppmc.prop;

public abstract class StateProp {
	public boolean isNested = false;
	public void print(){
		System.out.println(this.getClass().toString());
	}
	public void accept(StatePropVisitor visitor){
		visitor.visit(this);
	}
}
