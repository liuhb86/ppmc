package org.net9.simplex.ppmc.test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.ParseException;

import org.net9.simplex.ppmc.core.SimpleDTMC;
import org.net9.simplex.ppmc.util.Stdio;

public class ModelSerial {
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
		}
		model.writeTo(Stdio.out);
	}
}
