package org.net9.simplex.ppmc.prop;

public abstract class PathProperty {
	public StateProperty parent;
	public void print(){
		System.out.println(this.getClass().toString());
	}
	public abstract Object accept(PropertyVisitor visitor);
}
