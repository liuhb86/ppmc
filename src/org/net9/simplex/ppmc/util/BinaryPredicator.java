package org.net9.simplex.ppmc.util;

public interface BinaryPredicator<L,R> {
	boolean execute(L arg1, R arg2);
}
