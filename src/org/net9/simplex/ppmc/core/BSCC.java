package org.net9.simplex.ppmc.core;

import java.util.HashSet;

public class BSCC {
	int index;
	HashSet <Integer> states = new HashSet<Integer>();
	HashSet <BSCC> next = new HashSet<BSCC>();
	boolean isErgoric() {
		return next.size()==0;
	}
}
