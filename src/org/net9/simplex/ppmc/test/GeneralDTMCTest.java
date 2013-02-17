package org.net9.simplex.ppmc.test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.ParseException;

import org.net9.simplex.ppmc.core.BSCC;
import org.net9.simplex.ppmc.core.GeneralDTMC;
import org.net9.simplex.ppmc.core.SimpleDTMC;
import org.net9.simplex.ppmc.util.Stdio;

public class GeneralDTMCTest {
	public static void main(String[] args) {
		FileReader fr;
		GeneralDTMC model;
		try {
			fr = new FileReader("sample/model_general.txt");
		} catch (FileNotFoundException e) {
			return;
		}
		try {
			 model = GeneralDTMC.loadFrom(fr);
		} catch (ParseException e) {
			Stdio.out.println("parse error: "+e.getMessage());
			return;
		}
		for(BSCC bscc:model.bsccSet){
			Stdio.out.println(bscc.states);
		}
	}
}
