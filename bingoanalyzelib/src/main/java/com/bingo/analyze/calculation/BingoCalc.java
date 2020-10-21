package com.bingo.analyze.calculation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.udojava.evalex.Expression;


public class BingoCalc {
	private static BingoCalc bingoCalc;
	public static double HS_DE = 0.72;
	public static double HS_LO = 21.7;
	public static double HS_BACANG = 0.7;
	public static double HS_XIEN_2 = 0.6;
	public static double HS_XIEN_3 = 0.6;
	public static double HS_XIEN_4 = 0.6;
	public static double HS_NONE = 0.6;
	
	public static int HS_THANG_DE = 70;
	public static int HS_THANG_LO = 80;
	public static int HS_THANG_BACANG = 400;
	public static int HS_THANG_XIEN_2 = 10;
	public static int HS_THANG_XIEN_3 = 40;
	public static int HS_THANG_XIEN_4 = 100;
	
	
	public static BingoCalc initial(){
		if(bingoCalc == null){
			bingoCalc = new BingoCalc();
		}
		return bingoCalc;
	}
	
	
	public BigDecimal calc(int guestMoney, double factor){
		BigDecimal bigDecimal = null;
		Expression expression = new Expression(CalcSyntax.formula(guestMoney, factor));
		//expression.setPrecision(2);
		bigDecimal = expression.eval();
		
		return bigDecimal;
	}
	
	public KetQua extractResultSMS(String key, String content){
		String reg = "";
		switch (key) {
		case "1":
			reg = "("+key+"):([0-9]{5})";
			break;
		case "2":
			reg = "("+key+"):([0-9\\-]{11})";
			break;
		case "3":
			reg = "("+key+"):([0-9\\-]{35})";
			break;
		case "4":
			reg = "("+key+"):([0-9\\-]{19})";
			break;
		case "5":
			reg = "("+key+"):([0-9\\-]{29})";
			break;
		case "6":
			reg = "("+key+"):([0-9\\-]{11})";
			break;
		case "7":
			reg = "("+key+"):([0-9\\-]{11})";
			break;
		case "DB":
			reg = "("+key+"):([0-9]{5})";
			break;
		case "MB":
			reg = "("+key+")-([0-9\\/]{5})";
			break;
		default://DEFAUL FOR GET DATE
			reg = "("+key+")-([0-9\\/]{5})";
			break;
		}
		
		Pattern patternJ = Pattern.compile(reg);
		Matcher matcherJ = patternJ.matcher(content);
		KetQua kq = new KetQua();
		ArrayList<String> lsDaySo = new ArrayList<>();
		
		while (matcherJ.find()) {
			String group1 = matcherJ.group(1).trim();
			String group2 = matcherJ.group(2).trim();
			if(key.equals("MB")){
				kq.ngay = group2;
				break;
			}
			
			kq.giai = group1;
			if(group2.indexOf("-") > 0){
				String[] arrSo = group2.split("-");
				for (String so : arrSo) {
					lsDaySo.add(so);
				}
			}else{
				lsDaySo.add(group2);
			}
			
			kq.daySo = lsDaySo;
			break;
		}
		return kq;
	}
	
}
