package com.bingo.common.utils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bingo.analyze.ExtractObject;

public class Ghep {
	public static ExtractObject getGhep(String syntax) {
		ExtractObject extractObject = null;
		boolean isSyntax = syntax.matches(".*dau\\s*[0-9\\s]+ghep\\s*(dit|duoi)[0-9\\s]+\\s*x[0-9]+\\s*(k|d)");
		if (/*isSyntax*/false) {
			
			extractObject = Utils.extractUnitPrice(syntax);
			extractObject.Numbers = new ArrayList<>();
			ArrayList<String> arrDau = new ArrayList<>();
			ArrayList<String> arrDuoi = new ArrayList<>();
			
			String regexDau = ".*dau\\s*([0-9]{1}).*";
			Matcher matcherDau = Pattern.compile(regexDau).matcher(syntax);
			while (matcherDau.find()) {
				String num = matcherDau.group(1).trim();
				arrDau.add(num);
			}
			
			String regexDuoi = ".*(duoi|dit)\\s*([0-9]{1}).*";
			Matcher matcherDuoi = Pattern.compile(regexDuoi).matcher(syntax);
			while (matcherDuoi.find()) {
				String num = matcherDuoi.group(1).trim();
				arrDuoi.add(num);
			}
			
			for (String dau : arrDau) {
				for (String duoi : arrDuoi) {
					extractObject.Numbers.add(dau+duoi);
				}
			}
			
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
