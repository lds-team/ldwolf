package bingo.com.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bingo.analyze.Analyze;
import com.bingo.analyze.AnalyzeSMSNew;
import com.bingo.analyze.NumberAndUnit;
import com.bingo.analyze.calculation.BingoCalc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import bingo.com.enumtype.LotoType;
import bingo.com.enumtype.TypeMessage;
import bingo.com.helperdb.ContactDBHelper;
import bingo.com.helperdb.FollowDBHelper;
import bingo.com.helperdb.MessageDBHelper;
import bingo.com.model.ContactModel;
import bingo.com.model.FollowModel;
import bingo.com.model.ResultReturnModel;
import bingo.com.model.StatisticInfoModel;

public class StatisticControlImport {

    public static ResultReturnModel[] getResultWithDay(Context context, Calendar calendars) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calendars.getTimeInMillis());

        String today = getStringDate(calendar);
        String toyear = String.valueOf(calendar.get(Calendar.YEAR));

        calendar.add(Calendar.DATE, -1);

        String yesterday = getStringDate(calendar);
        String yesterdayYear = String.valueOf(calendar.get(Calendar.YEAR));

        ResultReturnModel resultToday = LoaderSQLite.getResultFromDate(context, today, toyear, false);
        //TODO de quy de lay ket qua ngay gan nhat khong null.
        ResultReturnModel resultYesterday = LoaderSQLite.getResultFromDate(context, yesterday, yesterdayYear, false);

        calendar.clear();

        return new ResultReturnModel[]{resultToday, resultYesterday};
    }

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

    private static String getStringDate(Calendar calendar) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM", Locale.US);

        return format.format(calendar.getTime());
    }
}
