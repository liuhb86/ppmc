package org.net9.simplex.ppmc.prop;

public class PropNot extends StateProp {
	public StateProp p1;
	public PropNot (StateProp p1) {
		this.p1 = p1;
	}
	public void print(){
		System.out.println("NOT{");
		p1.print();
		System.out.println("}");
	}
}
