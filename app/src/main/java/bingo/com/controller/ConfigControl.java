package bingo.com.controller;

import com.bingo.analyze.calculation.BingoCalc;

import java.util.Set;

import bingo.com.enumtype.LotoType;
import bingo.com.model.ContactModel;
import bingo.com.utils.Constant;

public class ConfigControl {

    public static void setConfigForUser(ContactModel config) {
        BingoCalc.HS_DE = config.getDITDB() / 1000d;
        BingoCalc.HS_LO = config.getLO() / 1000d;
        BingoCalc.HS_BACANG = config.getBACANG() / 1000d;
        BingoCalc.HS_XIEN_2 = config.getXIEN2() / 1000d;
        BingoCalc.HS_XIEN_3 = config.getXIEN3() / 1000d;
        BingoCalc.HS_XIEN_4 = config.getXIEN4() / 1000d;

        BingoCalc.HS_THANG_DE = (int) (config.getDITDB_LANAN() / 1000d);
        BingoCalc.HS_THANG_LO = (int) (config.getLO_LANAN() / 1000d);
        BingoCalc.HS_THANG_BACANG = (int) (config.getBACANG_LANAN() / 1000d);
        BingoCalc.HS_THANG_XIEN_2 = (int) (config.getXIEN2_LANAN() / 1000d);
        BingoCalc.HS_THANG_XIEN_3 = (int) (config.getXIEN3_LANAN() / 1000d);
        BingoCalc.HS_THANG_XIEN_4 = (int) (config.getXIEN4_LANAN() / 1000d);
    }

    public static double getHeSo(ContactModel config, String type) {

        return config.getHesogiu(LotoType.convert(type));
    }

    public static String buildTypeNumber(String origin, String num) {
        String build = origin;

        if (build.contains("xien") || build.contains("xq"))
        {
            build = "xien".concat(String.valueOf(num.split(Constant.REGEX_SEPARATE_NUMBER_ANALYZE).length));
        }

        return build;
    }

    public static int getCountSubstring(String sub, String all) {
        if (sub == null || all == null) return 0;

        return (all.length() - all.replace(sub, "").length()) / sub.length();
    }

    public static String getPointWin(String num, String type, String price, String winNumber) {

        if (type.equalsIgnoreCase("lo"))
        {
            return String.valueOf(Double.parseDouble(price) * getCountSubstring(num, winNumber));
        }
        else
        {
            return getCountSubstring(num, winNumber) > 0 ? price : "0";
        }
    }
}
