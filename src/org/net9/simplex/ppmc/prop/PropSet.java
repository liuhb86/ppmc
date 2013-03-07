package org.net9.simplex.ppmc.prop;

import java.util.BitSet;

public class PropSet extends StateProperty {
	public BitSet item = new BitSet();
	
	public void print(){
		System.out.print("STATE {");
		for (int i=item.nextSetBit(0);i>=0;i=item.nextSetBit(i+1)){
			System.out.print(i);
			System.out.print(" ,");
		}
		System.out.println("}");
	}

	@Override
	public Object accept(PropertyVisitor visitor) {
		return visitor.visit(this);
	}
}
