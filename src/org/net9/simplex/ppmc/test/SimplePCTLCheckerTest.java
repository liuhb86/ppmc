package org.net9.simplex.ppmc.test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Scanner;

import org.net9.simplex.ppmc.checker.SimplePCTLChecker;
import org.net9.simplex.ppmc.core.SimpleDTMC;
import org.net9.simplex.ppmc.prop.StateProperty;
import org.net9.simplex.ppmc.prop.parser.PropertyParser;
import org.net9.simplex.ppmc.solver.Solver;
import org.net9.simplex.ppmc.util.Stdio;

public class SimplePCTLCheckerTest {

	public static void main(String[] args) {
		FileReader fr;
		SimpleDTMC model;
		try {
			fr = new FileReader("sample/model.txt");
		} catch (FileNotFoundException e) {
			return;
		}
		try {
			 model = SimpleDTMC.loadFrom(fr);
		} catch (ParseException e) {
			Stdio.out.println("parse error: "+e.getMessage());
			return;
		} finally {
			try {
				fr.close();
			} catch (IOException e) {}
		}
		SimplePCTLChecker checker = new SimplePCTLChecker(model);
		Scanner s = new Scanner(System.in);
		PropertyParser parser= new PropertyParser();
		StateProperty p;
		while(true) {
			String line = s.nextLine();
			if (s.equals("q")) break;
			try {
				 p = parser.parse(line);
				p.print();
			} catch (ParseException e) {
				System.out.println(e.toString());
				continue;
			}
			Solver solver = checker.check(p);
			Stdio.out.println(solver.solve(null));
		}
	}

}