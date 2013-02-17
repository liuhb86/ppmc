package org.net9.simplex.ppmc.core;

import java.util.HashSet;

public class BSCC {
	public int index;
	public HashSet <Integer> states = new HashSet<Integer>();
	public HashSet <BSCC> next = new HashSet<BSCC>();
	public boolean isErgoric() {
		return next.size()==0;
	}
}
