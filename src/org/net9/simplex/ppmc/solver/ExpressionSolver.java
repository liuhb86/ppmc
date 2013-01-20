package org.net9.simplex.ppmc.solver;

import java.util.BitSet;

import org.lsmp.djep.xjep.NodeFactory;
import org.lsmp.djep.xjep.XJep;
import org.net9.simplex.ppmc.core.Assignment;
import org.net9.simplex.ppmc.util.BinaryPredicator;
import org.nfunk.jep.ASTConstant;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

public class ExpressionSolver extends Solver implements NumericSolver{
	Node expr;
	BinaryPredicator<Double,Double> pred;
	double value;
	
	public ExpressionSolver(Node expr){
		this.expr = expr;
	}
	
	@Override
	public boolean solve(Assignment val) {
		return pred.execute(this.solveNumeric(val), value);
	}

	@Override
	public BitSet solveSet(Assignment val) {
		throw new UnsupportedOperationException();
	}
	
	@Override	
	public boolean isConstant(){
		return expr instanceof ASTConstant;
	}

	@Override
	public double solveNumeric(Assignment val) {
		return Assignment.evaluate(val, expr);	
	}

	@Override
	public double solveNumeric(Assignment val, int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setConstraints(BinaryPredicator<Double, Double> pred,
			double value) {
		this.pred = pred;
		this.value = value;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public void complement() {
		if (expr instanceof ASTConstant){
			((ASTConstant) expr).setValue(1-Assignment.evaluateConst(expr));
		} else {
			XJep jep = new XJep();
			NodeFactory nf = jep.getNodeFactory();
			try {
				expr = nf.buildOperatorNode(
						jep.getOperatorSet().getSubtract(),
						nf.buildConstantNode(1),
						expr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
}
