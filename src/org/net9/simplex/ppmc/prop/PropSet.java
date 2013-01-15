package org.net9.simplex.ppmc.prop;

import java.util.HashSet;

public class PropSet extends PathProp {
	public HashSet<Integer> item;
	public PropSet(){
		
	}
	public void print(){
		System.out.println("STATE {");
		for (int i :item){
			System.out.print(i);
			System.out.print(" ,");
		}
		System.out.println("}");
	}
}
