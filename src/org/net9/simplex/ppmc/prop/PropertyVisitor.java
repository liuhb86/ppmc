package org.net9.simplex.ppmc.prop;

public interface PropertyVisitor {
	void visit(PropTrue p);
	void visit(PropAtom p);
	void visit(PropNot p);
	void visit(PropAnd p);
	void visit(PropOr p);
	void visit(PropSet p);
	void visit(PropProb p);
	void visit(PropEventually p);
	void visit(PropAlways propAlways);
	void visit(PropNext propNext);
	void visit(PropUntil propUntil);
	void visit(PropBoundedUntil propBoundedUntil);
}
