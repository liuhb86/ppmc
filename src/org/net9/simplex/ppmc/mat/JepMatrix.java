package org.net9.simplex.ppmc.mat;

import org.lsmp.djep.matrixJep.*;
import org.lsmp.djep.matrixJep.nodeTypes.MatrixNodeI;
import org.lsmp.djep.sjep.PolynomialCreator;
import org.lsmp.djep.vectorJep.Dimensions;
import org.nfunk.jep.*;


public class JepMatrix {
	
	public static MatrixJep jep = new MatrixJep();
	
	public static final ASTConstant zero = (ASTConstant) jep.getNumberFactory().getZero();
	public static final MatrixNodeFactory factory = (MatrixNodeFactory) jep.getNodeFactory();
	public static final MatrixOperatorSet op = (MatrixOperatorSet) jep.getOperatorSet();
	public static final PolynomialCreator simplifier = new PolynomialCreator(jep);
	
	public static MatrixNodeI power(MatrixNodeI m, int exp){
		assert(exp>0);
		if (exp==1) return m;
		int half = exp/2;
		MatrixNodeI mh = power(m,half);
		MatrixNodeI result = multiply(mh, mh);
		if (exp%2==1) {
			result = multiply(result, m);
		}
		return result;
	}
	
	public static MatrixNodeI multiply(MatrixNodeI m1, MatrixNodeI m2) {
		assert(m1.getDim().is2D());
		assert(m2.getDim().is2D());
		assert(m1.getDim().getLastDim()==m2.getDim().getFirstDim());
		int m = m1.getDim().getFirstDim();
		int n = m1.getDim().getLastDim();
		int p = m2.getDim().getLastDim();
		Node[] entry = new Node[m*p];
		int index = 0;
		try {
			for (int i=0;i<m;++i){
				for (int j=0;j<p;++j){
					Node node = zero;
	
					for (int k=0;k<n;++k){
							Node item = factory.buildOperatorNode(
									op.getMultiply(),
									get(m1,i,k),
									get(m2,k,j));
							node = factory.buildOperatorNode(
									op.getAdd(),
									node,
									item);
					}
					entry[index]=simplifier.simplify(node);
					++index;
				}
			}
		} catch (ParseException e){}
		return (MatrixNodeI) factory.buildOperatorNode(
				op.getMList(),
				entry,
				Dimensions.valueOf(m, p));
		
	}
	static Node get(MatrixNodeI m, int row, int col){
		return m.jjtGetChild(row*m.getDim().getLastDim()+col);
	}
}
