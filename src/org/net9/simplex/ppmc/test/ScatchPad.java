package org.net9.simplex.ppmc.test;

import org.lsmp.djep.matrixJep.MatrixJep;
import org.lsmp.djep.matrixJep.MatrixNodeFactory;
import org.lsmp.djep.matrixJep.MatrixOperatorSet;
import org.lsmp.djep.matrixJep.nodeTypes.MatrixNodeI;
import org.lsmp.djep.vectorJep.values.Matrix;
import org.lsmp.djep.vectorJep.values.MatrixValueI;
import org.net9.simplex.ppmc.checker.LTLChecker;
import org.net9.simplex.ppmc.prop.PathProperty;
import org.net9.simplex.ppmc.prop.PropLTL;
import org.net9.simplex.ppmc.prop.PropProb;
import org.net9.simplex.ppmc.prop.PropertyParser;
import org.net9.simplex.ppmc.prop.StateProperty;
import org.net9.simplex.ppmc.util.Stdio;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

public class ScatchPad {

	/**
	 * @param args
	 * @throws ParseException 
	 * @throws java.text.ParseException 
	 */
	public static void main(String[] args) throws ParseException, java.text.ParseException {
		PropertyParser parser = new PropertyParser();
		StateProperty prop1 = parser.parse("P>0 (!(a U b))");
		prop1.print();
		PropProb  prop = (PropProb) prop1;
		LTLChecker checker = new LTLChecker(null, null);
		checker.check((PropLTL) prop.p1);
	}
}
