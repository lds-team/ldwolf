package com.bingo.common.utils;

import com.bingo.analyze.ExtractObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tong {
	public static ExtractObject getTong(String syntax) {
		ExtractObject extractObject = null;
		String _syntax = Utils.getRootSyntaxWithCost(syntax);
		boolean isSyntax = _syntax.matches(".*tong\\W*([0-9][\\s.,]*)+\\W*(bor[\\s\\w]+)?x[0-9]+\\s*(k|d)");
		if (isSyntax) {
			extractObject = Utils.extractUnitPrice(syntax);
			ArrayList<String> totalType = Utils.getTypeTotal(_syntax);
			extractObject.Numbers = new ArrayList<>();
			for (String num : totalType) {
				extractObject.Numbers.addAll(layTong(num));
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

	public static ExtractObject getTongTrenDuoi(String syntax) {
		ExtractObject extractObject = null;
		boolean isSyntax = syntax.matches(".*tong\\s*(tren|duoi).*x[0-9]+\\s*(k|d)");
		if (isSyntax) {
			extractObject = Utils.extractUnitPrice(syntax);
			extractObject.Numbers = new ArrayList<>();

			if (syntax.matches(".*tong\\s*tren.*"))
			{
				extractObject.Numbers.addAll(layTongTrenDuoi(true));
			}
			else if (syntax.matches(".*tong\\s*duoi.*"))
			{
				extractObject.Numbers.addAll(layTongTrenDuoi(false));
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

	public static ExtractObject getTongLeChan(String syntax) {
		ExtractObject extractObject = null;
		boolean isSyntax = syntax.matches(".*tong\\W*(le|chan).*x[0-9]+\\s*(k|d)");
		if (isSyntax) {
			extractObject = Utils.extractUnitPrice(syntax);
			extractObject.Numbers = new ArrayList<>();

			String regex = ".*tong\\W*(le|chan).*";
			Matcher matcher = Pattern.compile(regex).matcher(syntax);
			while (matcher.find()) {
				String num = matcher.group(1).trim();
				extractObject.Numbers.addAll(layTongLeChan(num.trim().contains("le")));
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

	public static ExtractObject getTongToBe(String syntax) {
		ExtractObject extractObject = null;
		boolean isSyntax = syntax.matches(".*tong\\W*(to|be).*x[0-9]+\\s*(k|d)");
		if (isSyntax) {
			extractObject = Utils.extractUnitPrice(syntax);
			extractObject.Numbers = new ArrayList<>();

			String regex = ".*tong\\W*(to|be).*";
			Matcher matcher = Pattern.compile(regex).matcher(syntax);
			while (matcher.find()) {
				String num = matcher.group(1).trim();
				extractObject.Numbers.addAll(layTongToBe(num.trim().contains("to")));
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
	
	public static ExtractObject getTongChiaBa(String syntax) {
		ExtractObject extractObject = null;
		boolean isSyntax = syntax.matches(".*tong\\s*(khong)*\\s*chia\\s*(ba|3).*x[0-9]+\\s*(k|d)") && !syntax.matches(".*tong\\s*chia\\s*(ba|3)+\\s*du.*");
		if (isSyntax) {
			extractObject = Utils.extractUnitPrice(syntax);

			if (syntax.matches(".*tong\\s*khong.*"))
			{
				extractObject.Numbers = Tong.layTongKhongChiaBa();
			}
			else
			{
				extractObject.Numbers = Tong.layTongChiaBa();
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
	
	
	public static ExtractObject getTongChiaBaDu1(String syntax) {
		ExtractObject extractObject = null;
		boolean isSyntax = syntax.matches(".*tong\\s*chia\\s*(ba|3)+\\s*du\\s*(1|mot)+.*x[0-9]+\\s*(k|d)");
		if (isSyntax) {
			extractObject = Utils.extractUnitPrice(syntax);
			extractObject.Numbers = Tong.layTongChiaBaDu1();
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
	
	public static ExtractObject getTongChiaBaDu2(String syntax) {
		ExtractObject extractObject = null;
		boolean isSyntax = syntax.matches(".*tong\\s*chia\\s*(ba|3)+\\s*du\\s*(2|hai)+.*x[0-9]+\\s*(k|d)");
		if (isSyntax) {
			extractObject = Utils.extractUnitPrice(syntax);
			extractObject.Numbers = Tong.layTongChiaBaDu2();
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

	public static ArrayList<String> layTongChiaBaDu2() {
		String[] tongChiaBaDu2 = { "02", "05", "08", "11", "14", "17", "20", "23", "26", "29", "32", "35", "38", "41",
				"44", "47", "50", "53", "56", "59", "62", "65", "68", "71", "74", "77", "80", "83", "86", "89", "92",
				"95", "98" };
		ArrayList<String> result = new ArrayList<String>(Arrays.asList(tongChiaBaDu2));

		return result;
	}

	public static ArrayList<String> layTongChiaBaDu1() {
		String[] tongChiaBaDu1 = { "01", "04", "07", "10", "13", "16", "19", "22", "25", "28", "31", "34", "37", "40",
				"43", "46", "49", "52", "55", "58", "61", "64", "67", "70", "73", "76", "79", "82", "85", "88", "91",
				"94", "97" };
		ArrayList<String> result = new ArrayList<String>(Arrays.asList(tongChiaBaDu1));

		return result;
	}

	public static ArrayList<String> layTongChiaBa(){
		String[] deTongChiaBa = { "03", "06", "09", "12", "15", "18", "21", "24", "27", "30", "33", "36", "39",
				"42", "45", "48", "51", "54", "57", "60", "63", "66", "69", "72", "75", "78", "81", "84", "87", "90",
				"93", "96", "99" };
		ArrayList<String> result = new ArrayList<String>(Arrays.asList(deTongChiaBa));

		return result;
	}

	public static ArrayList<String> layTongKhongChiaBa() {
		String[] tongKhongChiaBa = { "00", "01", "04", "07", "10", "13", "16", "19", "22", "25", "28", "31", "34", "37", "40",
				"43", "46", "49", "52", "55", "58", "61", "64", "67", "70", "73", "76", "79", "82", "85", "88", "91",
				"94", "97", "02", "05", "08", "11", "14", "17", "20", "23", "26", "29", "32", "35", "38", "41",
				"44", "47", "50", "53", "56", "59", "62", "65", "68", "71", "74", "77", "80", "83", "86", "89", "92",
				"95", "98" };
		ArrayList<String> result = new ArrayList<String>(Arrays.asList(tongKhongChiaBa));

		return result;
	}

	protected static ArrayList<String> layTongTrenDuoi(boolean isTren10) {
		//Default for number = 10.
		String[] tren10 = {"29","38","39","47","48","49","56","57","58","59","65","66","67","68","69","74","75","76","77","78","79","83","84","85","86","87","88","89","92","93","94","95","96","97","98","99 "};

		String[] duoi10 = {"01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","20","21","22","23","24","25","26","27","30","31","32","33","34","35","36","40","41","42","43","44","45","50","51","52","53","54","60","61","62","63","70","71","72","80","81","90"};

		if (isTren10)
			return new ArrayList<>(Arrays.asList(tren10));
		else
			return new ArrayList<>(Arrays.asList(duoi10));
	}
	
	protected static ArrayList<String> layTong(String number) {
		String[] tong0 = { "19", "91", "28", "82", "37", "73", "46", "64", "55", "00" };
		String[] tong1 = { "01", "10", "29", "92", "38", "83", "47", "74", "56", "65" };
		String[] tong2 = { "02", "20", "39", "93", "48", "84", "57", "75", "11", "66" };
		String[] tong3 = { "03", "30", "12", "21", "49", "94", "58", "85", "67", "76" };
		String[] tong4 = { "04", "40", "13", "31", "59", "95", "68", "86", "22", "77" };
		String[] tong5 = { "05", "50", "14", "41", "23", "32", "69", "96", "78", "87" };
		String[] tong6 = { "06", "60", "15", "51", "24", "42", "79", "97", "33", "88" };
		String[] tong7 = { "07", "70", "16", "61", "25", "52", "34", "43", "89", "98" };
		String[] tong8 = { "08", "80", "17", "71", "26", "62", "35", "53", "44", "99" };
		String[] tong9 = { "09", "90", "18", "81", "27", "72", "36", "63", "45", "54" };

		ArrayList<String> result = new ArrayList<String>();
		if (ArrayUtils.indexOf(tong0, number.trim()) >= 0 || number.equals("0")) {
			result = new ArrayList<String>(Arrays.asList(tong0));
		} else if (ArrayUtils.indexOf(tong1, number.trim()) >= 0 || number.equals("1")) {
			result = new ArrayList<String>(Arrays.asList(tong1));
		} else if (ArrayUtils.indexOf(tong2, number.trim()) >= 0 || number.equals("2")) {
			result = new ArrayList<String>(Arrays.asList(tong2));
		} else if (ArrayUtils.indexOf(tong3, number.trim()) >= 0 || number.equals("3")) {
			result = new ArrayList<String>(Arrays.asList(tong3));
		} else if (ArrayUtils.indexOf(tong4, number.trim()) >= 0 || number.equals("4")) {
			result = new ArrayList<String>(Arrays.asList(tong4));
		} else if (ArrayUtils.indexOf(tong5, number.trim()) >= 0 || number.equals("5")) {
			result = new ArrayList<String>(Arrays.asList(tong5));
		} else if (ArrayUtils.indexOf(tong6, number.trim()) >= 0 || number.equals("6")) {
			result = new ArrayList<String>(Arrays.asList(tong6));
		} else if (ArrayUtils.indexOf(tong7, number.trim()) >= 0 || number.equals("7")) {
			result = new ArrayList<String>(Arrays.asList(tong7));
		} else if (ArrayUtils.indexOf(tong8, number.trim()) >= 0 || number.equals("8")) {
			result = new ArrayList<String>(Arrays.asList(tong8));
		} else if (ArrayUtils.indexOf(tong9, number.trim()) >= 0 || number.equals("9")) {
			result = new ArrayList<String>(Arrays.asList(tong9));
		}

		return result;
	}

	protected static ArrayList<String> layTongLeChan(boolean layTongle) {

		String[] tongLe = {
				"01", "10", "29", "92", "38", "83", "47", "74", "56", "65",
				"03", "30", "12", "21", "49", "94", "58", "85", "67", "76",
				"05", "50", "14", "41", "23", "32", "69", "96", "78", "87",
				"07", "70", "16", "61", "25", "52", "34", "43", "89", "98",
				"09", "90", "18", "81", "27", "72", "36", "63", "45", "54"};

		String[] tongChan = {
				"19", "91", "28", "82", "37", "73", "46", "64", "55", "00",
				"02", "20", "39", "93", "48", "84", "57", "75", "11", "66",
				"04", "40", "13", "31", "59", "95", "68", "86", "22", "77",
				"06", "60", "15", "51", "24", "42", "79", "97", "33", "88",
				"08", "80", "17", "71", "26", "62", "35", "53", "44", "99"};

		if (layTongle)
		{
			return new ArrayList<>(Arrays.asList(tongLe));
		}
		else
		{
			return new ArrayList<>(Arrays.asList(tongChan));
		}
	}

	protected static ArrayList<String> layTongToBe(boolean layTongTo) {

		String[] tongTo = {
				"05", "50", "14", "41", "23", "32", "69", "96", "78", "87",
				"06", "60", "15", "51", "24", "42", "79", "97", "33", "88",
				"07", "70", "16", "61", "25", "52", "34", "43", "89", "98",
				"08", "80", "17", "71", "26", "62", "35", "53", "44", "99",
				"09", "90", "18", "81", "27", "72", "36", "63", "45", "54"};

		String[] tongBe = {
				"00", "55", "19", "91", "28", "82", "37", "73", "46", "64",
				"01", "10", "29", "92", "38", "83", "47", "74", "56", "65",
				"02", "20", "39", "93", "48", "84", "57", "75", "11", "66",
				"03", "30", "12", "21", "49", "94", "58", "85", "67", "76",
				"04", "40", "13", "31", "59", "95", "68", "86", "22", "77"};

		if (layTongTo)
		{
			return new ArrayList<>(Arrays.asList(tongTo));
		}
		else
		{
			return new ArrayList<>(Arrays.asList(tongBe));
		}
	}
}
