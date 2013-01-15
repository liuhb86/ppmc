package org.net9.simplex.ppmc.prop.parser;

import java.text.ParseException;

import org.net9.simplex.ppmc.prop.StateProperty;
import org.net9.simplex.ppmc.prop.parser.javacc.Parser;

public class PropertyParser {
	Parser parser = new Parser();
	public StateProperty parse(String s) throws ParseException
	{
		return parser.parse(s);
	}
}
