package com.bingo.analyze;

import java.math.BigDecimal;

public class Analyze {
	public NumberAndUnit numberAndUnit;
	/*
	 * Thực thu
	 */
	public BigDecimal actuallyCollected;
	/*
	 * Tổng điểm khách đánh
	 */
	public int amountPoint;
	/*
	 * Tổng tiền khách thắng
	 */
	public BigDecimal guestWin;
	/*
	 * Số trúng
	 */
	public String winNumber;
	/*
	 * Cú pháp
	 */
	public String winSyntax;
	/*
	 * Loại trúng
	 */
	public String winType;
	public String errorMessage;
	public boolean error;
	public String analyzeMessage;
	public String errorNotify;
}
