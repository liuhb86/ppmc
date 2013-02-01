package org.net9.simplex.ppmc.solver;

import java.io.PrintWriter;
import java.util.BitSet;

import org.lsmp.djep.xjep.NodeFactory;
import org.lsmp.djep.xjep.XJep;
import org.net9.simplex.ppmc.core.Assignment;
import org.net9.simplex.ppmc.util.BinaryPredicator;
import org.nfunk.jep.ASTConstant;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

public class ExpressionSolver extends Solver implements NumericSolver{
	Node expr = null;
	Node[] exprArray = null;
	boolean isSingle;
	boolean isConst;
	BinaryPredicator<Double,Double> pred;
	String strPred;
	double value;
	static XJep jep = new XJep();
	
	public ExpressionSolver(Node expr){
		this.expr = expr;
		this.isSingle = true;
		this.isConst = expr instanceof ASTConstant;
	}
	
	public ExpressionSolver(Node[] expr){
		this.exprArray = expr;
		this.isSingle = false;
		this.isConst = true;
		for(Node n: expr){
			if (n instanceof ASTConstant) continue;
			this.isConst = false;
			break;
		}
	}

	public double solveExprNumeric(Assignment val, Node expr) {
		return Assignment.evaluate(val, expr);	
	}
	
	boolean solveExpr(Assignment val, Node expr){
		return pred.execute(this.solveExprNumeric(val, expr), value);
	}
	
	@Override
	public boolean solve(Assignment val) {
		return this.solveExpr(val, this.expr);
	}

	@Override
	public BitSet solveSet(Assignment val) {
		int len = exprArray.length;
		BitSet bs = new BitSet(len);
		for (int i=0;i<len;++i){
			bs.set(i, this.solveExpr(val, exprArray[i]));
		}
		return bs;
	}
	
	@Override	
	public boolean isConstant(){
		return this.isConst;
	}

	@Override
	public double solveNumeric(Assignment val) {
;		return this.solveExprNumeric(val, this.expr);
	}

	@Override
	public double solveNumeric(Assignment val, int index) {
		return this.solveExprNumeric(val, this.exprArray[index]);
	}

	@Override
	public void setConstraints(String strPred, BinaryPredicator<Double, Double> pred,
			double value) {
		this.strPred = strPred;
		this.pred = pred;
		this.value = value;
	}

	@Override
	public boolean isSingle() {
		return this.isSingle;
	}

	@Override
	public void complement() {
		if (isSingle) {
			this.expr = this.complementExpr(this.expr);
		} else {
			for(int i=0;i<exprArray.length; ++i){
				exprArray[i]=this.complementExpr(exprArray[i]);
			}
		}
	}
	public Node complementExpr(Node expr) {
		if (expr instanceof ASTConstant){
			((ASTConstant) expr).setValue(1-Assignment.evaluateConst(expr));
		} else {
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
		return expr;
	}

	@Override
	public void writeTo(PrintWriter writer) {
		if (this.isSingle){
			writer.println("Expression: ");
			writer.println(jep.toString(this.expr));
			writer.print(this.strPred);
			writer.println(this.value);
		}
	}
}
