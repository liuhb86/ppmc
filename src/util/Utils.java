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
}
