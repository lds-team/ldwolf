package com.bingo.analyze;

import com.bingo.common.utils.Bacang;
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
import com.bingo.common.utils.Trung;
import com.bingo.common.utils.Utils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtracDe {
	
	public static ArrayList<ExtractObject> analyze(String syntax){
		ArrayList<ExtractObject> extractObjectArray = new ArrayList<>();

		String syntaxMain = Utils.getRootSyntaxWithCost(syntax);
		String rmSyntax = Utils.getRemoveSyntaxWithCost(syntax);
		
		ExtractObject obExtractObjectDe = ExtracDe.phanTichDe(syntaxMain, rmSyntax);
		if(obExtractObjectDe != null){
			extractObjectArray.add(obExtractObjectDe);
		}
		
		ExtractObject obExtractObjectDeBo = ExtracDe.phanTichDeBo(syntaxMain, rmSyntax);
		if(obExtractObjectDeBo != null){
			extractObjectArray.add(obExtractObjectDeBo);
		}
		ExtractObject obExtractObjectDeKepBang = ExtracDe.phanTichDeKepBang(syntaxMain, rmSyntax);
		if(obExtractObjectDeKepBang != null){
			extractObjectArray.add(obExtractObjectDeKepBang);
		}
		ExtractObject obExtractObjectDeKepLech = ExtracDe.phanTichDeKepLech(syntaxMain, rmSyntax);
		if(obExtractObjectDeKepLech != null){
			extractObjectArray.add(obExtractObjectDeKepLech);
		}
		ExtractObject obExtractObjectDeSatKep = ExtracDe.phanTichDeSatKep(syntaxMain, rmSyntax);
		if(obExtractObjectDeSatKep != null){
			extractObjectArray.add(obExtractObjectDeSatKep);
		}
		
		ExtractObject obExtractObjectDe100So = ExtracDe.phanTichDe100So(syntaxMain, rmSyntax);
		if(obExtractObjectDe100So != null){
			extractObjectArray.add(obExtractObjectDe100So);
		}
		
		ExtractObject obExtractObjectDeTong = ExtracDe.phanTichDeTong(syntaxMain, rmSyntax);
		if(obExtractObjectDeTong != null){
			extractObjectArray.add(obExtractObjectDeTong);
		}

		ExtractObject obExtractObjectDeTongTrenDuoi = ExtracDe.phanTichDeTongTrenDuoi(syntaxMain, rmSyntax);
		if(obExtractObjectDeTongTrenDuoi != null){
			extractObjectArray.add(obExtractObjectDeTongTrenDuoi);
		}

		ExtractObject obExtractObjectDeTongLeChan = ExtracDe.phanTichDeTongLeChan(syntaxMain, rmSyntax);
		if(obExtractObjectDeTongLeChan != null){
			extractObjectArray.add(obExtractObjectDeTongLeChan);
		}

		ExtractObject obExtractObjectDeTongToBe= ExtracDe.phanTichDeTongToBe(syntaxMain, rmSyntax);
		if(obExtractObjectDeTongToBe != null){
			extractObjectArray.add(obExtractObjectDeTongToBe);
		}
		
		ExtractObject obExtractObjectDeTongChiaBa = ExtracDe.phanTichDeTongChiaBa(syntaxMain, rmSyntax);
		if(obExtractObjectDeTongChiaBa != null){
			extractObjectArray.add(obExtractObjectDeTongChiaBa);
		}
		
		ExtractObject obExtractObjectDeTongChiaBaDu1 = ExtracDe.phanTichDeTongChiaBaDu1(syntaxMain, rmSyntax);
		if(obExtractObjectDeTongChiaBaDu1 != null){
			extractObjectArray.add(obExtractObjectDeTongChiaBaDu1);
		}
		
		ExtractObject obExtractObjectDeTongChiaBaDu2 = ExtracDe.phanTichDeTongChiaBaDu2(syntaxMain, rmSyntax);
		if(obExtractObjectDeTongChiaBaDu2 != null){
			extractObjectArray.add(obExtractObjectDeTongChiaBaDu2);
		}
		
		ExtractObject obExtractObjectDeDau = ExtracDe.phanTichDeDau(syntaxMain, rmSyntax);
		if(obExtractObjectDeDau != null){
			extractObjectArray.add(obExtractObjectDeDau);
		}
		
		ExtractObject obExtractObjectDeCham = ExtracDe.phanTichDeCham(syntaxMain, rmSyntax);
		if(obExtractObjectDeCham != null){
			extractObjectArray.add(obExtractObjectDeCham);
		}
		
		ExtractObject obExtractObjectDeChanLe = ExtracDe.phanTichDeChanLe(syntaxMain, rmSyntax);
		if(obExtractObjectDeChanLe != null){
			extractObjectArray.add(obExtractObjectDeChanLe);
		}
		
		ExtractObject obExtractObjectDeToNho = ExtracDe.phanTichDeToNho(syntaxMain, rmSyntax);
		if(obExtractObjectDeToNho != null){
			extractObjectArray.add(obExtractObjectDeToNho);
		}
		
		ExtractObject obExtractObjectDeDan = ExtracDe.phanTichDeDan(syntaxMain, rmSyntax);
		if(obExtractObjectDeDan != null){
			extractObjectArray.add(obExtractObjectDeDan);
		}

		ExtractObject obExtractObjectBacang = ExtracDe.phanTichBaCang(syntaxMain, rmSyntax);
		if(obExtractObjectBacang != null){
			extractObjectArray.add(obExtractObjectBacang);
		}
		
		return extractObjectArray;
	}
	
	protected static ExtractObject phanTichDe(String syntax, String rmSyntax) {
		// de 17,12,21x50k
		boolean isRightSyntax = syntax.matches("de[\\s:]*([0-9]{2,3}([-.,/\\s]*))+\\s*x[0-9]+\\s*k|de\\s*([0-9]{2,3}([-.,/\\s]*))+\\s*x[0-9]+\\s*d");
		ExtractObject extractObject = null;
		ArrayList<RemoveObject> removeNumbers = null;
		if (isRightSyntax) {
			removeNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));
			extractObject = Utils.extractNumberAndUnitPrice(syntax, "de");
			extractObject.RemoveNumbers = removeNumbers;
		}
		return extractObject;
	}

	protected static ExtractObject phanTichDeBo(String syntax, String rmSyntax) {
		// de 17,12,21x50k
		syntax = syntax.replaceAll("bo[\\s]*([0-9]+)", "boj $1");

		ExtractObject extractObject = Bo.getBo(syntax);

		if (extractObject != null)
		{
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));
		}

		return extractObject;
	}

	
	protected static ExtractObject phanTichDeKepBang(String syntax, String rmSyntax) {
		ExtractObject extractObject = Kep.getKepBang(syntax);
		if(extractObject != null){
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));
		}
		return extractObject;
	}
	
	protected static ExtractObject phanTichDeKepLech(String syntax, String rmSyntax) {
		ExtractObject extractObject = Kep.getKepLech(syntax);
		if(extractObject != null){
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));
		}
		return extractObject;
	}
	
	protected static ExtractObject phanTichDeSatKep(String syntax, String rmSyntax) {
		ExtractObject extractObject = Kep.getSatKep(syntax);
		if(extractObject != null){
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));
		}
		return extractObject;
	}
	
	protected static ExtractObject phanTichDe100So(String syntax, String rmSyntax) {
		ExtractObject extractObject = OneHundred.get100So(syntax);
		if(extractObject != null){
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));
		}
		return extractObject;
	}
	
	protected static ExtractObject phanTichDeTong(String syntax, String rmSyntax) {
		ExtractObject extractObject = Tong.getTong(syntax);
		if(extractObject != null){
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));
		}
		return extractObject;
	}

	protected static ExtractObject phanTichDeTongTrenDuoi(String syntax, String rmSyntax) {
		ExtractObject extractObject = Tong.getTongTrenDuoi(syntax);
		if(extractObject != null){
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));
		}
		return extractObject;
	}

	protected static ExtractObject phanTichDeTongLeChan(String syntax, String rmSyntax) {
		ExtractObject extractObject = Tong.getTongLeChan(syntax);
		if(extractObject != null){
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));
		}
		return extractObject;
	}

	protected static ExtractObject phanTichDeTongToBe(String syntax, String rmSyntax) {
		ExtractObject extractObject = Tong.getTongToBe(syntax);
		if(extractObject != null){
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));
		}
		return extractObject;
	}
	
	protected static ExtractObject phanTichDeTongChiaBa(String syntax, String rmSyntax) {
		ExtractObject extractObject = Tong.getTongChiaBa(syntax);
		if(extractObject != null){
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));
		}
		return extractObject;
	}
	
	protected static ExtractObject phanTichDeTongChiaBaDu1(String syntax, String rmSyntax) {
		ExtractObject extractObject = Tong.getTongChiaBaDu1(syntax);
		if(extractObject != null){
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));
		}
		return extractObject;
	}
	
	protected static ExtractObject phanTichDeTongChiaBaDu2(String syntax, String rmSyntax) {
		ExtractObject extractObject = Tong.getTongChiaBaDu2(syntax);
		if(extractObject != null){
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));
		}
		return extractObject;
	}
	
	protected static ExtractObject phanTichDeDau(String syntax, String rmSyntax) {
		ExtractObject extractObject = DauDit.getDauDuoi(syntax);
		if(extractObject != null){
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));
		}
		return extractObject;
	}
	
	protected static ExtractObject phanTichDeCham(String syntax, String rmSyntax) {
		ExtractObject extractObject = Cham.getCham(syntax);
		if(extractObject != null){
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));
		}
		return extractObject;
	}
	
	protected static ExtractObject phanTichDeChanLe(String syntax, String rmSyntax) {
		ExtractObject extractObject = ChanLe.getChanLe(syntax);
		if(extractObject != null){
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));
		}
		return extractObject;
	}
	
	protected static ExtractObject phanTichDeToNho(String syntax, String rmSyntax) {
		ExtractObject extractObject = ToNho.getToNho(syntax);
		if(extractObject != null){
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));
		}
		return extractObject;
	}
	
	protected static ExtractObject phanTichDeDan(String syntax, String rmSyntax) {
		ExtractObject extractObject = Dan.getDan(syntax);
		if(extractObject != null){
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));
		}
		return extractObject;
	}
	
	protected static ExtractObject phanTichDeGhep(String syntax, String rmSyntax) {
		ExtractObject extractObject = Ghep.getGhep(syntax);
		if(extractObject != null){
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));
		}
		return extractObject;
	}

	protected static ExtractObject phanTichBaCang(String syntax, String rmSyntax) {
		ExtractObject extractObject = Bacang.getBacang(syntax);
		if(extractObject != null){
			extractObject.RemoveNumbers = Utils.extractRemoveNumber(Utils.getRemoveNumber(rmSyntax));
		}
		return extractObject;
	}
}
