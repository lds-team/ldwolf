package bingo.com.enumtype;

public enum TableType {

    received_table,
    de_table,
    lo_table,
    xien_table,
    bacang_table,
    keep_table;

    public static TableType getTableName(String type) {
        switch (type)
        {
            case "de":
                return de_table;
            case "lo":
                return lo_table;
            case "xien":
            case "xien2":
            case "xien3":
            case "xien4":
                return xien_table;
            case "xq":
                return xien_table;
            case "bacang":
                return bacang_table;
            case "bc":
                return bacang_table;
            default:
                if (type.contains("xien") || type.contains("xq"))
                {
                    return xien_table;
                }
                else
                {
                    return null;
                }
        }
    }
}
