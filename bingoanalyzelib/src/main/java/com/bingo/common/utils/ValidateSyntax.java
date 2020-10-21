package com.bingo.common.utils;

import android.text.TextUtils;

import com.bingo.analyze.AnalyzeSMSNew;
import com.bingo.analyze.ResultObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateSyntax {
	public static ResultObject validate(String syntax){
		ResultObject resultObject = new ResultObject();

		String prefix = validatePrefix(syntax);

		if (syntax.isEmpty() || prefix == null)
		{
			resultObject.Error = true;
			resultObject.isErrorCost = true;
			resultObject.MsgError = syntax;
			resultObject.errNotify = "Lỗi thiếu tiền tố cú pháp.";
			return resultObject;
		}

		//Check simple syntax.
		if (syntax.matches(".*\\Wbo\\W.*"))
		{
			resultObject.Error = true;
			resultObject.isErrorCost = true;
			resultObject.MsgError = "bo ";
			resultObject.errNotify = "Lỗi: bo";
			return resultObject;
		}

		//Check unit syntax.
		boolean isRightSyntax = syntax.trim().matches(".*x[0-9]+\\s*k|.*x[0-9]+\\s*d");

		resultObject.Error = /*!isRightSyntax*/true; //Default error = true, if syntax match regex below, error will change to false.
		if(!isRightSyntax)
		{
			resultObject.isErrorCost = true;
			resultObject.MsgError = Utils.getCost(syntax);
			if (syntax.equals(resultObject.MsgError) || resultObject.MsgError.trim().isEmpty()
					|| syntax.matches("(.*x[0-9]+\\s*k|.*x[0-9]+\\s*d)\\W*[\\S]+.*"))
			{
				int length = syntax.length();
				for (int i = length; i > 0; i--)
				{
					if (syntax.substring(i - 1, i).matches("\\W") && (length - i > 1))
					{
						resultObject.MsgError = syntax.substring(i - 1, length);
						break;
					}
				}
			}
			resultObject.errNotify = "Lỗi đơn vị cú pháp.";
			return resultObject;
		}
		else if (syntax.trim().matches(".*tr[^uoe].*"))
		{
			resultObject.isErrorCost = true;
			resultObject.MsgError = "tr";
			resultObject.errNotify = "Lỗi đơn vị cú pháp: tr.";
			return resultObject;
		}
		else if (syntax.trim().matches(".*(x[0-9]+\\s*k|.*x[0-9]+\\s*d)\\W*(x[0-9]+\\s*k|.*x[0-9]+\\s*d)"))
		{
			resultObject.isErrorCost = true;
			resultObject.MsgError = syntax.trim().substring(syntax.trim().lastIndexOf("x") + 1);
			resultObject.errNotify = "Lỗi đơn vị cú pháp: " + syntax;
			return resultObject;
		}

		boolean hasBor = false;
		boolean hasTrung = false;

		String removeSyntax = Utils.getRemoveSyntaxString(syntax);
		if (!TextUtils.isEmpty(removeSyntax))
		{
			hasBor = true;
//			String[] remove = removeSyntax.split("\\W*bor\\W*");
//
//			for (String rm : remove)
//			{
//				if (!rm.isEmpty())
//				{
//					boolean match = rm.trim().matches("(([0-9]{2,3}([-.,\\s]+))*[0-9]{2,3}[-.,\\s]*)" +
//							"|(dau\\s*dit\\s*(([0-9][\\W]*)+))|(dau\\s*(([0-9][\\W]*)+))|(dit\\s*(([0-9][\\W]*)+))|(dau\\s*(([0-9][\\W]*)+))(dit\\s*(([0-9][\\W]*)+))" +
//							"|(tong\\s*(([0-9][\\W]*)+))" +
//							"|(boj\\s*([0-9][0-9]([,.\\s]*))+)" +
//							"|(dau\\s*(to|nho)\\W*|dit\\s*(to|nho)\\W*)|(dau\\s*(nho|to|be|chan|le)\\s*ghep\\s*dit\\s*(nho|to|be|chan|le))" +
//							"|(dau\\s*dit\\s*(to|nho|chan|le)\\W*)" +
//							"|(dau\\s*(chan|le)\\W*|dit\\s*(chan|le)\\W*)|(tong\\s*chan\\W*)|(tong\\s*le\\W*)" +
//							"|(kep\\s*bang\\W*)|(kep\\s*lech\\W*)|(sat\\s*kep\\W*)" +
//							"|(tong\\s*chia\\s*[ba3]+\\W*)|(tong\\s*chia\\s*[ba3\\+]+\\s*du\\s*[1mot]+\\W*)|(tong\\s*chia\\s*[ba3\\+]+\\s*du\\s*[2hai]+\\W*)" +
//							"|(trung|trong)|(cham\\s*([0-9][\\s.,]*)+\\W*)|(dan\\s*([0-9]{2}[\\W]*)+)|((\\s*(chan|le)\\s*(chan|le)\\s*)+)|((\\s*(to|be)\\s*(to|be)\\s*)+)" +
//							"|(tong\\s*(tren|duoi)\\s*10)");
//
//					if (match)
//					{
//						if (rm.contains("cham") || rm.contains("tong") || rm.contains("dau") || rm.contains("dit"))
//						{
//							match = validateMistakeSyntax(rm);
//						}
//
//						if (rm.contains("boj") || rm.contains("dan"))
//						{
//							match = validateMistakeSyntax2Char(rm);
//						}
//
//						/*if (rm.contains("trung") || rm.contains("trong"))
//						{
//							match = !rm.matches(".*[\\w]+.*(trung|trong).*");
//							resultObject.errNotify = "Không hỗ trợ cú pháp trùng sau cú pháp bor.";
//						}*/
//					}
//
//					if (!match)
//					{
//						resultObject.isErrorCost = true;
//						resultObject.MsgError = formatToShortSyntax(rm);
//						resultObject.errNotify = resultObject.errNotify == null ? "Lỗi: " + rm : resultObject.errNotify;
//						return resultObject;
//					}
//				}
//			}
		}

		//------------For check case number 3 chars.
		String mainSyntax = Utils.getRootSyntaxWithRemove(syntax).trim();
		String cost = Utils.getCost(syntax).trim();
		//------------

		resultObject.isErrorCost = false;

		if(syntax.contains("trung") || syntax.contains("trong")){
			hasTrung = true;

			isRightSyntax = syntax.matches("(de|lo)[\\s:]*.*(trung|trong).*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)");
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax;
				return resultObject;
			} /*else if ((syntax.contains("trung") && syntax.contains("trong"))
					|| (syntax.indexOf("trung") != syntax.lastIndexOf("trung"))
					|| (syntax.indexOf("trong") != syntax.lastIndexOf("trong"))) {
				resultObject.Error = true;
				int lastIndex = syntax.lastIndexOf("trung") != -1 ? syntax.lastIndexOf("trung") : syntax.lastIndexOf("trong");
				resultObject.MsgError = syntax.substring(lastIndex);
				return resultObject;
			}*/ else if (syntax.contains("de") && syntax.contains("lo")) {
				resultObject.Error = true;
				resultObject.MsgError = syntax;
				return resultObject;
			} else if (syntax.contains("xien") || syntax.contains("xq") || syntax.contains("xg")) {
				resultObject.Error = true;
				resultObject.MsgError = syntax;
				return resultObject;
			}
		}

		if (syntax.contains("bor"))
		{
			if (syntax.contains("xien") || syntax.contains("xq") || syntax.contains("xg")) {
				resultObject.Error = true;
				resultObject.MsgError = Utils.getRemoveSyntaxString(syntax);
				return resultObject;
			}
		}

		String regex = hasBor ? "bor" : "";
		regex = hasTrung ? regex.isEmpty() ? regex.concat("trung|trong") : regex.concat("|trung|trong") : regex;

		if (hasBor || hasTrung)
		{
			String[] breakSyntax = mainSyntax.split("\\W*(" + regex + ")\\W*");
			for (String s : breakSyntax)
			{
				if (s.isEmpty()) continue;
				if (!s.contains(prefix))
				{
					s = prefix + " " + s;
				}

				if (!s.contains(cost))
				{
					s = s + cost;
				}

				resultObject = explainAndValidate(resultObject, s.trim(), mainSyntax, prefix, cost);
			}
		}
		else
		{
			resultObject = explainAndValidate(resultObject, syntax.trim(), mainSyntax, prefix, cost);
		}
		
		return resultObject;
	}

	private static ResultObject explainAndValidate(ResultObject resultObject, String s, String mainSyntax, String prefix, String cost) {
		AnalyzeSMSNew analyzeSMSNew = new AnalyzeSMSNew();
		String[] sExplain = analyzeSMSNew.rebuildShortSyntax(s, prefix).split("\\n");
		for (String sEx : sExplain)
		{
			if (sEx.isEmpty()) continue;
			if (!sEx.contains(prefix))
			{
				sEx = prefix + " " + sEx;
			}

			if (!sEx.contains(cost))
			{
				sEx = sEx + cost;
			}

			resultObject = validateNormalCase(sEx.trim(), mainSyntax, prefix, cost);
			if (resultObject.Error)
			{
				String error = resultObject.MsgError;
				error = error.replaceAll("x\\W*[0-9]+\\W*[knd]", "").replaceAll(prefix, "");
				resultObject.MsgError = error;
				return resultObject;
			}
		}

		return resultObject;
	}

	private static ResultObject validateNormalCase(String syntax, String mainSyntax, String prefix, String cost) {
		ResultObject resultObject = new ResultObject();
		boolean isRightSyntax;

		if(syntax.contains("lo") && syntax.contains("boj")){
			/*syntax = syntax.replaceAll("bo[\\s]*([0-9]+)", "boj $1");*/
			isRightSyntax = syntax.matches("lo\\s*boj\\s*([0-9][0-9]([,.\\s]*))+\\s*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)");
			isRightSyntax = isRightSyntax && validateMistakeSyntax2Char(syntax);
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax.replaceAll("(" + prefix + "|" + cost + ")", "");
				return resultObject;
			}
		}else if(syntax.contains("de") && syntax.contains("boj")){
			/*syntax = syntax.replaceAll("bo[\\s]*([0-9]+)", "boj $1");*/
			isRightSyntax = syntax.matches("de\\s*boj\\s*([0-9][0-9]([,.\\s]*))+\\s*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)");
			isRightSyntax = isRightSyntax && validateMistakeSyntax2Char(syntax);
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax.replaceAll("(" + prefix + "|" + cost + ")", "");
				return resultObject;
			}
		}else if(syntax.contains("kep") && syntax.contains("lech")){
			isRightSyntax = syntax.matches("\\W*(de|lo)[\\s:]*kep\\s*lech\\s*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)");
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax.replaceAll("(" + prefix + "|" + cost + ")", "");
				return resultObject;
			}
		}else if(syntax.contains("kep") && syntax.contains("sat")){
			isRightSyntax = syntax.matches("\\W*(de|lo)[\\s:]*sat\\s*kep\\s*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)");
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax.replaceAll("(" + prefix + "|" + cost + ")", "");
				return resultObject;
			}
		}else if(syntax.contains("kep") /*&& syntax.contains("bang")*/){
			isRightSyntax = syntax.matches("\\W*(de|lo)[\\s:]*kep\\s*(bang\\s*)*\\s*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)");
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = formatToShortSyntax(syntax);
				return resultObject;
			}
		}else if((syntax.contains("xien") && syntax.contains("ghep")) || syntax.contains("xg")){
			isRightSyntax = syntax.matches("\\W*(xien\\s*ghep|xg)\\s*[0-9][\\s:]+[0-9\\s\\.\\,\\/\\-\\;]+\\s*x[0-9]+\\s*(k|d)");
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax.replaceAll("(" + prefix + "|" + cost + ")", "");
				return resultObject;
			}
		}else if(syntax.contains("xien") || syntax.contains("xq")){

			boolean isXien2 = syntax.matches("xien\\W*(2|hai)\\W.*");
			boolean isXien3 = syntax.matches("xien\\W*(3|ba)\\W.*");
			boolean isXien4 = syntax.matches("xien\\W*(4|bon)\\W.*");
			boolean hasFormatXien = syntax.matches("xien\\W*[2-4]\\W.*");

			syntax = syntax.replaceAll("xien\\W*[2-4]\\W", "xien ");
			isRightSyntax = syntax.matches("\\W*(xien|lo\\s*xien|xien\\s*ghep|xien\\s*quay|xq)[\\s:]*[0-9]{2}+[0-9\\s\\.\\,\\/\\-\\;]*[0-9]+\\s*x[0-9]+\\s*(k|d)");

			if (!isRightSyntax)
			{
				if (syntax.matches(".*xien\\W*quay\\W*[0-9][^0-9].*"))
				{
					String regNumber = "(xien\\W*quay\\W*[0-9])";
					Pattern patternNumber = Pattern.compile(regNumber);
					Matcher matcherNumber = patternNumber.matcher(mainSyntax);
					while (matcherNumber.find()) {
						String err = matcherNumber.group(1);
						if (err != null)
						{
							resultObject.Error = true;
							resultObject.MsgError = err;
							return resultObject;
						}
					}
				}

				isRightSyntax = hasFormatXien && syntax.matches("\\W*(xien)[\\s:]*[0-9]{2}+[0-9\\W]*[0-9]+\\s*x[0-9]+\\s*(k|d)");
			}

			String tempSyntax = Utils.getRootSyntaxString(syntax).trim();
			tempSyntax = tempSyntax.replaceFirst("xien\\s*quay\\W*", "").trim();
			tempSyntax = tempSyntax.replaceFirst("xien\\s*ghep\\W*", "").trim();
			tempSyntax = tempSyntax.replaceFirst("xien\\s*[0-9]?\\W+", "").trim();

			if (isRightSyntax)
			{
				tempSyntax = tempSyntax.replaceAll("\\s*(\\W)\\s*", "$1");
				tempSyntax = tempSyntax.replaceAll("\\s+", " ");

				if (!hasFormatXien)
				{
					if (tempSyntax.matches(".*[0-9]{3}[0-9]+.*") || tempSyntax.matches(".*\\W[0-9]\\W.*") || tempSyntax.matches("^[0-9]\\W.*") || tempSyntax.matches(".*\\W[0-9]$"))
					{
						resultObject.Error = true;
						resultObject.MsgError = getErrorNumbers(tempSyntax, true);
						return resultObject;
					}

					if (tempSyntax.replaceAll("[0-9\\s\\.\\,\\/\\-\\;]", "").length() > 0)
					{
						isRightSyntax = false;
					}

					Set<String> listSepa = new LinkedHashSet<>();

					int length = tempSyntax.length();
					int lastSepa = 0;
					for (int i = 0; i < length; i++)
					{
						String sepa = tempSyntax.substring(i, i + 1);
						if (sepa.matches("\\W"))
						{
							if (i == lastSepa + 1)
							{
								resultObject.Error = true;
								resultObject.MsgError = tempSyntax.substring(lastSepa, i + 1);
								return resultObject;
							}
							listSepa.add(sepa);
							lastSepa = i;
						}
					}

					int sizeSepa = listSepa.size();
					if (sizeSepa > 2)
					{
						isRightSyntax = false;
					}

					if (tempSyntax.matches(".*[0-9]{3}.*") && tempSyntax.matches("\\W[0-9][0-9]\\W"))
					{
						if (sizeSepa > 1)
						{
							isRightSyntax = false;
						}
					}

					if (isRightSyntax)
					{
						String numErr = validateDigit3Chars(mainSyntax);
						if (numErr != null)
						{
							resultObject.Error = true;
							resultObject.isErrorCost = true;
							resultObject.MsgError = numErr;
							resultObject.errNotify = "Kiểm tra số: " + numErr;
							return resultObject;
						}

						if (sizeSepa == 2)
						{
							//Only number 2 character.
							String sp = (String) listSepa.toArray()[1];
							String[] syntaxBreak;
							if (sp.matches("\\."))
								syntaxBreak = tempSyntax.split("\\.");
							else
								syntaxBreak = tempSyntax.split(sp);

							for (String syn : syntaxBreak)
							{
								String sepa = (String) listSepa.toArray()[0];
								String[] numBreak;
								if (sepa.matches("\\."))
									numBreak = syn.split("\\.");
								else
									numBreak = syn.split(sepa);

								if (numBreak.length < 2)
								{
									boolean err = false;
									if (numBreak.length == 0)
									{
										err = true;
									}
									else
									{
										if (numBreak[0].trim().length() != 3)
										{
											err = true;
										}
									}

									if (err)
									{
										resultObject.Error = true;
										resultObject.MsgError = syn;
										resultObject.errNotify = "Lỗi xiên không đủ số.";
										return resultObject;
									}
								}

								if (numBreak.length > 4)
								{
									resultObject.Error = true;
									resultObject.MsgError = syn;
									resultObject.errNotify = "Thêm dấu , hoăc khoảng trắng vào giữa các xiên.";
									return resultObject;
								}

								for (String s : numBreak)
								{
									if (syn.indexOf(s) != syn.lastIndexOf(s))
									{
										resultObject.Error = true;
										resultObject.MsgError = syn;
										resultObject.errNotify = "Lỗi trùng số trong xiên";
										return resultObject;
									}
								}
							}
						}
						else if (sizeSepa == 1)
						{
							//Has number 3 character.
							String sepa = (String) listSepa.toArray()[0];
							String[] numBreak;
							if (sepa.matches("\\."))
								numBreak = tempSyntax.split("\\.");
							else
								numBreak = tempSyntax.split(sepa);

							if (tempSyntax.matches(".*[0-9]{3}.*"))
							{
								int maxNumCount = (tempSyntax.trim().matches("^\\W*[0-9]{2}\\W+.*")
										|| tempSyntax.trim().matches(".*\\W+[0-9]{2}\\W*$")
										|| tempSyntax.trim().matches(".*\\W+[0-9]{2}\\W+.*")) ? 3 : 2;

								if (numBreak.length > maxNumCount)
								{
									resultObject.Error = true;
									resultObject.MsgError = tempSyntax;
									resultObject.errNotify = "Lỗi thừa số trong xiên.";
									return resultObject;
								}

								if (maxNumCount == 3)
								{
									boolean[] checks = new boolean[3];
									checks[0] = tempSyntax.trim().matches("^\\W*[0-9]{3}.*");
									checks[1] = tempSyntax.trim().matches(".*[0-9]{3}$");
									checks[2] = tempSyntax.trim().matches("\\W+[0-9]{3}\\W+");
									if (checks.toString().indexOf("true") != checks.toString().lastIndexOf("true"))
									{
										resultObject.Error = true;
										resultObject.MsgError = tempSyntax;
										resultObject.errNotify = "Lỗi thừa số trong xiên.";
										return resultObject;
									}
								}
							}
							else
							{
								if (numBreak.length > 4)
								{
									resultObject.Error = true;
									resultObject.MsgError = tempSyntax;
									resultObject.errNotify = "Thêm dấu , hoăc khoảng trắng vào giữa các xiên.";
									return resultObject;
								}

								if (numBreak.length < 2)
								{
									resultObject.Error = true;
									resultObject.MsgError = tempSyntax;
									resultObject.errNotify = "Thêm dấu , hoăc khoảng trắng vào giữa các xiên.";
									return resultObject;
								}
							}

							for (String s : numBreak)
							{
								if (tempSyntax.indexOf(s) != tempSyntax.lastIndexOf(s))
								{
									resultObject.Error = true;
									resultObject.MsgError = tempSyntax;
									resultObject.errNotify = "Lỗi trùng số trong xiên";
									return resultObject;
								}
							}
						}
					}
				}
				else
				{
					if (tempSyntax.matches(".*[0-9]{3}[0-9]+.*") || tempSyntax.matches(".*\\W[0-9]\\W.*") || tempSyntax.matches("^[0-9]\\W.*") || tempSyntax.matches(".*\\W[0-9]$"))
					{
						resultObject.Error = true;
						resultObject.MsgError = getErrorNumbers(tempSyntax, true);
						return resultObject;
					}

					String numErr = validateDigit3Chars(mainSyntax);
					if (numErr != null)
					{
						resultObject.Error = true;
						resultObject.isErrorCost = true;
						resultObject.MsgError = numErr;
						resultObject.errNotify = "Kiểm tra số: " + numErr;
						return resultObject;
					}

					if (mainSyntax.matches(".*\\W[0-9]{3}\\W.*"))
					{
						String regexXien3 = "([0-9][0-9][0-9])";
						Matcher matcherXien3 = Pattern.compile(regexXien3).matcher(tempSyntax);
						while (matcherXien3.find()) {
							String num = matcherXien3.group(1).trim();

							String num1 = num.substring(0, 2);
							String num2 = num.substring(1, num.length());

							tempSyntax = tempSyntax.replaceAll(num, num1 + " " + num2);
						}
					}

					int numCount = tempSyntax.replaceAll("[^0-9]", "").length();
					isRightSyntax = isXien2 ? (numCount % 4 == 0) : isXien3 ? (numCount % 6 == 0) : isXien4 && (numCount % 8 == 0);

					if (!isRightSyntax)
					{
						resultObject.Error = true;
						resultObject.MsgError = tempSyntax;
						resultObject.errNotify = "Lỗi không đủ số trong xiên.";
						return resultObject;
					}
					else
					{
						int jump = isXien2 ? 2 : isXien3 ? 3 : 4;

						ArrayList<String> numbers = new ArrayList<>();

						String regexXien2 = "([0-9][0-9])";
						Matcher matcherXien2 = Pattern.compile(regexXien2).matcher(tempSyntax);
						while (matcherXien2.find()) {
							String num = matcherXien2.group(1).trim();
							numbers.add(num);
						}

						Set<String> checkDuplicate = new LinkedHashSet<>();
						int length = numbers.size();
						for (int i = 0; i < length; i += jump)
						{
							checkDuplicate.clear();
							for (int j = i; j < i + jump; j++)
							{
								checkDuplicate.add(numbers.get(j));
							}
							if (checkDuplicate.size() != jump)
							{
								resultObject.Error = true;
								resultObject.MsgError = tempSyntax;
								resultObject.errNotify = "Lỗi trùng số trong xiên";
								return resultObject;
							}
						}
					}
				}
			}

			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax.replaceAll("(" + prefix + "|" + cost + ")", "");
				return resultObject;
			}
		}else if(syntax.contains("dan")){
			isRightSyntax = syntax.matches("\\W*(de|lo)[\\s:]*dan[\\s:.]*([0-9]{2}[\\W]*)+\\s*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)");
			isRightSyntax = isRightSyntax && validateMistakeSyntax2Char(syntax);
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax.replaceAll("(" + prefix + "|" + cost + ")", "");
				return resultObject;
			}
		}else if(syntax.contains("cham")){
			isRightSyntax = syntax.matches("\\W*(de|lo)[\\s:]*cham[\\s:.]*([0-9][\\s.,]*)+(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)");
			isRightSyntax = isRightSyntax && validateMistakeSyntax(syntax);
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax.replaceAll("(" + prefix + "|" + cost + ")", "");
				return resultObject;
			}
		}else if(syntax.contains("ghep")){
			isRightSyntax = syntax.matches("\\W*(de|lo)[\\s:]*dau\\s*([0-9]+|be|to|le|chan)\\s*ghep\\s*[a-zA-Z]*\\s*(dit)\\s*([0-9|be|to|le|chan]+)\\s*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)");
			isRightSyntax = isRightSyntax && validateMistakeSyntax(syntax);
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax.replaceAll("(" + prefix + "|" + cost + ")", "");
				return resultObject;
			}
		}else if((syntax.contains("dau") || syntax.contains("dit"))){
			isRightSyntax = syntax.matches("\\W*(de|lo)[\\s:]*((dau|dit)\\W*(dau|dit)*[\\s:]*[0-9]+[0-9\\W]*)+\\W*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)")
					|| syntax.matches("\\W*(de|lo)[\\s:]*((dau|dit)[\\W]*[0-9]+[0-9\\W]*\\W*(dau|dit)*[\\W]*[0-9]+[0-9\\W]*)+\\W*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)")
					|| syntax.matches("\\W*(de|lo)[\\s:]*(dau)\\W*(to|be|nho|lon)\\W*(hon)\\W*(dit)\\W*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)")
					|| syntax.matches("\\W*(de|lo)[\\s:]*(dit)\\W*(to|be|nho|lon)\\W*(hon)\\W*(dau)\\W*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)")
					|| syntax.matches("\\W*(de|lo)[\\s:]*(dau)\\W*(to|be)\\W*(dit)\\W*(to|be)\\W*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)")
					|| syntax.matches("\\W*(de|lo)[\\s:]*((dau|dit)[\\s:]*(to|be)\\W*)+\\W*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)")
					|| syntax.matches("\\W*(de|lo)[\\s:]*((dau|dit)[\\s:]*(chan|le)\\W*)+\\W*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)");
			isRightSyntax = isRightSyntax && validateMistakeSyntax(syntax);
			if (isRightSyntax)
			{
				isRightSyntax = !syntax.matches("^\\W*(de|lo|xien|bacang)\\W*dit\\W*dau.*");
			}
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax.replaceAll("(" + prefix + "|" + cost + ")", "");
				return resultObject;
			}
		}else if(syntax.matches(".*((\\s*(chan|le)\\s*(chan|le)\\s*)+).*")){
			isRightSyntax = !syntax.replaceAll("((\\s*(chan|le)\\s*(chan|le)\\s*)+)", "").matches(".*(chan|le).*");
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax.replaceAll("(" + prefix + "|" + cost + ")", "");
				return resultObject;
			}
		}else if((syntax.contains("lo") || syntax.contains("de")) && syntax.contains("100") && syntax.contains("so")){
			isRightSyntax = syntax.matches(".*100\\s*so\\s*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)");
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax.replaceAll("(" + prefix + "|" + cost + ")", "");
				return resultObject;
			}
		}/*else if (syntax.contains("tong") && syntax.contains("to")){
			isRightSyntax = syntax.matches(".*tong\\s*to\\s*x[0-9]+\\s*(k|d)");
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax.replaceAll("(" + prefix + "|" + cost + ")", "");
				return resultObject;
			}
		}else if (syntax.contains("tong") && syntax.contains("be")){
			isRightSyntax = syntax.matches(".*tong\\s*be\\s*x[0-9]+\\s*(k|d)");
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax.replaceAll("(" + prefix + "|" + cost + ")", "");
				return resultObject;
			}
		}*/else if (syntax.contains("tong") && syntax.contains("chan")){
			isRightSyntax = syntax.matches(".*tong\\s*chan\\s*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)");
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax.replaceAll("(" + prefix + "|" + cost + ")", "");
				return resultObject;
			}
		}else if (syntax.contains("tong") && syntax.contains("le")){
			isRightSyntax = syntax.matches(".*tong\\s*le\\s*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)");
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax.replaceAll("(" + prefix + "|" + cost + ")", "");
				return resultObject;
			}
		}else if ((syntax.contains("tong") && syntax.contains("tren")) || syntax.matches(".*tong\\s*(duoi).*")){
			isRightSyntax = syntax.matches(".*tong\\s*(tren|duoi)\\s*10\\s*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)");
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax.replaceAll("(" + prefix + "|" + cost + ")", "");
				return resultObject;
			}
		}else if(syntax.contains("tong") && syntax.contains("chia")){
			isRightSyntax = syntax.matches(".*tong\\s*(khong)*\\s*chia\\s*[ba3]+\\s*(bor[\\s\\w]+)?x[0-9]+\\s*(k|d)");
			boolean isSyntaxDu1 = syntax.matches(".*tong\\s*chia\\s*[ba3\\+]+\\s*du\\s*[1mot]+\\s*(bor[\\s\\w]+)?x[0-9]+\\s*(k|d)");
			boolean isSyntaxDu2 = syntax.matches(".*tong\\s*chia\\s*[ba3\\+]+\\s*du\\s*[2hai]+\\s*(bor[\\s\\w]+)?x[0-9]+\\s*(k|d)");

			resultObject.Error = !(isRightSyntax | isSyntaxDu1 | isSyntaxDu2);
			if(!isRightSyntax){
				resultObject.MsgError = syntax.replaceAll("(" + prefix + "|" + cost + ")", "");
				return resultObject;
			}
		}else if(syntax.contains("tong")){
			isRightSyntax = syntax.matches(".*tong\\W*(([0-9][\\s.,]*)+|to|be|le|chan)\\s*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)");
			isRightSyntax = isRightSyntax && validateMistakeSyntax(syntax);
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax.replaceAll("(" + prefix + "|" + cost + ")", "");
				return resultObject;
			}
		}else if(syntax.replaceAll(" ", "").contains("toto") || syntax.replaceAll(" ", "").contains("nhonho")
				|| syntax.replaceAll(" ", "").contains("nhoto") || syntax.replaceAll(" ", "").contains("tonho")
				|| syntax.replaceAll(" ", "").contains("bebe") || syntax.replaceAll(" ", "").contains("tobe")
				|| syntax.replaceAll(" ", "").contains("beto") || syntax.replaceAll(" ", "").contains("tobe")){
			isRightSyntax = syntax.matches(".*((nho|be)\\s*(nho|be)|to\\s*to|(nho|be)\\s*to|to\\s*(nho|be))\\s*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)");
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax.replaceAll("(" + prefix + "|" + cost + ")", "");
				return resultObject;
			}
		}else if(syntax.contains("de")){
			isRightSyntax = syntax.matches("de[\\s:.]*([0-9]{2,3}([-.,/\\s]+))*[0-9]{2,3}[-.,/\\s]*\\s*x[0-9]+\\s*(k|d)");

			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				//Include unknown syntax.
				resultObject.isErrorCost = true;
				resultObject.MsgError = getErrorNumbers(Utils.getRootSyntaxString(syntax.replaceAll("\\W*(de)\\W*", "")), false);

				resultObject.errNotify = "Lỗi: " + resultObject.MsgError;
				return resultObject;
			}
			else
			{
				String numErr = validateDigit3Chars(mainSyntax);
				if (numErr != null)
				{
					resultObject.Error = true;
					resultObject.isErrorCost = true;
					resultObject.MsgError = numErr;
					resultObject.errNotify = "Kiểm tra số: " + numErr;
					return resultObject;
				}
			}
		}else if(syntax.contains("lo")){
			isRightSyntax = syntax.matches("lo[\\s:.]*([0-9]{2,3}([-.,/\\s]+))*[0-9]{2,3}[-.,/\\s]*\\s*x[0-9]+\\s*(k|d)");

			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				//Include unknown syntax.
				resultObject.isErrorCost = true;
				resultObject.MsgError = getErrorNumbers(Utils.getRootSyntaxString(syntax.replaceAll("\\W*(lo)\\W*", "")), false);
				resultObject.errNotify = "Lỗi: " + resultObject.MsgError;
				return resultObject;
			}
			else
			{
				String numErr = validateDigit3Chars(mainSyntax);
				if (numErr != null)
				{
					resultObject.Error = true;
					resultObject.isErrorCost = true;
					resultObject.MsgError = numErr;
					resultObject.errNotify = "Kiểm tra số: " + numErr;
					return resultObject;
				}
			}
		}else if(syntax.contains("bacang")){
			isRightSyntax = syntax.matches("(bc|bacang|ba\\scang)[\\s:]*([0-9]{3}([-.,\\s]*))+\\s*x[0-9]+\\s*(k|d)");

			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax.replaceAll("(" + prefix + "|" + cost + ")", "");
				return resultObject;
			}
		}
		else
		{
			resultObject.Error = true;
			resultObject.isErrorCost = true;
			resultObject.MsgError = syntax.replaceAll("(" + prefix + "|" + cost + ")", "");
			resultObject.errNotify = "Lỗi: Cú pháp không được support.";
		}

		return resultObject;
	}
	
	private static boolean validateMistakeSyntax(String syntax) {
		String mainSyntax = Utils.getRootSyntaxString(syntax);
		boolean isRight = true;

		if (mainSyntax.matches(".*[0-9]\\W+[0-9].*") && mainSyntax.matches(".*[0-9]{2,3}.*"))
		{
			isRight = false;
		}

		//Add more other validate syntax here.

		return isRight;
	}

	private static boolean validateMistakeSyntax2Char(String syntax) {
		String mainSyntax = Utils.getRootSyntaxString(syntax).trim();
		boolean isRight = true;

		if (mainSyntax.matches(".*[0-9]\\W+[0-9].*"))
		{
			if (mainSyntax.matches(".*[0-9]{2}[0-9]+.*") || mainSyntax.matches(".*\\W[0-9]\\W.*"))
			{
				return false;
			}

			mainSyntax = mainSyntax.replaceAll("\\s*([,.-])\\s*", "$1");
			mainSyntax = mainSyntax.replaceAll("\\s+", " ");
			mainSyntax = mainSyntax.replaceAll("^[A-Za-z\\W]+", "");

			Set<String> listSepa = new LinkedHashSet<>();

			String[] _temp = mainSyntax.split("\\w+");

			for (String _s : _temp)
			{
				if (!_s.equals(""))
				{
					listSepa.add(_s);
				}
			}

			if (listSepa.size() > 1)
			{
				isRight = false;
			}
		}

		//Add more other validate syntax here.

		return isRight;
	}

	private static String validateDigit3Chars(String mainSyntax) {
		if (mainSyntax.matches(".*\\W[0-9]{3}\\W.*"))
		{
			String regNumber = "([0-9]{3})";
			Pattern patternNumber = Pattern.compile(regNumber);
			Matcher matcherNumber = patternNumber.matcher(mainSyntax);
			while (matcherNumber.find()) {
				String numbers = matcherNumber.group(1);
				if (numbers != null)
				{
					String[] sepaNum = numbers.split("");
					Set<String> nums = new LinkedHashSet<>();
					boolean error = false;
					for (String s : sepaNum)
					{
						if (!s.isEmpty())
						{
							if (!nums.contains(s))
							{
								nums.add(s);
							}
							else
							{
								if (nums.size() == 1 || (nums.size() == 2 && nums.toArray()[1].equals(s)))
									error = true;
							}
						}
					}

					if (nums.size() != 2 || error)
					{
						return numbers;
					}
				}
			}
		}

		return null;
	}

	private static String getErrorNumbers(String mainSyntax, boolean forceget) {
		if (mainSyntax.matches(".*[0-9]{4}.*") || mainSyntax.matches(".*\\W[0-9]\\W.*") || mainSyntax.matches("^[0-9]\\W.*") || mainSyntax.matches(".*\\W[0-9]$"))
		{
			forceget = true;
		}

		if (forceget)
		{
			String regNumber = "([0-9][0-9][0-9][0-9]+|^[0-9]\\W|\\W[0-9]\\W|\\W[0-9]$)";
			Pattern patternNumber = Pattern.compile(regNumber);
			Matcher matcherNumber = patternNumber.matcher(mainSyntax);
			while (matcherNumber.find()) {
				String numbers = matcherNumber.group(1);
				if (numbers != null)
				{
					return numbers;
				}
			}
		}

		return mainSyntax;
	}

	private static String formatToShortSyntax(String syntax) {
		syntax = syntax.replaceAll("kep\\s*bang", "kep");

		return syntax;
	}

	private static String validatePrefix(String syntax) {
		String prefix = null;
		if (syntax.contains("de"))
		{
			prefix = "de";
		}

		if (syntax.contains("lo"))
		{
			if (prefix != null)
			{
				return null;
			}
			prefix = "lo";
		}

		if (syntax.contains("xien"))
		{
			if (prefix != null)
			{
				return null;
			}
			prefix = "xien";
		}

		if (syntax.contains("bacang"))
		{
			if (prefix != null)
			{
				return null;
			}
			prefix = "bacang";
		}

		return prefix;
	}

	public static boolean validateBor(String syntax) {
		if (syntax == null) return false;

		String removeSyntax = Utils.getRemoveSyntaxString(syntax);
		String tmp = removeSyntax.replaceAll("bor\\W*trung", "");
		if ((tmp.contains("trung") || tmp.contains("trong")) && (tmp.indexOf("bor") != tmp.lastIndexOf("bor")))
		{
			return false;
		}

		return true;
	}
}
