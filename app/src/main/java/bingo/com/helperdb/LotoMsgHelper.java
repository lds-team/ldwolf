package bingo.com.helperdb;

import android.net.Uri;

public class LotoMsgHelper {

    public static String URL = "content://com.bingo.provider.Loto/lotomsgs";

    public static Uri uri = Uri.parse(URL);

    public static Uri getUriWithYear(String year) {

        String url = "content://com.bingo.provider.Loto/lotomsgs/year/" + year;

        return Uri.parse(url);
    }
}
