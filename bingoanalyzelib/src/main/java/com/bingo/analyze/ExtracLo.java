package com.bingo.analyze;

import java.util.ArrayList;

import com.bingo.common.utils.Bo;
import com.bingo.common.utils.Cham;
import com.bingo.common.utils.ChanLe;
import com.bingo.common.utils.Dan;
import com.bingo.common.utils.DauDit;
import com.bingo.common.utils.Ghep;
import com.bingo.common.utils.Kep;
import com.bingo.common.utils.OneHundred;
import com.bingo.common.utils.ToNho;
import com.bingo.common.utils.Tong;
import com.bingo.common.utils.Utils;
import com.bingo.common.utils.Xien;

public class ExtracLo {
	
	public static ArrayList<ExtractObject> analyze(String syntax){
		ArrayList<ExtractObject> extractObjectArray = new ArrayList<>();

		String syntaxMain = Utils.getRootSyntaxWithCost(syntax);
		String rmSyntax = Utils.getRemoveSyntaxWithCost(syntax);

		ExtractObject obExtractObjectLo = ExtracLo.phanTichLo(syntaxMain, rmSyntax);
		if(obExtractObjectLo != null){
			extractObjectArray.add(obExtractObjectLo);
		}

		ExtractObject obExtractObjectLoBo = ExtracLo.phanTichLoBo(syntaxMain, rmSyntax);
		if(obExtractObjectLoBo != null){
			extractObjectArray.add(obExtractObjectLoBo);
		}
		ExtractObject obExtractObjectLoKepBang = ExtracLo.phanTichLoKepBang(syntaxMain, rmSyntax);
		if(obExtractObjectLoKepBang != null){
			extractObjectArray.add(obExtractObjectLoKepBang);
		}
		ExtractObject obExtractObjectLoKepLech = ExtracLo.phanTichLoKepLech(syntaxMain, rmSyntax);
		if(obExtractObjectLoKepLech != null){
			extractObjectArray.add(obExtractObjectLoKepLech);
		}
		ExtractObject obExtractObjectLoSatKep = ExtracLo.phanTichLoSatKep(syntaxMain, rmSyntax);
		if(obExtractObjectLoSatKep != null){
			extractObjectArray.add(obExtractObjectLoSatKep);
		}

		ExtractObject obExtractObjectLo100So = ExtracLo.phanTichLo100So(syntaxMain, rmSyntax);
		if(obExtractObjectLo100So != null){
			extractObjectArray.add(obExtractObjectLo100So);
		}

		ExtractObject obExtractObjectLoTong = ExtracLo.phanTichLoTong(syntaxMain, rmSyntax);
		if(obExtractObjectLoTong != null){
			extractObjectArray.add(obExtractObjectLoTong);
		}

		ExtractObject obExtractObjectLoTongTrenDuoi = ExtracLo.phanTichLoTongTrenDuoi(syntaxMain, rmSyntax);
		if(obExtractObjectLoTongTrenDuoi != null){
			extractObjectArray.add(obExtractObjectLoTongTrenDuoi);
		}

		ExtractObject obExtractObjectLoTongLeChan = ExtracLo.phanTichLoTongLeChan(syntaxMain, rmSyntax);
		if(obExtractObjectLoTongLeChan != null){
			extractObjectArray.add(obExtractObjectLoTongLeChan);
		}

		ExtractObject obExtractObjectLoTongToBe = ExtracLo.phanTichLoTongToBe(syntaxMain, rmSyntax);
		if(obExtractObjectLoTongToBe != null){
			extractObjectArray.add(obExtractObjectLoTongToBe);
		}

		ExtractObject obExtractObjectLoTongChiaBa = ExtracLo.phanTichLoTongChiaBa(syntaxMain, rmSyntax);
		if(obExtractObjectLoTongChiaBa != null){
			extractObjectArray.add(obExtractObjectLoTongChiaBa);
		}

		ExtractObject obExtractObjectLoTongChiaBaDu1 = ExtracLo.phanTichLoTongChiaBaDu1(syntaxMain, rmSyntax);
		if(obExtractObjectLoTongChiaBaDu1 != null){
			extractObjectArray.add(obExtractObjectLoTongChiaBaDu1);
		}

		ExtractObject obExtractObjectLoTongChiaBaDu2 = ExtracLo.phanTichLoTongChiaBaDu2(syntaxMain, rmSyntax);
		if(obExtractObjectLoTongChiaBaDu2 != null){
			extractObjectArray.add(obExtractObjectLoTongChiaBaDu2);
		}

		ExtractObject obExtractObjectLoDau = ExtracLo.phanTichLoDau(syntaxMain, rmSyntax);
		if(obExtractObjectLoDau != null){
			extractObjectArray.add(obExtractObjectLoDau);
		}

		ExtractObject obExtractObjectLoCham = ExtracLo.phanTichLoCham(syntaxMain, rmSyntax);
		if(obExtractObjectLoCham != null){
			extractObjectArray.add(obExtractObjectLoCham);
		}

		ExtractObject obExtractObjectLoChanLe = ExtracLo.phanTichLoChanLe(syntaxMain, rmSyntax);
		if(obExtractObjectLoChanLe != null){
			extractObjectArray.add(obExtractObjectLoChanLe);
		}

		ExtractObject obExtractObjectLoToNho = ExtracLo.phanTichLoToNho(syntaxMain, rmSyntax);
		if(obExtractObjectLoToNho != null){
			extractObjectArray.add(obExtractObjectLoToNho);
		}

		ExtractObject obExtractObjectLoDan = ExtracLo.phanTichLoDan(syntaxMain, rmSyntax);
		if(obExtractObjectLoDan != null){
			extractObjectArray.add(obExtractObjectLoDan);
		}

		ExtractObject obExtractObjectXien = ExtracLo.phanTichXien(syntaxMain, rmSyntax);
		if(obExtractObjectXien != null){
			extractObjectArray.add(obExtractObjectXien);
		}

		return extractObjectArray;
	}

