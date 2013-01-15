package org.net9.simplex.ppmc.prop;

public class PropNot extends StateProperty {
	public StateProperty p1;
	public PropNot (StateProperty p1) {
		this.p1 = p1;
	}
	public void print(){
		System.out.println("NOT{");
		p1.print();
		System.out.println("}");
	}
}
