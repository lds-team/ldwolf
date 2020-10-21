package bingo.com.utils;

import android.content.Context;

import com.bingo.analyze.Analyze;
import com.bingo.analyze.AnalyzeSMSNew;

import bingo.com.pref.ConfigPreference;

public class Validate {

    public static String canAnalyze(Context context, String content) {
        AnalyzeSMSNew analyze = new AnalyzeSMSNew();
        Analyze analyzeObj = analyze.validateMessage(content, ConfigPreference.getRegexCustom(context));

        if (analyzeObj.error)
        {
            return analyzeObj.errorMessage;
        }

        return null;
    }

    public static Analyze validate(Context context, String content) {
        AnalyzeSMSNew analyzeSMS = new AnalyzeSMSNew();

        return analyzeSMS.validateMessage(content, ConfigPreference.getRegexCustom(context));
    }
}
