package com.bingo.analyze;

import com.bingo.analyze.calculation.BingoCalc;
import com.bingo.analyze.calculation.KetQua;
import com.bingo.common.utils.ArrayUtils;
import com.bingo.common.utils.Utils;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalyzeSMS {
	private ArrayList<String> failSyntax;

	private ArrayList<String> allNumbersResult;

	private String sDB = "";

	public AnalyzeSMS() {
	}

	public ArrayList<String> getFailSyntax() {
		return failSyntax;
	}

	public ArrayList<Analyze> bingoAnalyze(String smsContent, String todayBingoResult, String yesterdayBingoResult) {

		Analyze objAnalyze = new Analyze();
		ArrayList<Analyze> arrAnalyze = new ArrayList<>();

		AnalyzeSMS analyze = new AnalyzeSMS();
		boolean hadError = false;
		StringBuffer sb = new StringBuffer();

		ArrayList<String> arrValidate = analyze.validate(smsContent);
		for (String string : arrValidate) {
			if (string.indexOf("ERR") >= 0) {
				hadError = true;
				sb.append(string.replaceAll("ERR", "Sai cú pháp")).append("\n");
				objAnalyze.error = true;
				objAnalyze.errorMessage = sb.toString();
			}
		}

		if (hadError) {
			arrAnalyze.add(objAnalyze);
			return arrAnalyze;
		} else {
			ArrayList<NumberAndUnit> _syntaxs = analyze.analyzeMessage(smsContent, yesterdayBingoResult);
			BigDecimal thucThu = BigDecimal.ONE;

			ArrayList<String> allNumbers = new ArrayList<>();
			double HE_SO = 0;
			double HE_SO_THANG = 0;
			int amountPoint = 0;
			String ketQua = todayBingoResult;
			String sDB = "";
			if (!ketQua.equals("")) {
				// Lấy tất cả kết quả xổ số

				KetQua db = BingoCalc.initial().extractResultSMS("DB", ketQua);
				KetQua giai1 = BingoCalc.initial().extractResultSMS("1", ketQua);
				KetQua giai2 = BingoCalc.initial().extractResultSMS("2", ketQua);
				KetQua giai3 = BingoCalc.initial().extractResultSMS("3", ketQua);
				KetQua giai4 = BingoCalc.initial().extractResultSMS("4", ketQua);
				KetQua giai5 = BingoCalc.initial().extractResultSMS("5", ketQua);
				KetQua giai6 = BingoCalc.initial().extractResultSMS("6", ketQua);
				KetQua giai7 = BingoCalc.initial().extractResultSMS("7", ketQua);

				sDB = db.daySo.get(0);
				allNumbers.add(sDB.substring(sDB.length() - 2));

				for (String so : giai1.daySo) {
					allNumbers.add(so.substring(so.length() - 2));
				}

				for (String so : giai2.daySo) {
					allNumbers.add(so.substring(so.length() - 2));
				}

				for (String so : giai3.daySo) {
					allNumbers.add(so.substring(so.length() - 2));
				}

				for (String so : giai4.daySo) {
					allNumbers.add(so.substring(so.length() - 2));
				}

				for (String so : giai5.daySo) {
					allNumbers.add(so.substring(so.length() - 2));
				}

				for (String so : giai6.daySo) {
					allNumbers.add(so.substring(so.length() - 2));
				}

				for (String so : giai7.daySo) {
					allNumbers.add(so.substring(so.length() - 2));
				}
			}
			if (_syntaxs == null) {
				objAnalyze.error = true;
				objAnalyze.errorMessage = "Không thể nhận diện được cú pháp này";
				arrAnalyze.add(objAnalyze);
				return arrAnalyze;
			}
			for (NumberAndUnit _numberAndUnit : _syntaxs) {

				thucThu = BigDecimal.ZERO;
				amountPoint = 0;
				
				objAnalyze = new Analyze();
				objAnalyze.numberAndUnit = _numberAndUnit;

				// Calculate:
				String extractType = "";
				for (String number : _numberAndUnit.Numbers) {
					if (!number.trim().equals("")) {
						// Số ti�?n thu của khách

						if (_numberAndUnit.Type.indexOf("de") >= 0) {
							HE_SO = BingoCalc.HS_DE;
							extractType = "DB";
						} else {
							HE_SO = BingoCalc.HS_NONE;
							extractType = "ALL";
						}

					}

					if (!ketQua.equals("")) {

						if (extractType.equals("DB")) {
							BigDecimal result = BingoCalc.initial().calc(Integer.parseInt(_numberAndUnit.Price), HE_SO);
							thucThu = thucThu.add(result);
							objAnalyze.actuallyCollected = thucThu;
							
							amountPoint += Integer.parseInt(_numberAndUnit.Price);

							int count = number.length() - number.replace("-", "").length();
							String[] arrNums = number.split("-");
							if (count == 0) {
								if (sDB.substring(sDB.length() - 2).equals(number.trim())) {
									BigDecimal resultThangDe = BingoCalc.initial()
											.calc(Integer.parseInt(_numberAndUnit.Price), BingoCalc.HS_THANG_DE);
									objAnalyze.guestWin = resultThangDe;
									objAnalyze.winNumber = number.trim();
									objAnalyze.winSyntax = objAnalyze.winNumber + "x"+_numberAndUnit.Price+""+_numberAndUnit.Unit;
								}
							} else {
								for (String num : arrNums) {
									if (sDB.substring(sDB.length() - 2).equals(num.trim())) {
										BigDecimal resultThangDe = BingoCalc.initial()
												.calc(Integer.parseInt(_numberAndUnit.Price), BingoCalc.HS_THANG_DE);
										objAnalyze.guestWin = resultThangDe;
										objAnalyze.winNumber = num.trim();
										objAnalyze.winSyntax = objAnalyze.winNumber + "x"+_numberAndUnit.Price+""+_numberAndUnit.Unit;
									}
								}
							}

						} else {

							// Xác định loại và lấy cặp số
							if (_numberAndUnit.Type.equals("lo")) {
								
								for (String _num : number.split("-")) {
									// Tính thực thu Lô
									BigDecimal resLo = BingoCalc.initial().calc(Integer.parseInt(_numberAndUnit.Price),
											HE_SO);
									thucThu = thucThu.add(resLo);
									objAnalyze.actuallyCollected = thucThu;
									amountPoint += Integer.parseInt(_numberAndUnit.Price);
									
									for (String kq : allNumbers) {
										
										if (_num.equals(kq)) {

											// Trúng lô
											BigDecimal resultThangLo = BingoCalc.initial()
													.calc(Integer.parseInt(_numberAndUnit.Price), BingoCalc.HS_THANG_LO);
											objAnalyze.guestWin = resultThangLo;
											objAnalyze.winNumber = objAnalyze.winNumber == null ? _num.trim() : objAnalyze.winNumber + "-" + _num.trim();
											objAnalyze.winSyntax = objAnalyze.winNumber + "x"+_numberAndUnit.Price+""+_numberAndUnit.Unit;
										}
									}
								}
								
							} else {
								if (number.indexOf("-") > 0) {
									int count = number.length() - number.replace("-", "").length();

									switch (count) {
									case 1:
										// xien2
										HE_SO = BingoCalc.HS_XIEN_2;
										HE_SO_THANG = BingoCalc.HS_THANG_XIEN_2;
										break;
									case 2:
										// xien3
										HE_SO = BingoCalc.HS_XIEN_3;
										HE_SO_THANG = BingoCalc.HS_THANG_XIEN_3;
										break;
									case 3:
										// xien4
										HE_SO = BingoCalc.HS_XIEN_4;
										HE_SO_THANG = BingoCalc.HS_THANG_XIEN_4;
										break;
									}

									// Tính thực thu
									BigDecimal resXien = BingoCalc.initial()
											.calc(Integer.parseInt(_numberAndUnit.Price), HE_SO);
									thucThu = thucThu.add(resXien);
									amountPoint += Integer.parseInt(_numberAndUnit.Price);

									String[] arrNums = number.split("-");
									int[] countXien = new int[arrNums.length];
									int size = arrNums.length;

									for (int i = 0; i < size; i++)
									{
										objAnalyze.actuallyCollected = thucThu;

										for (String kq : allNumbers) {
											if (arrNums[i].equals(kq)) {
												countXien[i]++;
											}
										}
									}

									Arrays.sort(countXien);
									int countMin = countXien[0];

									if (countMin > 0) {
										// Trúng xien
										BigDecimal resultThangLo = BingoCalc.initial()
												.calc(Integer.parseInt(_numberAndUnit.Price), HE_SO_THANG);
										objAnalyze.winSyntax = objAnalyze.winNumber + "x"+_numberAndUnit.Price+""+_numberAndUnit.Unit;
										objAnalyze.guestWin = resultThangLo;
										objAnalyze.winNumber = number.trim();

									}

								} else {

								}
							}
						}

					}
				}

				objAnalyze.amountPoint = amountPoint;
				objAnalyze.winType = _numberAndUnit.Type;
				arrAnalyze.add(objAnalyze);
			}

		}

		return arrAnalyze;
	}

	public void setResult(String todayBingoResult) {

		if (allNumbersResult != null) {
			allNumbersResult.clear();
		}
		else
		{
			allNumbersResult = new ArrayList<>();
		}

		String ketQua = todayBingoResult;

		if (!ketQua.equals("")) {
			// Lấy tất cả kết quả xổ số

			KetQua db = BingoCalc.initial().extractResultSMS("DB", ketQua);
			KetQua giai1 = BingoCalc.initial().extractResultSMS("1", ketQua);
			KetQua giai2 = BingoCalc.initial().extractResultSMS("2", ketQua);
			KetQua giai3 = BingoCalc.initial().extractResultSMS("3", ketQua);
			KetQua giai4 = BingoCalc.initial().extractResultSMS("4", ketQua);
			KetQua giai5 = BingoCalc.initial().extractResultSMS("5", ketQua);
			KetQua giai6 = BingoCalc.initial().extractResultSMS("6", ketQua);
			KetQua giai7 = BingoCalc.initial().extractResultSMS("7", ketQua);

			sDB = db.daySo.get(0);
			allNumbersResult.add(sDB.substring(sDB.length() - 2));

			for (String so : giai1.daySo) {
				allNumbersResult.add(so.substring(so.length() - 2));
			}

			for (String so : giai2.daySo) {
				allNumbersResult.add(so.substring(so.length() - 2));
			}

			for (String so : giai3.daySo) {
				allNumbersResult.add(so.substring(so.length() - 2));
			}

			for (String so : giai4.daySo) {
				allNumbersResult.add(so.substring(so.length() - 2));
			}

			for (String so : giai5.daySo) {
				allNumbersResult.add(so.substring(so.length() - 2));
			}

			for (String so : giai6.daySo) {
				allNumbersResult.add(so.substring(so.length() - 2));
			}

			for (String so : giai7.daySo) {
				allNumbersResult.add(so.substring(so.length() - 2));
			}
		}
	}

	public Analyze getAnalyze(String number, String type, String price, String unit, String todayBingoResult) {

		Analyze objAnalyze = new Analyze();

		if (allNumbersResult == null)
		{
			return objAnalyze;
		}

		BigDecimal thucThu;

		double HE_SO = 0;
		double HE_SO_THANG = 0;
		int amountPoint = 0;
		String ketQua = todayBingoResult;

		thucThu = BigDecimal.ZERO;
		amountPoint = 0;

		String extractType = "";

		if (!number.trim().equals("")) {
			// Số ti�?n thu của khách

			if (type.indexOf("de") >= 0) {
				HE_SO = BingoCalc.HS_DE;
				extractType = "DB";
			} else {
				HE_SO = BingoCalc.HS_NONE;
				extractType = "ALL";
			}

		}

		if (!ketQua.equals("")) {

			if (extractType.equals("DB")) {
				BigDecimal result = BingoCalc.initial().calc(Integer.parseInt(price), HE_SO);
				thucThu = thucThu.add(result);
				objAnalyze.actuallyCollected = thucThu;

				amountPoint += Integer.parseInt(price);

				int count = number.length() - number.replace("-", "").length();
				String[] arrNums = number.split("-");
				if (count == 0) {
					if (sDB.substring(sDB.length() - 2).equals(number.trim())) {
						BigDecimal resultThangDe = BingoCalc.initial()
								.calc(Integer.parseInt(price), BingoCalc.HS_THANG_DE);
						objAnalyze.guestWin = resultThangDe;
						objAnalyze.winNumber = number.trim();
						objAnalyze.winSyntax = objAnalyze.winNumber + "x" + price + "" + unit;
					}
				} else {
					for (String num : arrNums) {
						if (sDB.substring(sDB.length() - 2).equals(num.trim())) {
							BigDecimal resultThangDe = BingoCalc.initial()
									.calc(Integer.parseInt(price), BingoCalc.HS_THANG_DE);
							objAnalyze.guestWin = resultThangDe;
							objAnalyze.winNumber = num.trim();
							objAnalyze.winSyntax = objAnalyze.winNumber + "x" + price + "" + unit;
						}
					}
				}

			} else {

				// Xác định loại và lấy cặp số
				if (type.equals("lo")) {

					// Tính thực thu Lô
					BigDecimal resLo = BingoCalc.initial().calc(Integer.parseInt(price),
							HE_SO);
					thucThu = thucThu.add(resLo);
					objAnalyze.actuallyCollected = thucThu;
					amountPoint += Integer.parseInt(price);

					for (String kq : allNumbersResult) {

						if (number.equals(kq)) {

							// Trúng lô
							BigDecimal resultThangLo = BingoCalc.initial().calc(Integer.parseInt(price), BingoCalc.HS_THANG_LO);

							objAnalyze.guestWin = resultThangLo;
							objAnalyze.winNumber = objAnalyze.winNumber == null ? number.trim() : objAnalyze.winNumber + "-" + number.trim();
							objAnalyze.winSyntax = objAnalyze.winNumber + "x" + price + "" + unit;
						}
					}

				} else {
					if (number.indexOf("-") > 0) {
						int count = number.length() - number.replace("-", "").length();

						switch (count) {
							case 1:
								// xien2
								HE_SO = BingoCalc.HS_XIEN_2;
								HE_SO_THANG = BingoCalc.HS_THANG_XIEN_2;
								break;
							case 2:
								// xien3
								HE_SO = BingoCalc.HS_XIEN_3;
								HE_SO_THANG = BingoCalc.HS_THANG_XIEN_3;
								break;
							case 3:
								// xien4
								HE_SO = BingoCalc.HS_XIEN_4;
								HE_SO_THANG = BingoCalc.HS_THANG_XIEN_4;
								break;
						}

						// Tính thực thu
						BigDecimal resXien = BingoCalc.initial().calc(Integer.parseInt(price), HE_SO);
						thucThu = thucThu.add(resXien);
						amountPoint += Integer.parseInt(price);
						String[] arrNums = number.split("-");
						int[] countXien = new int[arrNums.length];
						int size = arrNums.length;

						for (int i = 0; i < size; i++)
						{
							objAnalyze.actuallyCollected = thucThu;

							for (String kq : allNumbersResult) {
								if (arrNums[i].equals(kq)) {
									countXien[i]++;
								}
							}
						}

						Arrays.sort(countXien);
						int countMin = countXien[0];

						if (countMin > 0) {
							// Trúng xien
							BigDecimal resultThangLo = BingoCalc.initial()
									.calc(Integer.parseInt(price), HE_SO_THANG);
							objAnalyze.winSyntax = objAnalyze.winNumber + "x" + price + "" + unit;
							objAnalyze.guestWin = resultThangLo;
							for (int i = 0; i < countMin; i++)
							{
								if (i == 0)
								{
									objAnalyze.winNumber = number.trim();
								}
								else
								{
									objAnalyze.winNumber = objAnalyze.winNumber.concat("-").concat(number.trim());
								}
							}

						}
					}
				}
			}

			objAnalyze.amountPoint = amountPoint;
			objAnalyze.winType = type;
		}

		return objAnalyze;
	}

	public ArrayList<String> validate(String data) {
		
		boolean Error = false;
		ArrayList<String> result = new ArrayList<>();

		ArrayList<String> disSyntax = new ArrayList<>();
		
		data = data.replaceAll("ộ", "oj");
		data = data.replaceAll("Ộ", "oj");
		data = data.replaceAll("ỏ", "or");
		data = data.replaceAll("Ỏ", "or");
		data = data.replaceAll("diem", "d");
		
		String content = VNCharacterUtils.removeAccent(data).toLowerCase();
		
		
		
		String[] forMatSyntaxs = content.split("\\d+[knd]+");
		for (String string : forMatSyntaxs) {
			if(string.trim().equals("")) continue;
			string = string.replace("*", "x");
			string = string.replaceAll("×", "x");
			
			String checkString = string.trim().substring(string.trim().length() - 1);
			if(!checkString.equals("x")){
				if(string.lastIndexOf("trieu") > 0){
					
				}else{
					result.add("ERR:[" + string + "]");
					
					return result;
				}
				
				
			}
			
		}
		
		
		content = content.replaceAll("lo\\s*xien", "###xien");
		content = content.replace("de", "###de");
		content = content.replace("lo", "###lo");
		content = content.replaceAll("xien quay", "###xien quay");
		content = content.replace("xienquay", "###xienquay");
		content = content.replace("xq", "###xq");
		content = content.replaceAll("de bor", "de 100 bo");
		
		String[] arrSyntaxs = content.split("###*");

		Pattern pattern0 = Pattern.compile("([bcdfghjklmnpqrstvwxyz]+0)");
		Pattern patternJ = Pattern.compile("([bcdfghjklmnpqrstvwxyz]+oj)");

		
		for (String string : arrSyntaxs) {
			if (string.indexOf("quay") < 0 && string.indexOf("xien") > 0) {
				string = string.replace("xien", "###xien");
				for (String subSyntax : string.split("###*")) {
					disSyntax.add(subSyntax);
				}

			} else {
				disSyntax.add(string);
			}
		}

		for (String syntax : disSyntax) {
			int iTrieu = syntax.indexOf("trieu");
			if(iTrieu >=0 && (syntax.indexOf("k") >= iTrieu || syntax.indexOf("n") >= iTrieu || syntax.indexOf("d") >= iTrieu) ){
				syntax = syntax.replaceAll("trieu", "");
			}else{
				syntax = syntax.replaceAll("trieu", "000k");
			}
			
			
			String checkBeginSyntax = syntax.replaceAll("xien|kep|de|lo|dau|dit|xq|chan|le|tong|kep|bang|duoi|dan|dau|boj|bor|bo|.", "");
			
			checkBeginSyntax = checkBeginSyntax.replaceAll("k|n|d|diem", "###");
			String[] arrCheckBeginSyntax = checkBeginSyntax.split("###");
			for (String begin : arrCheckBeginSyntax) {
				if(begin.trim().equals("")) continue;
				if(!Utils.isInteger(begin.trim())){
					Error = true;
				}
			}
			
			// format syntax
			syntax = syntax.trim().replace(".", "");

			if (syntax.equals("") || syntax.equals("\n"))
				continue;

			if (syntax.indexOf("de") < 0 && syntax.indexOf("lo") < 0 && syntax.indexOf("xien") < 0
					&& syntax.indexOf("xq") < 0) {
				result.add("ERR:[" + syntax + "]");
				continue;
			}

			Matcher matcher0 = pattern0.matcher(syntax);

			while (matcher0.find()) {
				String charHaveZero = matcher0.group(0);
				String charFormated = charHaveZero.replace("0", "o");
				syntax = syntax.replaceAll(charHaveZero, charFormated);
			}

			Matcher matcherJ = patternJ.matcher(syntax);

			while (matcherJ.find()) {
				String charHaveJ = matcherJ.group(0);
				String charFormated = charHaveJ.replace("oj", "o");
				syntax = syntax.replaceAll(charHaveJ, charFormated);
			}

			syntax = syntax.replaceAll("moi con", "x");
			syntax = syntax.replace("*", "x");
			syntax = syntax.replaceAll("×", "x");
			syntax = syntax.replaceAll("₫", "d");
			syntax = syntax.replaceAll("[\\s]*diem", "d");
			syntax = syntax.replaceAll("[\\s]*+djem", "d");
			syntax = syntax.replaceAll(":", "");
			syntax = syntax.replaceAll("ok", "");
			String sValid = syntax.trim().replace("!", "").replace(";", " ").replace("\n", " ").replaceAll("\\,", "")
					.replaceAll("\\.", "").toLowerCase().trim();
			// validate

			if (sValid.matches("^(?i)(de?|lo?|xien?|xq).*$")) {
				if (Pattern.matches(".*[0-9a-z.;-]*x[0-9.;-]+.*[knd;.-]", sValid.replaceAll(" ", ""))) {
					if (sValid.replaceAll(" ", "").indexOf("xien") >= 0) {
						String checkXien = sValid.replaceAll("^([xien\\s]+)", "");
						checkXien = checkXien.substring(0, checkXien.indexOf("x")).trim();
						if (checkXien.length() < 4) {
							Error = true;
						}
					}

					if(sValid.indexOf("xien") >=0){
						/*String[] arrValidSyntaks = sValid.split("[0-9]+[knd]{1}");
						for (String string : arrValidSyntaks) {
							if (Character.isDigit(string.trim().charAt(0))) {
								Error = true;
							}
						}*/
						if (Character.isDigit(sValid.trim().charAt(0))) {
							Error = true;
						}
					}
					
					
					if (sValid.replaceAll(" ", "").indexOf("xienquay") >= 0
							|| sValid.replaceAll(" ", "").indexOf("xq") >= 0) {
						sValid = sValid.replaceAll("^([xienquay\\s]+\\d{1}\\s+)", "");
						String xq = sValid.replaceAll(" ", "").replaceAll("xienquay", "").replaceAll("xq", "");
						xq = xq.substring(0, xq.indexOf("x"));
						if (xq.length() == 3 || xq.length() == 4 || xq.length() == 5 || xq.length() == 6
								|| xq.length() == 7 || xq.length() == 8) {
						} else {
							Error = true;
						}
					}

					if (sValid.matches("^(?i)(xien)[0-9a-z]+quay.*$")) {
						Error = true;
					}

					int countX = countOccurrences(sValid.replaceAll("xien|xq", ""), 'x');

					String test = sValid.replaceAll("xien|kep|de|lo|dau|dit|xq|chan|le|tong|kep|bang|duoi|dan|dau", "");

					int countUnit = countOccurrences(test, 'n');
					int countk = countOccurrences(test, 'k');
					
					if(sValid.trim().indexOf("lo") >=0 && (countUnit >0 || countk > 0)){
						Error = true;
					}
					
					countUnit += countOccurrences(test, 'k');

					countUnit += countOccurrences(test, 'd');


					sValid = sValid.replaceAll("xien|chan|de|lo|kep|dau|dit|xq", "").trim();

					if (sValid.lastIndexOf("n") == (sValid.length() - 1)) {
					} else if (sValid.lastIndexOf("k") == (sValid.length() - 1)) {
					} else if (sValid.lastIndexOf("d") == (sValid.length() - 1)) {
					} else {
						Error = true;
					}

					if (countUnit != countX) {
						Error = true;
					}

					// int duplicateWord =
					// duplicateWords(syntax).toArray().length;
					//
					// if (duplicateWord > 0) {
					// Error = true;
					// }

					if (syntax.indexOf("=") >= 0) {
						Error = true;
					}

					if (syntax.indexOf("dex") >= 0 || syntax.indexOf("xienx") >= 0 || syntax.indexOf("lox") >= 0) {
						Error = true;
					}

					if (!Error) {
						result.add(syntax);
					} else {
						result.add("ERR:[" + syntax + "]");
						Error = false;
					}

				} else {
					result.add("ERR:[" + syntax + "]");
				}
			} else {
				result.add("ERR:[" + syntax + "]");
			}

		}
		return result;
	}

	public Set<String> duplicateWords(String input) {
		if (input == null || input.isEmpty()) {
			return Collections.emptySet();
		}
		Set<String> duplicates = new HashSet<>();
		String[] words = input.split("\\s+|,|;|:|-");
		Set<String> set = new HashSet<>();
		for (String word : words) {
			if (!set.add(word)) {
				duplicates.add(word);
			}
		}
		return duplicates;
	}

	public int countOccurrences(String haystack, char needle) {
		int count = 0;
		for (int i = 0; i < haystack.length(); i++) {
			if (haystack.charAt(i) == needle) {
				count++;
			}
		}
		return count;
	}

	protected ArrayList<NumberAndUnit> analyzeDeBo(String syntax) {
		ArrayList<NumberAndUnit> allNumbers = new ArrayList<>();
		String _syntax = syntax.replace("de bo", "");
		String _syntaxRemove = "";
		String _unit = "";
		String _numbers = "";
		if (_syntax.indexOf("bo") > 0) {
			_syntaxRemove = _syntax.substring(_syntax.indexOf("bo"), _syntax.indexOf("x") - 1).replaceAll("bo", "")
					.replaceAll("[\\.\\;\\-\\,]", "-");
			_numbers = _syntax.substring(0, _syntax.indexOf("bo") - 1).replaceAll("[\\.\\;\\-\\,]", "-");
		} else {
			_numbers = _syntax.substring(0, _syntax.indexOf("x") - 1);
		}

		_unit = _syntax.substring(_syntax.indexOf("x"), _syntax.length());

		String[] arrNumbers = _numbers.replaceAll("[\\.\\;\\-\\,]", "-").split("-");
		for (String string : arrNumbers) {
			ArrayList<String> lsBoSo = layBoSo(string.trim());
			if (!_syntaxRemove.equals("")) {
				String[] removeNumbers = _syntaxRemove.split("-");
				for (String string2 : removeNumbers) {
					lsBoSo.remove(string2.trim());
				}
			}
		}

		return allNumbers;
	}

	public ArrayList<NumberAndUnit> analyzeMessage(String content, String yesterdayNumber) {

		ArrayList<NumberAndUnit> Syntaxs = new ArrayList<>();
		ArrayList<String> temp = new ArrayList<>();
		failSyntax = new ArrayList<>();

		String type = "", orgNumber = "";
		ArrayList<String> lsRemoveNumber = new ArrayList<>();
		try {

			ArrayList<String> lsKeyWordRemoveNumber = new ArrayList<>();
			lsKeyWordRemoveNumber.add("chan");
			lsKeyWordRemoveNumber.add("le");
			lsKeyWordRemoveNumber.add("kep");
			lsKeyWordRemoveNumber.add("dau");
			lsKeyWordRemoveNumber.add("dit");
			lsKeyWordRemoveNumber.add("duoi");

			content = content.replaceAll("bộ", "boj");
			content = content.replaceAll("bỏ", "bor");

			content = VNCharacterUtils.removeAccent(content.toLowerCase());

			content = content.replaceAll("o", "0");
			content = content.replaceAll("l[0]+", "lo");
			content = content.replaceAll(",", ".");
			content = content.replaceAll(";", ".");
			content = content.replaceAll("x", "×");
			content = content.replaceAll("×i", "xi");
			content = content.replaceAll("[\\s\\d]+xien[\\s\\d]+quay", "xq");
			content = content.replaceAll("×q", "xq");
			content = content.replaceAll("d", "₫");
			content = content.replaceAll("₫a", "da");
			content = content.replaceAll("₫e", "de");
			content = content.replaceAll("₫i", "di");
			content = content.replaceAll("₫u", "du");
			content = content.replaceAll("nh[0]+", "nho");
			content = content.replaceAll("t[0]+", "to");
			content = content.replaceAll("[0]+i", "oi");
			content = content.replaceAll("b[0]+", "bo");
			content = content.replaceAll("[\\s]+n", "n");
			content = content.replaceAll("[\\s]+k", "k");
			content = content.replaceAll("kep", " kep");
			content = content.replaceAll("[\\s]+×[\\s]+", "×");
			content = content.replaceAll("-", ".");
			content = content.replaceAll("diem", "₫");
			content = content.replaceAll("[\\.]+", ".");
			content = content.replaceAll("[\\.]+×", "×");
			content = content.replaceAll(":", "");
			content = content.replaceAll("[\n\r]", " ");
			content = content.replaceAll("c[0]+", "co");
			content = content.replaceAll("s[0]+", "so");
			content = content.replaceAll("t[0]+ng", "tong");
			content = content.replaceAll("[\\s]+₫", "₫");
			content = content.replaceAll("[\\.]+", " ");
			content = content.replaceAll("[\\=]+", "x");
			content = content.replaceAll("jem", "iem");
			content = content.replaceAll("lo\\s*xien", "xien");
			content = content.replaceAll("!", "");
			content = content.replaceAll("de bor", "de 100 bor");
			content = content.replaceAll("de b0r", "de 100 bor");
			
			
			Pattern patternX = Pattern.compile("[\\s\\.×]+[\\d]+[kn₫]");
			Matcher matcherX = patternX.matcher(content);

			while (matcherX.find()) {
				if (matcherX.group(0).indexOf("×") < 0) {
					content = content.replaceAll(matcherX.group(0),
							"×" + matcherX.group(0).trim().replace(".", "").replaceAll("[\\s]+", ""));
				} else {
					content = content.replaceAll(matcherX.group(0),
							matcherX.group(0).trim().replace(".", "").replaceAll("[\\s]+", ""));
				}
			}

			content = content.replaceAll("[×]+", "×");
			content = content.replaceAll("[\\.]+[×]+", "×");

			content = content.replaceAll("de[\\s]+bo", "de bo");
			content = content.replaceAll("de[\\s]+boj", "de boj");
			content = content.replaceAll("de[\\s]+tong", "de tong");
			content = content.replaceAll("de[\\s]+tong[\\s]+le", "de tong le");
			content = content.replaceAll("de[\\s]+tong[\\s]+chan", "de tong chan");
			content = content.replaceAll("de[\\s]+tong[\\s]+chia[\\s]+ba", "de tong chia ba");
			content = content.replaceAll("de[\\s]+tong[\\s]+chia[\\s]+3", "de tong chia ba");
			content = content.replaceAll("de[\\s]+tong[\\s]+chia[\\s]+3[\\s]+du[\\s]+1", "de tong chia ba du 1");
			content = content.replaceAll("de[\\s]+tong[\\s]+chia[\\s]+ba[\\s]+du[\\s]+1", "de tong chia ba du 1");
			content = content.replaceAll("de[\\s]+tong[\\s]+chia[\\s]+3[\\s]+\\+[\\s]+1", "de tong chia ba du 1");
			content = content.replaceAll("de[\\s]+tong[\\s]+chia[\\s]+ba[\\s]+\\+[\\s]+1", "de tong chia ba du 1");

			content = content.replaceAll("de[\\s]+tong[\\s]+chia[\\s]+3[\\s]+du[\\s]+2", "de tong chia ba du 2");
			content = content.replaceAll("de[\\s]+tong[\\s]+chia[\\s]+ba[\\s]+du[\\s]+2", "de tong chia ba du 2");
			content = content.replaceAll("de[\\s]+tong[\\s]+chia[\\s]+3[\\s]+\\+[\\s]+2", "de tong chia ba du 2");
			content = content.replaceAll("de[\\s]+tong[\\s]+chia[\\s]+ba[\\s]+\\+[\\s]+2", "de tong chia ba du 2");

			content = content.replaceAll("de[\\s]+dau", "de dau");
			content = content.replaceAll("de[\\s]+dau[\\s]+dit", "de dau dit");
			content = content.replaceAll("de[\\s]+duoi", "de duoi");
			content = content.replaceAll("de[\\s]+dit", "de dit");
			content = content.replaceAll("de[\\s]+cham", "de cham");
			content = content.replaceAll("de[\\s]+chan[\\s]+chan", "de chan chan");
			content = content.replaceAll("de[\\s]+le[\\s]+le", "de le le");
			content = content.replaceAll("de[\\s]+chan[\\s]+le", "de chan le");
			content = content.replaceAll("de[\\s]+le[\\s]+chan", "de le chan");
			content = content.replaceAll("de[\\s]+nho[\\s]+nho", "de nho nho");
			content = content.replaceAll("denhonho", "de nho nho");
			content = content.replaceAll("detoto", "de to to");
			content = content.replaceAll("denhoto", "de nho to");
			content = content.replaceAll("denho to", "de nho to");
			content = content.replaceAll("de tonho", "de nho to");
			content = content.replaceAll("de[\\s]+to[\\s]+to", "de to to");
			content = content.replaceAll("de[\\s]+tong[\\s]+chia[\\s]+ba", "de tong chia ba");
			content = content.replaceAll("de[\\s]+tong[\\s]+chia[\\s]+3", "de tong chia ba");
			content = content.replaceAll("de[\\s]+dan[\\s]+", "de dan ");
			content = content.replaceAll("bo", "bo ");
			content = content.replaceAll("bor", "bor ");
			content = content.replaceAll("h0", "ho");
			content = content.replaceAll("u0", "uo");
			content = content.replaceAll("dauo", "dau 0");
			content = content.replaceAll("de[\\s]+kep[\\s]+bang", "de kep bang");
			content = content.replaceAll("de[\\s]+kep[\\s]+lech", "de kep lech");
			content = content.replaceAll("de[\\s]+sat[\\s]+kep", "de sat kep");

			content = content.replaceAll("lo[\\s]+kep[\\s]+bang", "lo kep bang");
			content = content.replaceAll("lo[\\s]+kep[\\s]+lech", "lo kep lech");
			content = content.replaceAll("lo[\\s]+sat[\\s]+kep", "lo sat kep");
			content = content.replaceAll("lo[\\s]+tong[\\s]+le", "lo tong le");
			content = content.replaceAll("lo[\\s]+tong[\\s]+chan", "lo tong chan");
			content = content.replaceAll("xiennquay", "xq");
			content = content.replaceAll(":", "");
			content = content.replaceAll("\\*", "×");
			content = content.replaceAll("moi con", "×");
			content = content.replaceAll("[×]+", "×");
			content = content.replaceAll("bo[\\s]+j", "boj");
			content = content.replaceAll("bo[\\s]+r", "bor");

			content = content.replaceAll("duoi", "dit");
			
			if(content.indexOf("de") >=0 && content.indexOf("dau") >=0 && content.indexOf("dit") >=0  
					&& content.replaceAll(" ", "").indexOf("daudit") < 0){
				String[] _content = content.split("dit");
				content = _content[0] + " de dit" + _content[1];
			}
			
			if(content.indexOf("lo") >=0 && content.indexOf("dau") >=0 && content.indexOf("dit") >=0  
					&& content.replaceAll(" ", "").indexOf("daudit") < 0){
				String[] _content = content.split("dit");
				content = _content[0] + " lo dit" + _content[1];
			}
			
			String reg = "([xqbodelouiaystkprchnmgj\\.\\,\\s\\d]+[\\d\\,×trieukn₫\\.\\s\\-]+)";
			Pattern pattern = Pattern.compile(reg);
			Matcher matcher = pattern.matcher(content);

			String regNumberOfGroup = "([\\d]+)([×kn₫\\s]+)";
			Pattern patternNumberOfGroup = Pattern.compile(regNumberOfGroup);
			String lastType = "";
			while (matcher.find()) {
				lastType = type;
				type = "";
				orgNumber = "";
				lsRemoveNumber = new ArrayList<>();
				String syntax = matcher.group(0);
				
				int iTrieu = syntax.indexOf("trieu");
				if(iTrieu >=0 && (syntax.indexOf("k") >= iTrieu || syntax.indexOf("n") >= iTrieu || syntax.indexOf("d") >= iTrieu) ){
					syntax = syntax.replaceAll("trieu", "");
				}else{
					syntax = syntax.replaceAll("trieu", "000k");
				}
				
				syntax = syntax.replaceAll("(\\d+?\\s+?)(boj)", "$1");

				syntax = syntax.replaceAll("xien[\\s\\d]+quay", "xq");
				syntax = syntax.replaceAll("xien[\\s]+ghep[\\s]*2", "xien ghep 2");

				if (syntax.replaceAll(" ", "").indexOf("xq2") >= 0) {

				} else {
					syntax = syntax.replaceAll("xq\\s*\\d{1}\\s+", "xq ");
				}

				if (syntax.indexOf("xien") >= 0 && syntax.indexOf("quay") > 0) {
					syntax = syntax.replaceAll("xien[\\s\\dn]*quay[y]*", "xq");
				}

				if (syntax.indexOf("de") < 0 && syntax.indexOf("lo") < 0 && syntax.indexOf("xien") < 0
						&& syntax.indexOf("xq") < 0) {
					syntax = lastType + " " + syntax.trim();
				}

				if (syntax.indexOf("de bo") >= 0 || syntax.indexOf("de boj") >= 0) {
					type = "de bo";

					orgNumber = syntax.substring(0, syntax.indexOf("×"));

					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("de bo", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("de kep bang") >= 0) {
					type = "de kep bang";

					orgNumber = syntax.substring(0, syntax.indexOf("×"));

					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("de kep bang", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("de kep lech") >= 0) {
					type = "de kep lech";

					orgNumber = syntax.substring(0, syntax.indexOf("×"));

					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("de kep lech", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("de sat kep") >= 0) {
					type = "de sat kep";

					orgNumber = syntax.substring(0, syntax.indexOf("×"));

					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("de sat kep", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				}
				// lo kep
				else if (syntax.indexOf("lo kep bang") >= 0) {
					type = "lo kep bang";

					orgNumber = syntax.substring(0, syntax.indexOf("×"));

					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("lo kep bang", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("lo kep lech") >= 0) {
					type = "lo kep lech";

					orgNumber = syntax.substring(0, syntax.indexOf("×"));

					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("lo kep lech", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("lo sat kep") >= 0) {
					type = "lo sat kep";

					orgNumber = syntax.substring(0, syntax.indexOf("×"));

					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("lo sat kep", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("de 100") >= 0) {
					type = "de 100 so";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber
							.matcher(syntax.replace("de 100 so", "").replace("bo nhung con hom truoc da ra", ""));
					while (matcherRemoveNumber.find()) {
						// 98 29 dau 1 dau 0 dit 6 dit 1
						String rm = matcherRemoveNumber.group(2);
						if (rm.replaceAll("[\\s]+", "").indexOf("daudit") > 0
								|| rm.replaceAll("[\\s]+", "").indexOf("dauduoi") > 0) {
							rm = rm.replaceAll("dau", "###dau");
						} else {
							rm = rm.replaceAll("dau", "###dau").replaceAll("dit", "###dit").replaceAll("duoi",
									"###duoi");
						}
						if (rm.indexOf("boj") >= 0) {
							rm = rm.replaceAll("boj", "###boj");
						} else {
							rm = rm.replaceAll("bo", "###bo");
						}
						rm = rm.replaceAll("tong", "###tong");
						rm = rm.replaceAll("dan", "###dan").replaceAll("kep", "###kep");
						rm = rm.replaceAll("sat", "###sat").replaceAll("duoi", "###duoi");
						rm = rm.replaceAll("cham", "###cham").replaceAll("chan", "###chan");
						rm = rm.replaceAll("le", "###le").replaceAll("nho", "###nho");
						rm = rm.replaceAll("to", "###to");

						String[] arrRm = rm.split("###*");
						for (String string : arrRm) {
							lsRemoveNumber.add(string);
						}

					}
					if (syntax.indexOf("bo nhung con hom truoc da ra") >= 0) {
						lsRemoveNumber.add("bo nhung con hom truoc da ra");
					}

				} else if (syntax.indexOf("de tong chia ba du 1") >= 0 || syntax.indexOf("de tong chia 3 du 1") >= 0) {
					type = "de tong chia ba du 1";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber
							.matcher(content.replace("de tong chia ba du 1", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("de tong chia 3 + 1") >= 0 || syntax.indexOf("de tong chia ba + 1") >= 0) {
					type = "de tong chia ba du 1";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber
							.matcher(syntax.replace("de tong chia ba du 1", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("de tong chia ba du 2") >= 0 || syntax.indexOf("de tong chia 3 du 2") >= 0) {
					type = "de tong chia ba du 2";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber
							.matcher(content.replace("de tong chia ba du 2", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("de tong chia 3 + 2") >= 0 || syntax.indexOf("de tong chia ba + 2") >= 0) {
					type = "de tong chia ba du 2";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber
							.matcher(syntax.replace("de tong chia ba du 2", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("de tong chia 3") >= 0) {
					type = "de tong chia ba";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("de tong chia 3", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("de tong chia ba") >= 0) {
					type = "de tong chia ba";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schankpleduitokp]+)([×])";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("de tong chia ba", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("de tong le") >= 0) {
					type = "de tong le";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("de tong le", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("de tong chan") >= 0) {
					type = "de tong chan";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("de tong chan", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("de tong") >= 0) {
					type = "de tong";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanledukpitokp]+)([×])";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("de tong", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("de dau dit") >= 0 || (syntax.contains("dau") && syntax.contains("dit") && syntax.contains("de"))) {
					type = "de dau dit";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));

					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("de dau dit", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("de dau duoi") >= 0 || syntax.indexOf("de dau dit") >= 0) {
					type = "de dau duoi";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));

					String regRemoveNumber = "([bor]{2,3})([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("de dau", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("de dau") >= 0) {
					type = "de dau";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));

					String regRemoveNumber = "([bor]{2,3})([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("de dau", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("de duoi") >= 0 || syntax.indexOf("de dit") >= 0) {
					type = "de duoi";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber
							.matcher(syntax.replace("de duoi", "").replace("de dit", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("de cham") >= 0) {
					type = "de cham";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("de cham", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("de chan chan") >= 0) {
					type = "de chan chan";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("de chan chan", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} // Them
				else if (syntax.indexOf("de le le") >= 0) {
					type = "de le le";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("de le le", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("de chan le") >= 0) {
					type = "de chan le";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("de chan le", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("de le chan") >= 0) {
					type = "de le chan";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("de le chan", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("de nho nho") >= 0) {
					type = "de nho nho";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("de nho nho", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("de to to") >= 0) {
					type = "de to to";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("de to to", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("de nho to") >= 0) {
					type = "de nho to";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("de nho to", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("de to nho") >= 0) {
					type = "de to nho";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("de to nho", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("de tong chia ba") >= 0) {
					type = "de tong chia ba";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("de tong chia ba", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("de dan") >= 0) {
					type = "de dan";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("de dan 05", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				}
				// Lô
				else if (syntax.indexOf("lo bo") >= 0 || syntax.indexOf("lo boj") >= 0) {
					type = "lo bo";

					orgNumber = syntax.substring(0, syntax.indexOf("×"));

					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("lo bo", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("lo kep bang") >= 0) {
					type = "lo kep bang";

					orgNumber = syntax.substring(0, syntax.indexOf("×"));

					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("lo kep bang", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("lo kep lech") >= 0) {
					type = "lo kep lech";

					orgNumber = syntax.substring(0, syntax.indexOf("×"));

					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("lo kep lech", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("lo sat kep") >= 0) {
					type = "lo sat kep";

					orgNumber = syntax.substring(0, syntax.indexOf("×"));

					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("lo sat kep", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				}
				// lo kep
				else if (syntax.indexOf("lo kep bang") >= 0) {
					type = "lo kep bang";

					orgNumber = syntax.substring(0, syntax.indexOf("×"));

					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("lo kep bang", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("lo kep lech") >= 0) {
					type = "lo kep lech";

					orgNumber = syntax.substring(0, syntax.indexOf("×"));

					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("lo kep lech", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("lo sat kep") >= 0) {
					type = "lo sat kep";

					orgNumber = syntax.substring(0, syntax.indexOf("×"));

					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("lo sat kep", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("lo 100") >= 0) {
					type = "lo 100 so";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber
							.matcher(syntax.replace("lo 100 so", "").replace("bo nhung con hom truoc da ra", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}
					if (syntax.indexOf("bo nhung con hom truoc da ra") >= 0) {
						lsRemoveNumber.add("bo nhung con hom truoc da ra");
					}

				} else if (syntax.indexOf("lo tong chia ba du 1") >= 0 || syntax.indexOf("lo tong chia 3 du 1") >= 0) {
					type = "lo tong chia ba du 1";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber
							.matcher(content.replace("lo tong chia ba du 1", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("lo tong chia 3 + 1") >= 0 || syntax.indexOf("lo tong chia ba + 1") >= 0) {
					type = "lo tong chia ba du 1";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber
							.matcher(syntax.replace("lo tong chia ba du 1", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("lo tong chia ba du 2") >= 0 || syntax.indexOf("lo tong chia 3 du 2") >= 0) {
					type = "lo tong chia ba du 2";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber
							.matcher(content.replace("lo tong chia ba du 2", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("lo tong chia 3 + 2") >= 0 || syntax.indexOf("lo tong chia ba + 2") >= 0) {
					type = "lo tong chia ba du 2";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber
							.matcher(syntax.replace("lo tong chia ba du 2", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("lo tong chia 3") >= 0) {
					type = "lo tong chia ba";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("lo tong chia 3", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("lo tong chia ba") >= 0) {
					type = "lo tong chia ba";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schankpleduitokp]+)([×])";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("lo tong chia ba", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("lo tong le") >= 0) {
					type = "lo tong le";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanledukpitokp]+)([×])";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("lo tong le", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("lo tong chan") >= 0) {
					type = "lo tong chan";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanledukpitokp]+)([×])";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("lo tong chan", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("lo tong") >= 0) {
					type = "lo tong";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanledukpitokp]+)([×])";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("lo tong", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("lo dau dit") >= 0 || (syntax.contains("dau") && syntax.contains("dit") && syntax.contains("lo"))) {
					type = "lo dau dit";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));

					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("lo", "").replace("lo dau dit", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("lo dau") >= 0) {
					type = "lo dau";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));

					String regRemoveNumber = "([bor]{2,3})([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("lo dau", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("lo duoi") >= 0 || syntax.indexOf("lo dit") >= 0) {
					type = "lo duoi";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber
							.matcher(syntax.replace("lo duoi", "").replace("lo dit", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("lo cham") >= 0) {
					type = "lo cham";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("lo cham", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("lo chan chan") >= 0) {
					type = "lo chan chan";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("lo chan chan", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} // Them
				else if (syntax.indexOf("lo le le") >= 0) {
					type = "lo le le";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("lo le le", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("lo chan le") >= 0) {
					type = "lo chan le";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("lo chan le", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("lo le chan") >= 0) {
					type = "lo le chan";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("lo le chan", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("lo nho nho") >= 0) {
					type = "lo nho nho";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("lo nho nho", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("lo to to") >= 0) {
					type = "lo to to";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("lo to to", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("lo nho to") >= 0) {
					type = "lo nho to";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("lo nho to", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("lo to nho") >= 0) {
					type = "lo to nho";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("lo to nho", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("lo tong chia ba") >= 0) {
					type = "lo tong chia ba";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("lo tong chia ba", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} else if (syntax.indexOf("lo dan") >= 0) {
					type = "lo dan";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					String regRemoveNumber = "([bor]+)([\\d\\schanleduitokp]+)";
					Pattern patternRemoveNumber = Pattern.compile(regRemoveNumber);
					Matcher matcherRemoveNumber = patternRemoveNumber.matcher(syntax.replace("lo dan 05", ""));
					while (matcherRemoveNumber.find()) {
						lsRemoveNumber.add(matcherRemoveNumber.group(2));
					}

				} 

				else if (syntax.indexOf("de") >= 0 && syntax.indexOf("×") >= 0) {
					type = "de";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
				} else if (syntax.indexOf("lo xien") >= 0 && syntax.indexOf("×") >= 0) {
					type = "lo xien";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
				} else if (syntax.indexOf("lo") >= 0 && syntax.indexOf("×") >= 0) {
					type = "lo";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
				} else if (syntax.indexOf("xq 2") >= 0 && syntax.indexOf("×") >= 0) {
					type = "xq 2";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
				} else if (syntax.indexOf("xien ghep 2") >= 0 && syntax.indexOf("×") >= 0) {
					type = "xien ghep 2";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
				} else if (syntax.indexOf("xq") >= 0 && syntax.indexOf("×") >= 0) {
					type = "xq";
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
				} else if ((syntax.indexOf("xien") >= 0) && syntax.indexOf("×") >= 0) {
					orgNumber = syntax.substring(0, syntax.indexOf("×"));
					if (syntax.matches("xien[\\s]*[0-9]{1}[\\s].*")) {
						String remove = syntax.replaceAll("xien[\\s]*([0-9]{1})[\\s\\.\\,\\;\\:\\-]+.*", "$1");
						// xien2 60 63 63 92 60 92×500k
						String syntaxRebuild = syntax.replaceAll("xien\\s*\\d{1}", "").replaceAll("xien", "");
						syntaxRebuild = syntaxRebuild.substring(0, syntaxRebuild.indexOf("×"));
						String[] arrRebuild = syntaxRebuild.trim().split("[\\s]+");
						int count = 0;
						StringBuffer sbXien = new StringBuffer();
						for (String rebuild : arrRebuild) {
							if (count == 0) {
								sbXien.append("xien");
							}

							sbXien.append(" " + rebuild);
							count++;

						}
						syntax = sbXien.append(syntax.substring(syntax.indexOf("×"))).toString();
						type = "xien" + remove;
					} else {
						type = "xien";
						orgNumber = syntax.substring(0, syntax.indexOf("×"));
					}

				} else {
					failSyntax.add(syntax);
					continue;
				}
				orgNumber = syntax.substring(0, syntax.indexOf("×"));
				boolean concated = false;
				if (orgNumber.replaceAll(" ", "").indexOf("ghepduoi") > 0
						|| orgNumber.replaceAll(" ", "").indexOf("ghepdit") > 0) {
					String buildNumber = orgNumber.replaceAll(" ", "").replaceAll("dedau", "").replaceAll("lodau", "");
					String[] buildNumbers = buildNumber.split("ghepduoi|ghepdit");
					String[] daus = buildNumbers[0].split("");
					String[] duois = buildNumbers[1].split("");
					ArrayList<String> arrBuildNumber = new ArrayList<>();
					for (String dau : daus) {
						for (String duoi : duois) {
							arrBuildNumber.add(dau + duoi);
						}
					}
					mergeArrayList(temp, arrBuildNumber);
					concated = true;
				} else if (orgNumber.replaceAll(" ", "").indexOf("ghepdau") > 0) {
					String buildNumber = orgNumber.replaceAll(" ", "").replaceAll("deduoi", "").replaceAll("loduoi",
							"");
					buildNumber = orgNumber.replaceAll(" ", "").replaceAll("dedit", "").replaceAll("lodit", "");
					String[] buildNumbers = buildNumber.split("ghepdau");
					String[] duois = buildNumbers[0].split("");
					String[] daus = buildNumbers[1].split("");
					ArrayList<String> arrBuildNumber = new ArrayList<String>();
					for (String duoi : duois) {
						for (String dau : daus) {
							arrBuildNumber.add(dau + duoi);
						}
					}
					mergeArrayList(temp, arrBuildNumber);
					concated = true;
				}

				String regGroupNumber = "([\\d×kn₫\\s\\.]+)";
				Pattern patternGroupNumber = Pattern.compile(regGroupNumber);
				Matcher matcherGroupNumber = patternGroupNumber
						.matcher(syntax.replace(".", " ").replaceAll("xien", ""));
				String oldType = type;
				while (matcherGroupNumber.find()) {
					String groupNumber = matcherGroupNumber.group(0).trim();
					if(oldType.contains("dau dit") && !groupNumber.equals("") && syntax.replace(" ", "").indexOf("daudit") <= 0){
						String check = syntax.replaceAll(" ", "");
						if(check.indexOf("dau"+groupNumber) >=0){
							type = oldType.replaceAll("dit", "");
						}else if(check.indexOf("dit"+groupNumber) >=0){
							type = oldType.replaceAll("dau", "");
						}
					}

					if (lsRemoveNumber.size() > 0 && groupNumber.indexOf("×") >= 0) {
						groupNumber = groupNumber.substring(groupNumber.indexOf("×"), groupNumber.length());
						ArrayList<String> reformatArr = new ArrayList<>();
						for (String _numbers : lsRemoveNumber) {
							if (lsKeyWordRemoveNumber.indexOf(_numbers.replaceAll("[\\d\\s]+", "").trim()) >= 0) {
								if (_numbers.trim().equals("chan")) {
									for (String string : temp) {
										if (Integer.parseInt(string) % 2 == 0) {
											reformatArr.add(string);
										}
									}
								} else if (_numbers.trim().equals("le")) {
									for (String string : temp) {
										if (Integer.parseInt(string) % 2 != 0) {
											reformatArr.add(string);
										}
									}
								} else if (_numbers.replaceAll("[\\d\\s]+", "").trim().equals("dau")) {
									String _tempNum = _numbers.replaceAll("[a-z\\s]+", "").trim();
									if (_tempNum.indexOf(" ") > 0) {
										String[] arrTemp = _tempNum.trim().split(" ");
										for (String rnum : arrTemp) {
											for (String string : temp) {
												String a1 = string.substring(0, 1);
												if (a1.equals(rnum)) {
													reformatArr.add(string);
												}
											}
										}

									} else {
										for (String string : temp) {
											String a1 = string.substring(0, 1);
											if (_tempNum.equals(a1)) {
												reformatArr.add(string);
											}
										}
									}

								} else if (_numbers.replaceAll("[\\d\\s]+", "").trim().equals("dit")
										|| _numbers.replaceAll("[\\d\\s]+", "").trim().equals("duoi")) {
									String _tempNum = _numbers.replaceAll("[a-z\\s]+", "").trim();
									if (_tempNum.indexOf(" ") > 0) {
										String[] arrTemp = _tempNum.trim().split(" ");
										for (String rnum : arrTemp) {
											for (String string : temp) {
												String a1 = string.substring(1, 2);
												if (a1.equals(rnum)) {
													reformatArr.add(string);
												}
											}
										}

									} else {
										for (String string : temp) {
											String a1 = string.substring(1, 2);
											if (_tempNum.equals(a1)) {
												reformatArr.add(string);
											}
										}
									}

								} else if (_numbers.trim().equals("kep")) {
									for (String string : temp) {
										String a1 = string.substring(0, 1);
										String a2 = string.substring(1, 2);
										if (Integer.parseInt(a1) == Integer.parseInt(a2)) {
											reformatArr.add(string);
										}
									}
								}
							} else {
								if (_numbers.trim().equals("chan chan")) {
									reformatArr.addAll(layDeDanChanChan(""));
								} else if (_numbers.trim().equals("le le")) {
									reformatArr.addAll(layDeDanLeLe(""));
								} else if (_numbers.trim().equals("chan le")) {
									reformatArr.addAll(layDeDanChanLe(""));
								} else if (_numbers.trim().equals("le chan")) {
									reformatArr.addAll(layDeDanLeChan(""));
								} else if (_numbers.trim().equals("nho nho")) {
									reformatArr.addAll(layDeDanNhoNho(""));
								} else if (_numbers.trim().equals("to to")) {
									reformatArr.addAll(layDeDanToTo(""));
								} else if (_numbers.trim().equals("nho to")) {
									reformatArr.addAll(layDeDanNhoTo(""));
								} else if (_numbers.trim().equals("to nho")) {
									reformatArr.addAll(layDeDanToNho(""));
								} else if (_numbers.trim().equals("chia ba du 1")) {
									reformatArr.addAll(layDeTongChiaBaDu1(""));
								} else if (_numbers.trim().equals("chia ba du 2")) {
									reformatArr.addAll(layDeTongChiaBaDu2(""));
								} else if (_numbers.trim().equals("chia ba")) {
									reformatArr.addAll(layDeTongChiaBa(""));
								} else if (_numbers.trim().equals("bo nhung con hom truoc da ra")) {
									String[] lsyEsterdayNumber = yesterdayNumber.split(",");
									reformatArr.addAll(Arrays.asList(lsyEsterdayNumber));
								} else {
									// L�?c trư�?ng hợp viết li�?n b�? đầu đít
									ArrayList<String> lsContainsFirst = new ArrayList<>();
									ArrayList<String> lsContainsLast = new ArrayList<>();
									String regFistLastNumber = "([dauit]*[\\s]*[\\d]+)";
									Pattern patternFistLastNumber = Pattern.compile(regFistLastNumber);
									Matcher matcherFistLastNumber = patternFistLastNumber.matcher(_numbers.trim());
									while (matcherFistLastNumber.find()) {
										String fistLastNumber = matcherFistLastNumber.group(0).trim();
										if (fistLastNumber.trim().indexOf("dau") >= 0) {
											String _dau = fistLastNumber.replaceAll("[\\s]*dau[\\s]*", "").trim();
											lsContainsFirst.add(_dau);

										} else if (fistLastNumber.trim().indexOf("dit") >= 0) {
											String _dau = fistLastNumber.replaceAll("[\\s]*dit[\\s]*", "").trim();
											lsContainsLast.add(_dau);
										} else {
											reformatArr.add(fistLastNumber);
										}

										removeContainsFistNumber(temp, lsContainsFirst);
										removeContainsLastNumber(temp, lsContainsLast);
									}
								}

							}
						}
						removeNumber(temp, reformatArr);
						reformatArr = null;
					}

					// Them
					if (groupNumber.indexOf("×") < 0) {
						if (!concated) {
							mergeArrayList(temp, getNumber(groupNumber.trim(), type));
						}
					} else if (!groupNumber.trim().equals("") && groupNumber.indexOf("×") >= 0) {
						if (concated) {
							groupNumber = groupNumber.substring(groupNumber.indexOf("×"));
						}
						Matcher matcherNumberOfGroup = patternNumberOfGroup.matcher(groupNumber);

						while (matcherNumberOfGroup.find()) {
							String number = matcherNumberOfGroup.group(0).replace("×", "");

							NumberAndUnit _numberAndUnit = new NumberAndUnit();
							_numberAndUnit.syntax = syntax;
							if (number.indexOf("n") > 0
									&& (number.indexOf("n") < (number.indexOf("k") < 0 ? 999 : number.indexOf("k")))) {

								_numberAndUnit.OrgNumber = orgNumber;
								_numberAndUnit.Numbers = temp;
								_numberAndUnit.Price = number.replaceAll("[kn₫]+", "").trim();

								_numberAndUnit.Unit = "n";
								_numberAndUnit.Type = type;
								Syntaxs.add(_numberAndUnit);
								temp = new ArrayList<>();
							} else if (number.indexOf("k") > 0
									&& (number.indexOf("k") < (number.indexOf("n") < 0 ? 999 : number.indexOf("n")))) {
								_numberAndUnit.OrgNumber = orgNumber;
								_numberAndUnit.Numbers = temp;
								_numberAndUnit.Price = number.replaceAll("[kn₫]+", "").trim();
								_numberAndUnit.Unit = "k";
								_numberAndUnit.Type = type;
								Syntaxs.add(_numberAndUnit);
								temp = new ArrayList<>();
							} else if (number.indexOf("₫") > 0) {
								_numberAndUnit.OrgNumber = orgNumber;
								_numberAndUnit.Numbers = temp;
								_numberAndUnit.Price = number.replaceAll("[kn₫]+", "").trim();
								_numberAndUnit.Unit = "d";
								_numberAndUnit.Type = type;
								Syntaxs.add(_numberAndUnit);
								temp = new ArrayList<>();
							} else {
								if ((type.equals("de dau") || type.equals("de dit") || type.equals("de duoi"))
										&& number.trim().length() == 2) {
									type = "de";
									orgNumber = "de";
								}

								if (type.equals("de bo") && number.trim().length() == 3) {
									type = "de";
									orgNumber = "de";
								} else if (type.equals("de bo")) {
									orgNumber = "de bo";
								}
								temp.addAll(getNumber(number.trim(), type));

								if (number.trim().length() > 1 && type.indexOf(" ") > 0 && !type.equals("lo xien")
										&& number.trim().length() == 3) {
									orgNumber = type.substring(0, type.indexOf(" "));
								}

								if (type.equals("lo xien")) {
									orgNumber = type + " " + temp.size();
								}
							}

						}
					}

				}
			}

			for (NumberAndUnit _numberAndUnit : Syntaxs) {
				if (_numberAndUnit.Type.indexOf("xq") >=0 && _numberAndUnit.Numbers.size() == 3) {
					_numberAndUnit.Type = "xq 3";
					String xien2_1 = _numberAndUnit.Numbers.get(0) + "-" + _numberAndUnit.Numbers.get(1);
					String xien2_2 = _numberAndUnit.Numbers.get(0) + "-" + _numberAndUnit.Numbers.get(2);
					String xien2_3 = _numberAndUnit.Numbers.get(1) + "-" + _numberAndUnit.Numbers.get(2);

					ArrayList<String> xien3 = new ArrayList<>();
					for (String string : _numberAndUnit.Numbers) {
						xien3.add(string);
					}

					_numberAndUnit.Numbers.clear();
					_numberAndUnit.Numbers.add(ArrayUtils.join(xien3.toArray(new String[xien3.size()]), "-"));

					_numberAndUnit.Numbers.add(xien2_1);
					_numberAndUnit.Numbers.add(xien2_2);
					_numberAndUnit.Numbers.add(xien2_3);
				} else if (_numberAndUnit.Type.indexOf("xq") >=0 && _numberAndUnit.Numbers.size() == 4) {
					_numberAndUnit.Type = "xq 4";
					ArrayList<String> xien4 = new ArrayList<>();
					for (String string : _numberAndUnit.Numbers) {
						xien4.add(string);
					}
					// lấy 4 cặp này ghép xiên 2 sẽ được 6 cặp xiên
					String xien2_1 = _numberAndUnit.Numbers.get(0) + "-" + _numberAndUnit.Numbers.get(1);
					String xien2_2 = _numberAndUnit.Numbers.get(0) + "-" + _numberAndUnit.Numbers.get(2);
					String xien2_3 = _numberAndUnit.Numbers.get(0) + "-" + _numberAndUnit.Numbers.get(3);
					String xien2_4 = _numberAndUnit.Numbers.get(1) + "-" + _numberAndUnit.Numbers.get(2);
					String xien2_5 = _numberAndUnit.Numbers.get(1) + "-" + _numberAndUnit.Numbers.get(3);
					String xien2_6 = _numberAndUnit.Numbers.get(2) + "-" + _numberAndUnit.Numbers.get(3);

					// và 4 cặp xiên 3
					String xien2_7 = _numberAndUnit.Numbers.get(0) + "-" + _numberAndUnit.Numbers.get(1) + "-"
							+ _numberAndUnit.Numbers.get(2);
					String xien2_8 = _numberAndUnit.Numbers.get(0) + "-" + _numberAndUnit.Numbers.get(1) + "-"
							+ _numberAndUnit.Numbers.get(3);
					String xien2_9 = _numberAndUnit.Numbers.get(1) + "-" + _numberAndUnit.Numbers.get(2) + "-"
							+ _numberAndUnit.Numbers.get(3);
					String xien2_10 = _numberAndUnit.Numbers.get(2) + "-" + _numberAndUnit.Numbers.get(3) + "-"
							+ _numberAndUnit.Numbers.get(0);

					_numberAndUnit.Numbers.clear();
					// xiên 4
					_numberAndUnit.Numbers.add(ArrayUtils.join(xien4.toArray(new String[xien4.size()]), "-"));
					// lấy 4 cặp này ghép xiên 2 sẽ được 6 cặp xiên
					_numberAndUnit.Numbers.add(xien2_1);
					_numberAndUnit.Numbers.add(xien2_2);
					_numberAndUnit.Numbers.add(xien2_3);
					_numberAndUnit.Numbers.add(xien2_4);
					_numberAndUnit.Numbers.add(xien2_5);
					_numberAndUnit.Numbers.add(xien2_6);
					// và 4 cặp xiên 3
					_numberAndUnit.Numbers.add(xien2_7);
					_numberAndUnit.Numbers.add(xien2_8);
					_numberAndUnit.Numbers.add(xien2_9);
					_numberAndUnit.Numbers.add(xien2_10);

				} else if (_numberAndUnit.Type.indexOf("xq 2") >= 0 || _numberAndUnit.Type.indexOf("xien ghep 2") >= 0 || (_numberAndUnit.Type.indexOf("xq") >= 0 && _numberAndUnit.Numbers.size() == 2)) {
					_numberAndUnit.Type = "xq 2";
					ArrayList<String> _nums = new ArrayList<>();
					String sNum = "";
					boolean exist = false;
					for (String number : _numberAndUnit.Numbers) {
						if (number.trim().length() == 1)
							continue;
						for (String number2 : _numberAndUnit.Numbers) {
							if (number2.trim().length() == 1)
								continue;
							exist = false;
							if (!number.equals(number2)) {
								int total1 = Integer.parseInt(number.trim()) + Integer.parseInt(number2.trim());
								int total2 = 0;
								for (String existNum : _nums) {
									String[] arr = existNum.split("-");
									total2 = Integer.parseInt(arr[0].trim()) + Integer.parseInt(arr[1].trim());
									if (total2 == total1) {
										exist = true;
										break;
									}
								}
								if (!exist) {
									sNum = number + "-" + number2;
									_nums.add(sNum);

								}

							}
						}

					}
					_numberAndUnit.Numbers = _nums;
				} else if (_numberAndUnit.Type.indexOf("xien2") >= 0 || _numberAndUnit.Numbers.size() == 2) {

					ArrayList<String> _nums = new ArrayList<>();
					String sNum = "";
					boolean exist = false;
					for (String number : _numberAndUnit.Numbers) {
						for (String number2 : _numberAndUnit.Numbers) {
							exist = false;
							if (!number.equals(number2)) {
								int total1 = Integer.parseInt(number.trim()) + Integer.parseInt(number2.trim());
								int total2 = 0;
								for (String existNum : _nums) {
									String[] arr = existNum.split("-");
									total2 = Integer.parseInt(arr[0].trim()) + Integer.parseInt(arr[1].trim());
									if (total2 == total1) {
										exist = true;
										break;
									}
								}
								if (!exist) {
									sNum = number + "-" + number2;
									if (_nums.size() < 2) {
										_nums.add(sNum);
									}

								}

							}
						}

					}
					_numberAndUnit.Numbers = _nums;
				} else if (_numberAndUnit.Type.indexOf("xien3") >= 0 || _numberAndUnit.Numbers.size() == 3) {
					int count = 1;
					ArrayList<String> _nums = new ArrayList<>();
					String sNum = "";
					for (String number : _numberAndUnit.Numbers) {
						sNum += number;
						if (count != 3) {
							sNum += "-";
						} else {
							_nums.add(sNum);
							count = 0;
							sNum = "";
						}
						count++;
					}
					_numberAndUnit.Numbers = _nums;
				} else if (_numberAndUnit.Type.indexOf("xien4") >= 0 || _numberAndUnit.Numbers.size() == 4) {
					int count = 1;
					ArrayList<String> _nums = new ArrayList<>();
					String sNum = "";
					for (String number : _numberAndUnit.Numbers) {
						sNum += number;
						if (count != 4) {
							sNum += "-";
						} else {
							_nums.add(sNum);
							count = 0;
							sNum = "";
						}
						count++;
					}
					_numberAndUnit.Numbers = _nums;
				} else if (_numberAndUnit.Type.equals("xien")) {
					_numberAndUnit.Type = "xien " + _numberAndUnit.Numbers.size();
				}
			}

			return Syntaxs;
		} catch (Exception e) {
			return null;
		}
	}

	protected ArrayList<String> mergeArrayList(ArrayList<String> arr1, ArrayList<String> arr2) {
		if (arr1 != null && !equalLists(arr1, arr2)) {
			for (String string : arr2) {
				arr1.add(string);
			}
		}

		return arr1;
	}

	protected ArrayList<String> removeNumber(ArrayList<String> orgArr, ArrayList<String> rmArr) {
		for (String rmNumber : rmArr) {
			orgArr.remove(rmNumber.trim());
		}
		return orgArr;
	}

	protected ArrayList<String> removeContainsFistNumber(ArrayList<String> orgArr, ArrayList<String> rmArr) {

		for (String rmNumber : rmArr) {
			for (int i = 0; i < 10; i++) {
				orgArr.remove(rmNumber.trim() + "" + i);
			}

		}
		return orgArr;
	}

	protected ArrayList<String> removeContainsLastNumber(ArrayList<String> orgArr, ArrayList<String> rmArr) {
		for (String rmNumber : rmArr) {
			for (int i = 0; i < 10; i++) {
				orgArr.remove(i + "" + rmNumber.trim());
			}

		}
		return orgArr;
	}

	// Them
	protected ArrayList<String> getNumber(String sNumber, String type) {
		// ×kn₫\\s\\.

		ArrayList<String> arrNumber = new ArrayList<>();
		if (sNumber.trim().equals("×") || sNumber.trim().equals("k") || sNumber.trim().equals("n")
				|| sNumber.trim().equals("₫") || sNumber.trim().equals(".")) {
			return arrNumber;
		}
		// System.out.println("type: "+type);
		if (type.replace("de", "").indexOf("bo") >= 0 && !sNumber.equals("")) {
			arrNumber.clear();
			ArrayList<String> boSo = layBoSo(sNumber.trim());
			arrNumber.add(ArrayUtils.join(boSo.toArray(new String[boSo.size()]), "-"));
		} else if (type.replace("de", "").indexOf("kep bang") >= 0) {
			arrNumber = layDeKepBang(sNumber.trim());
		} else if (type.replace("de", "").indexOf("kep lech") >= 0) {
			arrNumber = layDeKepLech(sNumber.trim());
		} else if (type.replace("de", "").indexOf("sat kep") >= 0) {
			arrNumber = layDeSatKep(sNumber.trim());
		} else if (type.replace("de", "").indexOf("dau dit") >= 0 || type.replace("de", "").indexOf("dau duoi") >= 0) {
			arrNumber = layDeDau(sNumber.trim());
			arrNumber.addAll(layDeDuoi(sNumber.trim()));
		} else if (type.replace("de", "").indexOf("100 so") >= 0) {
			arrNumber = layDe100So(sNumber.trim());
		} else if (type.replace("de", "").indexOf("dau") >= 0) {
			arrNumber = layDeDau(sNumber.trim());
		} else if (type.replace("de", "").indexOf("duoi") >= 0 || type.replace("de", "").indexOf("dit") >= 0) {
			arrNumber = layDeDuoi(sNumber.trim());
		} else if (type.replace("de", "").indexOf("cham") >= 0) {
			arrNumber = layCham(sNumber.trim());
		} else if (type.replace("de", "").indexOf("chan chan") >= 0) {
			arrNumber = layDeDanChanChan(sNumber.trim());
		} else if (type.replace("de", "").indexOf("le le") >= 0) {
			arrNumber = layDeDanLeLe(sNumber.trim());
		} else if (type.replace("de", "").indexOf("chan le") >= 0) {
			arrNumber = layDeDanChanLe(sNumber.trim());
		} else if (type.replace("de", "").indexOf("le chan") >= 0) {
			arrNumber = layDeDanLeChan(sNumber.trim());
		} else if (type.replace("de", "").indexOf("nho nho") >= 0) {
			arrNumber = layDeDanNhoNho(sNumber.trim());
		} else if (type.replace("de", "").indexOf("to to") >= 0) {
			arrNumber = layDeDanToTo(sNumber.trim());
		} else if (type.replace("de", "").indexOf("nho to") >= 0) {
			arrNumber = layDeDanNhoTo(sNumber.trim());
		} else if (type.replace("de", "").indexOf("to nho") >= 0) {
			arrNumber = layDeDanToNho(sNumber.trim());
		} else if (type.replace("de", "").indexOf("tong chia ba du 1") >= 0) {
			arrNumber = layDeTongChiaBaDu1(sNumber.trim());
		} else if (type.replace("de", "").indexOf("tong chia 3 + 1") >= 0) {
			arrNumber = layDeTongChiaBaDu1(sNumber.trim());
		} else if (type.replace("de", "").indexOf("tong chia 3 du 2") >= 0) {
			arrNumber = layDeTongChiaBaDu2(sNumber.trim());
		} else if (type.replace("de", "").indexOf("tong chia 3 + 2") >= 0) {
			arrNumber = layDeTongChiaBaDu2(sNumber.trim());
		} else if (type.replace("de", "").indexOf("tong chia ba du 2") >= 0) {
			arrNumber = layDeTongChiaBaDu2(sNumber.trim());
		} else if (type.replace("de", "").indexOf("tong chia ba + 2") >= 0) {
			arrNumber = layDeTongChiaBaDu2(sNumber.trim());
		} else if (type.replace("de", "").indexOf("tong chia 3") >= 0
				|| type.replace("de", "").indexOf("tong chia ba") >= 0) {
			arrNumber = layDeTongChiaBa(sNumber.trim());
		} else if (type.replace("de", "").indexOf("tong le") >= 0) {
			arrNumber = layTongChanLe(true);
		} else if (type.replace("de", "").indexOf("tong chan") >= 0) {
			arrNumber = layTongChanLe(false);
		} else if (type.replace("de", "").indexOf("tong") >= 0) {
			arrNumber = layDeBoTong(sNumber.trim());
		} else if (type.replace("de", "").indexOf("dan") >= 0) {
			if (!sNumber.trim().equals("")) {
				arrNumber = layDeDan(sNumber);
			}

		} /*
			 * else if (type.replace("de", "").indexOf("dan 05") >= 0) {
			 * arrNumber = layDeDan("05"); } else if (type.replace("de",
			 * "").indexOf("dan 16") >= 0) { arrNumber = layDeDan("16"); } else
			 * if (type.replace("de", "").indexOf("dan 27") >= 0) { arrNumber =
			 * layDeDan("27"); } else if (type.replace("de",
			 * "").indexOf("dan 38") >= 0) { arrNumber = layDeDan("38"); } else
			 * if (type.replace("de", "").indexOf("dan 49") >= 0) { arrNumber =
			 * layDeDan("49"); }
			 */ else {
			//// ////System.out.println("sNumber " + sNumber);

			if (isNumeric(sNumber.trim().replace(" ", ""))) {
				if (sNumber.trim().length() == 3) {
					arrNumber.add(sNumber.trim().substring(0, 2));
					arrNumber.add(sNumber.trim().substring(1, 3));
				} else {
					if (!sNumber.trim().equals("")) {
						arrNumber.add(sNumber.trim());
					}

				}
			}

		}

		return arrNumber;
	}

	public boolean isNumeric(String str) {
		for (char c : str.toCharArray()) {
			if (!Character.isDigit(c))
				return false;
		}
		return true;
	}

	protected ArrayList<String> layDeKepBang(String number) {
		String[] kepBang = { "00", "11", "22", "33", "44", "55", "66", "77", "88", "99" };
		return new ArrayList<String>(Arrays.asList(kepBang));
	}

	protected ArrayList<String> layDeKepLech(String number) {
		String[] kepBang = { "05", "50", "16", "61", "27", "72", "38", "83", "49", "94" };
		return new ArrayList<String>(Arrays.asList(kepBang));
	}

	protected ArrayList<String> layDeSatKep(String number) {
		String[] kepBang = { "09", "90", "01", "10", "12", "21", "23", "32", "34", "43", "45", "54", "56", "65", "67",
				"76", "78", "87", "89", "98" };
		return new ArrayList<String>(Arrays.asList(kepBang));
	}

	protected ArrayList<String> layTongChanLe(boolean le) {
		ArrayList<String> lsNums = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (le && ((i + j) % 2) != 0) {
					lsNums.add(i + "" + j);
				} else if (!le && ((i + j) % 2) == 0) {
					lsNums.add(i + "" + j);
				}
			}
		}
		return lsNums;
	}

	protected ArrayList<String> layBoSo(String number) {

		String[] bo01 = { "01", "10", "06", "60", "51", "15", "56", "65" };
		String[] bo02 = { "02", "20", "07", "70", "52", "25", "57", "75" };
		String[] bo03 = { "03", "30", "08", "80", "53", "35", "58", "85" };
		String[] bo04 = { "04", "40", "09", "90", "54", "45", "59", "95" };
		String[] bo12 = { "12", "21", "17", "71", "62", "26", "67", "76" };
		String[] bo13 = { "13", "31", "18", "81", "63", "36", "68", "86" };
		String[] bo14 = { "14", "41", "19", "91", "64", "46", "69", "96" };
		String[] bo23 = { "23", "32", "28", "82", "73", "37", "78", "87" };
		String[] bo24 = { "24", "42", "29", "92", "74", "47", "79", "97" };
		String[] bo34 = { "34", "43", "39", "93", "84", "48", "89", "98" };
		String[] bo00 = { "00", "55", "05", "50" };
		String[] bo11 = { "11", "66", "16", "61" };
		String[] bo22 = { "22", "77", "27", "72" };
		String[] bo33 = { "33", "88", "38", "83" };
		String[] bo44 = { "44", "99", "49", "94" };

		ArrayList<String> result = new ArrayList<String>();
		if (ArrayUtils.indexOf(bo01, number.trim()) >= 0 || number.equals("01")) {
			result = new ArrayList<String>(Arrays.asList(bo01));
		} else if (ArrayUtils.indexOf(bo02, number.trim()) >= 0 || number.equals("02")) {
			result = new ArrayList<String>(Arrays.asList(bo02));
		} else if (ArrayUtils.indexOf(bo03, number.trim()) >= 0 || number.equals("03")) {
			result = new ArrayList<String>(Arrays.asList(bo03));
		} else if (ArrayUtils.indexOf(bo04, number.trim()) >= 0 || number.equals("04")) {
			result = new ArrayList<String>(Arrays.asList(bo04));
		} else if (ArrayUtils.indexOf(bo12, number.trim()) >= 0 || number.equals("12")) {
			result = new ArrayList<String>(Arrays.asList(bo12));
		} else if (ArrayUtils.indexOf(bo13, number.trim()) >= 0 || number.equals("13")) {
			result = new ArrayList<String>(Arrays.asList(bo13));
		} else if (ArrayUtils.indexOf(bo14, number.trim()) >= 0 || number.equals("14")) {
			result = new ArrayList<String>(Arrays.asList(bo14));
		} else if (ArrayUtils.indexOf(bo23, number.trim()) >= 0 || number.equals("23")) {
			result = new ArrayList<String>(Arrays.asList(bo23));
		} else if (ArrayUtils.indexOf(bo24, number.trim()) >= 0 || number.equals("24")) {
			result = new ArrayList<String>(Arrays.asList(bo24));
		} else if (ArrayUtils.indexOf(bo34, number.trim()) >= 0 || number.equals("34")) {
			result = new ArrayList<String>(Arrays.asList(bo34));
		} else if (ArrayUtils.indexOf(bo00, number.trim()) >= 0 || number.equals("00")) {
			result = new ArrayList<String>(Arrays.asList(bo00));
		} else if (ArrayUtils.indexOf(bo11, number.trim()) >= 0 || number.equals("11")) {
			result = new ArrayList<String>(Arrays.asList(bo11));
		} else if (ArrayUtils.indexOf(bo22, number.trim()) >= 0 || number.equals("22")) {
			result = new ArrayList<String>(Arrays.asList(bo22));
		} else if (ArrayUtils.indexOf(bo33, number.trim()) >= 0 || number.equals("33")) {
			result = new ArrayList<String>(Arrays.asList(bo33));
		} else if (ArrayUtils.indexOf(bo44, number.trim()) >= 0 || number.equals("44")) {
			result = new ArrayList<String>(Arrays.asList(bo44));
		}

		return result;
	}

	protected ArrayList<String> layDeBoTong(String number) {
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

	protected ArrayList<String> layDe100So(String number) {

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

	protected ArrayList<String> layDeDau(String number) {

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

	protected ArrayList<String> layDeDuoi(String number) {

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

	protected ArrayList<String> layCham(String number) {
		ArrayList<String> result = new ArrayList<String>();
		if (number.trim().equals(""))
			return result;

		if (!number.trim().equals("")) {
			for (int i = 0; i < 10; i++) {
				if (result.indexOf(number + "" + i) < 0) {
					result.add(number + "" + i);
				}

				if (result.indexOf(i + "" + number) < 0) {
					result.add(i + "" + number);
				}

			}
		}

		return result;
	}

	protected ArrayList<String> layDeDanChanChan(String number) {
		String[] soChan = { "00", "22", "44", "66", "88", "02", "20", "04", "40", "06", "60", "08", "80", "24", "42",
				"26", "62 ", "28", "82", "46", "64", "48", "84", "68", "86" };

		ArrayList<String> result = new ArrayList<String>(Arrays.asList(soChan));

		return result;
	}

	protected ArrayList<String> layDeDanLeLe(String number) {
		String[] soLe = { "11", "33", "55", "77", "99", "13", "31", "15", "51", "17", "71", "19", "91", "35", "53",
				"37", "73 ", "39", "93", "57", "75", "59", "95", "79", "97" };

		ArrayList<String> result = new ArrayList<String>(Arrays.asList(soLe));

		return result;
	}

	protected ArrayList<String> layDeDanChanLe(String number) {
		String[] deChanLe = { "01", "03", "05", "07", "09", "21", "23", "25", "27", "29", "41", "43", "45", "47", "49",
				"61", "63 ", "65", "67", "69", "81", "83", "85", "87", "89" };
		ArrayList<String> result = new ArrayList<String>(Arrays.asList(deChanLe));

		return result;
	}

	protected ArrayList<String> layDeDanLeChan(String number) {

		String[] deLeChan = { "10", "12", "14", "16", "18", "30", "32", "34", "36", "38", "50", "52", "54", "56", "58",
				"70", "72 ", "74", "76", "78", "90", "92", "94", "96", "98" };
		ArrayList<String> result = new ArrayList<String>(Arrays.asList(deLeChan));

		return result;
	}

	protected ArrayList<String> layDeDanNhoNho(String number) {

		String[] deNhoNho = { "00", "11", "22", "33", "44", "01", "10", "02", "20", "03", "30", "04", "40", "12", "21",
				"13", "31 ", "14", "41", "23", "32", "24", "42", "34", "43" };
		ArrayList<String> result = new ArrayList<String>(Arrays.asList(deNhoNho));

		return result;
	}

	protected ArrayList<String> layDeDanToTo(String number) {

		String[] deToTo = { "55", "66", "77", "88", "99", "56", "65", "57", "75", "58", "85", "59", "95", "67", "76",
				"68", "86 ", "69", "96", "78", "87", "79", "97", "89", "98" };

		ArrayList<String> result = new ArrayList<String>(Arrays.asList(deToTo));

		return result;
	}

	protected ArrayList<String> layDeDanNhoTo(String number) {

		String[] deToTo = { "05", "06", "07", "08", "09", "15", "16", "17", "18", "19", "25", "26", "27", "28", "29",
				"35", "36 ", "37", "38", "39", "45", "46", "47", "48", "49" };

		ArrayList<String> result = new ArrayList<String>(Arrays.asList(deToTo));

		return result;
	}

	protected ArrayList<String> layDeDanToNho(String number) {

		String[] deToTo = { "90", "91", "92", "93", "94", "80", "81", "82", "83", "84", "70", "71", "72", "73", "74",
				"60", "61 ", "62", "63", "64", "50", "51", "52", "53", "54" };

		ArrayList<String> result = new ArrayList<String>(Arrays.asList(deToTo));

		return result;
	}

	protected ArrayList<String> layDeTongChiaBa(String number) {
		String[] deTongChiaBa = { "00", "03", "06", "09", "12", "15", "18", "21", "24", "27", "30", "33", "36", "39",
				"42", "45", "48", "51", "54", "57", "60", "63", "66", "69", "72", "75", "78", "81", "84", "87", "90",
				"93", "96", "99" };
		ArrayList<String> result = new ArrayList<String>(Arrays.asList(deTongChiaBa));

		return result;
	}

	protected ArrayList<String> layDeTongChiaBaDu1(String number) {
		String[] deTongChiaBa = { "01", "04", "07", "10", "13", "16", "19", "22", "25", "28", "31", "34", "37", "40",
				"43", "46", "49", "52", "55", "58", "61", "64", "67", "70", "73", "76", "79", "82", "85", "88", "91",
				"94", "97" };
		ArrayList<String> result = new ArrayList<String>(Arrays.asList(deTongChiaBa));

		return result;
	}

	protected ArrayList<String> layDeTongChiaBaDu2(String number) {
		String[] deTongChiaBa = { "02", "05", "08", "11", "14", "17", "20", "23", "26", "29", "32", "35", "38", "41",
				"44", "47", "50", "53", "56", "59", "62", "65", "68", "71", "74", "77", "80", "83", "86", "89", "92",
				"95", "98" };
		ArrayList<String> result = new ArrayList<String>(Arrays.asList(deTongChiaBa));

		return result;
	}

	protected ArrayList<String> layDeDan(String number) {
		ArrayList<String> result = new ArrayList<String>();
		String[] arrNums = number.split("");
		int iFirstNum = Integer.parseInt(arrNums[0]);
		int iSecondNum = Integer.parseInt(arrNums[1]);
		ArrayList<Integer> arrFirstNums = new ArrayList();
		ArrayList<Integer> arrSecondNumNums = new ArrayList();

		for (int i = iFirstNum; i <= iSecondNum; i++) {
			arrFirstNums.add(i);
			arrSecondNumNums.add(i);
		}

		for (int fNum : arrFirstNums) {
			for (int sNum : arrFirstNums) {
				result.add(fNum + "" + sNum);
			}
		}

		/*
		 * String[] deDan05 = { "00", "01", "02", "03", "04", "05", "10", "11",
		 * "12", "13", "14", "15", "20", "21", "22", "23", "24", "25", "30",
		 * "31", "32", "33", "34", "35", "40", "41", "42", "43", "44", "45",
		 * "50", "51", "52", "5 3", "54", "55" };
		 * 
		 * 
		 * String[] deDan16 = { "11", "12", "13", "14", "15", "16", "21", "22",
		 * "23", "24", "25", "26", "31", "32", "33", "34", "35", "36", "41",
		 * "42", "43", "44", "45", "46", "51", "52", "53", "54", "55", "56",
		 * "61", "62", "63", "6 4", "65", "66" };
		 * 
		 * String[] deDan27 = { "22", "23", "24", "25", "26", "27", "32", "33",
		 * "34", "35", "36", "37", "42", "43", "44", "45", "46", "47", "52",
		 * "53", "54", "55", "56", "57", "62", "63", "64", "65", "66", "67",
		 * "72", "73", "74", "7 5", "76", "77" };
		 * 
		 * String[] deDan38 = { "33", "34", "35", "36", "37", "38", "43", "44",
		 * "45", "46", "47", "48", "53", "54", "55", "56", "57", "58", "63",
		 * "64", "65", "66", "67", "68", "73", "74", "75", "76", "77", "78",
		 * "83", "84", "85", "8 6", "87", "88" };
		 * 
		 * String[] deDan49 = { "44", "45", "46", "47", "48", "49", "54", "55",
		 * "56", "57", "58", "59", "64", "65", "66", "67", "68", "69", "74",
		 * "75", "76", "77", "78", "79", "84", "85", "86", "87", "88", "89",
		 * "94", "95", "96", "9 7", "98", "99" };
		 * 
		 * 
		 * 
		 * if (number.equals("05")) { result = new
		 * ArrayList<String>(Arrays.asList(deDan05)); } else if
		 * (number.equals("16")) { result = new
		 * ArrayList<String>(Arrays.asList(deDan16)); } else if
		 * (number.equals("27")) { result = new
		 * ArrayList<String>(Arrays.asList(deDan27)); } else if
		 * (number.equals("38")) { result = new
		 * ArrayList<String>(Arrays.asList(deDan38)); } else if
		 * (number.equals("49")) { result = new
		 * ArrayList<String>(Arrays.asList(deDan49)); }
		 */
		return result;
	}

	public boolean equalLists(ArrayList<String> a, ArrayList<String> b) {
		// Check for sizes and nulls
		if ((a.size() != b.size()) || (a == null && b != null) || (a != null && b == null)) {
			return false;
		}

		// Sort and compare the two lists
		Collections.sort(a);
		Collections.sort(b);
		return a.equals(b);
	}
}
