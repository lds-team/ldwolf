package bingo.com.dialog;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import bingo.com.R;
import bingo.com.base.baseview.BaseDialog;
import bingo.com.callbacks.UpdateSync;
import bingo.com.controller.DeleteController;
import bingo.com.controller.UpdateController;
import bingo.com.enumtype.TypeMessage;
import bingo.com.model.ChooseMessage;
import butterknife.Bind;
import butterknife.OnClick;

public class EditMessageDialog extends BaseDialog implements View.OnClickListener {

    @Bind(R.id.dialog_em_editext)
    EditText editText;

    private String name;

    private String phone;

    private String time;

    private String type;

    private Context context;

    private UpdateSync listener;

    public EditMessageDialog(Context context, boolean notCancel) {
        super(context, notCancel);

        this.context = context;
    }

    @Override
    public int layout() {
        return R.layout.dialog_editmessage;
    }

    @Override
    public void dismissed() {
        if (listener != null)
            listener.onUpdate();
    }

    public void setData(ChooseMessage message) {
        editText.setText(message.getRawContent());

        this.name = message.getName();
        this.phone = message.getPhone();
        this.time = message.getTime();
        this.type = message.getType();
    }

    @OnClick({R.id.dialog_em_edit, R.id.dialog_em_delete})
    @Override
    public void onClick(View v) {
        Toast.makeText(context, "Hệ thống đang xóa dữ liệu tin nhắn.", Toast.LENGTH_LONG).show();
        dismiss();
        switch (v.getId())
        {
            case R.id.dialog_em_edit:
                /*ResultReturnModel[] results = StatisticControlImport.getResultTodayAndYesterday(context.getApplicationContext());*/

                UpdateController.updateMessageHasAnalyze(context, name, phone, time, editText.getText().toString(), type, null);
                break;

            case R.id.dialog_em_delete:
                DeleteController.deleteMessageHasAnalyze(name, phone, time, TypeMessage.convert(type));
                break;

            default:break;
        }
    }

    public void setOnUpdateOnDismissListner(UpdateSync listner) {
        this.listener = listner;
    }
}
