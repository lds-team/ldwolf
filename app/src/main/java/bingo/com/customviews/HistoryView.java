package bingo.com.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import bingo.com.R;
import bingo.com.enumtype.LotoType;
import bingo.com.model.ContactModel;

public class HistoryView extends LinearLayout {

    private Context context;

    public HistoryView(Context context) {
        super(context);
        init();
        this.context = context;
    }

    public HistoryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        this.context = context;
    }

    public HistoryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        this.context = context;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HistoryView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
        this.context = context;
    }

    private void init() {
        setOrientation(VERTICAL);
    }

    public void add(String name, double[] line) {

        View view = View.inflate(context, R.layout.item_historyview, null);

        TextViewDegit type = (TextViewDegit) view.findViewById(R.id.historyview_text1);
        if (name.equals(LotoType.unknown.name()))
        {
            type.setText("T.toán");
            type.setTextColor(Color.RED);
        }
        else
        {
            type.setText(name);
            type.setTextColor(Color.BLACK);
        }
        ((TextViewDegit) view.findViewById(R.id.historyview_text2)).setResult(line[0]);
        ((TextViewDegit) view.findViewById(R.id.historyview_text3)).setResult(line[1]);
        ((TextViewDegit) view.findViewById(R.id.historyview_text4)).setResult(line[2]);

        this.addView(view);
    }

    public void addBottom(double... sum) {

        View sumView = View.inflate(context, R.layout.item_resultviewsum, null);

        if (sum.length == 1)
        {
            sumView.findViewById(R.id.resultview_socuoi).setVisibility(GONE);
            ((TextViewDegit) sumView.findViewById(R.id.resultview_sum1)).setVisibility(GONE);
            ((TextViewDegit) sumView.findViewById(R.id.resultview_sum3)).setVisibility(GONE);

            ((TextViewDegit) sumView.findViewById(R.id.resultview_sum2)).setResult(sum[0]);
        }
        else
        {
            sumView.findViewById(R.id.resultview_socuoi).setVisibility(VISIBLE);
            ((TextViewDegit) sumView.findViewById(R.id.resultview_sum1)).setVisibility(GONE);
            ((TextViewDegit) sumView.findViewById(R.id.resultview_sum3)).setVisibility(VISIBLE);

            ((TextViewDegit) sumView.findViewById(R.id.resultview_sum2)).setResult(sum[0]);
            ((TextViewDegit) sumView.findViewById(R.id.resultview_sum3)).setResult(sum[1]);
        }


        this.addView(sumView);
    }

    public void addAlls(ContactModel contact, boolean isChuNhanSo, Cursor cursor, double sumRecursive, String... columns) {
        removeAllViews();

        double HS_THANG_DE = 1;
        double HS_THANG_BACANG = 1;
        if (contact != null)
        {
            HS_THANG_DE = contact.getDITDB_LANAN() / 1000;
            HS_THANG_BACANG = contact.getBACANG_LANAN() / 1000;
        }

        double sum = 0;

        if (cursor != null)
        {
            for (int i = 0; i < cursor.getCount(); i++)
            {
                cursor.moveToPosition(i);

                String type = cursor.getString(cursor.getColumnIndex(columns[0]));

                double money = cursor.getDouble(cursor.getColumnIndex(columns[2]));
                double pointWin = cursor.getDouble(cursor.getColumnIndex(columns[3]));

                if (type.equals(LotoType.de.name()))
                {
                    pointWin = HS_THANG_DE == 0 ? 0 : (int) (pointWin / HS_THANG_DE);
                }
                else if (type.equals(LotoType.bacang.name()))
                {
                    pointWin = HS_THANG_BACANG == 0 ? 0 : (int) (pointWin / HS_THANG_BACANG);
                }

                if (isChuNhanSo)
                {
                    money = 0 - money;
                }

                sum += money;

                this.add(type,
                        new double[]{
                                cursor.getDouble(cursor.getColumnIndex(columns[1])),
                                money,
                                pointWin
                        });

                if (i == cursor.getCount() - 1)
                {
                    addBottom(sum, sumRecursive);
                }

            }
        }

    }
}
