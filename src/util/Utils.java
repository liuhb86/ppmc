package util;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

public class Utils {
	public static PrintWriter getWriter(Object o) {
		if (o instanceof PrintWriter) return (PrintWriter) o;
		if (o instanceof PrintStream) return new PrintWriter((OutputStream)o);
		return null;
	}
	
	public static int hashCombine(int h, int h1) {
		//Ref: hash_combine in boost library
		return h ^ (h1 + 0x9e3779b9 + (h << 6) + (h >> 2));
	}
	
	public static int hashPair(int h1, int h2) {
		//Ref: hash/pair.hpp in boost library 
		return hashCombine(hashCombine(0,h1),h2);
	}
}
