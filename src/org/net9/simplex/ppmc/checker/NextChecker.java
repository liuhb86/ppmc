package org.net9.simplex.ppmc.checker;

import java.util.BitSet;

import org.lsmp.djep.xjep.XJep;
import org.net9.simplex.ppmc.mat.SmartMatrix;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

public class NextChecker {
	SmartMatrix trans;
	XJep jep = new XJep();
	public NextChecker (SmartMatrix trans) {
		this.trans = trans;
		jep.setAllowUndeclared(true);
	}
	
	public Node check (int from, BitSet to) {
		StringBuffer sb = new StringBuffer();
		for(int i=to.nextSetBit(0);i>=0;i=to.nextSetBit(i+1)){
			sb.append('+');
			sb.append(trans.getEntry(from, i, true));
		}
		try {
			return jep.simplify(jep.parse(sb.toString()));
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
}
