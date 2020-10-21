package com.bingo.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bingo.analyze.Constant;
import com.bingo.analyze.ExtractObject;
import com.bingo.analyze.RemoveObject;

public class Utils {
	public static ArrayList<String> getRemoveNumber(String syntax) {
		// de bo 12, bo 13 bor 21, 31x500k
		Pattern pattern = Pattern.compile("bor");
		int count = countMatches(pattern, syntax); // Returns 2
		ArrayList<String> removeSyntaxArray = new ArrayList<>();
		if (count == 1) {
			int idxStart = syntax.indexOf("bor") + 3;
			int idxEnd = syntax.lastIndexOf("x");
			if (idxStart < idxEnd)
			{
				String subString = syntax.substring(idxStart, idxEnd);
				removeSyntaxArray.add(subString.trim());
			}
		} else {

			String[] subStrings = syntax.split("bor");
			int index = 0;
			for (String sub : subStrings) {
				if (index == 0) {
				} else {
					boolean isEndSyntax = sub.matches(".*x[0-9]+\\s*k|.*x[0-9]+\\s*d");
					if (isEndSyntax) {
						sub = sub.substring(0, sub.indexOf("x"));
					}
					removeSyntaxArray.add(sub.trim());
				}

				index++;
			}

		}

		return removeSyntaxArray;
	}

