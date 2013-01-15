package org.net9.simplex.ppmc.prop;

import java.util.LinkedList;

public class PropOr extends StateProperty {
	public LinkedList<StateProperty> item =  new LinkedList<StateProperty>();

	public PropOr (StateProperty p1, StateProperty p2) {
		item.add(p1);
		item.add(p2);
	}
	public void print(){
		System.out.println("OR{");
		for (StateProperty p :item){
			p.print();
			System.out.println(",");
		}
		System.out.println("}");
	}
	@Override
	public void accept(StatePropertyVisitor visitor) {
		visitor.visit(this);
	}
}
