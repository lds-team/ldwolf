package bingo.com.utils;

import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;

import com.bingo.analyze.Analyze;
import com.bingo.analyze.AnalyzeSMSNew;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class Formats {

    public static String formatBigDecimal(BigDecimal decimal) {
        DecimalFormat df = new DecimalFormat();

        df.setMaximumFractionDigits(1);

        df.setMinimumFractionDigits(1);

        df.setGroupingUsed(false);

        return df.format(decimal);
    }

    public static double formatBigDecimalToDouble(BigDecimal decimal) {

        return decimal.doubleValue();
    }

    public static String formatDouble(double d) {
        DecimalFormat df = new DecimalFormat("0.0");

        return df.format(d);
    }

    public static double formatToDouble(String s) {
        try
        {
            return Double.valueOf(s);
        }
        catch (NumberFormatException e)
        {
            return 0;
        }
    }

    public static String getDotNumberText(long input) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###,###", symbols);

        return decimalFormat.format(input);
    }

    public static Spanned getHtmlText(String text1, String text2, String color2) {
        String text = "<font color=" + "#808080" + ">" + text1 + "</font> <font color=" + color2 + ">" + text2 + "</font>";
        return Html.fromHtml(text);
    }

    public static String getStringHtmlText(String text1, String text2, String color2) {
        return "<font color=" + "#808080" + ">" + text1 + "</font> <font color=" + color2 + ">" + text2 + "</font>";
    }

    public static String getStringHtmlText(String color2, int idxHtmlString, String... text) {
        String html = "";
        int count = text.length;
        for (int i = 0; i < count; i++)
        {
            if (i == idxHtmlString)
            {
                html = html + "<font color=" + color2 + ">" + text[i] + "</font>";
            }
            else
            {
                html = html + "<font color=" + "#000000" + ">" + text[i] + "</font>";
            }
        }
        return html;
    }

    public static String formatDate(Calendar calendar) {
        SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
        return dateformat.format(calendar.getTime());
    }

    public static Spanned formatStringError(String text, String error) {
        Spanned last = new SpannableString("");

        int lastPosition = 0;

        int position = 0;

        for (String e : error.split(Constant.SEPARATE)) {
            //TODO
            int f = /*getFirstDigit(error);*/ e.indexOf("[");
            int l = /*getLastDigit(error);*/ e.indexOf("]");

            if (f > -1)
                e = e.substring(f + 1, l).trim();

            if (TextUtils.isEmpty(e.trim()))
            {
                return (Spanned) TextUtils.concat(last, getHtmlText("", text, "#FF0000"));
            }
            else
            {
                position = text.toLowerCase().indexOf(e.toLowerCase().concat(" ")); //This concat to sure that is in the end of syntax.

                if (position == -1)
                {
                    position = text.toLowerCase().lastIndexOf(e.toLowerCase().trim());
                }

                if (position != - 1)
                {
                    last = (Spanned) TextUtils.concat(last, getHtmlText(text.substring(lastPosition, position), e, "#FF0000"));
                    lastPosition = position + e.length();
                }
            }
        }

        if (lastPosition < text.length() -1)
        {
            if (last.toString().isEmpty())
            {
                last = (Spanned) TextUtils.concat(last, getHtmlText("", text, "#FF0000"));
            }
            else
            {
                last = (Spanned) TextUtils.concat(last, text.substring(lastPosition));
            }
        }

        return last;
    }

    public static Spanned formatStringWin(String text, String win) {
        Spanned last = new SpannableString("");

        int lastPosition = 0;

        int position = 0;

        for (String e : win.split(Constant.SEPARATE)) {

            if (TextUtils.isEmpty(e.trim()))
            {
                return new SpannableString(text);
            }
            else
            {
                while (true)
                {
                    position = text.toLowerCase().indexOf(e.toLowerCase().concat(" "), lastPosition); //This concat to sure that is in the end of syntax.

                    if (position != - 1)
                    {
                        last = (Spanned) TextUtils.concat(last, getHtmlText(text.substring(lastPosition, position).trim(), e + " ", "#FF0000"));
                        lastPosition = position + e.length();
                    }
                    else
                    {
                        position = text.toLowerCase().lastIndexOf(e.toLowerCase().trim(), lastPosition);

                        if (position != - 1 && position > lastPosition)
                        {
                            last = (Spanned) TextUtils.concat(last, getHtmlText(text.substring(lastPosition, position).trim(), e, "#FF0000"));
                            lastPosition = position + e.length();
                        }

                        break;
                    }
                }
            }
        }

        if (lastPosition < text.length() -1)
            last = (Spanned) TextUtils.concat(last, text.substring(lastPosition).trim());

        return last;
    }

    public static Spanned getError(String rawString, Analyze eObj) {
        if (eObj.error && eObj.errorMessage != null)
        {
            String error = "[";

            error = error.concat(eObj.errorMessage).trim();

            if (error.length() == 1)
            {
                return getHtmlText("", rawString, "#FF0000");
            }
            else
            {
                error = error.concat("]");
                return formatStringError(eObj.analyzeMessage, error);
            }
        }
        else
        {
            return new SpannableString(rawString);
        }
    }

    public static Spanned getError(String rawString, String errorMessage) {
        if (!TextUtils.isEmpty(errorMessage))
        {
            String error = "[";

            error = error.concat(errorMessage).trim();

            if (error.length() == 1)
            {
                return getHtmlText("", rawString, "#FF0000");
            }
            else
            {
                error = error.concat("]");
                return formatStringError(rawString, error);
            }
        }
        else
        {
            return new SpannableString(rawString);
        }
    }

    public static int getFirstDigit(String s) {
        for (int i = 0; i < s.length(); i++)
        {
            if (Character.isDigit(s.charAt(i)))
                return i - 1;
        }

        return -1;
    }

    public static int getLastDigit(String s) {
        for (int i = s.length() - 1; i >= 0; i--)
        {
            if (Character.isDigit(s.charAt(i)))
                return i - 1;
        }

        return -1;
    }
}
