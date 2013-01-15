package org.net9.simplex.ppmc.prop;

import java.util.LinkedList;

public class PropOr extends StateProp {
	public LinkedList<StateProp> item =  new LinkedList<StateProp>();

	public PropOr (StateProp p1, StateProp p2) {
		item.add(p1);
		item.add(p2);
	}
	public void print(){
		System.out.println("OR{");
		for (StateProp p :item){
			p.print();
			System.out.println(",");
		}
		System.out.println("}");
	}
}
