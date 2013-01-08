package solver;

import java.util.HashMap;

import org.lsmp.djep.sjep.PolynomialCreator;
import org.lsmp.djep.xjep.XJep;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.SymbolTable;

import mat.MatrixIndex;
import mat.SmartMatrix;
import mat.SparseMatrix;
import core.SimpleDTMC;

public class SimpleReachabilitySolver {
	SimpleDTMC model;
	private SmartMatrix M=null;
	HashMap <MatrixIndex, String> results = new HashMap <MatrixIndex, String>();
	XJep jep = new XJep();
	PolynomialCreator simplifier = new PolynomialCreator(jep);
	
	public SimpleReachabilitySolver(SimpleDTMC model) {
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
	
	public String solve(int from, int to) {
		MatrixIndex i = new MatrixIndex(from,to);
		String r = results.get(i);
		if (r!=null) return r;
		r = solveT2A(from, to);
		results.put(i, r);
		return r;
	}
	
	public String solveT2A(int from, int to) {
		assert(from<model.numTransients);
		assert(to>=model.numTransients);

		SmartMatrix sm= this.getM();
		String deta=sm.determinant();

		String num="0";
		for(int i=0;i<model.numTransients;i++){	
			if(model.trans.getNumericEntry(i, to)!=0){
				char coef=(from+i)%2==0?'+':'-';
				SmartMatrix mino=sm.minor(i, from);
				num=num+coef+"("+mino.determinant()+")*"+model.trans.getEntry(i, to, true);
			}
		}	
		String both="("+num+")/("+deta+")";
	
		String simpl="-1";
		try {
			simpl = jep.toString(simplifier.simplify(jep.parse(both)));
		} catch (ParseException e) {
			System.out.println(both);
			e.printStackTrace();
		}
		return simpl; 
	}
		
}
