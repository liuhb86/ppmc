package org.net9.simplex.ppmc.test;

import java.util.Scanner;

import org.net9.simplex.ppmc.prop.StateProp;
import org.net9.simplex.ppmc.prop.parser.ParseException;
import org.net9.simplex.ppmc.prop.parser.PropParser;


public class PropParserTest {

	/**
	 * @param args
	 * @throws ParseException 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner s = new Scanner(System.in);
		while(true) {
			String line = s.nextLine();
			if (s.equals("q")) break;
			try {
				StateProp p = PropParser.parse(line);
				p.print();
			} catch (ParseException e) {
				System.out.println(e.toString());
			}
		}
	}

}
