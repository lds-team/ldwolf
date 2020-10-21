package com.bingo.common.utils;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bingo.analyze.ExtractObject;

public class DauDit {
	public static ExtractObject getDauDuoi(String syntax) {
		ExtractObject extractObject = null;

		String regexCompare = ".*(dau|dit|duoi)\\W*(to|be|nho|lon)\\W*(hon)\\W*(dit|duoi|dau)\\W*(bor[\\s\\w]+)?x[0-9]+\\s*(k|d)";
		boolean isSyntax = syntax.matches(regexCompare);

		if (isSyntax)
		{
			extractObject = Utils.extractUnitPrice(syntax);
			extractObject.Numbers = new ArrayList<>();

			Matcher matcherCompare = Pattern.compile(regexCompare).matcher(syntax);
			while (matcherCompare.find()) {
				String num = matcherCompare.group(2).trim();
				String daudit = matcherCompare.group(1).trim();
				boolean isConvenient = daudit.contains("dau");
				boolean isGreater = num.contains("to") || num.contains("lon");
				if (isGreater)
					extractObject.Numbers.addAll(layDauSoSanhDit(isConvenient));
				else
					extractObject.Numbers.addAll(layDauSoSanhDit(!isConvenient));
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


		isSyntax = !isSyntax && syntax.matches(".*(de|lo)\\s*(dau|dit|duoi)\\W*([0-9\\s]+|to|be|le|chan).*x[0-9]+\\s*(k|d)");
		if (isSyntax) {
			extractObject = Utils.extractUnitPrice(syntax);
			extractObject.Numbers = new ArrayList<>();

			if (syntax.contains("ghep"))
			{
				getGhep(extractObject, syntax);
			}
			else
			{
				getNotGhep(extractObject, syntax);
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

	private static void getGhep(ExtractObject extractObject, String syntax) {
		boolean isSyntax = syntax.matches(".*dau.*ghep.*(dit|duoi).*x[0-9]+\\s*(k|d)");

		if (isSyntax)
		{
			String regexType1 = "dau\\s*([0-9]+|be|to|le|chan)\\s*ghep\\s*[a-zA-Z]*\\s*(dit|duoi)\\s*([0-9]+|be|to|le|chan)";
			Matcher matcherType1 = Pattern.compile(regexType1).matcher(syntax);
			while (matcherType1.find()) {
				String numDau = extractName(matcherType1.group(1).trim());
				String numDit = extractName(matcherType1.group(3).trim());
				extractObject.Numbers.addAll(ghep(numDau, numDit));
			}
		}
	}

	private static String extractName(String name) {
		switch (name) {
			case "be":
				return "01234";
			case "to":
				return "56789";
			case "le":
				return "13579";
			case "chan":
				return "02468";
			default:
				return name;
		}
	}

	protected static ArrayList<String> ghep(String numDau, String numDit) {

		ArrayList<String> number = new ArrayList<>();

		int sizeDau = numDau.length();
		int sizeDit = numDit.length();

		for (int i = 0; i < sizeDau; i++)
		{
			for (int j = 0; j < sizeDit; j++)
			{
				String numGhep = String.valueOf(numDau.charAt(i)).concat(String.valueOf(numDit.charAt(j)));
				number.add(numGhep);
			}
		}

		return number;
	}

	private static void getNotGhep(ExtractObject extractObject, String syntax) {

		String regexDau = ".*dau\\W*(([0-9]([-.,\\s]*))+).*";
		Matcher matcherDau = Pattern.compile(regexDau).matcher(syntax);
		while (matcherDau.find()) {
			String num = matcherDau.group(1).trim();
			num = num.replaceAll("\\W+","");
			String[] nums = num.split("");
			for (String number : nums)
			{
				if (!TextUtils.isEmpty(number))
				{
					extractObject.Numbers.addAll(layDau(number));
				}
			}
		}

		String regexDitDau = ".*(dit|duoi)\\s*dau\\W*(([0-9]([-.,\\s]*))+).*";
		Matcher matcherDitDau = Pattern.compile(regexDitDau).matcher(syntax);
		while (matcherDitDau.find()) {
			String num = matcherDitDau.group(2);
			num = num.replaceAll("\\W+","");
			String[] nums = num.split("");
			for (String number : nums)
			{
				if (!TextUtils.isEmpty(number))
				{
					extractObject.Numbers.addAll(layDuoi(number));
				}
			}
		}

		String regexDauDit = ".*dau\\s*(dit|duoi)\\W*(([0-9]([-.,\\s]*))+).*";
		Matcher matcherDauDit = Pattern.compile(regexDauDit).matcher(syntax);
		while (matcherDauDit.find()) {
			String num = matcherDauDit.group(2);
			num = num.replaceAll("\\W+","");
			String[] nums = num.split("");
			for (String number : nums)
			{
				if (!TextUtils.isEmpty(number))
				{
					extractObject.Numbers.addAll(layDau(number));
				}
			}
		}

		String regexDit = ".*(dit|duoi)\\W*(([0-9]([-.,\\s]*))+).*";
		Matcher matcherDit = Pattern.compile(regexDit).matcher(syntax);
		while (matcherDit.find()) {
			String num = matcherDit.group(2);
			num = num.replaceAll("\\W+","");
			String[] nums = num.split("");
			for (String number : nums)
			{
				if (!TextUtils.isEmpty(number))
				{
					extractObject.Numbers.addAll(layDuoi(number));
				}
			}
		}

		String regexDauBe = ".*dau\\s*((be){1}).*";
		Matcher matcherDauBe = Pattern.compile(regexDauBe).matcher(syntax);
		while (matcherDauBe.find()) {
			extractObject.Numbers.addAll(layDauBe());
		}

		String regexDauTo = ".*dau\\s*((to){1}).*";
		Matcher matcherDauTo = Pattern.compile(regexDauTo).matcher(syntax);
		while (matcherDauTo.find()) {
			extractObject.Numbers.addAll(layDauTo());
		}

		String regexDitBe = ".*(dit|duoi)\\s*((be){1}).*";
		Matcher matcherDitBe = Pattern.compile(regexDitBe).matcher(syntax);
		while (matcherDitBe.find()) {
			extractObject.Numbers.addAll(layDitBe());
		}

		String regexDitTo = ".*(dit|duoi)\\s*((to){1}).*";
		Matcher matcherDitTo = Pattern.compile(regexDitTo).matcher(syntax);
		while (matcherDitTo.find()) {
			extractObject.Numbers.addAll(layDitTo());
		}

		String regexDauChan = ".*(dau)\\s*((chan){1}).*";
		Matcher matcherDauChan = Pattern.compile(regexDauChan).matcher(syntax);
		while (matcherDauChan.find()) {
			extractObject.Numbers.addAll(layDauChan());
		}

		String regexDitChan = ".*(dit|duoi)\\s*((chan){1}).*";
		Matcher matcherDitChan = Pattern.compile(regexDitChan).matcher(syntax);
		while (matcherDitChan.find()) {
			extractObject.Numbers.addAll(layDitChan());
		}

		String regexDauLe = ".*(dau)\\s*((le){1}).*";
		Matcher matcherDauLe = Pattern.compile(regexDauLe).matcher(syntax);
		while (matcherDauLe.find()) {
			extractObject.Numbers.addAll(layDauLe());
		}

		String regexDitLe = ".*(dit|duoi)\\s*((le){1}).*";
		Matcher matcherDitLe = Pattern.compile(regexDitLe).matcher(syntax);
		while (matcherDitLe.find()) {
			extractObject.Numbers.addAll(layDitLe());
		}
	}
	
	protected static ArrayList<String> layDau(String number) {

		ArrayList<String> result = new ArrayList<String>();
		if (number.trim().equals(""))
			return result;
		if (number.trim().length() > 1 && number.trim().indexOf(" ") < 0) {
			result.add(number);
		} else {

			String[] arr = number.split(" ");
			for (String iNumber : arr) {
				if (iNumber.trim().equals(""))
					continue;
				for (int i = 0; i < 10; i++) {

					result.add(iNumber + "" + i);

				}
			}
		}

		return result;
	}

	protected static ArrayList<String> layDauBe() {

		ArrayList<String> result = new ArrayList<String>();

		for (int i = 0; i < 5; i++)
		{
			for (int j = 0; j < 10; j++)
			{
				result.add(String.valueOf(i).concat(String.valueOf(j)));
			}
		}

		return result;
	}

	protected static ArrayList<String> layDauTo() {

		ArrayList<String> result = new ArrayList<String>();

		for (int i = 5; i < 10; i++)
		{
			for (int j = 0; j < 10; j++)
			{
				result.add(String.valueOf(i).concat(String.valueOf(j)));
			}
		}

		return result;
	}
	
	protected static ArrayList<String> layDuoi(String number) {

		ArrayList<String> result = new ArrayList<String>();
		if (number.trim().equals(""))
			return result;
		if (number.trim().length() > 1 && number.trim().indexOf(" ") < 0) {
			result.add(number);
		} else {

			String[] arr = number.split(" ");
			for (String iNumber : arr) {
				if (iNumber.trim().equals(""))
					continue;
				for (int i = 0; i < 10; i++) {

					result.add(i + "" + iNumber);

				}
			}
		}

		return result;
	}

	protected static ArrayList<String> layDitBe() {

		ArrayList<String> result = new ArrayList<String>();

		for (int i = 0; i < 5; i++)
		{
			for (int j = 0; j < 10; j++)
			{
				result.add(String.valueOf(j).concat(String.valueOf(i)));
			}
		}

		return result;
	}

	protected static ArrayList<String> layDitTo() {

		ArrayList<String> result = new ArrayList<String>();

		for (int i = 5; i < 10; i++)
		{
			for (int j = 0; j < 10; j++)
			{
				result.add(String.valueOf(j).concat(String.valueOf(i)));
			}
		}

		return result;
	}

	protected static ArrayList<String> layDauChan() {

		ArrayList<String> result = new ArrayList<String>();

		for (int i = 0; i < 10; i+=2)
		{
			for (int j = 0; j < 10; j++)
			{
				result.add(String.valueOf(i).concat(String.valueOf(j)));
			}
		}

		return result;
	}

	protected static ArrayList<String> layDitChan() {

		ArrayList<String> result = new ArrayList<String>();

		for (int i = 0; i < 10; i+=2)
		{
			for (int j = 0; j < 10; j++)
			{
				result.add(String.valueOf(j).concat(String.valueOf(i)));
			}
		}

		return result;
	}

	protected static ArrayList<String> layDauLe() {

		ArrayList<String> result = new ArrayList<String>();

		for (int i = 1; i < 10; i+=2)
		{
			for (int j = 0; j < 10; j++)
			{
				result.add(String.valueOf(i).concat(String.valueOf(j)));
			}
		}

		return result;
	}

	protected static ArrayList<String> layDitLe() {

		ArrayList<String> result = new ArrayList<String>();

		for (int i = 1; i < 10; i+=2)
		{
			for (int j = 0; j < 10; j++)
			{
				result.add(String.valueOf(j).concat(String.valueOf(i)));
			}
		}

		return result;
	}

	protected static ArrayList<String> layDauSoSanhDit(boolean isConvenient) {
		if (isConvenient)
		{
			return layDauToHonDit();
		}
		else
		{
			return layDauNhoHonDit();
		}
	}

	protected static ArrayList<String> layDauToHonDit() {
		ArrayList<String> result = new ArrayList<String>();

		for (int i = 1; i < 10; i++)
		{
			for (int j = 0; j < i; j++)
			{
				result.add(String.valueOf(i).concat(String.valueOf(j)));
			}
		}

		return result;
	}

	protected static ArrayList<String> layDauNhoHonDit() {
		ArrayList<String> result = new ArrayList<String>();

		for (int i = 0; i < 10; i++)
		{
			for (int j = i + 1; j < 10; j++)
			{
				result.add(String.valueOf(i).concat(String.valueOf(j)));
			}
		}

		return result;
	}
}
