package com.bingo.common.utils;

import java.util.ArrayList;

import com.bingo.analyze.ExtractObject;

public class OneHundred {
	public static ExtractObject get100So(String syntax) {
		ExtractObject extractObject = null;
		boolean isSyntax = syntax.matches(".*100\\s*so\\s*(bor.*)?x[0-9]+\\s*(k|d)");
		if (isSyntax) {
			extractObject = Utils.extractUnitPrice(syntax);
			extractObject.Numbers = lay100So();
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
	
	protected static ArrayList<String> lay100So() {

		ArrayList<String> result = new ArrayList<String>();
		for (int i = 0; i < 100; i++) {
			if (i < 10) {
				result.add("0" + i);
			} else {
				result.add("" + i);
			}

		}

		return result;
	}
}
