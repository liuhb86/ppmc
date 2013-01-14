package org.net9.simplex.ppmc.checker;

import java.util.HashMap;

import org.lsmp.djep.sjep.PolynomialCreator;
import org.lsmp.djep.xjep.XJep;
import org.net9.simplex.ppmc.core.SimpleDTMC;
import org.net9.simplex.ppmc.mat.MatrixIndex;
import org.net9.simplex.ppmc.mat.SmartMatrix;
import org.net9.simplex.ppmc.mat.SparseMatrix;
import org.nfunk.jep.ParseException;

public class SimpleReachabilityChecker {
	SimpleDTMC model;
	SmartMatrix M=null;
	HashMap <MatrixIndex, String> cofactor = new HashMap <MatrixIndex, String>();
	XJep jep = new XJep();
	PolynomialCreator simplifier = new PolynomialCreator(jep);
	
	public SimpleReachabilityChecker(SimpleDTMC model) {
		this.model = model;
		jep.setAllowUndeclared(true);
	}
	
	public SmartMatrix getM(){
		if(this.M==null){
			int dim = model.numTransients;
			SmartMatrix trans = model.trans;
			double smd[][]=new double[dim][dim];
			SparseMatrix<String> vars = trans.getVars().clone();
			for(int i=0;i<dim;i++){
				for(int j=0;j<dim;j++){
					if (trans.isSymbolicEntry(i, j)) {
						vars.put(i, j, ((i==j)? "1":"") + "-(" +trans.getEntry(i, j, true)+")");
						smd[i][j] = -1;
					} else {
						smd[i][j]=((i==j)?1:0)-trans.getNumericEntry(i, j);
					}
				}
			}
			this.M=new SmartMatrix(smd,vars);
		}
		return this.M;
	}
	
	public String check(int from, int to) {
		if (model.isAbsorbingState(from))
			return this.checkA2S(from, to);
		if (model.isAbsorbingState(to)) 
			return this.checkT2A(from, to);
		return this.checkT2T(from, to);
	}
	
	String checkA2S(int from, int to) {
		//assert(from>=model.numTransients);
		return (from==to)?"1":"0";
	}
	
	/**
	 * Check the reachability from transient state 'from' to transient state 'to'
	 * @param from
	 * @param to
	 * @return
	 */
	String checkT2T(int from, int to) {
		//assert(from<model.numTransients);
		//assert(to<model.numTransients);
		String aji = this.getCofactor(to, from);
		String ajj = this.getCofactor(to, to);
		return this.simplify("("+aji+")/("+ajj+")");
	}
	
	/**
	 * Check the reachability from transient state 'from' to absorbing state 'to'
	 * @param from
	 * @param to
	 * @return
	 */
	public String checkT2A(int from, int to) {
		//assert(from<model.numTransients);
		//assert(to>=model.numTransients);

		SmartMatrix sm= this.getM();
		String deta=sm.determinant();

		StringBuffer num = new StringBuffer("0");
		for(int i=0;i<model.numTransients;i++){	
			if(model.trans.getNumericEntry(i, to)!=0){
				String n = this.getCofactor(i, from);
				if (!n.equals("0")) {
					char coef=(from+i)%2==0?'+':'-';
					num.append(coef).append('(').append(n).append(")*")
						.append(model.trans.getEntry(i, to, true));
				}
			}
		}	
		String both="("+num.toString()+")/("+deta+")";
	
		String simpl= simplify(both);
		return simpl; 
	}
	
	String getCofactor(int i, int j) {
		MatrixIndex index = new MatrixIndex(i,j);
		String c = cofactor.get(index);
		if (c==null) {
			SmartMatrix mino=this.getM().minor(i, j);
			c = mino.determinant();
			cofactor.put(index, c);
		}
		return c;
	}
	
	String simplify(String expr){
		try {
			return jep.toString(simplifier.simplify(jep.parse(expr)));
		} catch (ParseException e) {
			System.out.println(expr);
			e.printStackTrace();
			return "-1";
		}
	}
		
}
