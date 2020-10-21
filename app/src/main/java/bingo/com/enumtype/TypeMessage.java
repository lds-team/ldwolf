package bingo.com.enumtype;

public enum TypeMessage {

    SENT,
    RECEIVED,
    SMS,
    KEEP,
    REMAIN; //Add new.

    public static TypeMessage convert(String type) {
        if (type.equals(SENT.name()))
            return SENT;
        else if (type.equals(RECEIVED.name()))
            return RECEIVED;
        else if (type.equals(SMS.name()))
            return SMS;
        else
            return KEEP;
    }
}