	public static ArrayList<RemoveObject> extractRemoveNumber(ArrayList<String> removeSyntaxArray) {
		if (true) return null;
		ArrayList<RemoveObject> numbers = new ArrayList<RemoveObject>();
		String regxDaudit = ".*dau\\s*dit\\s*(([0-9][\\W]*)+).*";
		String regxDau = ".*dau\\s*(([0-9][\\W]*)+).*";
		String regxDit = ".*dit\\s*(([0-9][\\W]*)+).*";
		String regxTong = ".*tong\\s*(([0-9][\\s.,]*)+).*";
		String regxBo = ".*boj\\s*([0-9][0-9]([,.\\s]*))+.*";
		String regxDauDitTNCL = ".*dau\\s*dit\\s*(chan|le|to|nho).*";
		String regxDauDitToNho = ".*dau\\s*(to|nho).*|.*dit\\s*(to|nho).*";
		String regxDauDitChanle = ".*dau\\s*(chan|le).*|.*dit\\s*(chan|le).*";
		String regxChanAndLe = ".*((\\s*(chan|le)\\s*(chan|le)\\s*)+).*";
		String regxToAndBe = ".*((\\s*(to|be|nho)\\s*(to|be|nho)\\s*)+).*";

		String regxTongTrenDuoi = ".*tong\\s*(tren|duoi)\\s*10.*";
		String regxTongChan = ".*tong\\s*chan.*";
		String regxTongLe = ".*tong\\s*le.*";
		String regxTongChia3 = ".*tong\\s*chia\\s*[ba3]+.*";
		String regxTongChia3Du1 = ".*tong\\s*chia\\s*[ba3\\+]+\\s*du\\s*[1mot]+.*";
		String regxTongChia3Du2 = ".*tong\\s*chia\\s*[ba3\\+]+\\s*du\\s*[2hai]+.*";

		String regxKepBang = ".*kep\\s*bang.*";
		String regxKepLech = ".*kep\\s*lech.*";
		String regxSatKep = ".*sat\\s*kep.*";

		String regxToNho = ".*dau\\s*(nho|to|be)\\s*ghep\\s*dit\\s*(nho|to|be).*";
		String regxChanLe = ".*dau\\s*(chan|le)\\s*ghep\\s*dit\\s*(chan|le).*";

		String regxDan = ".*dan\\s*([0-9]{2}[\\W]*)+.*";

		String regxCham = ".*cham\\s*([0-9][\\s.,]*)+.*";

		for (String sub : removeSyntaxArray) {
			String newSub = sub;

			if (sub.matches(regxDaudit)) {
				String reg = "dau\\s*dit\\s*(([0-9][\\s.,]*)+)";
				Pattern pattern = Pattern.compile(reg);
				Matcher matcher = pattern.matcher(sub);
				while (matcher.find()) {
					String[] nums = matcher.group(1).split("");
					for (String num : nums) {
						if (!num.trim().matches("\\W*") && isInteger(num.trim())) {
							//TODO: hot fix.
							ArrayList<String> numRemoves = DauDit.layDau(num);
							numRemoves.addAll(DauDit.layDuoi(num));
							for (String remove : numRemoves)
							{
								RemoveObject obj = new RemoveObject();
								obj.Number = remove.trim();
								obj.Type = "daudit";
								numbers.add(obj);
							}
						}
					}
				}

				newSub = newSub.replaceAll(reg, "");
			}

			if (sub.matches(regxDau)) {
				String reg = "(dau\\s*)(([0-9][\\W]*)+)";
				Pattern pattern = Pattern.compile(reg);
				Matcher matcher = pattern.matcher(sub);
				while (matcher.find()) {
					String[] nums = matcher.group(2).split("");
					for (String num : nums) {
						if (!num.trim().matches("\\W*") && isInteger(num.trim())) {
							//TODO: hot fix.
							ArrayList<String> numRemoves = DauDit.layDau(num);
							for (String remove : numRemoves)
							{
								RemoveObject obj = new RemoveObject();
								obj.Number = remove.trim();
								obj.Type = "dau";
								numbers.add(obj);
							}
						}
					}
				}

				newSub = newSub.replaceAll(reg, "");
			}

			if (sub.matches(regxDit)) {
				String reg = "(dit\\s*)(([0-9][\\W]*)+)";
				Pattern pattern = Pattern.compile(reg);
				Matcher matcher = pattern.matcher(sub);
				while (matcher.find()) {
					String[] nums = matcher.group(2).split("");
					for (String num : nums) {
						if (!num.trim().matches("\\W*") && isInteger(num.trim())) {
							//TODO: hot fix.
							ArrayList<String> numRemoves = DauDit.layDuoi(num);
							for (String remove : numRemoves)
							{
								RemoveObject obj = new RemoveObject();
								obj.Number = remove.trim();
								obj.Type = "dit";
								numbers.add(obj);
							}
						}
					}
				}

				newSub = newSub.replaceAll(reg, "");
			}

			if (sub.matches(regxDauDitTNCL)) {
				String reg = "(dau\\s*dit)\\s*(to|nho|chan|le)";
				Pattern pattern = Pattern.compile(reg);
				Matcher matcher = pattern.matcher(sub);
				while (matcher.find()) {
					String nums = matcher.group(2);
					ArrayList<String> numRemoves = null;

					if (nums.contains("to"))
					{
						numRemoves = DauDit.layDauTo();
						numRemoves.addAll(DauDit.layDitTo());
					}
					else if (nums.contains("nho"))
					{
						numRemoves = DauDit.layDauBe();
						numRemoves.addAll(DauDit.layDitBe());
					}
					else if (nums.contains("chan"))
					{
						numRemoves = DauDit.layDauChan();
						numRemoves.addAll(DauDit.layDitChan());
					}
					else if (nums.contains("le"))
					{
						numRemoves = DauDit.layDauLe();
						numRemoves.addAll(DauDit.layDitLe());
					}

					if (numRemoves != null)
					{
						for (String remove : numRemoves)
						{
							RemoveObject obj = new RemoveObject();
							obj.Number = remove.trim();
							obj.Type = "daudit";
							numbers.add(obj);
						}
					}
				}

				newSub = newSub.replaceAll(reg, "");
			}

			if (sub.matches(regxDauDitToNho) && !sub.contains("ghep")) {
				String reg = "(dau|dit)\\s*(to|nho)";
				Pattern pattern = Pattern.compile(reg);
				Matcher matcher = pattern.matcher(sub);
				while (matcher.find()) {
					String type = matcher.group(1);
					String nums = matcher.group(2);
					ArrayList<String> numRemoves = null;

					if (type.contains("dau"))
					{
						if (nums.contains("to"))
						{
							numRemoves = DauDit.layDauTo();
						}
						else if (nums.contains("nho"))
						{
							numRemoves = DauDit.layDauBe();
						}
					}
					else
					{
						if (nums.contains("to"))
						{
							numRemoves = DauDit.layDitTo();
						}
						else if (nums.contains("nho"))
						{
							numRemoves = DauDit.layDitBe();
						}
					}

					if (numRemoves != null)
					{
						for (String remove : numRemoves)
						{
							RemoveObject obj = new RemoveObject();
							obj.Number = remove.trim();
							obj.Type = type.contains("dau") ? "dau" : "dit";
							numbers.add(obj);
						}
					}
				}

				newSub = newSub.replaceAll(reg, "");
			}

			if (sub.matches(regxTong)) {
				String reg = "(tong\\s*)(([0-9][\\W]*)+).*";
				Pattern pattern = Pattern.compile(reg);
				Matcher matcher = pattern.matcher(sub);
				while (matcher.find()) {
					String[] nums = matcher.group(2).split("");
					for (String num : nums) {
						if (!num.trim().matches("\\W*") && isInteger(num.trim())) {
							//TODO: hot fix.
							ArrayList<String> numRemoves = Tong.layTong(num);
							for (String remove : numRemoves)
							{
								RemoveObject obj = new RemoveObject();
								obj.Number = remove.trim();
								obj.Type = "tong";
								numbers.add(obj);
							}
						}
					}
				}

				newSub = newSub.replaceAll(reg, "");
			}

			if (sub.matches(regxBo)) {
				String reg = "(boj\\s*)(([0-9][0-9]([,.\\s]*))+)";
				Pattern pattern = Pattern.compile(reg);
				Matcher matcher = pattern.matcher(sub);
				while (matcher.find()) {
					Pattern pat = Pattern.compile("[0-9][0-9]");
					Matcher mat = pat.matcher(matcher.group(2));

					while (mat.find())
					{
						String num = mat.group(0);
						if (num != null)
						{
							ArrayList<String> numRemoves = Bo.layBo(num);
							for (String remove : numRemoves)
							{
								RemoveObject obj = new RemoveObject();
								obj.Number = remove.trim();
								obj.Type = "boj";
								numbers.add(obj);
							}
						}
					}
				}

				newSub = newSub.replaceAll(reg, "");
			}

			if (sub.matches(regxDauDitChanle) && !sub.contains("ghep")) {
				String reg = "(dau|dit)\\s*(chan|le)";
				Pattern pattern = Pattern.compile(reg);
				Matcher matcher = pattern.matcher(sub);
				while (matcher.find()) {
					String type = matcher.group(1);
					String nums = matcher.group(2);
					ArrayList<String> numRemoves = null;

					if (type.contains("dau"))
					{
						if (nums.contains("chan"))
						{
							numRemoves = DauDit.layDauChan();
						}
						else if (nums.contains("le"))
						{
							numRemoves = DauDit.layDauLe();
						}
					}
					else
					{
						if (nums.contains("chan"))
						{
							numRemoves = DauDit.layDitChan();
						}
						else if (nums.contains("le"))
						{
							numRemoves = DauDit.layDitLe();
						}
					}

					if (numRemoves != null)
					{
						for (String remove : numRemoves)
						{
							RemoveObject obj = new RemoveObject();
							obj.Number = remove.trim();
							obj.Type = type.contains("dau") ? "dau" : "dit";
							numbers.add(obj);
						}
					}
				}

				newSub = newSub.replaceAll(reg, "");
			}

			if (sub.matches(regxTongTrenDuoi)) {
				ArrayList<String> numRemoves = new ArrayList<>();

				if (sub.matches(".*tong\\s*tren\\s*10.*"))
				{
					numRemoves.addAll(Tong.layTongTrenDuoi(true));
				}
				else if (sub.matches(".*tong\\s*duoi\\s*10.*"))
				{
					numRemoves.addAll(Tong.layTongTrenDuoi(false));
				}

				for (String remove : numRemoves)
				{
					RemoveObject obj = new RemoveObject();
					obj.Number = remove.trim();
					obj.Type = "tong";
					numbers.add(obj);
				}

				newSub = newSub.replaceAll("tong\\s*(tren|duoi)\\s*10", "");
			}

			if (sub.matches(regxTongChan)) {
				ArrayList<String> numRemoves = Tong.layTongLeChan(false);
				for (String remove : numRemoves)
				{
					RemoveObject obj = new RemoveObject();
					obj.Number = remove.trim();
					obj.Type = "tong";
					numbers.add(obj);
				}

				newSub = newSub.replaceAll("tong\\s*chan", "");
			}

			if (sub.matches(regxTongLe)) {
				ArrayList<String> numRemoves = Tong.layTongLeChan(true);
				for (String remove : numRemoves)
				{
					RemoveObject obj = new RemoveObject();
					obj.Number = remove.trim();
					obj.Type = "tong";
					numbers.add(obj);
				}

				newSub = newSub.replaceAll("tong\\s*le", "");
			}

			if (sub.matches(regxTongChia3)) {
				ArrayList<String> numRemoves = Tong.layTongChiaBa();
				for (String remove : numRemoves)
				{
					RemoveObject obj = new RemoveObject();
					obj.Number = remove.trim();
					obj.Type = "tong";
					numbers.add(obj);
				}

				newSub = newSub.replaceAll("tong\\s*chia\\s*[ba3]+", "");
			}

			if (sub.matches(regxTongChia3Du1)) {
				ArrayList<String> numRemoves = Tong.layTongChiaBaDu1();
				for (String remove : numRemoves)
				{
					RemoveObject obj = new RemoveObject();
					obj.Number = remove.trim();
					obj.Type = "tong";
					numbers.add(obj);
				}

				newSub = newSub.replaceAll("tong\\s*chia\\s*[ba3\\+]+\\s*du\\s*[1mot]+", "");
			}

			if (sub.matches(regxTongChia3Du2)) {
				ArrayList<String> numRemoves = Tong.layTongChiaBaDu2();
				for (String remove : numRemoves)
				{
					RemoveObject obj = new RemoveObject();
					obj.Number = remove.trim();
					obj.Type = "tong";
					numbers.add(obj);
				}

				newSub = newSub.replaceAll("tong\\s*chia\\s*[ba3\\+]+\\s*du\\s*[2hai]+", "");
			}

			if (sub.matches(regxKepLech)) {
				ArrayList<String> numRemoves = Kep.layDeKepLech();
				for (String remove : numRemoves)
				{
					RemoveObject obj = new RemoveObject();
					obj.Number = remove.trim();
					obj.Type = "boj";
					numbers.add(obj);
				}

				newSub = newSub.replaceAll("kep\\s*lech", "");
			}

			if (sub.matches(regxSatKep)) {
				ArrayList<String> numRemoves = Kep.layDeSatKep();
				for (String remove : numRemoves)
				{
					RemoveObject obj = new RemoveObject();
					obj.Number = remove.trim();
					obj.Type = "boj";
					numbers.add(obj);
				}

				newSub = newSub.replaceAll("sat\\s*kep", "");
			}

			if (sub.matches(regxKepBang)) {
				ArrayList<String> numRemoves = Kep.layDeKepBang();
				for (String remove : numRemoves)
				{
					RemoveObject obj = new RemoveObject();
					obj.Number = remove.trim();
					obj.Type = "boj";
					numbers.add(obj);
				}

				newSub = newSub.replaceAll("kep\\s*bang", "");
			}

			if (sub.matches(regxDan)) {
				String reg = "(dan\\s*)(([0-9]{2}[\\W]*)+)";
				Pattern pattern = Pattern.compile(reg);
				Matcher matcher = pattern.matcher(sub);
				while (matcher.find()) {
					String nums = matcher.group(2);
					String[] numArr = nums.split("\\W");
					for (String num : numArr)
					{
						if (!num.matches("\\W"))
						{
							ArrayList<String> numRemoves = Dan.layDan(num);
							for (String remove : numRemoves)
							{
								RemoveObject obj = new RemoveObject();
								obj.Number = remove.trim();
								obj.Type = "dan";
								numbers.add(obj);
							}
						}
					}
				}

				newSub = newSub.replaceAll(reg, "");
			}

			if (sub.matches(regxCham)) {
				String regexCham = "cham\\s*(([0-9][\\s.,]*)+)";
				Matcher matcherCham = Pattern.compile(regexCham).matcher(sub);
				while (matcherCham.find()) {
					String num = matcherCham.group(1).trim();
					String[] nums = num.split("[\\s.,]");
					for (String n : nums)
					{
						if (!n.matches("\\W"))
						{
							ArrayList<String> numRemoves = Cham.layCham(n);
							for (String remove : numRemoves)
							{
								RemoveObject obj = new RemoveObject();
								obj.Number = remove.trim();
								obj.Type = "cham";
								numbers.add(obj);
							}
						}
					}
				}

				newSub = newSub.replaceAll(regexCham, "");
			}

			if (sub.matches(regxChanAndLe)) {
				String regexCL = "((chan|le)\\s*(chan|le))";
				Matcher matcherCL = Pattern.compile(regexCL).matcher(sub);
				while (matcherCL.find()) {
					String num = matcherCL.group(1).trim();
					ArrayList<String> numRemoves = new ArrayList<>();

					String regexChanChan = ".*chan\\s*chan.*";
					boolean isSyntaxChanChan = num.matches(regexChanChan);
					if(isSyntaxChanChan){
						numRemoves.addAll(ChanLe.layChanChan());
					}

					String regexLeLe = ".*le\\s*le.*";
					boolean isSyntaxLeLe = num.matches(regexLeLe);
					if(isSyntaxLeLe){
						numRemoves.addAll(ChanLe.layLeLe());
					}

					String regexchanLe = ".*chan\\s*le.*";
					boolean isSyntaxchanLe = num.matches(regexchanLe);
					if(isSyntaxchanLe){
						numRemoves.addAll(ChanLe.layChanLe());
					}

					String regexLeChan = ".*le\\s*chan.*";
					boolean isSyntaxLeChan= num.matches(regexLeChan);
					if(isSyntaxLeChan){
						numRemoves.addAll(ChanLe.layLeChan());
					}

					for (String remove : numRemoves)
					{
						RemoveObject obj = new RemoveObject();
						obj.Number = remove.trim();
						obj.Type = "chanle";
						numbers.add(obj);
					}
				}
				newSub = newSub.replaceAll(regexCL, "");
			}

			if (sub.matches(regxToAndBe)) {
				String regexCL = "((to|nho|be)\\s*(to|nho|be))";
				Matcher matcherCL = Pattern.compile(regexCL).matcher(sub);
				while (matcherCL.find()) {
					String num = matcherCL.group(1).trim();
					ArrayList<String> numRemoves = new ArrayList<>();

					String regexNhoNho = ".*(nho|be)\\s*(nho|be).*";
					boolean isSyntaxNhoNho = num.matches(regexNhoNho);
					if(isSyntaxNhoNho){
						numRemoves.addAll(ToNho.layNhoNho());
					}

					String regexToTo = ".*to\\s*to.*";
					boolean isSyntaxToTo = num.matches(regexToTo);
					if(isSyntaxToTo){
						numRemoves.addAll(ToNho.layToTo());
					}

					String regexNhoTo = ".*(nho|be)\\s*to.*";
					boolean isSyntaxNhoTo = num.matches(regexNhoTo);
					if(isSyntaxNhoTo){
						numRemoves.addAll(ToNho.layNhoTo());
					}

					String regexToNho = ".*to\\s*(nho|be).*";
					boolean isSyntaxToNho = num.matches(regexToNho);
					if(isSyntaxToNho){
						numRemoves.addAll(ToNho.layToNho());
					}

					for (String remove : numRemoves)
					{
						RemoveObject obj = new RemoveObject();
						obj.Number = remove.trim();
						obj.Type = "chanle";
						numbers.add(obj);
					}
				}
				newSub = newSub.replaceAll(regexCL, "");
			}

			if (sub.matches(regxToNho)) {
				String reg = "dau\\s*(nho|to|be)\\s*ghep\\s*dit\\s*(nho|to|be)";
				Pattern pattern = Pattern.compile(reg);
				Matcher matcher = pattern.matcher(sub);
				while (matcher.find()) {
					String type1 = matcher.group(1);
					String type2 = matcher.group(2);
					ArrayList<String> numRemoves = null;

					if (type1.contains("to"))
					{
						if (type2.contains("to"))
						{
							numRemoves = ToNho.layToTo();
						}
						else
						{
							numRemoves = ToNho.layToNho();
						}
					}
					else
					{
						if (type2.contains("to"))
						{
							numRemoves = ToNho.layNhoTo();
						}
						else
						{
							numRemoves = ToNho.layNhoNho();
						}
					}

					if (numRemoves != null)
					{
						for (String remove : numRemoves)
						{
							RemoveObject obj = new RemoveObject();
							obj.Number = remove.trim();
							obj.Type = "tonho";
							numbers.add(obj);
						}
					}
				}

				newSub = newSub.replaceAll(reg, "");
			}

			if (sub.matches(regxChanLe)) {
				String reg = "dau\\s*(chan|le)\\s*ghep\\s*dit\\s*(chan|le)";
				Pattern pattern = Pattern.compile(reg);
				Matcher matcher = pattern.matcher(sub);
				while (matcher.find()) {
					String type1 = matcher.group(1);
					String type2 = matcher.group(2);
					ArrayList<String> numRemoves = null;

					if (type1.contains("chan"))
					{
						if (type2.contains("chan"))
						{
							numRemoves = ChanLe.layChanChan();
						}
						else
						{
							numRemoves = ChanLe.layChanLe();
						}
					}
					else
					{
						if (type2.contains("chan"))
						{
							numRemoves = ChanLe.layLeChan();
						}
						else
						{
							numRemoves = ChanLe.layLeLe();
						}
					}

					if (numRemoves != null)
					{
						for (String remove : numRemoves)
						{
							RemoveObject obj = new RemoveObject();
							obj.Number = remove.trim();
							obj.Type = "chanle";
							numbers.add(obj);
						}
					}
				}

				newSub = newSub.replaceAll(reg, "");
			}

			if (newSub.equals(""))
				continue;
			String[] _words = newSub.split("\\W+");
			for (String word : _words) {
				if (!word.trim().matches("\\W*") && isInteger(word.trim())) {
					if (word.trim().length() == 2) {
						RemoveObject obj = new RemoveObject();
						obj.Number = word.trim();
						obj.Type = "sole";
						numbers.add(obj);
					} else if (word.trim().length() == 3) {
						RemoveObject obj = new RemoveObject();
						obj.Number = word.trim().substring(0, 2);
						obj.Type = "sole";
						numbers.add(obj);
						RemoveObject obj2 = new RemoveObject();
						obj2.Number = word.trim().substring(1, word.trim().length());
						obj2.Type = "sole";
						numbers.add(obj2);
					} else {
						ArrayList<String> splitNumberSize = splitEqually(word.trim(), 2);
						for (String numSize : splitNumberSize) {
							RemoveObject obj = new RemoveObject();
							obj.Number = numSize;
							obj.Type = "sole";
							numbers.add(obj);
						}
					}
				} else {

				}
			}

		}
		return numbers;
	}
	

