package bingo.com.model;

public class NumberTypeModel {

    String number;
    String date;
    String type;
    int price;
    String win;
    int countwin;

    public NumberTypeModel() {
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getWin() {
        return win;
    }

    public void setWin(String win) {
        this.win = win;
    }

    public int getCountwin() {
        return countwin;
    }

    public void setCountwin(int countwin) {
        this.countwin = countwin;
    }
}
