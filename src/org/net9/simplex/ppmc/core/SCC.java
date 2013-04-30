package org.net9.simplex.ppmc.core;

import java.util.HashSet;

public class SCC {
	public int index;
	public HashSet <Integer> states = new HashSet<Integer>();
	public HashSet <SCC> next = new HashSet<SCC>();
	public boolean isErgoric() {
		return next.size()==0;
	}
}
