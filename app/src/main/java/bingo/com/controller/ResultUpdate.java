package bingo.com.controller;

import java.math.BigDecimal;
import android.text.TextUtils;

import com.bingo.analyze.Analyze;
import com.bingo.analyze.AnalyzeSMSNew;
import com.bingo.analyze.calculation.BingoCalc;

import org.json.JSONArray;

import bingo.com.enumtype.LotoType;
import bingo.com.helperdb.MessageDBHelper;
import bingo.com.model.ContactModel;
import bingo.com.model.ReceivedModel;
import bingo.com.utils.Formats;
import bingo.com.utils.LoaderSQLite;
import bingo.com.utils.Utils;

public class ResultUpdate {

    private AnalyzeSMSNew analyzeSMS;

    private ContactModel config;

    private String lastType = null;

    private String time;
    private String typemessage;
    private String winMessage;

    private JSONArray detail;

    double de_win;
    double lo_win;
    double lo_point_win;
    double xien2_win;
    double xien3_win;
    double xien4_win;
    double bacang_win;

    public ResultUpdate(AnalyzeSMSNew analyzeSMS) {
        this.analyzeSMS = analyzeSMS;
    }

    public void setPhone(String phone) {
        config = LoaderSQLite.getConfigParameter(phone);

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

    public void setData(String time, String typeMessage, JSONArray detail) {
        this.time = time;
        this.typemessage = typeMessage;
        this.detail = detail;

        this.de_win = 0;
        this.lo_win = 0;
        this.lo_point_win = 0;
        this.xien2_win = 0;
        this.xien3_win = 0;
        this.xien4_win = 0;
        this.bacang_win = 0;
        this.winMessage = "";
        lastType = null;
    }

    public boolean calculateNumber(String number, String type, String price, String unit, boolean isLast) {

        if (config != null)
        {
            int fPrice = (int) Double.parseDouble(price);

            Analyze analyze = analyzeSMS.getAnalyze(number, type, String.valueOf(fPrice), unit);

            /*if (messageType != TypeMessage.KEEP && !TextUtils.isEmpty(analyze.winNumber))
            {
                MessageDBHelper.updateWinNumberSyntax(config.getPhone(), time, type.concat(" ").concat(analyze.winNumber));
            }*/

            String message = "";

            String mergeType = type.contains("xien") ? "xien" : type;

            boolean hasWin = !TextUtils.isEmpty(analyze.winNumber);

            unit = mergeType.equals(LotoType.lo.name()) ? "d" : "n";

            if (lastType == null)
            {
                message = message.concat(mergeType).concat("<br>");
            }
            else
            {
                if (!lastType.equals(mergeType))
                {
                    message = message.concat("<br>").concat(mergeType).concat("<br>");
                }
            }

            lastType = mergeType;

            if (hasWin)
            {
                //Build win.
                String countWin = "";

                int cntWin = ConfigControl.getCountSubstring(number, analyze.winNumber);

                for (int i = 0; i < cntWin; i++)
                {
                    countWin += "*";
                }

                number = Formats.getStringHtmlText("", number + countWin, "#FF0000");

                //Cal win.
                double win = calculateWin(analyze.guestWin);
                double winPoint = Double.parseDouble(price) * cntWin;
                updateCalWin(type, win, winPoint);
            }

            if (isLast)
            {
                message = message.concat(number).concat("x").concat(price).concat(unit).concat("<br>");
            }
            else
            {
                message = message.concat(number).concat(", ");
            }

            winMessage = winMessage.concat(message);
            return hasWin;
        }

        return false;
    }

    public void endSyntaxAnalyze() {
        if (!winMessage.matches(".*(<br>)\\s*"))
        {
            winMessage = winMessage.concat("<br>");
        }
    }

    public ReceivedModel build() {
        MessageDBHelper.updateWinMessage(config.getPhone(), time, typemessage, winMessage);

        return new ReceivedModel(
                config.getName(), config.getPhone(), time, Utils.convertDate(time), typemessage,
                String.valueOf(de_win), String.valueOf(lo_win), String.valueOf(lo_point_win), String.valueOf(xien2_win), String.valueOf(xien3_win), String.valueOf(xien4_win), String.valueOf(bacang_win),
                detail
        );
    }

    private void updateCalWin(String type, double win, double pointLo) {
        if (type.contains("de"))
        {
            de_win += win;
        }
        else if (type.contains("lo"))
        {
            lo_win += win;
            lo_point_win += pointLo;
        }
        else if (type.contains("xien2"))
        {
            xien2_win += win;
        }
        else if (type.contains("xien3"))
        {
            xien3_win += win;
        }
        else if (type.contains("xien4"))
        {
            xien4_win += win;
        }
        else if (type.contains("bacang") || type.contains("bc"))
        {
            bacang_win += win;
        }
    }

    private double calculateWin(BigDecimal win) {
        return win.doubleValue();
    }

    private double countAmountPoint(ContactModel config, String price, String type) {
        return Double.parseDouble(price) * config.getValue(LotoType.convert(type)) / 1000d;
    }
}
