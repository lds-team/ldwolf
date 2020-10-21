package bingo.com.model;

import android.database.Cursor;
import android.text.TextUtils;

import java.util.HashMap;

import bingo.com.dialog.XuatSoDialog;
import bingo.com.enumtype.LotoType;
import bingo.com.enumtype.TypeMessage;
import bingo.com.helperdb.ContactColumnMaps;
import bingo.com.helperdb.FollowDBHelper;
import bingo.com.helperdb.db.FollowDatabase;
import bingo.com.utils.Constant;

public class MessageForm {

    private String message;

    private String error;

    public MessageForm(Builder builder) {
        this.message = builder.message;
        this.error = builder.error;
    }

    public String getMessage() {
        return message;
    }

    public String error() {
        return error;
    }

    public static class Builder {

        private String message;

        private String error;

        //for keep.
        public Builder(Cursor cursor, HashMap<Integer, Double> heso, String phone) {
            message = "";

            if (cursor != null && cursor.moveToFirst())
            {

                while (!cursor.isAfterLast())
                {
                    String parentType = cursor.getString(cursor.getColumnIndex(FollowDatabase.DETAIL_PARENT_TYPE));

                    String type = cursor.getString(cursor.getColumnIndex(FollowDatabase.DETAIL_TYPE));

                    String number = cursor.getString(cursor.getColumnIndex(FollowDatabase.DETAIL_NUMBER));

                    //TODO hot fix.
                    FollowDBHelper.deleteKeepValue(phone, number, TypeMessage.KEEP, LotoType.convert(type));


                    Double dou = heso.get(ContactColumnMaps.getKeepColumn(LotoType.convert(type)));

                    double hs = dou == null ? 0 : dou;

                    if (hs == 0d)
                    {
                        cursor.moveToNext();
                        continue;
                    }

                    int nhan = FollowDBHelper.getSumReceivedValueOfNumberMF(number, type, phone);

                    String unit = type.equals(LotoType.lo.name()) ? "d" : "n";

                    if (parentType.equals(LotoType.xien.name()))
                    {
                        number = number.replaceAll("-", ",");
                    }

                    double price = nhan * hs;

                    double max = heso.get(ContactColumnMaps.getMaxColumn(LotoType.convert(type)));

                    if (max != 0 && price > max)
                    {
                        price = max;
                    }

                    if (price != 0)
                    {
                        message = message.concat(type).concat(" ");

                        message = message.concat(number).concat("x").concat(String.valueOf(Math.round(price))).concat(unit).concat(" ");
                    }

                    cursor.moveToNext();
                }
            }
        }

        //for keep xien.
        public Builder(Cursor cursor, HashMap<Integer, Double> heso) {
            message = "";

            if (cursor != null && cursor.moveToFirst())
            {

                while (!cursor.isAfterLast())
                {
                    String parentType = cursor.getString(cursor.getColumnIndex(FollowDatabase.DETAIL_PARENT_TYPE));

                    String type = cursor.getString(cursor.getColumnIndex(FollowDatabase.DETAIL_TYPE));

                    String number = cursor.getString(cursor.getColumnIndex(FollowDatabase.DETAIL_NUMBER));


                    Double dou = heso.get(ContactColumnMaps.getGiuDiemColumn(LotoType.convert(type)));

                    double price = dou == null ? 0 : dou;

                    String unit = "n";

                    if (price != 0)
                    {
                        if (parentType.equals(LotoType.xien.name()))
                        {
                            number = number.replaceAll("-", ",");
                        }

                        message = message.concat(type).concat(" ");

                        message = message.concat(number).concat("x").concat(String.valueOf(Math.round(price))).concat(unit).concat(" ");
                    }
                    else
                    {
                        //TODO hot fix.
                        FollowDBHelper.deleteKeepValue(Constant.UNKNOWN_PHONE_GIUXIEN, number, TypeMessage.KEEP, LotoType.convert(type));
                    }

                    cursor.moveToNext();
                }
            }
        }

        //for forward message.
        public Builder(Cursor cursor, int cIndexParentType, int cIndexType, int cIndexNum, int cIndexCost, int cIndexKeep, int cIndexSent) {
            message = "";

            if (cursor != null && cursor.moveToFirst())
            {

                while (!cursor.isAfterLast())
                {

                    String parentType = cursor.getString(cIndexParentType);

                    String type = cursor.getString(cIndexType);

                    long cost = Math.round(cursor.getInt(cIndexCost)) - Math.round(cursor.getInt(cIndexKeep)) - Math.round(cursor.getInt(cIndexSent)); //Cost is contain keep when sum so x2.

                    if (cost < 0)
                        cost = 0;

                    String unit = type.equals(LotoType.lo.name()) ? "d" : "n";

                    String num;

                    if (parentType.equals(LotoType.xien.name()))
                    {
                        num = cursor.getString(cIndexNum).replaceAll("-", ",");
                    }
                    else
                    {
                        num = cursor.getString(cIndexNum);
                    }

                    if (cost != 0)
                    {
                        message = message.concat(parentType).concat(" ");

                        message = message.concat(num).concat("x").concat(String.valueOf(cost)).concat(unit).concat(" ");
                    }

                    cursor.moveToNext();
                }
            }
        }

