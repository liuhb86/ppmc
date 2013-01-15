package org.net9.simplex.ppmc.test;

import java.text.ParseException;
import java.util.Scanner;

import org.net9.simplex.ppmc.prop.StateProperty;
import org.net9.simplex.ppmc.prop.parser.PropertyParser;


public class PropParserTest {

	/**
	 * @param args
	 * @throws ParseException 
	 */
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		PropertyParser parser= new PropertyParser();
		while(true) {
			String line = s.nextLine();
			if (s.equals("q")) break;
			try {
				StateProperty p = parser.parse(line);
				p.print();
			} catch (ParseException e) {
				System.out.println(e.toString());
			}
		}
	}

}
