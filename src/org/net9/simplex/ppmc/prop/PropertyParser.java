package org.net9.simplex.ppmc.prop;

import java.text.ParseException;

import org.net9.simplex.ppmc.prop.javacc.Parser;

public class PropertyParser {
	Parser parser = new Parser();
	public StateProperty parse(String s) throws ParseException
	{
		return parser.parse(s);
	}
}
