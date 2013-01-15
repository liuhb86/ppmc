package org.net9.simplex.ppmc.prop;

import java.util.LinkedList;

public class PropAnd extends StateProperty {
	public LinkedList<StateProperty> item =  new LinkedList<StateProperty>();
	public PropAnd (StateProperty p1, StateProperty p2) { 
		item.add(p1);
		item.add(p2);
	}
	public void print(){
		System.out.println("AND{");
		for (StateProperty p :item){
			p.print();
			System.out.println(",");
		}
		System.out.println("}");
	}
}
