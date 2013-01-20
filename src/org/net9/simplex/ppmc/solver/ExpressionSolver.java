package org.net9.simplex.ppmc.solver;

import java.util.BitSet;
import org.net9.simplex.ppmc.core.Assignment;
import org.net9.simplex.ppmc.util.BinaryPredicator;
import org.nfunk.jep.ASTConstant;
import org.nfunk.jep.Node;

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
}
