package bingo.com.screen.editmessagescreen.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bingo.analyze.Analyze;

import bingo.com.R;
import bingo.com.base.baseadapter.BaseCursorRecyclerAdapter;
import bingo.com.base.baseview.BaseRecyclerViewHolder;
import bingo.com.dialog.ReplaceCharacterDialog;
import bingo.com.helperdb.MessageDBHelper;
import bingo.com.helperdb.db.MessageDatabase;
import bingo.com.model.ChooseMessage;
import bingo.com.pref.ConfigPreference;
import bingo.com.utils.Formats;
import bingo.com.utils.Keysaved;
import bingo.com.utils.Utils;
import bingo.com.utils.Validate;
import butterknife.Bind;

public class EditMessageAdapter extends BaseCursorRecyclerAdapter<EditMessageAdapter.ViewHolder> {

    private Context context;

    private EditText textSelect;

    public ChooseMessage chooseMessage;

    private Spinner customer;

    private TextView errNotify;

    public boolean clickable = true;

    public boolean hasReload;

    public EditMessageAdapter(Context context, Cursor cursor, EditText textSelect, Spinner customer, TextView errNotify, boolean hasReload) {
        super(context, cursor);

        this.context = context;
        this.textSelect = textSelect;
        this.customer = customer;
        this.errNotify = errNotify;
        this.hasReload = hasReload;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        String content = cursor.getString(cursor.getColumnIndex(MessageDatabase.CONTENT));
        String error = cursor.getString(cursor.getColumnIndex(MessageDatabase.ERROR));
        String hasAnalyze = cursor.getString(cursor.getColumnIndex(MessageDatabase.HAS_ANALYZE));

        viewHolder.setData(content, TextUtils.isEmpty(error) && hasAnalyze.equals("0"));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.fragment_editmessageitem, null);

        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));

        return new ViewHolder(view);
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    public void setErrNotify(String errNotify) {
        if (TextUtils.isEmpty(errNotify))
        {
            this.errNotify.setText("");
            this.errNotify.setVisibility(View.GONE);
        }
        else
        {
            this.errNotify.setText(errNotify);
            this.errNotify.setVisibility(View.VISIBLE);
        }
    }

    public Cursor swapCursor(boolean hasReload, Cursor newCursor) {
        this.hasReload = hasReload;
        return super.swapCursor(newCursor);
    }

    class ViewHolder extends BaseRecyclerViewHolder {

        @Bind(android.R.id.text1)
        TextView textView;

        @Bind(R.id.fragment_editmessage_timeout)
        TextView timeout;

        public ViewHolder(final View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (clickable)
                    {
                        setErrNotify(null);
                        getCursor().moveToPosition(getLayoutPosition());

                        String content = getCursor().getString(getCursor().getColumnIndex(MessageDatabase.CONTENT));

                        String error = getCursor().getString(getCursor().getColumnIndex(MessageDatabase.ERROR));

                        Analyze eObj = Validate.validate(context, content);
                        Spanned last = Formats.getError(eObj.analyzeMessage, eObj);
                        if (TextUtils.isEmpty(last))
                        {
                            last = new SpannableString(eObj.analyzeMessage);
                        }

                        int positionError = 0;
                        /*if (eObj.errorMessage != null)
                            positionError = content.indexOf(eObj.errorMessage);
                        if (positionError == -1)    //Focus to position error.
                            positionError = 0;*/

                        textSelect.setText(last);
                        textSelect.requestFocus(positionError);

                        chooseMessage = new ChooseMessage(
                                getCursor().getString(getCursor().getColumnIndex(MessageDatabase.NAME)),
                                getCursor().getString(getCursor().getColumnIndex(MessageDatabase.PHONE)),
                                getCursor().getString(getCursor().getColumnIndex(MessageDatabase.TIME)),
                                getCursor().getString(getCursor().getColumnIndex(MessageDatabase.CONTENT)),
                                getCursor().getString(getCursor().getColumnIndex(MessageDatabase.TYPE))
                        );

                        chooseMessage.setShowingTimeout(timeout.getVisibility() == View.VISIBLE);

                        setCustomerSelection(chooseMessage.getPhone());
                        setErrNotify(!eObj.error ?
                                !TextUtils.isEmpty(error) ? "Lỗi phân tích cú pháp có kết quả  = 0:\n" + error : null : eObj.errorNotify);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showPopupMenu();
                    return true;
                }
            });
        }

        private void showPopupMenu() {
            PopupMenu menu = new PopupMenu(context, textView);
            MenuInflater inflater = menu.getMenuInflater();
            inflater.inflate(R.menu.menu_editmessage, menu.getMenu());

            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId())
                    {
                        case R.id.menu_editmessage:
                            showDialogChangeCharacter();
                            break;

                        case R.id.menu_copymessage:
                            getCursor().moveToPosition(getLayoutPosition());
                            String content = getCursor().getString(getCursor().getColumnIndex(MessageDatabase.CONTENT));

                            copyMessage(content);
                            break;

                        case R.id.menu_delmessage:
                            showConfirmDelete();
                            break;
                    }
                    return false;
                }
            });

            menu.show();
        }

        private void copyMessage(String text) {
            ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData data = ClipData.newPlainText(Keysaved.CLIPBOARD_BINGO, text);
            manager.setPrimaryClip(data);

            Toast.makeText(context, "Đã copy vào clipboard.", Toast.LENGTH_SHORT).show();
        }

        private void showDialogChangeCharacter() {
            if (context != null && getCursor() != null && !getCursor().isClosed())
            {
                ReplaceCharacterDialog dialog = new ReplaceCharacterDialog(context, false);

                getCursor().moveToPosition(getLayoutPosition());
                String phone = getCursor().getString(getCursor().getColumnIndex(MessageDatabase.PHONE));
                String time = getCursor().getString(getCursor().getColumnIndex(MessageDatabase.TIME));
                dialog.setInfo(phone, time);
                dialog.setTextPreview(getCursor().getString(getCursor().getColumnIndex(MessageDatabase.CONTENT)));

                dialog.show();
            }
        }

        private void showConfirmDelete() {
            if (context != null && getCursor() != null && !getCursor().isClosed())
            {
                /*Utils.showAlertTwoButton(context, null, "Xóa tin nhắn?", "Xóa", "Không", new Runnable() {
                    @Override
                    public void run() {*/
                        getCursor().moveToPosition(getLayoutPosition());
                        String phone = getCursor().getString(getCursor().getColumnIndex(MessageDatabase.PHONE));
                        String time = getCursor().getString(getCursor().getColumnIndex(MessageDatabase.TIME));

                        MessageDBHelper.deleteMessage(phone, time);
                        textSelect.setText("");
                        requestUpdate();
                    /*}
                }, null);*/
            }
        }

        private void setCustomerSelection(String phone) {
            for (int i = 0; i < customer.getCount(); i++)
            {
                ((Cursor) customer.getAdapter().getItem(i)).moveToPosition(i);
                if (((Cursor) customer.getAdapter().getItem(i)).getString(
                        ((Cursor) customer.getAdapter().getItem(i)).getColumnIndex(MessageDatabase.PHONE)).equals(phone))
                {
                    customer.setSelection(i);
                    return;
                }
            }
        }

        public void setData(String content, boolean showTimeout) {
            textView.setText(content);
            timeout.setVisibility(showTimeout && !hasReload ? View.VISIBLE : View.GONE);
        }
    }
}
