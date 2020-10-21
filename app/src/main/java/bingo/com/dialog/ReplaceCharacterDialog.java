package bingo.com.dialog;

import android.content.Context;
import android.text.Spanned;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import bingo.com.R;
import bingo.com.base.baseview.BaseDialog;
import bingo.com.helperdb.MessageDBHelper;
import bingo.com.utils.Formats;
import bingo.com.utils.Utils;
import bingo.com.utils.Validate;
import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class ReplaceCharacterDialog extends BaseDialog {

    @Bind(R.id.dialog_change1)
    EditText change1;

    @Bind(R.id.dialog_change2)
    EditText change2;

    @Bind(R.id.dialog_changepreview)
    TextView showPreview;

    @Bind(R.id.dialog_changenotify)
    TextView notify;

    String textPreview;

    String phone;
    String time;

    String error1 = "Chú ý: Bạn đang thêm ký tự đặc biêt.\nChúng tôi sẽ không chịu trách nhiệm nếu xảy ra lỗi.";
    String error2 = "Bạn đang thêm 2 ký tự đặc biệt liền nhau.";

    public ReplaceCharacterDialog(Context context, boolean notCancel) {
        super(context, notCancel);
    }

    public void setInfo(String phone, String time) {
        this.phone = phone;
        this.time = time;
    }

    public void setTextPreview(String text) {
        textPreview = text;
        showPreview.setText(text);
    }

    private void replaceText(String from, String to) {
        try {
            if (to.isEmpty())
            {
                to = " ";
            }

            if (!from.matches("\\s+"))
            {
                from = from.replaceAll("\\s+", " ");
                to = to.replaceAll("\\s+", " ");
                textPreview = textPreview.replaceAll("\\s+", " ");
                textPreview = textPreview.replaceAll(from, to);
            }
            else
            {
                textPreview = textPreview.replaceAll(from, to);
            }
        } catch (Exception e) {
            Utils.showAlert(contextWrap.get(), null, "Xảy ra lỗi khi thay đổi ký tự đặc biệt", "Ok", null);
        }
    }

    @OnClick(R.id.dialog_change_save)
    public void save() {
        preview();
        MessageDBHelper.updateContent(phone, time, textPreview);
        requestUpdate();
        dismiss();
    }

    @OnClick(R.id.dialog_change_preview)
    public void preview() {
        String text1 = change1.getText().toString();
        String text2 = change2.getText().toString();

        if (!text1.isEmpty())
        {
            replaceText(text1, text2);
            String error = Validate.canAnalyze(contextWrap.get(), textPreview);
            if (error != null)
            {
                Spanned last = Formats.formatStringError(textPreview, error);
                showPreview.setText(last);
            }
            else
            {
                showPreview.setText(textPreview);
            }
        }
    }

    @OnTextChanged({R.id.dialog_change1, R.id.dialog_change2})
    public void setText() {
        String text1 = change1.getText().toString();
        String text2 = change2.getText().toString();

        boolean hasError = false;

        if (text1.replaceAll("\\s+", "").matches(".*\\W+.*") || text2.replaceAll("\\s+", "").matches(".*\\W+.*"))
        {
            notify.setText(error1);
            hasError = true;
        }

        if (text1.replaceAll("\\s+", "").matches(".*(\\W+){2}.*") || text2.replaceAll("\\s+", "").matches(".*(\\W+){2}.*"))
        {
            notify.setText(error2);
            hasError = true;
        }

        notify(hasError);
    }

    private void notify(boolean show) {
        if (show)
            notify.setVisibility(View.VISIBLE);
        else
            notify.setVisibility(View.GONE);
    }

    @Override
    public int layout() {
        return R.layout.dialog_changecharacter;
    }

    @Override
    public void dismissed() {

    }
}
