package com.bingo.analyze.calculation;
//https://github.com/uklimaschewski/EvalEx
public class CalcSyntax {
	public static String FORMULA = "%s*%s";
	
	/**
	 * 
	 * @param gia: giá mỗi con
	 * @param heso: hệ số gấp bao nhiêu lần
	 * @return
	 */
	public static String formula(int guestMoney, double factor){
		return String.format(FORMULA, guestMoney, factor);
	}
	
}
