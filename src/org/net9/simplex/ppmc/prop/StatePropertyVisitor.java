package org.net9.simplex.ppmc.prop;

public interface StatePropertyVisitor {
	void visit(StateProperty p);
	void visit(PropTrue p);
	void visit(PropAtom p);
	void visit(PropNot p);
	void visit(PropAnd p);
	void visit(PropOr p);
}
