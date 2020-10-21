package bingo.com.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import bingo.com.R;

public class ResultView extends LinearLayout {

    private Context context;

    public static final int TYPE_4_ITEM = 4;
    public static final int TYPE_5_ITEM = 5;

    private int type;

    public ResultView(Context context) {
        super(context);

        this.context = context;
        init();
    }

    public ResultView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        init();
    }

    public ResultView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.context = context;
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ResultView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        this.context = context;
        init();
    }

    private void init() {
        setOrientation(VERTICAL);

        type = TYPE_4_ITEM;
    }

    public void setUp(int typeView) {
        if (typeView < 4 || typeView > 5)
            throw new IllegalStateException("Only 2 type : 4 or 5 child in view");

        type = typeView;
    }

    public void add(String name, double[] line) {
        if (line.length < 3)
            throw new IndexOutOfBoundsException("Array Result must have atlease 3 child.");

        View view = View.inflate(context, R.layout.item_resultview, null);

        if (type == TYPE_4_ITEM)
        {
            ((TextViewDegit) view.findViewById(R.id.resultview_text4)).setVisibility(GONE);

            ((TextViewDegit) view.findViewById(R.id.resultview_text1)).setText(name);
            ((TextViewDegit) view.findViewById(R.id.resultview_text2)).setResult(line[0]);
            ((TextViewDegit) view.findViewById(R.id.resultview_text3)).setResult(line[1]);
            ((TextViewDegit) view.findViewById(R.id.resultview_text5)).setResult(line[2]);
        }
        else
        {
            ((TextViewDegit) view.findViewById(R.id.resultview_text4)).setVisibility(VISIBLE);

            ((TextViewDegit) view.findViewById(R.id.resultview_text1)).setText(name);
            ((TextViewDegit) view.findViewById(R.id.resultview_text2)).setResult(line[0]);
            ((TextViewDegit) view.findViewById(R.id.resultview_text3)).setResult(line[1]);
            ((TextViewDegit) view.findViewById(R.id.resultview_text4)).setResult(line[2]);
            ((TextViewDegit) view.findViewById(R.id.resultview_text5)).setResult(line[3]);
        }

        this.addView(view);
    }

    public void addBottom(double... sum) {
        if (sum.length < 1)
            throw new IndexOutOfBoundsException("Must have Sum String.");

        View sumView = View.inflate(context, R.layout.item_resultviewsum, null);

        if (sum.length == 1)
        {
            ((TextViewDegit) sumView.findViewById(R.id.resultview_sum1)).setVisibility(GONE);

            ((TextViewDegit) sumView.findViewById(R.id.resultview_sum2)).setResult(sum[0]);
        }
        else
        {
            ((TextViewDegit) sumView.findViewById(R.id.resultview_sum1)).setVisibility(VISIBLE);

            ((TextViewDegit) sumView.findViewById(R.id.resultview_sum1)).setResult(sum[0]);
            ((TextViewDegit) sumView.findViewById(R.id.resultview_sum2)).setResult(sum[1]);
        }


        this.addView(sumView);
    }

    public void addAlls(Cursor cursor, String... columns) {
        if (columns.length < 4)
            throw new IndexOutOfBoundsException("Must have at lease 4 column in Result.");

        removeAllViews();

        double sum1 = 0;
        double sum2 = 0;

        if (cursor != null)
        {
            for (int i = 0; i < cursor.getCount(); i++)
            {
                cursor.moveToPosition(i);

                if (columns.length == 4)
                {
                    double money = cursor.getDouble(cursor.getColumnIndex(columns[3]));

                    sum2 += money;

                    this.add(cursor.getString(cursor.getColumnIndex(columns[0])),
                            new double[]{
                                    cursor.getDouble(cursor.getColumnIndex(columns[1])),
                                    cursor.getDouble(cursor.getColumnIndex(columns[2])),
                                    money
                            });

                    if (i == cursor.getCount() - 1)
                    {
                        addBottom(sum2);
                    }
                }
                else
                {
                    double money1 = cursor.getDouble(cursor.getColumnIndex(columns[3]));
                    double money2 = cursor.getDouble(cursor.getColumnIndex(columns[4]));

                    sum1 += money1;
                    sum2 += money2;

                    this.add(cursor.getString(cursor.getColumnIndex(columns[0])),
                            new double[]{
                                    cursor.getDouble(cursor.getColumnIndex(columns[1])),
                                    cursor.getDouble(cursor.getColumnIndex(columns[2])),
                                    money1, money2
                            });

                    if (i == cursor.getCount() - 1)
                    {
                        addBottom(sum1, sum2);
                    }
                }
            }
        }

    }
}
