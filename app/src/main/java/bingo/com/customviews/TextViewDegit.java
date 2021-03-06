package bingo.com.customviews;

import android.content.Context;
import android.util.AttributeSet;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import bingo.com.base.baseview.BaseTextView;

public class TextViewDegit extends BaseTextView {

    public TextViewDegit(Context context) {
        super(context);
    }

    public TextViewDegit(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextViewDegit(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TextViewDegit(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setResult(double result) {
        setText(getDotNumberText(result));
    }

    private String getDotNumberText(double input) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
//        symbols.setDecimalSeparator(',');
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###,###.##", symbols);

        return decimalFormat.format(input);
    }
}
