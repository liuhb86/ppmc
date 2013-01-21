package org.net9.simplex.ppmc.test;

import org.lsmp.djep.matrixJep.MatrixJep;
import org.lsmp.djep.matrixJep.MatrixNodeFactory;
import org.lsmp.djep.matrixJep.MatrixOperatorSet;
import org.lsmp.djep.matrixJep.nodeTypes.MatrixNodeI;
import org.lsmp.djep.vectorJep.values.Matrix;
import org.lsmp.djep.vectorJep.values.MatrixValueI;
import org.net9.simplex.ppmc.util.Stdio;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

public class ScatchPad {

	/**
	 * @param args
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws ParseException {
		MatrixJep j = new MatrixJep();
		j.setAllowUndeclared(true);
		Node d = j.parse("[[1+2,x],[3,4],[4,5]]*[[1,2],[3,y]]");
		d = j.preprocess(d);
		d = j.simplify(d);
		j.println(d);
		d = (MatrixNodeI) d.jjtGetChild(1);
		Node[] n = {d.jjtGetChild(0), d.jjtGetChild(1),
				d.jjtGetChild(2), d.jjtGetChild(3)};
		MatrixNodeI e = (MatrixNodeI) d;
		MatrixNodeFactory mnf = (MatrixNodeFactory) j.getNodeFactory();
		MatrixOperatorSet mos = (MatrixOperatorSet) j.getOperatorSet();
		Node t =mnf.buildOperatorNode(mos.getMList(),n, e.getDim());
		j.println(t);
	}
}
