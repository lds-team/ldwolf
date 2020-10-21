package bingo.com.model;

import bingo.com.enumtype.LotoType;

public class CongnoModel {

    String name;

    String phone;

    LotoType type;

    int point;

    int pointWin;

    String date;

    String money;

    String customerType;

    public CongnoModel(String name, String phone, LotoType type, int point, int pointWin, String date, String money, String customerType) {
        this.name = name;
        this.phone = phone;
        this.type = type;
        this.point = point;
        this.pointWin = pointWin;
        this.date = date;
        this.money = money;
        this.customerType = customerType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LotoType getType() {
        return type;
    }

    public void setType(LotoType type) {
        this.type = type;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getPointWin() {
        return pointWin;
    }

    public void setPointWin(int pointWin) {
        this.pointWin = pointWin;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }
}
