package org.net9.simplex.ppmc.prop;

import java.util.LinkedList;

public class PropAnd extends StateProp {
	public LinkedList<StateProp> item =  new LinkedList<StateProp>();
	public PropAnd (StateProp p1, StateProp p2) { 
		item.add(p1);
		item.add(p2);
	}
	public void print(){
		System.out.println("AND{");
		for (StateProp p :item){
			p.print();
			System.out.println(",");
		}
		System.out.println("}");
	}
}
