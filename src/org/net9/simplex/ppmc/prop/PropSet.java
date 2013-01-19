package org.net9.simplex.ppmc.prop;

import java.util.HashSet;

public class PropSet extends StateProperty {
	public HashSet<Integer> item = new HashSet<Integer>();
	
	public void print(){
		System.out.print("STATE {");
		for (int i :item){
			System.out.print(i);
			System.out.print(" ,");
		}
		System.out.println("}");
	}

	@Override
	public void accept(StatePropertyVisitor visitor) {
		visitor.visit(this);
	}
}
