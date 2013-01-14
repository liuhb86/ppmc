package org.net9.simplex.ppmc.prop;

public interface StatePropVisitor {
	void visit(StateProp p);
	void visit(PropTrue p);
	void visit(PropAtom p);
	void visit(PropNot p);
	void visit(PropAnd p);
	void visit(PropOr p);
}