        //for forward message. Hot fix. For only one type.
        public Builder(Cursor cursor, int cIndexType, int cIndexNum, int cIndexRemain, int tienXuat, XuatSoDialog.TYPE_TIEN_XUAT type_tien_xuat, int[] range) {
            message = "";

            String finalType = "";

            if (cursor != null && cursor.moveToFirst())
            {
                long lastCost = 0;

                for (int i = range[0] - 1; i < range[1]; i++)
                {
                    cursor.moveToPosition(i);

                    String type = cursor.getString(cIndexType);

                    String parentType = type.contains("xien") ? "xien" : type;

                    finalType = parentType;

                    String num = cursor.getString(cIndexNum);

                    String unit = type.equals(LotoType.lo.name()) ? "d" : "n";

                    long cost = cursor.getLong(cIndexRemain);

                    if (cost < 0)
                        cost = 0;

                    if (type_tien_xuat.equals(XuatSoDialog.TYPE_TIEN_XUAT.XUAT_LON_HON))
                    {
                        cost = cost > tienXuat ? cost - tienXuat : 0;
                    }
                    else if (type_tien_xuat.equals(XuatSoDialog.TYPE_TIEN_XUAT.XUAT_PHAN_TRAM))
                    {
                        double percent = tienXuat / 100f;
                        cost = (long) (cost * percent);
                    }
                    else if (type_tien_xuat.equals(XuatSoDialog.TYPE_TIEN_XUAT.XUAT_TIEN))
                    {
                        cost = tienXuat > cost ? cost : tienXuat;
                    }

                    if (parentType.equals(LotoType.xien.name()))
                    {
                        num = num.replaceAll("-", ",");
                    }

                    if (cost != 0)
                    {
                        boolean priceChanged = (lastCost != cost && lastCost != 0);

                        if (priceChanged)
                        {
                            message = message.concat("x").concat(String.valueOf(lastCost)).concat(unit).concat(" ");
                        }

                        message = message.concat(num);
                        message = message.concat(" ");

                        lastCost = cost;
                    }

                    if (i == range[1] - 1 && !TextUtils.isEmpty(message))
                    {
                        message = message.concat("x").concat(String.valueOf(lastCost)).concat(unit).concat(" ");
                    }
                }

                if (!TextUtils.isEmpty(message))
                {
                    message = finalType.concat(" ").concat(message);
                }
            }
        }

        //For message response.
        public Builder(Cursor cursor, int cIndexParentType, int cIndexType, int cIndexNum, int cIndexAmount, boolean hasNewLinePriceChange) {

            message = "";

            if (cursor != null && cursor.moveToFirst())
            {

                String lastType = null;

                long lastCost = 0;

                while (!cursor.isAfterLast())
                {

                    String parentType = cursor.getString(cIndexParentType);

                    String type = cursor.getString(cIndexType);

                    if (type.contains("xien"))
                    {
                        type = parentType;
                    }

                    String num = cursor.getString(cIndexNum);

                    int cost = cursor.getInt(cIndexAmount);

                    if (parentType.equals(LotoType.xien.name()))
                    {
                        num = num.replaceAll("-", ",");
                    }

                    boolean firstType = (lastType == null);

                    boolean typeChanged = (lastType != null && !lastType.equals(type));

                    boolean priceChanged = (lastCost != cost && lastCost != 0);

                    boolean force = cursor.isLast();

                    String unit = firstType ? (type.equals(LotoType.lo.name()) ? "d" : "n") : (lastType.equals(LotoType.lo.name()) ? "d" : "n");

                    if ((typeChanged || priceChanged))
                    {
                        message = message.concat("x").concat(String.valueOf(lastCost)).concat(unit).concat(" ");

                        if (hasNewLinePriceChange)
                        {
                            message = message.concat("<br>");
                        }
                    }

                    if (typeChanged || firstType)
                    {
                        message = message.concat(type).concat("<br>");
                        lastType = type;
                    }

                    message = message.concat(num);
                    message = message.concat(" ");

                    if (force)
                    {
                        message = message.concat("x").concat(String.valueOf(cost)).concat(unit).concat(" ");
                    }

                    lastCost = cost;


                    cursor.moveToNext();
                }
            }
        }

        //for reload when delete message.
        public Builder(Cursor cursor, HashMap<Integer, Double> heso, String phone, String timeMessageDelete) {
            message = "";

            if (cursor != null && cursor.moveToFirst())
            {

                while (!cursor.isAfterLast())
                {
                    String parentType = cursor.getString(cursor.getColumnIndex(FollowDatabase.DETAIL_PARENT_TYPE));

                    String type = cursor.getString(cursor.getColumnIndex(FollowDatabase.DETAIL_TYPE));

                    String number = cursor.getString(cursor.getColumnIndex(FollowDatabase.DETAIL_NUMBER));


                    Double dou = heso.get(ContactColumnMaps.getKeepColumn(LotoType.convert(type)));

                    double hs = dou == null ? 0 : dou;

                    if (hs == 0d)
                    {
                        cursor.moveToNext();
                        continue;
                    }

                    //TODO hot fix.
                    FollowDBHelper.deleteKeepValue(phone, number, TypeMessage.KEEP, LotoType.convert(type));

                    int nhan = FollowDBHelper.getSumReceivedValueOfNumberMF(number, type, phone)
                            - FollowDBHelper.getSumValueOfNumberByTypeWithTimeMF(number, type, phone, timeMessageDelete);

                    String unit = type.equals(LotoType.lo.name()) ? "d" : "n";

                    if (parentType.equals(LotoType.xien.name()))
                    {
                        number = number.replaceAll("-", ",");
                    }

                    double price = nhan * hs;

                    double max = heso.get(ContactColumnMaps.getMaxColumn(LotoType.convert(type)));

                    if (max != 0 && price > max)
                    {
                        price = max;
                    }

                    if (price != 0)
                    {
                        message = message.concat(type).concat(" ");

                        message = message.concat(number).concat("x").concat(String.valueOf(Math.round(price))).concat(unit).concat(" ");
                    }


                    cursor.moveToNext();
                }
            }
        }

        public MessageForm build() {
            return new MessageForm(this);
        }
    }
}
