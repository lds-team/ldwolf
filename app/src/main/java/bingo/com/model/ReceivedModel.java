package bingo.com.model;

import org.json.JSONArray;
import org.json.JSONObject;

public class ReceivedModel {

    String name;
    String phone;
    String time;
    String date;
    String typemessage;
    double actualcollect_de;
    double actualcollect_lo;
    double actualcollect_xien2;
    double actualcollect_xien3;
    double actualcollect_xien4;
    double actualcollect_bacang;
    String de_win;
    String lo_win;
    String lo_point_win;
    String xien2_win;
    String xien3_win;
    String xien4_win;
    String bacang_win;
    JSONArray detail;//JSON Array.

    public ReceivedModel() {
    }

    public ReceivedModel(String name, String phone, String time, String date, String typemessage, String de_win, String lo_win, String lo_point_win, String xien2_win, String xien3_win, String xien4_win, String bacang_win, JSONArray detail) {
        this.name = name;
        this.phone = phone;
        this.time = time;
        this.date = date;
        this.typemessage = typemessage;
        this.de_win = de_win;
        this.lo_win = lo_win;
        this.lo_point_win = lo_point_win;
        this.xien2_win = xien2_win;
        this.xien3_win = xien3_win;
        this.xien4_win = xien4_win;
        this.bacang_win = bacang_win;
        this.detail = detail;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTypemessage() {
        return typemessage;
    }

    public void setTypemessage(String typemessage) {
        this.typemessage = typemessage;
    }

    public double getActualcollect_de() {
        return actualcollect_de;
    }

    public void setActualcollect_de(double actualcollect_de) {
        this.actualcollect_de = actualcollect_de;
    }

    public double getActualcollect_lo() {
        return actualcollect_lo;
    }

    public void setActualcollect_lo(double actualcollect_lo) {
        this.actualcollect_lo = actualcollect_lo;
    }

    public double getActualcollect_xien2() {
        return actualcollect_xien2;
    }

    public void setActualcollect_xien2(double actualcollect_xien2) {
        this.actualcollect_xien2 = actualcollect_xien2;
    }

    public double getActualcollect_xien3() {
        return actualcollect_xien3;
    }

    public void setActualcollect_xien3(double actualcollect_xien3) {
        this.actualcollect_xien3 = actualcollect_xien3;
    }

    public double getActualcollect_xien4() {
        return actualcollect_xien4;
    }

    public void setActualcollect_xien4(double actualcollect_xien4) {
        this.actualcollect_xien4 = actualcollect_xien4;
    }

    public double getActualcollect_bacang() {
        return actualcollect_bacang;
    }

    public void setActualcollect_bacang(double actualcollect_bacang) {
        this.actualcollect_bacang = actualcollect_bacang;
    }

    public String getDe_win() {
        return de_win;
    }

    public void setDe_win(String de_win) {
        this.de_win = de_win;
    }

    public String getLo_win() {
        return lo_win;
    }

    public void setLo_win(String lo_win) {
        this.lo_win = lo_win;
    }

    public String getLo_point_win() {
        return lo_point_win;
    }

    public void setLo_point_win(String lo_point_win) {
        this.lo_point_win = lo_point_win;
    }

    public String getXien2_win() {
        return xien2_win;
    }

    public void setXien2_win(String xien2_win) {
        this.xien2_win = xien2_win;
    }

    public String getXien3_win() {
        return xien3_win;
    }

    public void setXien3_win(String xien3_win) {
        this.xien3_win = xien3_win;
    }

    public String getXien4_win() {
        return xien4_win;
    }

    public void setXien4_win(String xien4_win) {
        this.xien4_win = xien4_win;
    }

    public String getBacang_win() {
        return bacang_win;
    }

    public void setBacang_win(String bacang_win) {
        this.bacang_win = bacang_win;
    }

    public JSONArray getDetail() {
        return detail;
    }

    public void setDetail(JSONArray detail) {
        this.detail = detail;
    }
}
