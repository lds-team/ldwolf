package bingo.com.model;


import bingo.com.enumtype.TypeMessage;

public interface StatisticInfoModel {

    String getName();

    String getPhone();

    String getRawMessage();

    String getMessageEditted();

    String getTime();

    TypeMessage getType();

    ContactModel getConfig();
}
