package com.bingo.common.utils;

import com.bingo.BuildConfig;
import com.bingo.analyze.Constant;
import com.bingo.analyze.ExtractObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bo {

    public static ExtractObject getBo(String syntax) {
        ExtractObject extractObject = null;
        boolean isSyntax = syntax.matches(".*boj\\s*([0-9][0-9]([,\\s]*))*.*x[0-9]+\\s*k|.*boj\\s*([0-9][0-9]([,\\s]*))*.*x[0-9]+\\s*d");
        if (isSyntax) {
            extractObject = Utils.extractUnitPrice(syntax);
            extractObject.Numbers = new ArrayList<>();

            String tempSyntax = Utils.getRootSyntaxString(syntax);

            String regNumber = "([0-9][0-9])";
            Pattern patternNumber = Pattern.compile(regNumber);
            Matcher matcherNumber = patternNumber.matcher(tempSyntax);

            while (matcherNumber.find()) {
                String numbers = matcherNumber.group(1);

                if (numbers != null)
                {
                    for (String number : numbers.split(Constant.REGEX_SEPARATE)) {
                        if (number.trim().equals(""))
                            continue;
                        extractObject.Numbers.addAll(layBo(number.trim()));
                    }
                }
            }

            extractObject.Syntax = syntax;

            boolean isDe = syntax.matches(".*de.*x[0-9]+\\s*k|.*de.*x[0-9]+\\s*d");
            if(isDe){
                extractObject.Type = "de";
            }else{
                boolean isLo = syntax.matches(".*lo.*x[0-9]+\\s*k|.*lo.*x[0-9]+\\s*d");
                if(isLo){
                    extractObject.Type = "lo";
                }else{
                    extractObject.Type = "Unknow";
                }
            }
        }

        return extractObject;
    }

    protected static ArrayList<String> layBo(String num) {
        String[] bo01 = {"01", "10", "06", "60", "51", "15", "56", "65"};
        String[] bo02 = {"02", "20", "07", "70", "52", "25", "57", "75"};
        String[] bo03 = {"03", "30", "08", "80", "53", "35", "58", "85"};
        String[] bo04 = {"04", "40", "09", "90", "54", "45", "59", "95"};
        String[] bo12 = {"12", "21", "17", "71", "62", "26", "67", "76"};
        String[] bo13 = {"13", "31", "18", "81", "63", "36", "68", "86"};
        String[] bo14 = {"14", "41", "19", "91", "64", "46", "69", "96"};
        String[] bo23 = {"23", "32", "28", "82", "73", "37", "78", "87"};
        String[] bo24 = {"24", "42", "29", "92", "74", "47", "79", "97"};
        String[] bo34 = {"34", "43", "39", "93", "84", "48", "89", "98"};
        String[] bo00 = {"00", "55", "05", "50"};
        String[] bo11 = {"11", "66", "16", "61"};
        String[] bo22 = {"22", "77", "27", "72"};
        String[] bo33 = {"33", "88", "38", "83"};
        String[] bo44 = {"44", "99", "49", "94"};

        String[][] bo = {bo01, bo02, bo03, bo04, bo12, bo13, bo14, bo23, bo24, bo34, bo00, bo11, bo22, bo33, bo44};

        for (String[] _bo : bo)
        {
            if (Arrays.toString(_bo).contains(num))
            {
                return new ArrayList<>(Arrays.asList(_bo));
            }
        }

        return new ArrayList<>();
    }
}
