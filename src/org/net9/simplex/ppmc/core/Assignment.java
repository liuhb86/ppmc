package org.net9.simplex.ppmc.core;

import org.lsmp.djep.xjep.XJep;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

public class Assignment {
	//TODO: implement
	public static  final Assignment nullAssignment = new Assignment();
	XJep jep;
	
	public Assignment() {
		jep = new XJep();
	}
	
	public static double evaluateConst(Node expr)throws IllegalArgumentException{
		return nullAssignment.evaluate(expr);
	}
	
	public double evaluate(Node expr) throws IllegalArgumentException{
		return evaluate(jep, expr);
	}
	
	public static double evaluate(Assignment assignment, Node expr) throws IllegalArgumentException{
		if (assignment==null) return evaluateConst(expr);
		return assignment.evaluate(expr);
	}
	
	static double evaluate(XJep jep, Node expr) throws IllegalArgumentException{
		Object result;
		try {
			result = jep.evaluate(expr);
			if (result instanceof Double) return (Double) result;
			else throw new IllegalArgumentException("Wrong type: "+result.toString());
		} catch (ParseException e) {
			throw new IllegalArgumentException(
					"Insuffient assignment for "+jep.toString(expr)+
					" : " + e.getErrorInfo());
		}
	}
	
	public void addVariable(String var, double value){
		jep.addVariable(var, value);
	}
	public void removeVariable(String var, double value){
		jep.removeVariable(var);
	}
}
