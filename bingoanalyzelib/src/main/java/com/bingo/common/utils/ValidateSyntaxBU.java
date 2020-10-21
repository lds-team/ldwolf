package com.bingo.common.utils;

import android.text.TextUtils;

import com.bingo.analyze.ResultObject;

import java.util.LinkedHashSet;
import java.util.Set;

public class ValidateSyntaxBU {
	public static ResultObject validate(String syntax){
		ResultObject resultObject = new ResultObject();
		boolean isRightSyntax = syntax.trim().matches(".*x[0-9]+\\s*k|.*x[0-9]+\\s*d");

		resultObject.Error = /*!isRightSyntax*/true; //Default error = true, if syntax match regex below, error will change to false.
		if(!isRightSyntax){
			resultObject.MsgError = syntax;
			return resultObject;
		}

		String removeSyntax = Utils.getRemoveSyntaxString(syntax);
		if (!TextUtils.isEmpty(removeSyntax))
		{
			String[] remove = removeSyntax.split("\\W*bor\\W*");

			for (String rm : remove)
			{
				if (!rm.isEmpty())
				{
					boolean match = rm.trim().matches("(([0-9]{2,3}([-.,\\s]+))*[0-9]{2,3}[-.,\\s]*)|(.*dau\\s*[0-9]+.*)|(.*dit\\s*[0-9]+.*|.*duoi\\s*[0-9]+.*)|(.*tong\\s*[0-9]+.*)" +
							"|(.*boj\\s*[0-9]+.*)||(.*dau\\s*(to|nho).*|.*dit\\s*(to|nho).*|.*duoi\\s*(to|nho).*)" +
							"|(.*dau\\s*(chan|le).*|.*dit\\s*(chan|le).*|.*duoi\\s*(chan|le).*)|(.*tong\\s*chan.*)|(.*tong\\s*le.*)" +
							"|(.*kep\\s*bang.*)|(.*kep\\s*lech.*)|(.*sat\\s*kep.*)" +
							"|(.*tong\\s*chia\\s*[ba3]+.*)|(.*tong\\s*chia\\s*[ba3\\+]+\\s*du\\s*[1mot]+.*)|(.*tong\\s*chia\\s*[ba3\\+]+\\s*du\\s*[2hai]+.*)" +
							"|(trung|trong)|(.*(to|nho)\\s*(to|nho).*)");

					if (!match)
					{
						resultObject.MsgError = removeSyntax;
						return resultObject;
					}
				}
			}
		}

		syntax = Utils.getRootSyntaxWithCost(syntax);

		if(syntax.contains("lo") && syntax.contains("boj")){
			syntax = syntax.replaceAll("bo[\\s]*([0-9]+)", "boj $1");
			isRightSyntax = syntax.matches("lo\\s*boj\\s*([0-9][0-9]([,.\\s]*))*\\s*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)");
			isRightSyntax = isRightSyntax && validateMistakeSyntax2Char(syntax);
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax;
				return resultObject;
			}
		}else if(syntax.contains("de") && syntax.contains("boj")){
			syntax = syntax.replaceAll("bo[\\s]*([0-9]+)", "boj $1");
			isRightSyntax = syntax.matches("de\\s*boj\\s*([0-9][0-9]([,.\\s]*))*\\s*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)");
			isRightSyntax = isRightSyntax && validateMistakeSyntax2Char(syntax);
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax;
				return resultObject;
			}
		}else if(syntax.contains("trung") || syntax.contains("trong")){
			//TODO
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
		}else if(syntax.contains("kep") && syntax.contains("lech")){
			isRightSyntax = syntax.matches("\\W*(de|lo)[\\s:]*kep\\s*lech\\s*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)");
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax;
				return resultObject;
			}
		}else if(syntax.contains("kep") && syntax.contains("sat")){
			isRightSyntax = syntax.matches("\\W*(de|lo)[\\s:]*sat\\s*kep\\s*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)");
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax;
				return resultObject;
			}
		}else if(syntax.contains("kep") /*&& syntax.contains("bang")*/){
			isRightSyntax = syntax.matches("\\W*(de|lo)[\\s:]*kep\\s*(bang\\s*)*\\s*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)");
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax;
				return resultObject;
			}
		}else if((syntax.contains("xien") && syntax.contains("ghep")) || syntax.contains("xg")){
			isRightSyntax = syntax.matches("\\W*(xien\\s*ghep|xg)\\s*[0-9][\\s:]+[0-9\\s.,-]+\\s*x[0-9]+\\s*(k|d)");
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax;
				return resultObject;
			}
		}else if(syntax.contains("xien") || syntax.contains("xq")){
			isRightSyntax = syntax.matches("\\W*(xien|lo\\s*xien|xien\\s*ghep|xien\\s*quay|xq)[\\s:]*[0-9\\s.,/-]+\\s*x[0-9]+\\s*(k|d)");

			String tempSyntax = Utils.getRootSyntaxString(syntax).trim();
			tempSyntax = tempSyntax.replaceFirst("xien\\s*quay\\W*", "").trim();
			tempSyntax = tempSyntax.replaceFirst("xien\\s*ghep\\W*", "").trim();
			tempSyntax = tempSyntax.replaceFirst("xien\\s*[0-9]?\\W+", "").trim();

			if (tempSyntax.replaceAll("[0-9\\s,./-]", "").length() > 0)
			{
				isRightSyntax = false;
			}

			if (isRightSyntax)
			{
				tempSyntax = tempSyntax.replaceAll("\\s*([,./-])\\s*", "$1");
				tempSyntax = tempSyntax.replaceAll("\\s+", " ");

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

				if (tempSyntax.matches(".*[0-9]{3}.*"))
				{
					if (sizeSepa > 1)
					{
						isRightSyntax = false;
					}
				}
			}

			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax;
				return resultObject;
			}
		}else if(syntax.contains("dan")){
			isRightSyntax = syntax.matches("\\W*(de|lo)[\\s:]*dan\\s*([0-9]{2}[\\W]*)+\\s*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)");
			isRightSyntax = isRightSyntax && validateMistakeSyntax2Char(syntax);
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax;
				return resultObject;
			}
		}else if(syntax.contains("cham")){
			isRightSyntax = syntax.matches("\\W*(de|lo)[\\s:]*cham\\s*([0-9][\\s.,]*)+(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)");
			isRightSyntax = isRightSyntax && validateMistakeSyntax(syntax);
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax;
				return resultObject;
			}
		}else if(syntax.contains("ghep")){
			isRightSyntax = syntax.matches("\\W*(de|lo)[\\s:]*dau\\s*([0-9]+|be|to|le|chan)\\s*ghep\\s*[a-zA-Z]*\\s*(dit|duoi)\\s*([0-9|be|to|le|chan]+)\\s*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)");
			isRightSyntax = isRightSyntax && validateMistakeSyntax(syntax);
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax;
				return resultObject;
			}
		}else if((syntax.contains("dau") || syntax.contains("dit") || syntax.contains("duoi")) && !syntax.matches(".*tong\\s*(duoi).*")){
			isRightSyntax = syntax.matches("\\W*(de|lo)[\\s:]*((dau|dit|duoi)\\W*(dau|dit|duoi)*[\\s:]*[0-9]+[0-9\\W]*)+\\W*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)")
					|| syntax.matches("\\W*(de|lo)[\\s:]*(dau)\\W*(to|be|nho|lon)\\W*(hon)\\W*(dit|duoi)\\W*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)")
					|| syntax.matches("\\W*(de|lo)[\\s:]*(dit|duoi)\\W*(to|be|nho|lon)\\W*(hon)\\W*(dau)\\W*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)")
					|| syntax.matches("\\W*(de|lo)[\\s:]*(dau)\\W*(to|be)\\W*(dit|duoi)\\W*(to|be)\\W*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)")
					|| syntax.matches("\\W*(de|lo)[\\s:]*((dau|dit|duoi)[\\s:]*(to|be)\\W*)+\\W*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)")
					|| syntax.matches("\\W*(de|lo)[\\s:]*((dau|dit|duoi)[\\s:]*(chan|le)\\W*)+\\W*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)");
			isRightSyntax = isRightSyntax && validateMistakeSyntax(syntax);
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax;
				return resultObject;
			}
		}else if((syntax.contains("lo") || syntax.contains("de")) && syntax.contains("100") && syntax.contains("so")){
			isRightSyntax = syntax.matches(".*100\\s*so\\s*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)");
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax;
				return resultObject;
			}
		}/*else if (syntax.contains("tong") && syntax.contains("to")){
			isRightSyntax = syntax.matches(".*tong\\s*to\\s*x[0-9]+\\s*(k|d)");
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax;
				return resultObject;
			}
		}else if (syntax.contains("tong") && syntax.contains("be")){
			isRightSyntax = syntax.matches(".*tong\\s*be\\s*x[0-9]+\\s*(k|d)");
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax;
				return resultObject;
			}
		}*/else if (syntax.contains("tong") && syntax.contains("chan")){
			isRightSyntax = syntax.matches(".*tong\\s*chan\\s*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)");
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax;
				return resultObject;
			}
		}else if (syntax.contains("tong") && syntax.contains("le")){
			isRightSyntax = syntax.matches(".*tong\\s*le\\s*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)");
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax;
				return resultObject;
			}
		}else if ((syntax.contains("tong") && syntax.contains("tren")) || syntax.matches(".*tong\\s*(duoi).*")){
			isRightSyntax = syntax.matches(".*tong\\s*(tren|duoi)\\s*10\\s*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)");
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax;
				return resultObject;
			}
		}else if(syntax.contains("tong") && syntax.contains("chia")){
			isRightSyntax = syntax.matches(".*tong\\s*(khong)*\\s*chia\\s*[ba3]+\\s*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)");
			boolean isSyntaxDu1 = syntax.matches(".*tong\\s*chia\\s*[ba3\\+]+\\s*du\\s*[1mot]+\\s*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)");
			boolean isSyntaxDu2 = syntax.matches(".*tong\\s*chia\\s*[ba3\\+]+\\s*du\\s*[2hai]+\\s*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)");

			resultObject.Error = !(isRightSyntax | isSyntaxDu1 | isSyntaxDu2);
			if(!isRightSyntax){
				resultObject.MsgError = syntax;
				return resultObject;
			}
		}else if(syntax.contains("tong")){
			isRightSyntax = syntax.matches(".*tong\\s*(([0-9][\\s.,]*)+|to|be|le|chan)\\s*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)");
			isRightSyntax = isRightSyntax && validateMistakeSyntax(syntax);
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax;
				return resultObject;
			}
		}else if(syntax.replaceAll(" ", "").contains("toto") || syntax.replaceAll(" ", "").contains("nhonho")
				|| syntax.replaceAll(" ", "").contains("nhoto") || syntax.replaceAll(" ", "").contains("tonho")){
			isRightSyntax = syntax.matches(".*(nho\\s*nho|to\\s*to|nho\\s*to|to\\s*nho)\\s*(bor*[\\s\\w]*)?x[0-9]+\\s*(k|d)");
			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax;
				return resultObject;
			}
		}else if(syntax.contains("de")){
			isRightSyntax = syntax.matches("de[\\s:]*([0-9]{2,3}([-.,/\\s]+))*[0-9]{2,3}[-.,/\\s]*\\s*x[0-9]+\\s*(k|d)");

			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax;
				return resultObject;
			}
		}else if(syntax.contains("lo")){
			isRightSyntax = syntax.matches("lo[\\s:]*([0-9]{2,3}([-.,/\\s]+))*[0-9]{2,3}[-.,/\\s]*\\s*x[0-9]+\\s*(k|d)");

			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax;
				return resultObject;
			}
		}else if(syntax.contains("bacang")){
			isRightSyntax = syntax.matches("(bc|bacang|ba\\scang)[\\s:]*([0-9]{3}([-.,\\s]*))+\\s*x[0-9]+\\s*(k|d)");

			resultObject.Error = !isRightSyntax;
			if(!isRightSyntax){
				resultObject.MsgError = syntax;
				return resultObject;
			}
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
}