	public static ExtractObject extractNumberAndUnitPrice(String syntax, String type) {
		String tempSyntax = getRootSyntaxString(syntax);

		String regNumber = /*"([0-9]+.*)x([0-9]+\\s*[knd]{1})"*/"([0-9]{2,3})";
		Pattern patternNumber = Pattern.compile(regNumber);
		Matcher matcherNumber = patternNumber.matcher(tempSyntax);
		ExtractObject extractObject = extractUnitPrice(syntax);
		extractObject.Type = type;
		extractObject.Numbers = new ArrayList<>();
		while (matcherNumber.find()) {
			String numbers = matcherNumber.group(1);
			if (numbers != null)
			{
				extractObject.Numbers.add(numbers.trim());
			}
		}

		return extractObject;
	}
	
	public static ExtractObject extractUnitPrice(String syntax) {
		String regNumber = "x\\s*([0-9]+\\s*[kd]{1})";
		Pattern patternNumber = Pattern.compile(regNumber);
		Matcher matcherNumber = patternNumber.matcher(syntax);
		ExtractObject extractObject = new ExtractObject();
		while (matcherNumber.find()) {
			String unitAndMoney = matcherNumber.group(1);
			extractObject.Money = unitAndMoney.substring(0, unitAndMoney.length() - 1);
			extractObject.Unit = unitAndMoney.substring(unitAndMoney.length() - 1, unitAndMoney.length());
		}
		extractObject.Syntax = syntax;
		return extractObject;
	}

