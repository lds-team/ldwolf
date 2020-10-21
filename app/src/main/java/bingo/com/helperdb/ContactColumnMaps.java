package bingo.com.helperdb;

import bingo.com.enumtype.LotoType;
import bingo.com.helperdb.db.ContactDatabase;

public class ContactColumnMaps {

    public static final int KEY_ID = 0;
    public static final int NAME = 1;
    public static final int PHONE = 2;
    public static final int GIU_DE = 3;
    public static final int GIU_LO = 4;
    public static final int GIU_BACANG = 5;
    public static final int GIU_XIEN2 = 6;
    public static final int GIU_XIEN3 = 7;
    public static final int GIU_XIEN4 = 8;
    public static final int GIU_DIEM_XIEN2 = 9;
    public static final int GIU_DIEM_XIEN3 = 10;
    public static final int GIU_DIEM_XIEN4 = 11;
    public static final int GIU_DIEM_BACANG = 12;
    public static final int MAX_DE = 13;
    public static final int MAX_LO = 14;
    public static final int MAX_XIEN2 = 15;
    public static final int MAX_XIEN3 = 16;
    public static final int MAX_XIEN4 = 17;
    public static final int MAX_BACANG = 18;
    @Deprecated
    public static final int DAUDB = 19;
    @Deprecated
    public static final int DAUDB_LANAN = 20;
    public static final int DITDB = 21;
    public static final int DITDB_LANAN = 22;
    @Deprecated
    public static final int DAUNHAT = 23;
    @Deprecated
    public static final int DAUNHAT_LANAN = 24;
    @Deprecated
    public static final int DITNHAT = 25;
    @Deprecated
    public static final int DITNHAT_LANAN = 26;
    public static final int LO = 27;
    public static final int LO_LANAN = 28;
    public static final int XIEN2 = 29;
    public static final int XIEN2_LANAN = 30;
    public static final int XIEN3 = 31;
    public static final int XIEN3_LANAN = 32;
    public static final int XIEN4 = 33;
    public static final int XIEN4_LANAN = 34;
    public static final int BACANG = 35;
    public static final int BACANG_LANAN = 36;
    public static final int TYPE = 37;

    public static int[] COLUMNS_HESO = new int[] {
        DAUDB, DAUDB_LANAN, DITDB, DITDB_LANAN, DAUNHAT, DAUNHAT_LANAN, DITNHAT, DITNHAT_LANAN,
            LO, LO_LANAN, XIEN2, XIEN2_LANAN, XIEN3, XIEN3_LANAN, XIEN4, XIEN4_LANAN, BACANG, BACANG_LANAN
    };

    public static int getColumnIndex(String column) {
        switch (column)
        {
            case ContactDatabase.NAME:
                return NAME;

            case ContactDatabase.PHONE:
                return PHONE;

            case ContactDatabase.GIU_DE:
                return GIU_DE;

            case ContactDatabase.GIU_LO:
                return GIU_LO;

            case ContactDatabase.GIU_DIEM_XIEN2:
                return GIU_DIEM_XIEN2;

            case ContactDatabase.GIU_DIEM_XIEN3:
                return GIU_DIEM_XIEN3;

            case ContactDatabase.GIU_DIEM_XIEN4:
                return GIU_DIEM_XIEN4;

            case ContactDatabase.GIU_BACANG:
                return GIU_BACANG;

            case ContactDatabase.GIU_XIEN2:
                return GIU_XIEN2;

            case ContactDatabase.GIU_XIEN3:
                return GIU_XIEN3;

            case ContactDatabase.GIU_XIEN4:
                return GIU_XIEN4;

            case ContactDatabase.MAX_DE:
                return MAX_DE;

            case ContactDatabase.MAX_LO:
                return MAX_LO;

            case ContactDatabase.MAX_XIEN2:
                return MAX_XIEN2;

            case ContactDatabase.MAX_XIEN3:
                return MAX_XIEN3;

            case ContactDatabase.MAX_XIEN4:
                return MAX_XIEN4;

            case ContactDatabase.MAX_BACANG:
                return MAX_BACANG;

            case ContactDatabase.DITDB:
                return DITDB;

            case ContactDatabase.DITDB_LANAN:
                return NAME;

            case ContactDatabase.LO:
                return LO;

            case ContactDatabase.LO_LANAN:
                return LO_LANAN;

            case ContactDatabase.XIEN2:
                return XIEN2;

            case ContactDatabase.XIEN2_LANAN:
                return XIEN2_LANAN;

            case ContactDatabase.XIEN3:
                return XIEN3;

            case ContactDatabase.XIEN3_LANAN:
                return XIEN3_LANAN;

            case ContactDatabase.XIEN4:
                return XIEN4;

            case ContactDatabase.XIEN4_LANAN:
                return XIEN4_LANAN;

            case ContactDatabase.BACANG:
                return BACANG;

            case ContactDatabase.BACANG_LANAN:
                return BACANG_LANAN;

            case ContactDatabase.TYPE:
                return TYPE;

            default: return 0;
        }
    }

    public static int getKeepColumn(LotoType type) {
        int column = 0;

        if (type == LotoType.de)

            column = GIU_DE;
        else if (type == LotoType.lo)

            column = GIU_LO;
        else if (type == LotoType.bacang)

            column = GIU_BACANG;
        else if (type == LotoType.xien2)

            column = GIU_XIEN2;
        else if (type == LotoType.xien3)

            column = GIU_XIEN3;
        else if (type == LotoType.xien4)

            column = GIU_XIEN4;

        return column;
    }

    public static int getMaxColumn(LotoType type) {
        int column = 0;

        if (type == LotoType.de)

            column = MAX_DE;
        else if (type == LotoType.lo)

            column = MAX_LO;
        else if (type == LotoType.bacang)

            column = MAX_BACANG;
        else if (type == LotoType.xien2)

            column = MAX_XIEN2;
        else if (type == LotoType.xien3)

            column = MAX_XIEN3;
        else if (type == LotoType.xien4)

            column = MAX_XIEN4;

        return column;
    }

    public static int getGiuDiemColumn(LotoType type) {
        int column = 0;

        if (type == LotoType.bacang)

            column = GIU_DIEM_BACANG;
        else if (type == LotoType.xien2)

            column = GIU_DIEM_XIEN2;
        else if (type == LotoType.xien3)

            column = GIU_DIEM_XIEN3;
        else if (type == LotoType.xien4)

            column = GIU_DIEM_XIEN4;

        return column;
    }
}
