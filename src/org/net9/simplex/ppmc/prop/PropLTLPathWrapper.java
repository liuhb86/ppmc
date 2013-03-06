package org.net9.simplex.ppmc.prop;

public class PropLTLPathWrapper extends StateProperty {
	public PathProperty p1;
	
	public PropLTLPathWrapper(PathProperty p){
		this.p1 = p;
	}
	
	@Override
	public Object accept(PropertyVisitor visitor) {
		return visitor.visit(this);
	}

}
