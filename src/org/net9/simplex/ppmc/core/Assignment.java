package org.net9.simplex.ppmc.core;

import java.util.HashMap;
import org.lsmp.djep.xjep.XJep;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

public class Assignment {
	public static  final Assignment nullAssignment = new Assignment();
	JEP jep;
	HashMap<String,Double> map = new HashMap<String, Double>();
	
	public Assignment() {
		jep = new JEP();
	}
	
	public static double evaluateConst(Node expr)throws IllegalArgumentException{
		return nullAssignment.evaluate(expr);
	}
	public static double evaluateConst(String expr)throws IllegalArgumentException, ParseException{
		return nullAssignment.evaluate(expr);
	}
	
	public double evaluate(Node expr) throws IllegalArgumentException{
		return evaluate(jep, expr);
	}
	
	public double evaluate(String expr) throws IllegalArgumentException, ParseException{
		return evaluate(jep, jep.parse(expr));
	}
	
	public static double evaluate(Assignment assignment, String expr) throws IllegalArgumentException, ParseException{
		if (assignment==null) return evaluateConst(expr);
		return assignment.evaluate(expr);
	}
	
	public static double evaluate(Assignment assignment, Node expr) throws IllegalArgumentException{
		if (assignment==null) return evaluateConst(expr);
		return assignment.evaluate(expr);
	}
	
	static double evaluate(JEP jep, Node expr) throws IllegalArgumentException{
		Object result;
		try {
			OldEvaluatorVisitor ev = new OldEvaluatorVisitor();
			result = ev.getValue(expr, jep.getSymbolTable());
			if (result instanceof Double) return (Double) result;
			else throw new IllegalArgumentException("Wrong type: "+result.toString());
		} catch (ParseException e) {
			XJep xjep = new XJep();
			throw new IllegalArgumentException(
					"Insuffient assignment for "+xjep.toString(expr)+
					" : " + e.getErrorInfo());
		}
	}
	
	public void addVariable(String var, double value){
		jep.addVariable(var, value);
		map.put(var, value);
	}
	public void removeVariable(String var, double value){
		jep.removeVariable(var);
		map.remove(var);
	}
	public HashMap<String, Double> getVariableTable(){
		return map;
	}
}