	public static int countMatches(Pattern pattern, String string) {
		Matcher matcher = pattern.matcher(string);

		int count = 0;
		int pos = 0;
		while (matcher.find(pos)) {
			count++;
			pos = matcher.start() + 1;
		}

		return count;
	}

	public static ArrayList<String> splitEqually(String text, int size) {
		// Give the list the right capacity to start with. You could use an
		// array
		// instead if you wanted.
		ArrayList<String> ret = new ArrayList<String>((text.length() + size - 1) / size);

		for (int start = 0; start < text.length(); start += size) {
			ret.add(text.substring(start, Math.min(text.length(), start + size)));
		}
		return ret;
	}
	
	public static boolean isInteger(String str) {
    	String newString = str.substring(0, 1);
        int size = newString.length();

        for (int i = 0; i < size; i++) {
            if (!Character.isDigit(newString.charAt(i))) {
                return false;
            }
        }

        return size > 0;
    }
	
	public static ArrayList<String> getNumber(String line) {
		String regex = "(\\d+)";
		ArrayList<String> numbers = new ArrayList<>();
		Matcher matcher = Pattern.compile(regex).matcher(line);
		while (matcher.find()) {
			String num = matcher.group();
			numbers.add(num);
		}
		return numbers;
	}
	
	public static ArrayList<String> getNumber(String line, String firtLetter) {
		String regex = firtLetter+"(\\d+).*";
		ArrayList<String> numbers = new ArrayList<>();
		Matcher matcher = Pattern.compile(regex).matcher(line);
		while (matcher.find()) {
			String num = matcher.group();
			numbers.add(num);
		}
		return numbers;
	}
	
