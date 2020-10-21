package bingo.com.model;

public class KeepModel {

    String name;
    String phone;
    String date;
    String time;
    String number;
    String type;
    String parenttype;
    String price;
    String actualcollect;

    int max;

    public KeepModel() {
    }

    public KeepModel(String name, String phone, String date, String time, String number, String type, String parenttype, String price, int max, String actualcollect) {
        this.name = name;
        this.phone = phone;
        this.date = date;
        this.time = time;
        this.number = number;
        this.type = type;
        this.parenttype = parenttype;
        this.price = price;
        this.actualcollect = actualcollect;
        this.max = max;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParenttype() {
        return parenttype;
    }

    public void setParenttype(String parenttype) {
        this.parenttype = parenttype;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getActualcollect() {
        return actualcollect;
    }

    public void setActualcollect(String actualcollect) {
        this.actualcollect = actualcollect;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
