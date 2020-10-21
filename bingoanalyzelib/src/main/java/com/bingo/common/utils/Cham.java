package com.bingo.common.utils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bingo.analyze.ExtractObject;

public class Cham {
	public static ExtractObject getCham(String syntax) {
		ExtractObject extractObject = null;
		boolean isSyntax = syntax.matches(".*cham[\\s:.]*[0-9\\s]+.*x[0-9]+\\s*(k|d)");
		if (isSyntax) {
			extractObject = Utils.extractUnitPrice(syntax);
			extractObject.Numbers = new ArrayList<>();

			String tempSyntax = Utils.getRootSyntaxString(syntax);

			String regexDau = "([0-9])";
			Matcher matcherDau = Pattern.compile(regexDau).matcher(tempSyntax);
			while (matcherDau.find()) {
				String num = matcherDau.group(1).trim();
				extractObject.Numbers.addAll(layCham(num));
			}

			//Remove duplicate number.
			extractObject.Numbers = new ArrayList<String>(new LinkedHashSet<String>(extractObject.Numbers));
		
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
	
	protected static ArrayList<String> layCham(String number) {
		ArrayList<String> result = new ArrayList<String>();
		if (number.trim().equals(""))
			return result;

		if (!number.trim().equals("")) {
			for (int i = 0; i < 10; i++) {
				if (result.indexOf(number + "" + i) < 0) {
					result.add(number + "" + i);
				}

				if (result.indexOf(i + "" + number) < 0) {
					result.add(i + "" + number);
				}

			}
		}

		return result;
	}
}