	public static ArrayList<String> getTypeTotal(String syntax) {
		String regex = ".*tong\\s*(([0-9][\\s.,]*)+)\\s*x[0-9]+.*";
		ArrayList<String> numbers = new ArrayList<>();
		Matcher matcher = Pattern.compile(regex).matcher(syntax);
		while (matcher.find()) {
			String nums = matcher.group(1);
			if (nums != null)
			{
				nums = nums.replaceAll(Constant.REGEX_SEPARATE, "");
				nums = nums.replaceAll(Constant.REGEX_SEPARATE_2, "");

				String[] n = nums.split("");
				for (String num : n)
				{
					if (num.matches("[0-9]"))
					{
						numbers.add(num);
					}
				}
			}
		}

		return numbers;
	}

	//Just use for single syntax.
	public static String getRootSyntaxString(String syntax) {
		int index = syntax.lastIndexOf("x");

		String tempSyntax = syntax;

		if (index > 0)
		{
			tempSyntax = syntax.substring(0, index);
		}

		if (tempSyntax.contains("bor"))
		{
			tempSyntax = tempSyntax.substring(0, tempSyntax.indexOf("bor"));
		}

		return tempSyntax;
	}

	public static String getRootSyntaxWithRemove(String syntax) {
		int index = syntax.lastIndexOf("x");

		String tempSyntax = syntax;

		if (index > 0)
		{
			tempSyntax = syntax.substring(0, index);
		}

		return tempSyntax;
	}

