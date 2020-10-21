package com.bingo.common.utils;

import com.bingo.analyze.ExtractObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bacang {

    public static ExtractObject getBacang(String syntax) {
        ExtractObject extractObject = null;
        boolean isSyntax = syntax.matches(".*bacang\\s*[0-9\\s]+.*x[0-9]+\\s*(k|d)");
        if (isSyntax) {
            extractObject = Utils.extractUnitPrice(syntax);
            extractObject.Numbers = new ArrayList<>();

            String tempSyntax = Utils.getRootSyntaxString(syntax);

            String regexDau = "([0-9]{3})";
            Matcher matcherDau = Pattern.compile(regexDau).matcher(tempSyntax);
            while (matcherDau.find()) {
                String num = matcherDau.group(1).trim();
                extractObject.Numbers.add(num);
            }

            extractObject.Type = "bacang";
        }

        return extractObject;
    }
}
