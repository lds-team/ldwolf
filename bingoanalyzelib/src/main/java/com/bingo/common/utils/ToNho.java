package com.bingo.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bingo.analyze.ExtractObject;

public class ToNho {

	public static ExtractObject getToNho(String syntax) {
		ExtractObject extractObject = null;
		boolean isSyntax = syntax.matches(".*((nho|be)\\s*(nho|be)|to\\s*to|(nho|be)\\s*to|to\\s*(nho|be)).*x[0-9]+\\s*(k|d)");
		if (isSyntax) {
			extractObject = Utils.extractUnitPrice(syntax);
			extractObject.Numbers = new ArrayList<>();

			String regexTN = "((to|nho|be)\\s*(to|nho|be))";
			Matcher matcherTN = Pattern.compile(regexTN).matcher(syntax);
			while (matcherTN.find()) {
				String num = matcherTN.group(1).trim();

				String regexNhoNho = ".*(nho|be)\\s*(nho|be).*";
				boolean isSyntaxNhoNho = num.matches(regexNhoNho);
				if(isSyntaxNhoNho){
					extractObject.Numbers.addAll(ToNho.layNhoNho());
				}

				String regexToTo = ".*to\\s*to.*";
				boolean isSyntaxToTo = num.matches(regexToTo);
				if(isSyntaxToTo){
					extractObject.Numbers.addAll(ToNho.layToTo());
				}

				String regexNhoTo = ".*(nho|be)\\s*to.*";
				boolean isSyntaxNhoTo = num.matches(regexNhoTo);
				if(isSyntaxNhoTo){
					extractObject.Numbers.addAll(ToNho.layNhoTo());
				}

				String regexToNho = ".*to\\s*(nho|be).*";
				boolean isSyntaxToNho = num.matches(regexToNho);
				if(isSyntaxToNho){
					extractObject.Numbers.addAll(ToNho.layToNho());
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
	
	protected static ArrayList<String> layNhoNho() {

		String[] deNhoNho = { "00", "11", "22", "33", "44", "01", "10", "02", "20", "03", "30", "04", "40", "12", "21",
				"13", "31 ", "14", "41", "23", "32", "24", "42", "34", "43" };
		ArrayList<String> result = new ArrayList<String>(Arrays.asList(deNhoNho));

		return result;
	}

	protected static ArrayList<String> layToTo() {

		String[] deToTo = { "55", "66", "77", "88", "99", "56", "65", "57", "75", "58", "85", "59", "95", "67", "76",
				"68", "86 ", "69", "96", "78", "87", "79", "97", "89", "98" };

		ArrayList<String> result = new ArrayList<String>(Arrays.asList(deToTo));

		return result;
	}

	protected static ArrayList<String> layNhoTo() {

		String[] deToTo = { "05", "06", "07", "08", "09", "15", "16", "17", "18", "19", "25", "26", "27", "28", "29",
				"35", "36 ", "37", "38", "39", "45", "46", "47", "48", "49" };

		ArrayList<String> result = new ArrayList<String>(Arrays.asList(deToTo));

		return result;
	}

	protected static ArrayList<String> layToNho() {

		String[] deToTo = { "90", "91", "92", "93", "94", "80", "81", "82", "83", "84", "70", "71", "72", "73", "74",
				"60", "61 ", "62", "63", "64", "50", "51", "52", "53", "54" };

		ArrayList<String> result = new ArrayList<String>(Arrays.asList(deToTo));

		return result;
	}
}
