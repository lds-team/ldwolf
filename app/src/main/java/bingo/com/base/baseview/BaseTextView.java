package bingo.com.base.baseview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

public abstract class BaseTextView extends TextView {

    public BaseTextView(Context context) {
        super(context);

        if (!isInEditMode())
            init();
    }

    public BaseTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode())
            init();
    }

    public BaseTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (!isInEditMode())
            init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BaseTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        if (!isInEditMode())
            init();
    }

    private void init () {

    }
}
