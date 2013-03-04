package org.net9.simplex.ppmc.prop;

public interface PropertyVisitor {
	Object visit(PropTrue p);
	Object visit(PropAtom p);
	Object visit(PropNot p);
	Object visit(PropAnd p);
	Object visit(PropOr p);
	Object visit(PropSet p);
	Object visit(PropProb p);
	Object visit(PropEventually p);
	Object visit(PropAlways p);
	Object visit(PropNext p);
	Object visit(PropUntil p);
	Object visit(PropBoundedUntil p);
	Object visit(PropLTLPathWrapper p);
	Object visit(PropLTL p);
}
