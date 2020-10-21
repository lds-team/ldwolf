package com.bingo.analyze;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.bingo.analyze.calculation.BingoCalc;
import com.bingo.analyze.calculation.KetQua;
import com.bingo.common.utils.ArrayUtils;
import com.bingo.common.utils.Trung;
import com.bingo.common.utils.Utils;
import com.bingo.common.utils.ValidateSyntax;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AnalyzeSMSNew {

	private ArrayList<String> allNumbersResult;

	private String sDB = "";

	private String contentFormat;

	public ArrayList<Analyze> bingoAnalyze(String smsContent, String todayBingoResult, String yesterdayBingoResult) {

		Analyze objAnalyze = new Analyze();
		ArrayList<Analyze> arrAnalyze = new ArrayList<>();

		AnalyzeSMSNew analyze = new AnalyzeSMSNew();

		Analyze _analyze = analyze.validateMessage(smsContent, null);
		if (_analyze.error) {
			arrAnalyze.add(_analyze);
			return arrAnalyze;
		}

		ArrayList<NumberAndUnit> _syntaxs = analyze.analyzeMessage(_analyze.analyzeMessage);
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
								objAnalyze.winSyntax = objAnalyze.winNumber + "x" + _numberAndUnit.Price + ""
										+ _numberAndUnit.Unit;
							}
						} else {
							for (String num : arrNums) {
								if (sDB.substring(sDB.length() - 2).equals(num.trim())) {
									BigDecimal resultThangDe = BingoCalc.initial()
											.calc(Integer.parseInt(_numberAndUnit.Price), BingoCalc.HS_THANG_DE);
									objAnalyze.guestWin = resultThangDe;
									objAnalyze.winNumber = num.trim();
									objAnalyze.winSyntax = objAnalyze.winNumber + "x" + _numberAndUnit.Price + ""
											+ _numberAndUnit.Unit;
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
										objAnalyze.winNumber = objAnalyze.winNumber == null ? _num.trim()
												: objAnalyze.winNumber + "-" + _num.trim();
										objAnalyze.winSyntax = objAnalyze.winNumber + "x" + _numberAndUnit.Price + ""
												+ _numberAndUnit.Unit;
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

		return arrAnalyze;
	}

	public ArrayList<Analyze> analyzeMessageWithOutResult(String smsContent, Map<String, String> custom) {

		Analyze objAnalyze = new Analyze();
		ArrayList<Analyze> arrAnalyze = new ArrayList<>();

		AnalyzeSMSNew analyze = new AnalyzeSMSNew();

		Analyze _analyze = analyze.validateMessage(smsContent, custom);
		if (_analyze.error) {
			arrAnalyze.add(_analyze);
			return arrAnalyze;
		}

		ArrayList<NumberAndUnit> _syntaxs = analyze.analyzeMessage(_analyze.analyzeMessage);
		BigDecimal thucThu;

		double HE_SO = 0;

		if (_syntaxs == null) {
			objAnalyze.error = true;
			objAnalyze.errorMessage = "Không thể nhận diện được cú pháp này";
			arrAnalyze.add(objAnalyze);
			return arrAnalyze;
		}

		for (NumberAndUnit _numberAndUnit : _syntaxs) {

			thucThu = BigDecimal.ZERO;

			objAnalyze = new Analyze();
			objAnalyze.numberAndUnit = _numberAndUnit;

			if (_numberAndUnit.Numbers == null || _numberAndUnit.Numbers.size() == 0)
			{
				try {
					objAnalyze.error = true;
					String errSyntax = _analyze.analyzeMessage.replaceFirst("^(\\n)", "").split("\\n")[_syntaxs.indexOf(_numberAndUnit)];
					objAnalyze.errorMessage = errSyntax;
					objAnalyze.errorNotify = "Lỗi khi phân tích cú pháp: " + errSyntax;
				} catch (IndexOutOfBoundsException e) {
					objAnalyze.errorMessage = _analyze.analyzeMessage;
					objAnalyze.errorNotify = "Lỗi phân tích cú pháp: Number Size = 0";
				}
			}
			else
			{
				for (String number : _numberAndUnit.Numbers) {
					if (!number.trim().equals("")) {
						// Số ti�?n thu của khách
						String type = _numberAndUnit.Type;
						if (type.contains("de"))
						{
							HE_SO = BingoCalc.HS_DE;
						} else if (type.contains("lo"))
						{
							HE_SO = BingoCalc.HS_LO;
						}
						else
						{
							if (number.indexOf("-") > 0)
							{
								int count = number.length() - number.replace("-", "").length();

								switch (count) {
									case 1:
										// xien2
										HE_SO = BingoCalc.HS_XIEN_2;
										break;
									case 2:
										// xien3
										HE_SO = BingoCalc.HS_XIEN_3;
										break;
									case 3:
										// xien4
										HE_SO = BingoCalc.HS_XIEN_4;
										break;
									default:
										HE_SO = BingoCalc.HS_NONE;
										break;
								}
							}
						}

						BigDecimal resXien = BingoCalc.initial()
								.calc(Integer.parseInt(_numberAndUnit.Price), HE_SO);
						thucThu = thucThu.add(resXien);
						objAnalyze.actuallyCollected = thucThu;
					}
				}
			}

			arrAnalyze.add(objAnalyze);
		}

		return arrAnalyze;
	}

	@SuppressWarnings("Error format content.")
	private ArrayList<NumberAndUnit> analyzeMessage(String analyzeMessage) {
		ArrayList<ExtractObject> extractObjectArray = new ArrayList<>();

		// Format content
		/*String content = formatContent(smsContent);*/
		String[] contentArray = analyzeMessage.split("\n");

		for (int index = 0; index < contentArray.length; index++) {
			String syntax = contentArray[index].trim();

//			String regexTrung = "(de|lo)(.*)(trung|trong)(.*)(x[0-9]+\\s*k|x[0-9]+\\s*d)";
//			if (syntax.matches(regexTrung))
//			{
//				boolean removeDuplicate;
//
//				if (removeDuplicate = syntax.matches(".*bor\\s*(trung|trong)\\W*(x[0-9]+\\s*k|x[0-9]+\\s*d)"))
//				{
//					syntax = syntax.replaceAll("bor\\s*(trung|trong)", "");
//				}
//				else if (removeDuplicate = syntax.matches(".*bor\\s*(trung|trong).*"))
//				{
//					//TODO this case will support for syntax has "trung|trong" is not in the end.
//					//For case bor trung is not in the end of syntax.
//					syntax = syntax.replaceAll("bor\\s*(trung|trong)", "bor");
//					syntax = syntax.replaceAll("bor\\W*bor", "bor");
//				}
//
//				if (syntax.matches(".*(trung|trong).*"))
//				{
//					//TODO Not support for case ...bor ... trung... x...k
//					ArrayList<ExtractObject> analyzeSyntax = new ArrayList<>();
//					Matcher matcherTrung = Pattern.compile(regexTrung).matcher(syntax);
//					while (matcherTrung.find()) {
//						String type = matcherTrung.group(1).trim();
//						String cost = matcherTrung.group(5).trim();
//
//						String[] syntaxTrung = syntax.split("\\W*(trung|trong)\\W*");
//						for (String _sTrung : syntaxTrung)
//						{
//							if (!_sTrung.isEmpty())
//							{
//								if (!_sTrung.contains(type))
//								{
//									_sTrung = type + " " + _sTrung;
//								}
//
//								if (!_sTrung.contains(cost))
//								{
//									_sTrung = _sTrung + cost;
//								}
//
//								if (syntax.contains("de"))
//								{
//									analyzeSyntax.addAll(ExtracDe.analyze(_sTrung));
//								}
//								else
//								{
//									analyzeSyntax.addAll(ExtracLo.analyze(_sTrung));
//								}
//							}
//						}
//
//						int size = analyzeSyntax.size();
//						List<String>[] allNums = new ArrayList[size];
//						for (int i = 0; i < size; i++)
//						{
//							allNums[i] = analyzeSyntax.get(i).Numbers;
//						}
//
//						analyzeSyntax.get(size - 1).Numbers = Trung.getTrung(removeDuplicate, allNums);
//						extractObjectArray.add(analyzeSyntax.get(size - 1));
//					}
//				}
//				else
//				{
//					ArrayList<ExtractObject> analyzeSyntax = new ArrayList<>();
//
//					if (syntax.contains("de") || syntax.contains("bacang"))
//						analyzeSyntax.addAll(ExtracDe.analyze(syntax));
//					else
//						analyzeSyntax.addAll(ExtracLo.analyze(syntax));
//
//
//					int size = analyzeSyntax.size();
//					List<String>[] allNums = new ArrayList[size];
//					for (int i = 0; i < size; i++)
//					{
//						allNums[i] = analyzeSyntax.get(i).Numbers;
//					}
//
//					analyzeSyntax.get(size - 1).Numbers = Trung.getTrung(removeDuplicate, allNums);
//					extractObjectArray.add(analyzeSyntax.get(size - 1));
//				}
//			}
//			else
//			{
//				if (syntax.contains("de") || syntax.contains("bacang"))
//					extractObjectArray.addAll(ExtracDe.analyze(syntax));
//				else
//					extractObjectArray.addAll(ExtracLo.analyze(syntax));
//			}


			String prefix = getPrefix(syntax);
			String cost = Utils.getCost(syntax);
			boolean removeDuplicate;

			if (removeDuplicate = syntax.matches(".*bor\\s*(trung|trong)\\W*(x[0-9]+\\s*k|x[0-9]+\\s*d)"))
			{
				syntax = syntax.replaceAll("bor\\s*(trung|trong)", "");
			}
			else if (removeDuplicate = syntax.matches(".*bor\\s*(trung|trong).*"))
			{
				//For case bor trung is not in the end of syntax.
				syntax = syntax.replaceAll("bor\\s*(trung|trong)", "bor");
				syntax = syntax.replaceAll("bor\\W*bor", "bor");
			}

			if (syntax.contains("bor"))
			{
				ExtractObject obj = new ExtractObject();

				String mainSyntax = Utils.getRootSyntaxString(syntax);
				String remove = Utils.getRemoveSyntaxString(syntax);

				boolean extractRemove = extractRemoveSyntax(obj, remove, prefix, cost);
				boolean extractMain = exTractMainSyntax(removeDuplicate, obj, mainSyntax + cost, prefix, cost);

				if (!extractRemove || !extractMain) return null;

				obj.Syntax = syntax;
				extractObjectArray.add(obj);
			}
			else
			{
				ExtractObject extractObject = extractSyntaxNotContainBor(removeDuplicate, syntax, prefix, cost);
				if (extractObject == null) return null;
				extractObject.Syntax = syntax;
				extractObjectArray.add(extractObject);
			}
		}

		return explainNumber(extractObjectArray);
	}

	private boolean exTractMainSyntax(boolean removeDuplicate, ExtractObject obj, String syntax, String prefix, String cost) {
		ExtractObject extractObject = extractSyntaxNotContainBor(removeDuplicate, syntax, prefix, cost);
		if (extractObject == null)
		{
			return false;
		}
		else
		{
			obj.Numbers = extractObject.Numbers;
			obj.Syntax = syntax;
			obj.Money = extractObject.Money;
			obj.Unit = extractObject.Unit;
			obj.Type = extractObject.Type;
			return true;
		}
	}

	private boolean extractRemoveSyntax(ExtractObject obj, String removeSyntax, String prefix, String cost) {
		obj.RemoveNumbers = new ArrayList<>();

		String[] syntaxBor = removeSyntax.split("\\W*bor\\W*");
		for (String syntax : syntaxBor)
		{
			syntax = syntax.trim();
			if (syntax.isEmpty()) continue;

			if (syntax.matches(".*(trung|trong).*"))
			{
				String[] syntaxTrung = syntax.split("\\W*(trung|trong)\\W*");
				ArrayList<String>[] analyzeSyntax = new ArrayList[syntaxTrung.length];

				for (int i = 0; i < syntaxTrung.length; i++)
				{
					String s = syntaxTrung[i];
					if (s.isEmpty()) continue;

					s = buildToSyntax(s, prefix, cost);

					ArrayList<String> numRemove = new ArrayList<>();
					String[] breakBor = rebuildShortSyntax(s, prefix, true).split("\\n");
					for (String sBor : breakBor)
					{
						if (sBor.isEmpty()) continue;

						ArrayList<ExtractObject> extractObject = normalAnalyze(sBor.trim());
						for (ExtractObject object : extractObject)
						{
							if (object.Numbers == null || object.Numbers.size() == 0) return false;
							numRemove.addAll(object.Numbers);
						}
					}

					analyzeSyntax[i] = numRemove;
				}

				List<ArrayList<String>> list = new ArrayList<>();

				for(ArrayList<String> arrs : analyzeSyntax) {
					if(arrs != null && arrs.size() > 0) {
						list.add(arrs);
					}
				}

				analyzeSyntax = list.toArray(new ArrayList[list.size()]);
				ArrayList<String> removeNumbers = Trung.getTrung(false, false, analyzeSyntax);

				ArrayList<RemoveObject> removeList = new ArrayList<>();
				for (String rm : removeNumbers)
				{
					RemoveObject remove = new RemoveObject();
					remove.Number = rm;
					remove.Type = prefix;
					removeList.add(remove);
				}

				obj.RemoveNumbers.addAll(removeList);
			}
			else
			{
				syntax = buildToSyntax(syntax, prefix, cost);
				String[] breakBor = rebuildShortSyntax(syntax, prefix, true).split("\\n");
				for (String sBor : breakBor)
				{
					if (sBor.isEmpty()) continue;

					ExtractObject removeObj = getNumberFromSyntax(false, sBor, true);
					if (removeObj != null)
					{
						ArrayList<String> removeNumbers = removeObj.Numbers;

						ArrayList<RemoveObject> removeList = new ArrayList<>();
						for (String rm : removeNumbers)
						{
							RemoveObject remove = new RemoveObject();
							remove.Number = rm;
							remove.Type = removeObj.Type;
							removeList.add(remove);
						}

						obj.RemoveNumbers.addAll(removeList);
					}
					else
					{
						return false;
					}
				}
			}
		}

		return true;
	}

	private ExtractObject extractSyntaxNotContainBor(boolean removeDuplicate, String syntax, String prefix, String cost) {
		if (syntax.matches(".*(trung|trong).*"))
		{
			return getTrungFromList(removeDuplicate, syntax.replaceAll(prefix, "").replaceAll(cost, ""), prefix, cost);
		}
		else
		{
			return getNotTrungFromList(removeDuplicate, syntax, prefix, cost);
		}
	}

	private ExtractObject getTrungFromList(boolean removeDuplicate, String syntax, String prefix, String cost) {
		String[] syntaxTrung = syntax.split("\\W*(trung|trong)\\W*");
		ArrayList<ExtractObject> analyzeSyntax = new ArrayList<>();

		for (String s : syntaxTrung)
		{
			if (s.isEmpty()) continue;
			s = prefix + " " + s + cost;
			s = rebuildShortSyntax(s, prefix);
			String[] explainSyntax = s.split("\\n");
			ArrayList<ExtractObject> list = new ArrayList<>();
			for (String sm : explainSyntax)
			{
				if (sm.isEmpty()) continue;

				sm = buildToSyntax(sm, prefix, cost);

				ArrayList<ExtractObject> objects = normalAnalyze(sm.trim());

				for (ExtractObject eObj : objects) if (eObj.Numbers == null || eObj.Numbers.size() == 0) return null;

				list.addAll(objects);
			}

			if (list.size() == 0) return null;

			ArrayList<String> buildNumber = new ArrayList<>();
			ExtractObject extractObject = list.get(0);
			for (ExtractObject o : list)
			{
				if (o.Numbers == null || o.Numbers.size() == 0) return null;
				buildNumber.addAll(o.Numbers);
			}
			extractObject.Numbers = buildNumber;
			extractObject.Syntax = syntax;
			analyzeSyntax.add(extractObject);
		}

		return getNumberFromSyntax(removeDuplicate, analyzeSyntax, false);
	}

	private ExtractObject getNotTrungFromList(boolean removeDuplicate, String s, String prefix, String cost) {
		ArrayList<ExtractObject> analyzeSyntax = new ArrayList<>();

		s = rebuildShortSyntax(s, prefix);
		String[] explainSyntax = s.split("\\n");
		ArrayList<ExtractObject> list = new ArrayList<>();
		for (String sm : explainSyntax)
		{
			if (sm.isEmpty()) continue;

			sm = buildToSyntax(sm, prefix, cost);

			ArrayList<ExtractObject> objects = normalAnalyze(sm.trim());

			for (ExtractObject eObj : objects) if (eObj.Numbers == null || eObj.Numbers.size() == 0) return null;

			list.addAll(objects);
		}

		if (list.size() == 0) return null;

		ArrayList<String> buildNumber = new ArrayList<>();
		ExtractObject extractObject = list.get(0);
		for (ExtractObject o : list)
		{
			if (o.Numbers == null || o.Numbers.size() == 0) return null;
			buildNumber.addAll(o.Numbers);
		}
		extractObject.Numbers = buildNumber;
		extractObject.Syntax = s;
		analyzeSyntax.add(extractObject);

		return getNumberFromSyntax(removeDuplicate, analyzeSyntax, true);
	}

	private ExtractObject getNumberFromSyntax(boolean removeDuplicate, String syntax, boolean preventRemove) {
		ArrayList<ExtractObject> analyzeSyntax = normalAnalyze(syntax);

		if (analyzeSyntax.size() > 1)
		{
			return null;
		}

		return getNumberFromSyntax(removeDuplicate, analyzeSyntax, preventRemove);
	}

	private ExtractObject getNumberFromSyntax(boolean removeDuplicate, ArrayList<ExtractObject> analyzeSyntax, boolean preventRemove) {
		int size = analyzeSyntax.size();
		if (size == 0) return null;
		List<String>[] allNums = new ArrayList[size];
		for (int i = 0; i < size; i++)
		{
			allNums[i] = analyzeSyntax.get(i).Numbers;
			if (allNums[i] == null || allNums[i].size() == 0) return null;
		}

		analyzeSyntax.get(size - 1).Numbers = Trung.getTrung(removeDuplicate, preventRemove, allNums);

		return analyzeSyntax.get(size - 1);
	}

	private ArrayList<ExtractObject> normalAnalyze(String syntax) {
		syntax = syntax.trim();

		if (syntax.contains("de") || syntax.contains("bacang"))
			return ExtracDe.analyze(syntax);
		else
			return ExtracLo.analyze(syntax);
	}

	private String getPrefix(String syntax) {
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

	private ArrayList<NumberAndUnit> explainNumber(ArrayList<ExtractObject> extractObjectArray) {
		ArrayList<NumberAndUnit> _syntaxs = new ArrayList<>();
		ArrayList<String> removeArray = new ArrayList<>();
		NumberAndUnit _numberAndUnit = null;
		for (ExtractObject extractObject : extractObjectArray) {
			removeArray.clear();
			_numberAndUnit = new NumberAndUnit();
			if (extractObject.RemoveNumbers != null)
			{
				for (RemoveObject removeObject : extractObject.RemoveNumbers) {
					String rm = removeObject.Number.trim();
					if (rm.length() == 2)
					{
						removeArray.add(removeObject.Number);
					}
					else if (rm.length() == 3)
					{
						String rm1 = rm.substring(0, 2);
						String rm2 = rm.substring(1, rm.length());

						removeArray.add(rm1);
						removeArray.add(rm2);
					}
					else
					{
						return null;
					}
				}
			}

			if (extractObject.Type.contains("bacang"))
			{
				//With bacang type. Only access with simply syntax.
				_numberAndUnit.Numbers = extractObject.Numbers;
			}
			else
			{
				_numberAndUnit.Numbers = reformatNumbers(extractObject.Numbers, removeArray);
			}
			_numberAndUnit.Numbers = Utils.sortNumbers(_numberAndUnit.Numbers);
			_numberAndUnit.OrgNumber = extractObject.Type;
			_numberAndUnit.Price = extractObject.Money;
			_numberAndUnit.syntax = extractObject.Syntax;
			_numberAndUnit.Type = extractObject.Type;
			_numberAndUnit.Unit = extractObject.Unit;
			_syntaxs.add(_numberAndUnit);

			Log.d("AnalyzeSMSNew", "analyze " + "Numbers: " + Arrays.toString(_numberAndUnit.Numbers.toArray()));
		}

		return _syntaxs;
	}

	public Analyze validateMessage(String smsContent, Map<String, String> regexCustom) {
		Analyze objAnalyze = new Analyze();

		String applyCustom = smsContent;
		String curRegex = "";

		try {
			applyCustom = applyCustom.replaceAll("ộ", "oj");
			applyCustom = applyCustom.replaceAll("ỏ", "or");
			applyCustom = VNCharacterUtils.removeAccent(applyCustom).toLowerCase();

			//Replace by custom regex.
			if (regexCustom != null)
			{
				Set<String> iterator = regexCustom.keySet();
				for (String regex : iterator)
				{
					curRegex = regex;

					regex = regex.replaceAll("ộ", "oj");
					regex = regex.replaceAll("ỏ", "or");
					regex = VNCharacterUtils.removeAccent(regex).toLowerCase();
					regex = regex.replaceAll("\\s+", " ");
					applyCustom = applyCustom.replaceAll("\\s+", " ");
					applyCustom = applyCustom.replaceAll(regex, regexCustom.get(regex));
				}
			}

		} catch (Exception e) {
			// Error
			objAnalyze.error = true;
			objAnalyze.errorMessage = curRegex;
			objAnalyze.analyzeMessage = smsContent;
			objAnalyze.errorNotify = "Lỗi cú pháp tùy chỉnh: " + e.getMessage();
			return objAnalyze;
		}

		contentFormat = applyCustom;

		try {
			// Format content
			objAnalyze.analyzeMessage = formatContent(contentFormat);
			String[] contentArray = objAnalyze.analyzeMessage.split("\\n");

			for (String syntax : contentArray) {
				// Validate
				ResultObject resultObject = ValidateSyntax.validate(syntax.trim());
				if (resultObject.Error) {
					if (resultObject.isErrorCost) {
						objAnalyze.analyzeMessage = contentFormat;
					}
					// Error
					objAnalyze.error = true;
					objAnalyze.errorMessage = resultObject.MsgError;
					objAnalyze.errorNotify = resultObject.errNotify;
					return objAnalyze;
				}
			}
		} catch (Exception e) {
			// Error
			objAnalyze.error = true;
			objAnalyze.errorMessage = applyCustom;
			objAnalyze.analyzeMessage = applyCustom;
			objAnalyze.errorNotify = "Lỗi: " + e.getMessage();
		}

		return objAnalyze;
	}

	protected ArrayList<String> reformatNumbers(ArrayList<String> numbers, ArrayList<String> removeRumbers) {
		ArrayList<String> refNumbers = new ArrayList<>();
		for (String number : numbers) {
			if (number.trim().length() == 3) {
				String num1 = number.trim().substring(0, 2);
				if (ArrayUtils.indexOf(removeRumbers.toArray(new String[removeRumbers.size()]), num1) >= 0) {
					Log.d("AnalyzeSMSNew", "reformatNumbers " + "Removed num: " + number);
				} else {
					refNumbers.add(num1);
				}

				String num2 = number.trim().substring(1, number.trim().length());
				if (ArrayUtils.indexOf(removeRumbers.toArray(new String[removeRumbers.size()]), num2) >= 0) {
					Log.d("AnalyzeSMSNew", "reformatNumbers " + "Removed num: " + number);
				} else {
					refNumbers.add(num2);
				}
			} else {
				if (ArrayUtils.indexOf(removeRumbers.toArray(new String[removeRumbers.size()]), number.trim()) >= 0) {
					Log.d("AnalyzeSMSNew", "reformatNumbers " + "Removed num: " + number);
				} else {
					refNumbers.add(number.trim());
				}
			}
		}

		return refNumbers;
	}

	protected String formatContent(String content) {

		String newContent = "";

		content = content.toLowerCase();
		content = content.replaceFirst("\\s*tin\\W*[0-9]+(-)?[0-9]*\\s*", "").trim();//TODO hot fix.
		content = content.replaceAll("[@][0-9]+[@]", ""); //TODO support Shark.
		content = content.replaceAll("^\\W*[Tt]\\W*[0-9]+\\W*", ""); //TODO support other.

		content = content.replaceFirst("^\\W*", "").trim();

		content = content.replaceAll("ộ", "oj");
		content = content.replaceAll("ỏ", "or");
		// content = content.replaceAll("\\.|\\-|\\_|\\,|\\;|:|\\!", " ");
		content = content.replaceAll("\\s*(\\-)\\s*", "$1");//TODO
//		content = content.replaceAll("\\s*(\\-)\\s*", "$1");
//		content = content.replaceAll("\\s*(\\-)\\s*", "$1");
//		content = content.replaceAll("\\s*(\\-)\\s*", "$1");
//		content = content.replaceAll("([0-9]+)([_;:!])([0-9]{2})", "$1,$3");//Remove -.
		content = content.replaceAll("([0-9]+)(\\s*)\\*(\\s*)([0-9]+)([kd]{1})", "$1x$4$5");
		content = content.replaceAll("\\*", " ");
		content = content.replaceAll("×", "x");
		content = content.replaceAll("(x\\s*[0-9]+)\\s*(k|d|n|tr|nghin|trieu)(\\s*,)", "$1$2");
		content = content.replaceAll("₫", "d");

		newContent = VNCharacterUtils.removeAccent(content).toLowerCase();
		newContent = newContent.matches("[\\(]([a-z0-9\\s]+)[\\)]")
				? newContent.replaceAll("\\W*[\\)]\\W*[\\(]", ",") : newContent.replaceAll("\\W*[\\)]\\W*[\\(]", " ");
		newContent = newContent.replaceAll("[\\(]", " ");
		newContent = newContent.replaceAll("[\\)]", " ");
		newContent = newContent.replaceAll("\\W+([A-Za-z]+)\\W+", " $1 ");

		//qwrtpsdghklxcvbnm
		newContent = newContent.replaceAll("([qwrpsdghklxcvbm]+)0", "$1o");

		newContent = newContent.replaceAll("moi[\\s]*con\\W*[x=]\\W*", "x");
		newContent = newContent.replaceAll("moi[\\s]*con\\W*", "x");

		newContent = newContent.replaceAll("moi[\\s]*cap\\W*[x=]\\W*", "x");
		newContent = newContent.replaceAll("moi[\\s]*cap\\W*", "x");

		newContent = newContent.replaceAll("nghin|ngan", "k");
		newContent = newContent.replaceAll("([0-9]+)(\\s*)ng(\\W+|$)", "$1k ");
		newContent = newContent.replaceAll("\\W*x\\W*([0-9]+)\\W*(k|n)(\\W+|$)", "x$1k ");
		newContent = newContent.replaceAll("\\W*x\\W*([0-9]+)\\W*(d)(\\W+|$)", "x$1d ");
		newContent = newContent.replaceAll("([0-9]+)(\\s*)n(\\W+|$)", "$1k ");
		newContent = newContent.replaceAll("x([0-9]+)(\\s*)n", "x$1k");
		newContent = newContent.replaceAll("x(\\s+)([0-9]+)(\\s*)n", "x$2k");
		newContent = newContent.replaceAll("x([0-9]+)(\\s*)k\\W+", "x$1k ");
		newContent = newContent.replaceAll("x(\\s*)([0-9]+)(\\s*)d\\W+", "x$2d ");
		newContent = newContent.replaceAll("([0-9]+)(\\s+)([0-9]+)k", "$1x$3k");
		newContent = newContent.replaceAll("([0-9]+)(\\s+)([0-9]+)n", "$1x$3k");
		newContent = newContent.replaceAll("([0-9]+)(\\s+)([0-9]+)d", "$1x$3d");
		//Format foreign character.
		newContent = newContent.replaceAll("([^eynopg])\\s*=\\s*([0-9]+)\\s*(k|n)*(\\W+|$)", "$1x$2k");
		newContent = newContent.replaceAll("([^eynopg])\\s*=\\s*([0-9]+)\\s*(d)*(\\W+|$)", "$1x$2d");

		//Must end.
//		newContent = newContent.replaceAll("\\W+([0-9]+)\\s*n\\W+", "x$1k");
//		newContent = newContent.replaceAll("\\W+([0-9]+)\\s*d\\W+", "x$1d");

		newContent = newContent.replaceAll("\\s+", " ");
		newContent = newContent.replaceAll("\\s*diem", "d");
		newContent = newContent.replaceAll("[\\s]*x[\\s]*", "x");
		newContent = newContent.replaceAll("xien[\\s]*ghep[\\s]*2", "xien ghep 2");

//		newContent = newContent.replaceAll("(chan|le)\\s*(chan|le)", "dau $1 ghep dit $2");
//		newContent = newContent.replaceAll("(to|be|nho)\\s*(be|to|nho)", "dau $1 ghep dit $2");

		newContent = newContent.replaceAll("(boj)(\\s*)([0-9]+)(\\s*)(boj)(\\s*)([0-9]+)", "$1 $3 $7");

		if (newContent.replaceAll(" ", "").indexOf("debor") >= 0) {
			newContent = newContent.replaceAll("de[\\s]+bor", "de 100so bor");
		}

		newContent = newContent.replaceAll("100\\s+so", "100so");

		//Remove auto identified "boj" syntax.
		/*newContent = newContent.replaceAll("de[\\s]*bo[\\s]*([0-9]+)", "\nde boj $1");*/

		//Remove all space.
		newContent = newContent.replaceAll("\\s+", " ");

		// Format to righ syntax
		newContent = newContent.replaceAll("de[\\W]*", "\nde ");
		newContent = newContent.replaceAll("lo[\\W]*", "\nlo ");
		newContent = newContent.replaceAll("[\\W]*xi[\\W]+", "\nxien ");//Support for user use Ldpro.
		newContent = newContent.replaceAll("[\\W]*xi[0-9][\\W]+", "\nxien ");//Support for user use Ldpro.
		newContent = newContent.replaceAll("xien[\\W]*", "\nxien ");
		newContent = newContent.replaceAll("xquay[\\W]*", "\nxien quay ");
		newContent = newContent.replaceAll("xq[\\W]*", "\nxien quay ");
		newContent = newContent.replaceAll("xghep[\\W]*", "\nxien ghep ");
		newContent = newContent.replaceAll("xg[\\W]*", "\nxien ghep ");
		newContent = newContent.replaceAll("lo[\\s]*xien[\\W]*", "\nxien ");
		newContent = newContent.replaceAll("ba[\\s]*cang[\\W]*", "\nbacang ");
		newContent = newContent.replaceAll("bc[\\W]*", "\nbacang ");

		newContent = newContent.replaceAll("boj[\\W]*", "boj ");
		newContent = newContent.replaceAll("cham[\\W]*", "cham ");
		newContent = newContent.replaceAll("dau[\\W]*([0-9])", "dau $1");
		newContent = newContent.replaceAll("dit[\\W]*([0-9])", "dit $1");
//		newContent = newContent.replaceAll("tong[\\W]*([0-9])", "tong $1");
		newContent = newContent.replaceAll("tong[\\W]*", "tong ");
		newContent = newContent.replaceAll("dan[\\W]*", "dan ");

		//Format foreign language.
		//Case tong khong chia het.
		newContent = newContent.replaceAll("chia\\s*het\\s*cho\\s*(ba|3)", "chia 3");
		newContent = newContent.replaceAll("(de|lo)\\s*chia\\s*(ba|3)", "$1 tong chia 3");
		newContent = newContent.replaceAll("chia\\W*(ba|3)\\W*du\\W*(0|khong)", "chia 3");
		newContent = newContent.replaceAll("bor\\s*con\\s*([0-9]+)", "bor $1");
		newContent = newContent.replaceAll("dit\\s*(to|lon)\\s*hon\\s*dau", "dau be hon dit");
		newContent = newContent.replaceAll("dit\\s*(nho|be)\\s*hon\\s*dau", "dau to hon dit");
		newContent = newContent.replaceAll("(\\W+)ko(\\W+)", "$1khong$2");
		newContent = newContent.replaceAll("tong\\W*(tren|duoi)\\W*(10|muoi)", "tong $1 10");

		//Support cho cu phap duoi.
//		newContent = newContent.replaceAll("dau\\W*([0-9]+)\\W*duoi", "dau $1 dit");
//		newContent = newContent.replaceAll("(de|lo)\\W*(duoi)\\W*([0-9]+)", "$1 dit $3");

		contentFormat = newContent;

		ArrayList<String> syntaxs = new ArrayList<>(Arrays.asList(newContent.split("\n")));

		ArrayList<String> newSyntaxs = new ArrayList<>();

		for (String line : syntaxs) {
			if (line.trim().equals(""))
				continue;

			// line = line.replaceAll("[0-9]+[\\s]+", "lo $1");

			/*boolean needForMatUnit = line.replaceAll(" ", "").matches(".*[0-9]+(trieu)[0-9]+.*");
			boolean needForMatUnit2 = line.replaceAll(" ", "").matches(".*[0-9]+(trieu)(?![0-9]+).*");

			if (needForMatUnit) {
				line = line.replaceAll("trieu", "");
			} else if (needForMatUnit2) {
				line = line.replaceAll("trieu", "000k");
			}*/
			line = line.replaceAll("\\W*trieu\\W*", "000k ");
			line = line.replaceAll("\\s+([0-9]+)\\s*(tr\\W+|trieu)", " $1" + "000k");
			line = line.replaceAll("\\s+([0-9]+)\\s*(tr)$", " $1" + "000k");
			line = line.replaceAll("x\\s*([0-9]+)\\s*(tr\\W+|trieu)", " x$1" + "000k");
			line = line.replaceAll("x\\s*([0-9]+)\\s*(tr)$", " x$1" + "000k");

			//Remove dot at the end of syntax.
			line = line.replaceAll("\\W*(x\\s*[0-9]+\\s*[knd]*)[\\W]+", "$1 ");
			line = line.replaceAll("[:]", " ");
			//Case de 30 23 100n;
			line = line.replaceAll("([qwertyuiopasfghjlzcvbm]+)\\s*([0-9]+)\\s*([knd])(\\W+|$)", "$1x$2$3 ");
			line = line.replaceAll("\\W+([0-9]+)\\s*([knd])(\\W+|$)", "x$1$2 ");

			String newLine = "";
			if (isSyntax(line, "de")) {
				newLine = rebuildSyntax(line, "de", "k");
			} else if (isSyntax(line, "lo")) {
				newLine = rebuildSyntax(line, "lo", "d");
			} else if (isSyntax(line, "xien quay")) {
				newLine = rebuildSyntax(line, "xien quay", "k");
			} else if (isSyntax(line, "xien")) {
				newLine = rebuildSyntax(line, "xien", "k");
			} else if (isSyntax(line, "bacang")) {
				newLine = rebuildSyntax(line, "bacang", "k");
			}

			newLine = newLine.replaceAll("o([0-9])", "0$1");
			newLine = newLine.replaceAll("([0-9])o", "$10");

			newSyntaxs.add(newLine);

		}

		return ArrayUtils.join(newSyntaxs.toArray(new String[newSyntaxs.size()]), "\n");
	}

	protected boolean isSyntax(String line, String type) {
		line = line.trim();
		if (line.indexOf(type) >= 0) {
			return true;
		} else {
			return false;
		}
	}

	protected String rebuildSyntax(String line, String type, String unit) {

		String newLine = line;

		newLine = newLine.replaceAll("(x\\s*[0-9]+)[\\W]+", "$1" + unit + " ").trim();
		newLine = newLine.replaceAll("(x\\s*[0-9]+)$", "$1" + unit + " ").trim();
		//Only for case: x500kep lech... or x500de...
		newLine = newLine.replaceAll("(x\\s*[0-9]+)(kep|de)", "$1" + unit + " $2 ").trim();

		//Rebuild unit syntax.
		String[] tokens = newLine.trim().split("x\\s*[0-9]+\\s*k|x\\s*[0-9]+\\s*d");

		for (String _token : tokens) {

			if (_token.trim().equals("") || _token.trim().matches("[\\W]"))
				continue;

			if (_token.trim().matches("(x+)$"))
				_token = _token.trim().replaceAll("(x+)$", "");

			if (!_token.contains(type)) {

				newLine = newLine.replaceAll("(x\\s*[0-9]+\\s*k|x\\s*[0-9]+\\s*d)" + _token + "(x[0-9\\s]+k|x[0-9\\s]+d)",
						"$1\n" + type + " " + (_token.replace("\n", "") + "$2").trim());
			}

			Log.d("AnalyzeSMSNew", "rebuildSyntax " + "newLine: " + newLine);
		}

		//Rebuild detail syntax.
		/*ArrayList<String> newListSyntax = new ArrayList<>();
 		String[] buildEndSyntax = newLine.split("\n");
		for (int i = 0; i < buildEndSyntax.length; i++)
		{
			String endSyntax = buildEndSyntax[i];

			String mainSyntax = Utils.getRootSyntaxWithRemove(endSyntax);
			int countTrung = Utils.countSubString(mainSyntax, "trung");
			int countTrong = Utils.countSubString(mainSyntax, "trong");

			if (countTrung + countTrong == 0)
			{
				endSyntax = rebuildShortSyntax(endSyntax, type);
				newListSyntax.add(endSyntax);
			}
			else
			{
				newListSyntax.add(endSyntax);
			}
		}

		newLine = ArrayUtils.join(newListSyntax.toArray(new String[newListSyntax.size()]), "\n");

		Log.d("AnalyzeSMSNew", "rebuildSyntax " + "buildEndSyntax: " + newLine);*/

		return newLine;
	}

	public String rebuildShortSyntax(String syntax, String type) {
		return rebuildShortSyntax(syntax, type, false);
	}

	public String rebuildShortSyntax(String syntax, String type, boolean isRemove) {
		int idx = syntax.lastIndexOf("x");

		if (idx < 1) //not have x or x of xien.
			return syntax;

		String cost = syntax.substring(idx);
		/*String mainSyntax = Utils.getRootSyntaxWithRemove(syntax);*/ //For build syntax after bor.
		String mainSyntax = Utils.getRootSyntaxString(syntax);
		String removeSyntax = Utils.getRemoveSyntaxString(syntax);

		if (cost.trim().isEmpty() || cost.contains(type) || !mainSyntax.contains(type) || mainSyntax.replaceAll("\\W", "").equals(type) //Error cost.
				|| mainSyntax.matches(".*x\\W*[0-9]+\\W*[knd]\\W+.*") || mainSyntax.matches(".*x\\W*[0-9]+\\W*[knd]\\W*$") //Error because not analyze all cost.
				|| mainSyntax.matches(".*tr[^uoe].*") || unSupportSyntax(type, mainSyntax)) //Error not remove cost(contain unit tr).
		{
			return syntax;
		}

		mainSyntax = formatSyntax(mainSyntax, type).trim();

		if (!removeSyntax.isEmpty())
		{
			removeSyntax = formatSyntax(removeSyntax, "bor").trim();

			String borSyntax = removeSyntax;
			removeSyntax = "";
			String[] _tempBor = borSyntax.split("\\n");
			for (String _bor : _tempBor)
			{
				if (!_bor.matches("\\W*"))
				{
					_bor = _bor.replaceAll("^\\W*", "");
					_bor = _bor.replaceAll("\\W*$", "");
					if (_bor.startsWith("bor"))
					{
						removeSyntax = removeSyntax + _bor;
					}
					else
					{
						removeSyntax = removeSyntax + " bor " + _bor;
					}
				}
			}
		}
		removeSyntax = removeSyntax.replaceAll("\\n", " ").trim();

		if (TextUtils.isEmpty(mainSyntax))
		{
			//TODO hot fix. This case is had error syntax.
			return syntax;
		}

		String buildSyntax = "";
		String[] _tempSynx = mainSyntax.split("\\n");
		for (String _syntax : _tempSynx)
		{
			if (!_syntax.matches("\\W*"))
			{
				_syntax = _syntax.replaceAll("^\\W*", "");
				_syntax = _syntax.replaceAll("\\W*$", "");
				if (_syntax.length() > 1)
				{
					if (_syntax.contains(type))
					{
						buildSyntax = buildSyntax + _syntax + " " + removeSyntax + cost + " ";
					}
					else
					{
						buildSyntax = buildSyntax + type + " " + _syntax + " " + removeSyntax + cost + " ";
					}
				}
				else
				{
					buildSyntax = buildSyntax + _syntax;
				}
			}
		}

		buildSyntax = buildSyntax.replaceAll(type, "\n" + type).trim();

		return buildSyntax.trim();

		//TODO Add bor for all (follow ldpro) = stupid =))).
		/*String successSyntax = "";
		String[] _lastTemp = buildSyntax.split("\n");
		String lastBor = "";
		for (int i = _lastTemp.length - 1; i >= 0; i--)
		{
			String _syntax = _lastTemp[i];
			if (!_syntax.matches("\\W*"))
			{
				if (!_syntax.contains("bor") && !lastBor.isEmpty())
				{
					_syntax = _syntax.replaceAll("(x\\s*[0-9]+\\s*k|x\\s*[0-9]+\\s*d)", lastBor);
				}
				else
				{
					lastBor = Utils.getRemoveSyntaxWithCost(_syntax);
				}

				successSyntax = _syntax + " " + successSyntax;
			}
		}

		successSyntax = successSyntax.replaceAll("\\s*" + type, "\n" + type).trim();
		return successSyntax.trim();*/
	}

	protected String formatSyntax(String mainSyntax, String type) {

		mainSyntax = mainSyntax.replaceAll("^(\\W*" + type + "\\W*)", "").trim();

		//Syntax kep.
		mainSyntax = mainSyntax.replaceAll("(sat|ap)\\W*kep", "\n" + type + " aaaaaa" + "\n");
		mainSyntax = mainSyntax.replaceAll("kep\\W*bang", "\n" +  type + " bbbbbb" + "\n");
		mainSyntax = mainSyntax.replaceAll("kep\\W*lech", "\n" +  type + " cccccc" + "\n");
		mainSyntax = mainSyntax.replaceAll("kep", "\n" +  type + " kep bang");
		mainSyntax = mainSyntax.replaceAll("aaaaaa", "sat kep");
		mainSyntax = mainSyntax.replaceAll("bbbbbb", "kep bang");
		mainSyntax = mainSyntax.replaceAll("cccccc", "kep lech");

		//Syntax tong.
		mainSyntax = mainSyntax.replaceAll("tong", "\n" + type + " tong");
		mainSyntax = mainSyntax.replaceAll("tong\\s*chan", "\n" + type + " tong chan" + "\n");
		mainSyntax = mainSyntax.replaceAll("tong\\s*le", "\n" + type + " tong le" + "\n");
		mainSyntax = mainSyntax.replaceAll("tong\\s*(to|lon)", "\n" + type + " tong to" + "\n");
		mainSyntax = mainSyntax.replaceAll("tong\\s*(be|nho)", "\n" + type + " tong be" + "\n");
		mainSyntax = mainSyntax.replaceAll("tong\\s*(tren|duoi)\\s*10", "\n" + type + " tong $1 10" + "\n");

		//Syntax bo, dit , dau, cham, dan, 100so.
		mainSyntax = mainSyntax.replaceAll("100so", "\n" + type + " 100so");
		mainSyntax = mainSyntax.replaceAll("\\W+so\\W*", "\n" + type + " ");
		mainSyntax = mainSyntax.replaceAll("boj", "\n" + type + " boj");
		mainSyntax = mainSyntax.replaceAll("cham", "\n" + type + " cham");
		mainSyntax = mainSyntax.replaceAll("dan", "\n" + type + " dan");
		mainSyntax = mainSyntax.replaceAll("dit", "\n" + type + " dit");
		mainSyntax = mainSyntax.replaceAll("dau", "\n" + type + " dau");
		//Support cho cu phap duoi
//		mainSyntax = mainSyntax.replaceAll("duoi", "\n" + type + " duoi");
		//Support cho cu phap duoi
//		mainSyntax = mainSyntax.replaceAll("tong\\W*" + type + " duoi", "\n" + type + " tong duoi");
		mainSyntax = mainSyntax.replaceAll("dau\\W*([0-9]+.*)[\n]" + type + " (dit)", "\ndau $1 dit");
		//Support cho cu phap duoi
//		mainSyntax = mainSyntax.replaceAll("dau\\W*([0-9]+)\\W*" + type + " (dit|duoi)", "\ndau $1 dit");
		mainSyntax = mainSyntax.replaceAll("dau\\W*(to|be|nho|lon|chan|le)\\W*(hon|ghep|trung|trong)\\W*" + type + " (dit)", "\ndau $1 $2 dit");
//		mainSyntax = mainSyntax.replaceAll("dau\\W*(to|be|nho|lon|chan|le)\\W*(hon|ghep|trung|trong)\\W*" + type + " (dit|duoi)", "\ndau $1 $2 dit");
		mainSyntax = mainSyntax.replaceAll("dit\\W*(to|be|nho|lon|chan|le)\\W*(hon|ghep|trung|trong)\\W*" + type + " dau", "\ndit $1 $2 dau");
		mainSyntax = mainSyntax.replaceAll("dau\\W*" + type + " (dit)\\W*([0-9]|to|nho|be|lon|chan|le)", "\ndau dit $2");

		//Syntax chan le.
		mainSyntax = mainSyntax.replaceAll("(dau|dit|duoi|tong)\\W*(chan|le)\\W*((\\s*(chan|le)\\s*(chan|le)\\s*)+)", "\n" + type + " $1 $2" + "\n" + type + " $3" + "\n");
		mainSyntax = mainSyntax.replaceAll("((\\s*(chan|le)\\s*(chan|le)\\s*)+)", "\n" + type + " $1" + "\n");

		//Support cho cu phap duoi
//		mainSyntax = mainSyntax.replaceAll("dau\\W*" + type + " (dit|duoi)\\W*([0-9]|to|nho|be|lon|chan|le)", "\ndau dit $2");

		//This support for (dit to hon dau) but error when (dau to hon dit dau 123)
		/*mainSyntax = mainSyntax.replaceAll("dit\\W*" + type + " dau\\W*([0-9]|to|nho|be|lon|chan|le)", "\ndau dit $1");*/

		//Syntax foreign concat.
//		mainSyntax = mainSyntax.replaceAll("\\Wcon\\W*([0-9]+)", "\n" + type + " $1");
		mainSyntax = mainSyntax.replaceAll("(dau\\W*(to|be|nho|lon|chan|le)\\W*(hon|ghep|trung|trong)\\W*" + type + " dit)\\s*(dau|dit)", "$1\n$2");
		mainSyntax = mainSyntax.replaceAll("(dit\\W*(to|be|nho|lon|chan|le)\\W*(hon|ghep|trung|trong)\\W*" + type + " dau)\\s*(dau|dit)", "$1\n$2");
		mainSyntax = mainSyntax.replaceAll("\\W*" + type + "\\W*dit\\W*" + type + "\\W*dau\\W*([0-9])", /*"dau dit $1"*/" " + type + " dit dau $1");

		mainSyntax = mainSyntax.replaceAll("(\\W*" + type + "\\W*)+", "\n" + type + " ");
		mainSyntax = mainSyntax.replaceAll("^\\W*" + type + "\\W*", type + " ");
		mainSyntax = mainSyntax.replaceAll("\\W*" + type + "\\W*$", "");
		mainSyntax = mainSyntax.replaceAll("^\\W*", "");

		mainSyntax = mainSyntax.replaceAll("bor[\\W\\s]*" + type, "bor ");
		mainSyntax = mainSyntax.replaceAll(type + "[\\W\\s]*bor", " bor");
		mainSyntax = mainSyntax.replaceAll("[\\W\\s]*bor[\\W\\s]*", " bor ");

		mainSyntax = mainSyntax.replaceAll("([^bor])[\\W\\s]*trung[\\W\\s]*bor", "$1 trung");

		if (!type.contains("bor"))
		{
			mainSyntax = mainSyntax.replaceAll("[\\W\\s]*trung[\\W\\s]*" + type, " trung");
		}

		return mainSyntax;
	}

	public boolean unSupportSyntax(String type, String mainSyntax) {
		boolean notSupport;

		notSupport = mainSyntax.matches("^\\W*" + type + "\\W*dit\\W*dau.*");

		notSupport = notSupport && (mainSyntax.replaceAll(type, "").replaceAll("\\W", "").length() < 2);

		return notSupport;
	}

	public boolean setResult(String todayBingoResult) {

		if (allNumbersResult != null) {
			allNumbersResult.clear();
		}
		else
		{
			allNumbersResult = new ArrayList<>();
		}

		String ketQua = todayBingoResult;

		if (!TextUtils.isEmpty(ketQua)) {
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

			return true;
		}
		else
		{
			return false;
		}
	}

	public Analyze getAnalyze(String number, String type, String price, String unit) {

		Analyze objAnalyze = new Analyze();

		if (allNumbersResult == null)
		{
			return objAnalyze;
		}

		BigDecimal thucThu;

		double HE_SO = 0;
		double HE_SO_THANG = 0;
		int amountPoint = 0;

		thucThu = BigDecimal.ZERO;
		amountPoint = 0;

		String extractType = "";

		if (!number.trim().equals("")) {
			// Số ti�?n thu của khách

			if (type.indexOf("de") >= 0 || type.indexOf("bacang") >= 0) {
				HE_SO = type.indexOf("bacang") >= 0 ? BingoCalc.HS_BACANG : BingoCalc.HS_DE;
				extractType = "DB";
			} else {
				HE_SO = BingoCalc.HS_NONE;
				extractType = "ALL";
			}

		}

		if (extractType.equals("DB")) {
			BigDecimal result = BingoCalc.initial().calc(Integer.parseInt(price), HE_SO);
			thucThu = thucThu.add(result);
			objAnalyze.actuallyCollected = thucThu;

			amountPoint += Integer.parseInt(price);

			HE_SO_THANG = type.equals("bacang") ? BingoCalc.HS_THANG_BACANG : BingoCalc.HS_THANG_DE;
			int countNumResult = type.equals("bacang") ? 3 : 2;

			int count = number.length() - number.replace("-", "").length();
			String[] arrNums = number.split("-");
			if (count == 0) {
				if (sDB.substring(sDB.length() - countNumResult).equals(number.trim())) {
					BigDecimal resultThangDe = BingoCalc.initial()
							.calc(Integer.parseInt(price), HE_SO_THANG);
					objAnalyze.guestWin = resultThangDe;
					objAnalyze.winNumber = number.trim();
					objAnalyze.winSyntax = objAnalyze.winNumber + "x" + price + "" + unit;
				}
			} else {
				for (String num : arrNums) {
					if (sDB.substring(sDB.length() - countNumResult).equals(num.trim())) {
						BigDecimal resultThangDe = BingoCalc.initial()
								.calc(Integer.parseInt(price), HE_SO_THANG);
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

						if (objAnalyze.guestWin == null)
						{
							objAnalyze.guestWin = resultThangLo;
						}
						else
						{
							objAnalyze.guestWin = objAnalyze.guestWin.add(resultThangLo);
						}
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
						BigDecimal resultThangXien = BingoCalc.initial()
								.calc(Integer.parseInt(price), HE_SO_THANG);
						objAnalyze.winSyntax = objAnalyze.winNumber + "x" + price + "" + unit;

						if (objAnalyze.guestWin == null)
						{
							objAnalyze.guestWin = resultThangXien;
						}
						else
						{
							objAnalyze.guestWin = objAnalyze.guestWin.add(resultThangXien);
						}

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

		return objAnalyze;
	}

	private String buildToSyntax(String syntax, String prefix, String cost) {
		if (!syntax.contains(prefix))
		{
			syntax = prefix + " " + syntax;
		}

		if (!syntax.contains(cost))
		{
			syntax = syntax + cost;
		}

		return syntax;
	}
}
