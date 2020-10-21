package bingo.com.model;

import bingo.com.enumtype.TypeMessage;

public interface FollowModel {

    String getName();

    String getPhone();

    String getNumber();

    String getContent();

    double getAmountPoint();

    String getUnit();

    String getPrice();

    String getActualCollect();

    int getCountWin();

    String getPointWin();

    String getGuestWin();

    String getType();

    String getTime();

    TypeMessage getMessageType();

    String getWinSyntax();

    String getErrorMessage();
}
