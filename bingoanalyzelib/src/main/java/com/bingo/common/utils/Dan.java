package com.bingo.common.utils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bingo.analyze.ExtractObject;

public class Dan {
	public static ExtractObject getDan(String syntax) {
		ExtractObject extractObject = null;
		boolean isSyntax = syntax.matches(".*dan\\s*[0-9]{2}.*x[0-9]+\\s*(k|d)");
		if (isSyntax) {
			
			extractObject = Utils.extractUnitPrice(syntax);
			extractObject.Numbers = new ArrayList<>();

			String mainSyntax = Utils.getRootSyntaxString(syntax);
			
			String regexDan = "([0-9]{2})";
			Matcher matcherDan = Pattern.compile(regexDan).matcher(mainSyntax);
			while (matcherDan.find()) {
				String num = matcherDan.group(1).trim();
				extractObject.Numbers.addAll(layDan(num));
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
	protected static ArrayList<String> layDan(String number) {
		ArrayList<String> result = new ArrayList<String>();
		String[] arrNums = number.split("");
		int iFirstNum = Integer.parseInt(arrNums[1]);
		int iSecondNum = Integer.parseInt(arrNums[2]);

		for (int i = iFirstNum; i <= iSecondNum; i++)
		{
			for (int j = iFirstNum; j <= iSecondNum; j++)
			{
				String catNum = String.valueOf(i) + String.valueOf(j);
				result.add(catNum);
			}
		}

		return result;
	}
}