	protected static ExtractObject phanTichLo(String syntax, String rmSyntax) {
		// lo 17,12,21x50k
		boolean isRightSyntax = syntax.matches("lo[\\s:]*([0-9]{2,3}([-.,/\\s]*))+\\s*x[0-9]+\\s*k|lo\\s*([0-9]{2,3}([-.,/\\s]*))+\\s*x[0-9]+\\s*d");
		ExtractObject extractObject = null;
		ArrayList<RemoveObject> removeNumbers = null;
		if (isRightSyntax) {
			removeNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));
			extractObject = Utils.extractNumberAndUnitPrice(syntax, "lo");
			extractObject.RemoveNumbers = removeNumbers;
		}
		return extractObject;
	}

	protected static ExtractObject phanTichLoBo(String syntax, String rmSyntax) {
		// lo 17,12,21x50k
		syntax = syntax.replaceAll("bo[\\s]*([0-9]+)", "boj $1");

		ExtractObject extractObject = Bo.getBo(syntax);

		if (extractObject != null)
		{
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));;
		}

		return extractObject;
	}


	protected static ExtractObject phanTichLoKepBang(String syntax, String rmSyntax) {
		ExtractObject extractObject = Kep.getKepBang(syntax);
		if(extractObject != null){
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));;
		}
		return extractObject;
	}

	protected static ExtractObject phanTichLoKepLech(String syntax, String rmSyntax) {
		ExtractObject extractObject = Kep.getKepLech(syntax);
		if(extractObject != null){
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));;
		}
		return extractObject;
	}

	protected static ExtractObject phanTichLoSatKep(String syntax, String rmSyntax) {
		ExtractObject extractObject = Kep.getSatKep(syntax);
		if(extractObject != null){
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));;
		}
		return extractObject;
	}

	protected static ExtractObject phanTichLo100So(String syntax, String rmSyntax) {
		ExtractObject extractObject = OneHundred.get100So(syntax);
		if(extractObject != null){
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));;
		}
		return extractObject;
	}

	protected static ExtractObject phanTichLoTong(String syntax, String rmSyntax) {
		ExtractObject extractObject = Tong.getTong(syntax);
		if(extractObject != null){
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));;
		}
		return extractObject;
	}

	protected static ExtractObject phanTichLoTongTrenDuoi(String syntax, String rmSyntax) {
		ExtractObject extractObject = Tong.getTongTrenDuoi(syntax);
		if(extractObject != null){
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));
		}
		return extractObject;
	}

	protected static ExtractObject phanTichLoTongLeChan(String syntax, String rmSyntax) {
		ExtractObject extractObject = Tong.getTongLeChan(syntax);
		if(extractObject != null){
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));;
		}
		return extractObject;
	}

	protected static ExtractObject phanTichLoTongToBe(String syntax, String rmSyntax) {
		ExtractObject extractObject = Tong.getTongToBe(syntax);
		if(extractObject != null){
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));;
		}
		return extractObject;
	}

	protected static ExtractObject phanTichLoTongChiaBa(String syntax, String rmSyntax) {
		ExtractObject extractObject = Tong.getTongChiaBa(syntax);
		if(extractObject != null){
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));;
		}
		return extractObject;
	}

	protected static ExtractObject phanTichLoTongChiaBaDu1(String syntax, String rmSyntax) {
		ExtractObject extractObject = Tong.getTongChiaBaDu1(syntax);
		if(extractObject != null){
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));;
		}
		return extractObject;
	}

	protected static ExtractObject phanTichLoTongChiaBaDu2(String syntax, String rmSyntax) {
		ExtractObject extractObject = Tong.getTongChiaBaDu2(syntax);
		if(extractObject != null){
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));;
		}
		return extractObject;
	}

	protected static ExtractObject phanTichLoDau(String syntax, String rmSyntax) {
		ExtractObject extractObject = DauDit.getDauDuoi(syntax);
		if(extractObject != null){
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));;
		}
		return extractObject;
	}

	protected static ExtractObject phanTichLoCham(String syntax, String rmSyntax) {
		ExtractObject extractObject = Cham.getCham(syntax);
		if(extractObject != null){
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));;
		}
		return extractObject;
	}

	protected static ExtractObject phanTichLoChanLe(String syntax, String rmSyntax) {
		ExtractObject extractObject = ChanLe.getChanLe(syntax);
		if(extractObject != null){
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));;
		}
		return extractObject;
	}

	protected static ExtractObject phanTichLoToNho(String syntax, String rmSyntax) {
		ExtractObject extractObject = ToNho.getToNho(syntax);
		if(extractObject != null){
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));;
		}
		return extractObject;
	}

	protected static ExtractObject phanTichLoDan(String syntax, String rmSyntax) {
		ExtractObject extractObject = Dan.getDan(syntax);
		if(extractObject != null){
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));;
		}
		return extractObject;
	}

	protected static ExtractObject phanTichLoGhep(String syntax, String rmSyntax) {
		ExtractObject extractObject = Ghep.getGhep(syntax);
		if(extractObject != null){
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));;
		}
		return extractObject;
	}

	protected static ExtractObject phanTichXien(String syntax, String rmSyntax) {
		ExtractObject extractObject = Xien.getXien(syntax);
		if(extractObject != null){
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));;
		}
		return extractObject;
	}
}
