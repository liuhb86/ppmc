package org.net9.simplex.ppmc.prop;

public class PropAtom extends StateProperty {
	public String atom;
	public PropAtom (String atom) {this.atom = atom; }
	public void print(){
		System.out.println("Atom: "+atom);
	}
	@Override
	public void accept(PropertyVisitor visitor) {
		visitor.visit(this);
	}
}
