package bingo.com.dialog;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Map;

import bingo.com.R;
import bingo.com.base.baseview.BaseDialog;
import bingo.com.pref.ConfigPreference;
import bingo.com.screen.modelscreen.adapter.CustomRegexAdapter;
import bingo.com.utils.Utils;
import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;

public class AddRegexDialog extends BaseDialog {

    @Bind(R.id.dialog_addregexlist)
    ListView listView;

    @Bind(R.id.dialog_addregexnotify)
    TextView notify;

    @Bind(R.id.dialog_addregextext)
    EditText editText;

    @Bind(R.id.dialog_addregexcontent)
    EditText editTextContent;

    private String regex = "";
    private String content = "";

    private int posClick = -1;

    Map<String, String> regexSave;
    CustomRegexAdapter adapter;

    public AddRegexDialog(Context context, boolean notCancel) {
        super(context, notCancel);

        regexSave = ConfigPreference.getRegexCustom(getSaved());
        showList();
    }

    @Override
    public int layout() {
        return R.layout.dialog_addregex;
    }

    private void showList() {
        adapter = new CustomRegexAdapter(contextWrap.get(), regexSave);
        listView.setAdapter(adapter);
    }

    @OnClick(R.id.dialog_addregexsave)
    public void save() {
        if (!regex.isEmpty())
        {
            regex = regex.replaceAll("\\s+", " ");
            content = content.replaceAll("\\s+", " ");

            regexSave.put(regex, content);
            ConfigPreference.saveRegexCustom(getSaved(), regexSave);

            if (adapter != null)
            {
                adapter.notifyDataSetChanged();
                editText.setText("");
                editTextContent.setText("");
            }
        }
    }

    @OnClick(R.id.dialog_addregexcancel)
    public void cancel() {
        if (!regex.isEmpty())
        {
            if (regexSave.containsKey(regex))
            {
                regexSave.remove(adapter.getItem(posClick));
                ConfigPreference.saveRegexCustom(getSaved(), regexSave);
            }

            if (adapter != null)
            {
                adapter.notifyDataSetChanged();
                editText.setText("");
                editTextContent.setText("");
            }
        }
    }

    @OnTextChanged(R.id.dialog_addregextext)
    public void afterTextChanged(Editable s) {
        regex = s.toString();
        AddRegexDialog.this.notify(regex.replaceAll("\\s+", "").matches(".*\\W+.*") || content.replaceAll("\\s+", "").matches(".*\\W+.*"));
    }

    @OnTextChanged(R.id.dialog_addregexcontent)
    public void afterTextChanged2(Editable s) {
        content = s.toString();
        AddRegexDialog.this.notify(regex.replaceAll("\\s+", "").matches(".*\\W+.*") || content.replaceAll("\\s+", "").matches(".*\\W+.*"));
    }

    private void notify(boolean show) {
        if (show)
            notify.setVisibility(View.VISIBLE);
        else
            notify.setVisibility(View.GONE);
    }

    @Override
    public void dismissed() {
        if (!TextUtils.isEmpty(regex))
        {
            Utils.showAlertTwoButton(contextWrap.get(), null, "Bạn có muốn lưu?", "Save", "Cancel", new Runnable() {
                @Override
                public void run() {
                    save();
                }
            }, null);
        }
    }


    @OnItemClick(R.id.dialog_addregexlist)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        posClick = position;

        regex = adapter.getItem(position);
        content = regexSave.get(regex);
        editText.setText(regex);
        editTextContent.setText(content);
    }
}
