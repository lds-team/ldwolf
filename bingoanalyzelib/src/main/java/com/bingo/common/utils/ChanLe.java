package com.bingo.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bingo.analyze.ExtractObject;

public class ChanLe {
	public static ExtractObject getChanLe(String syntax) {
		ExtractObject extractObject = null;
		boolean isSyntax = syntax.matches(".*(chan\\s*chan|le\\s*le|chan\\s*le|le\\s*chan).*x[0-9]+\\s*(k|d)");
		if (isSyntax) {
			extractObject = Utils.extractUnitPrice(syntax);
			extractObject.Numbers = new ArrayList<>();

			String regexCL = "((chan|le)\\s*(chan|le))";
			Matcher matcherCL = Pattern.compile(regexCL).matcher(syntax);
			while (matcherCL.find()) {
				String num = matcherCL.group(1).trim();
				String regexChanChan = ".*chan\\s*chan.*";
				boolean isSyntaxChanChan = num.matches(regexChanChan);
				if(isSyntaxChanChan){
					extractObject.Numbers.addAll(ChanLe.layChanChan());
				}

				String regexLeLe = ".*le\\s*le.*";
				boolean isSyntaxLeLe = num.matches(regexLeLe);
				if(isSyntaxLeLe){
					extractObject.Numbers.addAll(ChanLe.layLeLe());
				}

				String regexchanLe = ".*chan\\s*le.*";
				boolean isSyntaxchanLe = num.matches(regexchanLe);
				if(isSyntaxchanLe){
					extractObject.Numbers.addAll(ChanLe.layChanLe());
				}

				String regexLeChan = ".*le\\s*chan.*";
				boolean isSyntaxLeChan= num.matches(regexLeChan);
				if(isSyntaxLeChan){
					extractObject.Numbers.addAll(ChanLe.layLeChan());
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
	
	protected static ArrayList<String> layChanChan() {
		String[] soChan = { "00", "22", "44", "66", "88", "02", "20", "04", "40", "06", "60", "08", "80", "24", "42",
				"26", "62 ", "28", "82", "46", "64", "48", "84", "68", "86" };

		ArrayList<String> result = new ArrayList<String>(Arrays.asList(soChan));

		return result;
	}
	
	protected static ArrayList<String> layLeLe() {
		String[] soLe = { "11", "33", "55", "77", "99", "13", "31", "15", "51", "17", "71", "19", "91", "35", "53",
				"37", "73 ", "39", "93", "57", "75", "59", "95", "79", "97" };

		ArrayList<String> result = new ArrayList<String>(Arrays.asList(soLe));

		return result;
	}

	protected static ArrayList<String> layChanLe() {
		String[] deChanLe = { "01", "03", "05", "07", "09", "21", "23", "25", "27", "29", "41", "43", "45", "47", "49",
				"61", "63 ", "65", "67", "69", "81", "83", "85", "87", "89" };
		ArrayList<String> result = new ArrayList<String>(Arrays.asList(deChanLe));

		return result;
	}

	protected static ArrayList<String> layLeChan() {

		String[] deLeChan = { "10", "12", "14", "16", "18", "30", "32", "34", "36", "38", "50", "52", "54", "56", "58",
				"70", "72 ", "74", "76", "78", "90", "92", "94", "96", "98" };
		ArrayList<String> result = new ArrayList<String>(Arrays.asList(deLeChan));

		return result;
	}
}
