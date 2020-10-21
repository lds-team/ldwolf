package com.bingo.common.utils;

import android.text.TextUtils;

import com.bingo.analyze.ExtractObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Xien {
	public static ExtractObject getXien(String syntax) {
		ExtractObject extractObject = null;
		boolean isSyntax = syntax.matches(".*(xien|lo\\s*xien|xien\\s*ghep|xien\\s*quay|xq|xg)[\\s:]*[0-9\\s\\.\\,\\/\\-\\;]+\\s*x[0-9]+\\s*(k|d)");
		boolean isSyntax2 = syntax.matches(".*(xien|lo\\s*xien)[\\s:]*[2-4]\\W+[0-9\\W]+\\s*x[0-9]+\\s*(k|d)");

		if (isSyntax2)
		{
			extractObject = Utils.extractUnitPrice(syntax);
			extractObject.Numbers = new ArrayList<>();

			String _tmpSyntax = syntax.replaceAll("(xien quay|xq)[\\W]*", "");
			_tmpSyntax = _tmpSyntax.replaceFirst("xien[\\s.:]*[0-9]?\\W+", "").trim();
			_tmpSyntax = _tmpSyntax.replaceFirst("(xien ghep|xg)[\\s.:]*[0-9]?\\W+", "").trim();
			_tmpSyntax = _tmpSyntax.substring(0, _tmpSyntax.indexOf("x")).trim();

			String[] numsArray = _tmpSyntax.split("\\W+");
			for (String num : numsArray)
			{
				if (num.isEmpty()) continue;
				if (num.length() == 2 && num.matches("[0-9][0-9]"))
				{
					extractObject.Numbers.add(num);
				}
				else if (num.length() == 3 && num.matches("[0-9][0-9][0-9]"))
				{
					String num1 = num.substring(0, 2);
					extractObject.Numbers.add(num1);

					String num2 = num.substring(1, num.length());
					extractObject.Numbers.add(num2);
				}
				else
				{
					extractObject.Numbers.clear();
					return null;
				}
			}

			boolean isXien2 = syntax.matches(".*(xien|lo\\s*xien)[\\s:]*2\\W+[0-9\\W]+\\s*x[0-9]+\\s*(k|d)");
			boolean isXien3 = syntax.matches(".*(xien|lo\\s*xien)[\\s:]*3\\W+[0-9\\W]+\\s*x[0-9]+\\s*(k|d)");
			boolean isXien4 = syntax.matches(".*(xien|lo\\s*xien)[\\s:]*4\\W+[0-9\\W]+\\s*x[0-9]+\\s*(k|d)");

			int jump = isXien2 ? 2 : isXien3 ? 3 : 4;
			int size = extractObject.Numbers.size();
			ArrayList<String> numbers = new ArrayList<>();
			for (int i = 0; i < size; i += jump)
			{
				String[] xien = new String[jump];
				for (int j = i; j < i + jump; j++)
				{
					xien[j - i] = extractObject.Numbers.get(j);
				}

				numbers.add(ArrayUtils.join(xien, "-"));
			}

			extractObject.Numbers.clear();
			extractObject.Numbers = numbers;

			extractObject.Type = "xien";
		}
		else if (isSyntax)
		{
			extractObject = Utils.extractUnitPrice(syntax);
			extractObject.Numbers = new ArrayList<>();

			String regexXien3 = "([0-9][0-9][0-9])";
			String _tmpSyntax = syntax.replaceAll("(xien quay|xq)[\\W]*", "");
			_tmpSyntax = _tmpSyntax.replaceFirst("xien[\\s.:]*[0-9]?\\W+", "").trim();
			_tmpSyntax = _tmpSyntax.replaceFirst("(xien ghep|xg)[\\s.:]*[0-9]?\\W+", "").trim();
			_tmpSyntax = _tmpSyntax.substring(0, _tmpSyntax.indexOf("x"));
			Matcher matcherXien3 = Pattern.compile(regexXien3).matcher(_tmpSyntax);
			while (matcherXien3.find()) {
				String num = matcherXien3.group(1).trim();

				String num1 = num.substring(0, 2);
				extractObject.Numbers.add(num1);

				String num2 = num.substring(1, num.length());
				extractObject.Numbers.add(num2);

				_tmpSyntax = _tmpSyntax.replaceAll(num, "");
			}

			String regexXien2 = "([0-9][0-9])";
			Matcher matcherXien2 = Pattern.compile(regexXien2).matcher(_tmpSyntax);
			while (matcherXien2.find()) {
				String num = matcherXien2.group(1).trim();

				extractObject.Numbers.add(num);

				_tmpSyntax = _tmpSyntax.replaceAll(num, "");
			}

			//Sort number from small to large.
			extractObject.Numbers = Utils.sortNumbers(extractObject.Numbers);

			//----------------
			//TODO add function to analyze xien. Stupid for if:
			if (syntax.matches(".*(xien\\s*quay|xq)\\s*[0-9\\s\\.\\,\\/\\-\\;]+\\s*x[0-9]+\\s*(k|d)"))
			{
				String regexDefind = ".*(xien\\s*quay|xq)\\s*([2-4])[\\W].*";
				if (/*syntax.matches(regexDefind)*/false)
				{	//TODO disable this case for version 2.
					String temp = syntax.replaceFirst(".*(xien\\s*quay|xq)\\W*", "");
					int xqType = Integer.parseInt(temp.substring(0, 1));

					if (extractObject.Numbers.size() >= xqType)
					{
						ArrayList<String> cache = new ArrayList<>(extractObject.Numbers);
						ArrayList<String> tempList = new ArrayList<>();
						extractObject.Numbers.clear();

						int sizeArr = cache.size();
						for (int i = 0; i < sizeArr; i++ )
						{
							tempList.add(cache.get(i));
							if (tempList.size() == xqType)
							{
								extractObject.Numbers.addAll(extractNumber(tempList.toArray(new String[xqType])));
								tempList.clear();
							}
							else if (i == sizeArr - 1 && tempList.size() < xqType)
							{
								extractObject.Numbers.add(ArrayUtils.join(tempList.toArray(new String[tempList.size()]), "-"));
							}
						}
					}
					else
					{

					}
				}
				else
				{
					buildNumberXienQuay(extractObject, syntax);
				}
			}
			else if (syntax.matches(".*(xien\\s*ghep|xg)\\s*[0-9]\\s[0-9\\s\\.\\,\\/\\-\\;]+\\s*x[0-9]+\\s*(k|d)"))
			{
				//Xien ghep.
				ArrayList<String> xienGhep = new ArrayList<>(extractObject.Numbers);
				extractObject.Numbers.clear();

				int size = xienGhep.size();
				for (int i = 0; i < size; i++)
				{
					for (int j = i; j < size; j++)
					{
						String num1 = xienGhep.get(i).trim();
						String num2 = xienGhep.get(j).trim();
						if (!num1.equals(num2))
						{
							extractObject.Numbers.add(num1 + "-" + num2);
						}
					}
				}
			}
			else
			{
				String tempSyntax = Utils.getRootSyntaxString(syntax).trim();
				tempSyntax = tempSyntax.replaceFirst("xien\\s*[0-9]?\\W+", "").trim();

				tempSyntax = tempSyntax.replaceAll("\\s*(\\W)\\s*", "$1");
				tempSyntax = tempSyntax.replaceAll("\\s+", " ");

				if (tempSyntax.contains(".") && tempSyntax.contains("-"))
				{
					tempSyntax = tempSyntax.replaceAll("\\.", "\\,");
				}
				else if (tempSyntax.contains(".") && tempSyntax.contains(","))
				{
					tempSyntax = tempSyntax.replaceAll("\\.", "\\-");
				}
				else if (tempSyntax.contains(".") && tempSyntax.contains(";"))
				{
					tempSyntax = tempSyntax.replaceAll("\\.", "\\-");
				}
				else
				{
					tempSyntax = tempSyntax.replaceAll("\\.", "\\,");
				}

				String numberSepa = null;
				String arrSepa = null;

				int length = tempSyntax.length();
				for (int i = 0; i < length; i++)
				{
					String sepa = tempSyntax.substring(i, i + 1);
					if (numberSepa == null && sepa.matches("\\W"))
						numberSepa = sepa;

					if (arrSepa == null && numberSepa != null && sepa.matches("\\W") && !sepa.matches(numberSepa))
						arrSepa = sepa;

					if (numberSepa != null && arrSepa != null)
						break;
				}

				if (numberSepa != null && arrSepa != null)
				{
					extractObject.Numbers.clear();

					boolean trueSepa = true;

					String temp = extractTripleNumber(tempSyntax, numberSepa);

					String[] tempArr = temp.split(arrSepa);

					for (String _tmp : tempArr)
					{
						int len = _tmp.length();
						if (len < 5 || len > 11)
						{
							trueSepa = false;
							break;
						}
					}

					if (!trueSepa)
					{
						String s = arrSepa;
						arrSepa = numberSepa;
						numberSepa = s;

						tempSyntax = extractTripleNumber(tempSyntax, numberSepa);
					}
					else
					{
						tempSyntax = temp;
					}

					String[] arrNums = tempSyntax.split(arrSepa);

					for (String arrNum : arrNums)
					{
						extractObject.Numbers.add(ArrayUtils.join(arrNum.split(numberSepa), "-"));
					}
				}
				else
				{
					//TODO
					ArrayList<String> xien = new ArrayList<>(extractObject.Numbers);

					extractObject.Numbers.clear();
					extractObject.Numbers.add(ArrayUtils.join(xien.toArray(new String[xien.size()]), "-"));
				}
			}

			extractObject.Type = "xien";
		}
		
		return extractObject;
	}

	private static ArrayList<String> extractNumber(String[] nums) {
		ArrayList<String> numbers = new ArrayList<>();

		Utils.sortNumbers(nums);

		if (nums.length == 4)
		{
			// lấy 4 cặp này ghép xiên 2 sẽ được 6 cặp xiên
			String xien2_1 = nums[0].trim() + "-" + nums[1].trim();
			String xien2_2 = nums[0].trim() + "-" + nums[2].trim();
			String xien2_3 = nums[0].trim() + "-" + nums[3].trim();
			String xien2_4 = nums[1].trim() + "-" + nums[2].trim();
			String xien2_5 = nums[1].trim() + "-" + nums[3].trim();
			String xien2_6 = nums[2].trim() + "-" + nums[3].trim();

			// và 4 cặp xiên 3
			String xien2_7 = nums[0].trim() + "-" + nums[1].trim() + "-"
					+ nums[2].trim();
			String xien2_8 = nums[0].trim() + "-" + nums[1].trim() + "-"
					+ nums[3].trim();
			String xien2_9 = nums[1].trim() + "-" + nums[2].trim() + "-"
					+ nums[3].trim();
			String xien2_10 = nums[0].trim() + "-" + nums[2].trim() + "-"
					+ nums[3].trim();

			// xiên 4
			numbers.add(ArrayUtils.join(nums, "-"));
			// lấy 4 cặp này ghép xiên 2 sẽ được 6 cặp xiên
			numbers.add(xien2_1);
			numbers.add(xien2_2);
			numbers.add(xien2_3);
			numbers.add(xien2_4);
			numbers.add(xien2_5);
			numbers.add(xien2_6);
			// và 4 cặp xiên 3
			numbers.add(xien2_7);
			numbers.add(xien2_8);
			numbers.add(xien2_9);
			numbers.add(xien2_10);
		}
		else if (nums.length == 3)
		{
			String xien2_1 = nums[0].trim() + "-" + nums[1].trim();
			String xien2_2 = nums[0].trim() + "-" + nums[2].trim();
			String xien2_3 = nums[1].trim() + "-" + nums[2].trim();

			numbers.add(ArrayUtils.join(nums, "-"));

			numbers.add(xien2_1);
			numbers.add(xien2_2);
			numbers.add(xien2_3);
		}
		else if (nums.length == 2)
		{
			String xien2_1 = nums[0].trim() + "-" + nums[1].trim();

			numbers.add(xien2_1);
		}

		return Utils.sortNumbers(numbers);
	}

	private static String extractTripleNumber(String numbersString, String sepa) {
		String regexXien3 = "([0-9][0-9][0-9])";
		Matcher matcherXien3 = Pattern.compile(regexXien3).matcher(numbersString);
		while (matcherXien3.find()) {
			String num = matcherXien3.group(1).trim();

			String num1 = num.substring(0, 2);
			String num2 = num.substring(1, num.length());

			numbersString = numbersString.replaceAll(num, num1 + sepa + num2);
		}

		return numbersString;
	}

	private static void buildNumberXienQuay(ExtractObject extractObject, String syntax) {
		if (extractObject.Numbers.size() == 4)
		{
			// lấy 4 cặp này ghép xiên 2 sẽ được 6 cặp xiên
			String xien2_1 = extractObject.Numbers.get(0) + "-" + extractObject.Numbers.get(1);
			String xien2_2 = extractObject.Numbers.get(0) + "-" + extractObject.Numbers.get(2);
			String xien2_3 = extractObject.Numbers.get(0) + "-" + extractObject.Numbers.get(3);
			String xien2_4 = extractObject.Numbers.get(1) + "-" + extractObject.Numbers.get(2);
			String xien2_5 = extractObject.Numbers.get(1) + "-" + extractObject.Numbers.get(3);
			String xien2_6 = extractObject.Numbers.get(2) + "-" + extractObject.Numbers.get(3);

			// và 4 cặp xiên 3
			String xien2_7 = extractObject.Numbers.get(0) + "-" + extractObject.Numbers.get(1) + "-"
					+ extractObject.Numbers.get(2);
			String xien2_8 = extractObject.Numbers.get(0) + "-" + extractObject.Numbers.get(1) + "-"
					+ extractObject.Numbers.get(3);
			String xien2_9 = extractObject.Numbers.get(1) + "-" + extractObject.Numbers.get(2) + "-"
					+ extractObject.Numbers.get(3);
			String xien2_10 = extractObject.Numbers.get(0) + "-" + extractObject.Numbers.get(2) + "-"
					+ extractObject.Numbers.get(3);

			ArrayList<String> cache = new ArrayList<>(extractObject.Numbers);
			extractObject.Numbers.clear();
			// xiên 4
			extractObject.Numbers.add(ArrayUtils.join(cache.toArray(new String[cache.size()]), "-"));
			// lấy 4 cặp này ghép xiên 2 sẽ được 6 cặp xiên
			extractObject.Numbers.add(xien2_1);
			extractObject.Numbers.add(xien2_2);
			extractObject.Numbers.add(xien2_3);
			extractObject.Numbers.add(xien2_4);
			extractObject.Numbers.add(xien2_5);
			extractObject.Numbers.add(xien2_6);
			// và 4 cặp xiên 3
			extractObject.Numbers.add(xien2_7);
			extractObject.Numbers.add(xien2_8);
			extractObject.Numbers.add(xien2_9);
			extractObject.Numbers.add(xien2_10);
		}
		else if (extractObject.Numbers.size() == 3)
		{
			String xien2_1 = extractObject.Numbers.get(0) + "-" + extractObject.Numbers.get(1);
			String xien2_2 = extractObject.Numbers.get(0) + "-" + extractObject.Numbers.get(2);
			String xien2_3 = extractObject.Numbers.get(1) + "-" + extractObject.Numbers.get(2);

			ArrayList<String> xien3 = new ArrayList<>(extractObject.Numbers);

			extractObject.Numbers.clear();
			extractObject.Numbers.add(ArrayUtils.join(xien3.toArray(new String[xien3.size()]), "-"));

			extractObject.Numbers.add(xien2_1);
			extractObject.Numbers.add(xien2_2);
			extractObject.Numbers.add(xien2_3);
		}
		else if (extractObject.Numbers.size() == 2)
		{
			String xien2_1 = extractObject.Numbers.get(0) + "-" + extractObject.Numbers.get(1);

			extractObject.Numbers.clear();

			extractObject.Numbers.add(xien2_1);
		}
		else if (extractObject.Numbers.size() > 4)
		{
			String tempSyntax = Utils.getRootSyntaxString(syntax).trim();
			tempSyntax = tempSyntax.replaceFirst("xien\\s*quay\\W*", "").trim();

			tempSyntax = tempSyntax.replaceAll("\\s*(\\W)\\s*", "$1");
			tempSyntax = tempSyntax.replaceAll("\\s+", " ");

			if (tempSyntax.contains(".") && tempSyntax.contains("-"))
			{
				tempSyntax = tempSyntax.replaceAll("\\.", "\\,");
			}
			else if (tempSyntax.contains(".") && tempSyntax.contains(","))
			{
				tempSyntax = tempSyntax.replaceAll("\\.", "\\-");
			}
			else if (tempSyntax.contains(".") && tempSyntax.contains(";"))
			{
				tempSyntax = tempSyntax.replaceAll("\\.", "\\-");
			}
			else
			{
				tempSyntax = tempSyntax.replaceAll("\\.", "\\,");
			}

			String numberSepa = null;
			String arrSepa = null;

			int length = tempSyntax.length();
			for (int i = 0; i < length; i++)
			{
				String sepa = tempSyntax.substring(i, i + 1);
				if (numberSepa == null && sepa.matches("\\W"))
					numberSepa = sepa;

				if (arrSepa == null && numberSepa != null && sepa.matches("\\W") && !sepa.matches(numberSepa))
					arrSepa = sepa;

				if (numberSepa != null && arrSepa != null)
					break;
			}

			if (numberSepa != null && arrSepa != null)
			{
				extractObject.Numbers.clear();

				boolean trueSepa = true;

				String temp = extractTripleNumber(tempSyntax, numberSepa);

				String[] tempArr = temp.split(arrSepa);

				for (String _tmp : tempArr)
				{
					if (_tmp.length() < 5)
					{
						trueSepa = false;
						break;
					}
				}

				if (!trueSepa)
				{
					String s = arrSepa;
					arrSepa = numberSepa;
					numberSepa = s;

					tempSyntax = extractTripleNumber(tempSyntax, numberSepa);
				}
				else
				{
					tempSyntax = temp;
				}

				String[] arrNums = tempSyntax.split(arrSepa);

				for (String arrNum : arrNums)
				{
					if (!TextUtils.isEmpty(arrNum))
					{
						String[] nums = arrNum.trim().split(numberSepa);
						extractObject.Numbers.addAll(extractNumber(nums));
					}
				}
			}
			else
			{
				//TODO force add all number fail in syntax in order to user can see.
				ArrayList<String> xien = new ArrayList<>(extractObject.Numbers);

				extractObject.Numbers.clear();
				extractObject.Numbers.add(ArrayUtils.join(xien.toArray(new String[xien.size()]), "-"));
			}
		}
	}
}
