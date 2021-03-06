package bingo.com.utils;

import bingo.com.model.ContactModel;

public class Default_Heso {

    @Deprecated
    public static final double DAUDB = 717;
    @Deprecated
    public static final double DAUDB_LANAN = 70000;
    public static final double DITDB = 717;
    public static final double DITDB_LANAN = 70000;
    @Deprecated
    public static final double DAUNHAT = 717;
    @Deprecated
    public static final double DAUNHAT_LANAN = 70000;
    @Deprecated
    public static final double DITNHAT = 717;
    @Deprecated
    public static final double DITNHAT_LANAN = 70000;
    public static final double LO = 21660;
    public static final double LO_LANAN = 80000;
    public static final double XIEN2 = 570;
    public static final double XIEN2_LANAN = 10000;
    public static final double XIEN3 = 570;
    public static final double XIEN3_LANAN = 40000;
    public static final double XIEN4 = 570;
    public static final double XIEN4_LANAN = 100000;
    public static final double BACANG = 1000;
    public static final double BACANG_LANAN = 450000;

    private static double[] DEFAULT = new double[]{DAUDB, DAUDB_LANAN, DITDB, DITDB_LANAN, DAUNHAT, DAUNHAT_LANAN, DITNHAT, DITNHAT_LANAN,
            LO, LO_LANAN, XIEN2, XIEN2_LANAN, XIEN3, XIEN3_LANAN, XIEN4, XIEN4_LANAN, BACANG, BACANG_LANAN};

    public static double[] loadDefault() {
        return DEFAULT;
    }

    public static ContactModel loadDefaultContact() {
        return new ContactModel(
            DAUDB, DAUDB_LANAN,
                DITDB, DITDB_LANAN,
                DAUNHAT, DAUNHAT_LANAN,
                DITNHAT, DITNHAT_LANAN,
                LO, LO_LANAN,
                XIEN2, XIEN2_LANAN,
                XIEN3, XIEN3_LANAN,
                XIEN4, XIEN4_LANAN,
                BACANG, BACANG_LANAN
        );
    }
}