	public static String getRootSyntaxWithCost(String syntax) {
		int index = syntax.lastIndexOf("x");

		String tempSyntax = syntax;

		String cost = "";

		if (index > 0)
		{
			tempSyntax = syntax.substring(0, index);
			cost = syntax.substring(index);
		}

		if (tempSyntax.contains("bor"))
		{
			tempSyntax = tempSyntax.substring(0, tempSyntax.indexOf("bor"));
		}

		return tempSyntax + cost;
	}

	public static String getCost(String syntax) {
		int index = syntax.lastIndexOf("x");

		String tempSyntax = syntax;

		if (index > 0)
		{
			tempSyntax = syntax.substring(index);
			return tempSyntax;
		}

		return tempSyntax;
	}

	public static String getRemoveSyntaxString(String syntax) {
		int index = syntax.lastIndexOf("x");

		String tempSyntax = syntax;

		if (index > 0)
		{
			tempSyntax = syntax.substring(0, index);
		}

		if (tempSyntax.contains("bor"))
		{
			tempSyntax = tempSyntax.substring(tempSyntax.indexOf("bor"));
			return tempSyntax;
		}
		else
		{
			return "";
		}
	}

	public static String getRemoveSyntaxWithCost(String syntax) {
		String tempSyntax = syntax;

		if (tempSyntax.contains("bor"))
		{
			tempSyntax = tempSyntax.substring(tempSyntax.indexOf("bor"));
			return tempSyntax;
		}
		else
		{
			int index = tempSyntax.lastIndexOf("x");

			if (index > 0)
			{
				tempSyntax = tempSyntax.substring(index);
			}

			return tempSyntax;
		}
	}

	public static ArrayList<String> sortNumbers(ArrayList<String> numbers) {
		Collections.sort(numbers, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				try {
					int i1 = Integer.parseInt(o1);
					int i2 = Integer.parseInt(o2);

					if (i1 > i2)
					{
						return 1;
					}
					else if (i1 < i2)
					{
						return -1;
					}
					else
					{
						return 0;
					}
				} catch (NumberFormatException e) {
					return 0;
				}
			}
		});

		return numbers;
	}

	public static String[] sortNumbers(String[] numbers) {
		Arrays.sort(numbers, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				try {
					int i1 = Integer.parseInt(o1);
					int i2 = Integer.parseInt(o2);

					if (i1 > i2)
					{
						return 1;
					}
					else if (i1 < i2)
					{
						return -1;
					}
					else
					{
						return 0;
					}
				} catch (NumberFormatException e) {
					return 0;
				}
			}
		});

		return numbers;
	}

	public static int countSubString(String str, String findStr) {
		int lastIndex = 0;
		int count = 0;

		while(lastIndex != -1){

			lastIndex = str.indexOf(findStr,lastIndex);

			if(lastIndex != -1){
				count ++;
				lastIndex += findStr.length();
			}
		}

		return count;
	}
}
