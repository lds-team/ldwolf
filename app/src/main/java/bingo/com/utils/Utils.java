package bingo.com.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bingo.com.R;

public class Utils {

    public static String currentDate;

    public static Dialog createDialog(Context context, int layout, boolean outsideCancel) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_choosermenu);
        dialog.setCancelable(!outsideCancel);

        return dialog;
    }

    public static Dialog createDialog(Context context, View view, boolean notCancel) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setCancelable(!notCancel);

        return dialog;
    }

    @NonNull
    public static String convertDate(String dateInMilliseconds) {
        String dateFormat = "dd/MM/yyyy";

        if(dateInMilliseconds == null){
            return "";
        }

        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }

    public static String convertTime(String dateInMilliseconds) {
        String dateFormat = "HH:mm:ss";

        if(dateInMilliseconds == null){
            return "";
        }

        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }

    public static long getTimeFromSaveConfig(String date, String time) {
        Pattern pattern1 = Pattern.compile("(?:(\\d+)/)?(?:(\\d+)/)?(?:(\\d+))?");
        Matcher matcher1 = pattern1.matcher(date);
        boolean matches1 = matcher1.matches();

        Pattern pattern2 = Pattern.compile("(?:(\\d+):)?(?:(\\d+))?");
        Matcher matcher2 = pattern2.matcher(time);
        boolean matches2 = matcher2.matches();

        if (matches1 && matches2)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Integer.parseInt(matcher1.group(3)), Integer.parseInt(matcher1.group(2)) - 1, Integer.parseInt(matcher1.group(1)),
                    Integer.parseInt(matcher2.group(1)), Integer.parseInt(matcher2.group(2)), 0);

            long timeInMillis = calendar.getTimeInMillis();
            calendar.clear();

            return timeInMillis;
        }

        return 0;
    }

    //Only for get Time from config to check receive message or not.
    @Deprecated
    public static long getTimeFromConfig(String timeFormat) {
        Pattern pattern2 = Pattern.compile("(?:(\\d+):)?(?:(\\d+))?");
        Matcher matcher2 = pattern2.matcher(timeFormat);
        boolean matches2 = matcher2.matches();

        if (matches2)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(matcher2.group(1)));
            calendar.set(Calendar.MINUTE, Integer.parseInt(matcher2.group(2)) + 1);
            calendar.set(Calendar.SECOND, 0);

            long timeInMillis = calendar.getTimeInMillis();
            calendar.clear();

            return timeInMillis;
        }

        return 0;
    }

    public static long convertLongTime(String date) {
        Pattern pattern = Pattern.compile("(?:(\\d+)/)?(?:(\\d+)/)?(?:(\\d+))?");
        Matcher matcher = pattern.matcher(date);
        boolean matches = matcher.matches();

        if (matches)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(2)) - 1, Integer.parseInt(matcher.group(1)));
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            long time = calendar.getTimeInMillis();
            calendar.clear();

            return time;
        }

        return 0;
    }

    public static String getCurrentDate() {
        return currentDate;
    }

    public static String getTimeWithCurrentConfigDate() {
        String[] dmy = currentDate.split("/");

        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Integer.parseInt(dmy[2]), Integer.parseInt(dmy[1]) - 1, Integer.parseInt(dmy[0]));

        String format = String.valueOf(mCalendar.getTimeInMillis());

        mCalendar.clear();

        return format;
    }

    public static String getNewTimeFromDate(String date) {
        String[] dmy = date.split("/");

        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Integer.parseInt(dmy[2]), Integer.parseInt(dmy[1]) - 1, Integer.parseInt(dmy[0]));

        long format = mCalendar.getTimeInMillis();

        mCalendar.clear();

        return String.valueOf(format);
    }

    public static long getLongTimeWithCurrentConfigDate() {
        String[] dmy = currentDate.split("/");

        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Integer.parseInt(dmy[2]), Integer.parseInt(dmy[1]) - 1, Integer.parseInt(dmy[0]));

        long format = mCalendar.getTimeInMillis();

        mCalendar.clear();

        return format;
    }

    public static String getTimeWithCurrentConfigDate(String timeInMilisecond) {
        String[] dmy = currentDate.split("/");

        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(Long.parseLong(timeInMilisecond));
        mCalendar.set(Integer.parseInt(dmy[2]), Integer.parseInt(dmy[1]) - 1, Integer.parseInt(dmy[0]));

        String format = String.valueOf(mCalendar.getTimeInMillis());

        mCalendar.clear();

        return format;
    }

    public static String getNewTimeFrom(String timeInMilisecond) {

        Calendar current = Calendar.getInstance();
        current.setTimeInMillis(Long.parseLong(timeInMilisecond));

        String[] dmy = Utils.convertDate(String.valueOf(current.getTimeInMillis())).split("/");

        current.clear();

        Calendar mCalendar = Calendar.getInstance();

        mCalendar.set(Integer.parseInt(dmy[2]), Integer.parseInt(dmy[1]) - 1, Integer.parseInt(dmy[0]));

        String format = String.valueOf(mCalendar.getTimeInMillis());

        mCalendar.clear();

        return format;
    }

    @Deprecated
    public static int[] getDayMonthYearFromCurrent() {
        String[] current = currentDate.split("/");

        return new int[]{Integer.parseInt(current[0]), Integer.parseInt(current[1]), Integer.parseInt(current[2])};
    }

    public static String today() {
        Calendar mCalendar = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        String format = dateformat.format(mCalendar.getTime());

        mCalendar.clear();

        return format;
    }

    public static String today(String format) {
        Calendar mCalendar = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat(format, Locale.US);

        String today = dateformat.format(mCalendar.getTime());

        mCalendar.clear();

        return today;
    }

    public static String getDateFrom(int day, int month, int year) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(year, month, day);

        SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        String format = dateformat.format(mCalendar.getTime());

        mCalendar.clear();

        return format;
    }

    public static String getDateFrom(String timeInLong) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(Long.valueOf(timeInLong));

        SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        String format = dateformat.format(mCalendar.getTime());

        mCalendar.clear();

        return format;
    }

    public static String getTimeFrom(int hour, int minute) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.HOUR_OF_DAY, hour);
        mCalendar.set(Calendar.MINUTE, minute);

        SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm", Locale.US);

        String format = dateformat.format(mCalendar.getTime());

        mCalendar.clear();

        return format;
    }

    public static boolean isSoFarTimeWithCurrent() {
        int nowYear = Calendar.getInstance().get(Calendar.YEAR);
        int nowMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int[] configDate = getDayMonthYearFromCurrent();
        int configMonth = configDate[1];
        int configYear = configDate[2];

        if (nowYear == configYear)
        {
            return nowMonth - configMonth > 2;
        }
        else
        {
            if (nowYear - configYear > 1)
            {
                return true;
            }
            else if (nowYear - configYear == 1)
            {
                return nowMonth + 12 - configMonth > 2;
            }
            else
            {
                return false;
            }
        }
    }

    public static String fixPhone(String phone) {
        if (phone.startsWith("+84"))
            phone = phone.replaceFirst("\\+84", "0");

        phone = phone.replaceAll("\\s+","");
        phone = phone.replaceAll("\\(","");
        phone = phone.replaceAll("\\)","");
        phone = phone.replaceAll("-","");
        return phone;
    }

    public static void showAlert(Context context, String title, String message, String button, final Runnable runnable) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (runnable != null)
                            runnable.run();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static void showAlertTwoButton(Context context, String title, String message, String btn1, String btn2, final Runnable runnable1, final Runnable runnable2) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(btn1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (runnable1 != null)
                            runnable1.run();
                    }
                })
                .setNegativeButton(btn2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (runnable2 != null)
                            runnable2.run();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static boolean isFirstLaunch(Context context) {
        boolean launchFlag;
        File installation = new File(context.getFilesDir(), "INSTALLATION");
        try {

            launchFlag = !installation.exists();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return launchFlag;
    }

    public static JSONArray sortByType(JSONArray array) {
        try {
            List<JSONObject> list = new ArrayList<>();

            for (int i = 0; i < array.length(); i++)
            {
                list.add(array.getJSONObject(i));
            }

            Collections.sort(list, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject o1, JSONObject o2) {
                    String val1 = null;
                    String val2 = null;
                    String price1 = null;
                    String price2 = null;

                    try {
                        val1 = o1.getString(JSONAnalyzeBuilder.KEY_TYPE);
                        val2 = o2.getString(JSONAnalyzeBuilder.KEY_TYPE);
                        price1 = o1.getString(JSONAnalyzeBuilder.KEY_PRICE);
                        price2 = o2.getString(JSONAnalyzeBuilder.KEY_PRICE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (val1 == null || val2 == null || price1 == null || price2 == null)
                    {
                        return 0;
                    }
                    else
                    {
                        int compare = val1.compareToIgnoreCase(val2);
                        if (compare == 0)
                        {
                            int p1 = Integer.parseInt(price1);
                            int p2 = Integer.parseInt(price2);
                            if (p1 > p2)
                            {
                                return -1;
                            }
                            else if (p1 == p2)
                            {
                                return 0;
                            }
                            else
                            {
                                return 1;
                            }
                        }
                        else
                        {
                            return compare;
                        }
                    }
                }
            });

            array = new JSONArray();
            for (int i = 0; i < list.size(); i++)
            {
                array.put(list.get(i));
            }
        } catch (JSONException e) {

        }

        return array;
    }

    public static String orQuery(String field, String[] list) {
        if (list == null)
        {
            return "";
        }

        String first = " AND ( " + field + " = '";
        String join = TextUtils.join("' OR " + field + " = '", list);
        String last = "') ";

        return first + join + last;
    }

    public static String getPackageNameForAPK(final String archiveFilePath, Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageArchiveInfo(archiveFilePath, PackageManager.GET_META_DATA);
            return info.packageName;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String stripHtml(String html) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            return Html.fromHtml(html).toString();
        }
    }

    public static String getDotNumberText(double input) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
//        symbols.setDecimalSeparator(',');
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###,###.##", symbols);

        return decimalFormat.format(input);
    }
}
