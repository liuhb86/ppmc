package org.net9.simplex.ppmc.core;

import org.nfunk.jep.ASTVarNode;
import org.nfunk.jep.EvaluatorVisitor;
import org.nfunk.jep.ParseException;

@SuppressWarnings("unchecked")
public class OldEvaluatorVisitor extends EvaluatorVisitor {
	
	/**
	 * Visit a variable node. The value of the variable is obtained from the
	 * symbol table (symTab) and pushed onto the stack.
	 */
	@Override
	public Object visit(ASTVarNode node, Object data) throws ParseException {

		// old code
		if (symTab == null) {
			String message = "Could not evaluate " + node.getName() + ": ";
			throw new ParseException(message += "the symbol table is null");
			
		}

		// Optimise (table lookup is costly?)
		Object temp = symTab.getValue(node.getName());

		/*
		// new code

		// try to get the variable object
		Variable var = node.getVar();
		if (var == null) {
			String message = "Could not evaluate " + node.getName() + ": ";
			throw new ParseException(message + "the variable was not found in the symbol table");
		}

		// get the variable value
		Object temp = var.getValue();
		*/
		
		if (trapNullValues && temp == null) {
			String message = "Could not evaluate " + node.getName() + ": ";
			throw new ParseException(message + "variable not set");
		}
		// all is fine
		// push the value on the stack
		stack.push(temp);
		return data;
	}
}
