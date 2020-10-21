package com.bingo.common.utils;

import java.util.ArrayList;
import java.util.Arrays;

import com.bingo.analyze.ExtractObject;

public class Kep {
	protected static ArrayList<String> layDeKepBang() {
		String[] kepBang = { "00", "11", "22", "33", "44", "55", "66", "77", "88", "99" };
		return new ArrayList<String>(Arrays.asList(kepBang));
	}
	
	protected static ArrayList<String> layDeKepLech() {
		String[] kepBang = { "05", "50", "16", "61", "27", "72", "38", "83", "49", "94" };
		return new ArrayList<String>(Arrays.asList(kepBang));
	}

	protected static ArrayList<String> layDeSatKep() {
		String[] satKep = { /*"09", "90", */"01", "10", "12", "21", "23", "32", "34", "43", "45", "54", "56", "65", "67",
				"76", "78", "87", "89", "98" };
		return new ArrayList<String>(Arrays.asList(satKep));
	}
	
	public static ExtractObject getKepBang(String syntax) {
		ExtractObject extractObject = null;
		boolean isSyntax = syntax.matches(".*(kep|kep\\s*bang)\\s*x[0-9]+\\s*(k|d)");
		if (isSyntax && syntax.replaceAll("(kep\\W*lech|sat\\W*kep)", "").contains("kep")) {
			extractObject = Utils.extractUnitPrice(syntax);
			extractObject.Numbers = layDeKepBang();
			boolean isDe = syntax.matches(".*de.*x[0-9]+\\s*k|.*de.*x[0-9]+\\s*d");
			if(isDe){
				extractObject.Type = "de";
			}else{
				boolean isLo = syntax.matches(".*lo.*x[0-9]+\\s*k|.*lo.*x[0-9]+\\s*d");
				if(isLo){
					extractObject.Type = "lo";
				}else{
					extractObject.Type = "Unknow";
				}
			}
			
		}
		
		return extractObject;
	}
	
	public static ExtractObject getKepLech(String syntax) {
		ExtractObject extractObject = null;
		boolean isSyntax = syntax.matches(".*kep\\s*lech\\s*x[0-9]+\\s*(k|d)");
		if (isSyntax) {
			extractObject = Utils.extractUnitPrice(syntax);
			extractObject.Numbers = layDeKepLech();
			boolean isDe = syntax.matches(".*de.*x[0-9]+\\s*k|.*de.*x[0-9]+\\s*d");
			if(isDe){
				extractObject.Type = "de";
			}else{
				boolean isLo = syntax.matches(".*lo.*x[0-9]+\\s*k|.*lo.*x[0-9]+\\s*d");
				if(isLo){
					extractObject.Type = "lo";
				}else{
					extractObject.Type = "Unknow";
				}
			}
			
		}
		
		return extractObject;
	}
	
	public static ExtractObject getSatKep(String syntax) {
		ExtractObject extractObject = null;
		boolean isSyntax = syntax.matches(".*sat\\s*kep\\s*x[0-9]+\\s*(k|d)");
		if (isSyntax) {
			extractObject = Utils.extractUnitPrice(syntax);
			extractObject.Numbers = layDeSatKep();
			boolean isDe = syntax.matches(".*de.*x[0-9]+\\s*k|.*de.*x[0-9]+\\s*d");
			if(isDe){
				extractObject.Type = "de";
			}else{
				boolean isLo = syntax.matches(".*lo.*x[0-9]+\\s*k|.*lo.*x[0-9]+\\s*d");
				if(isLo){
					extractObject.Type = "lo";
				}else{
					extractObject.Type = "Unknow";
				}
			}
			
		}
		
		return extractObject;
	}
}
