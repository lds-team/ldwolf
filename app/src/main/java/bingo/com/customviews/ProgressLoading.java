package bingo.com.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class ProgressLoading extends ProgressBar {

    private String text = "0";
    private Paint paint;
    private Rect rectText;

    public ProgressLoading(Context context) {
        super(context);
        init();
    }

    public ProgressLoading(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProgressLoading(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ProgressLoading(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(18);
        paint.setStyle(Paint.Style.FILL);

        rectText = new Rect();
    }

    public void setNumber(int num) {
        this.text = String.valueOf(num);
        requestLayout();

        if (num == 0)
        {
            this.setVisibility(GONE);
        }
        else
        {
            this.setVisibility(VISIBLE);
        }
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Rect rect = canvas.getClipBounds();
        paint.getTextBounds(text, 0, text.length(), rectText);

        float x = rect.centerX() - rectText.right / 2 - 1;
        float y = rect.centerY() - rectText.top / 2;

        canvas.drawText(text, x, y, paint);
    }
}
