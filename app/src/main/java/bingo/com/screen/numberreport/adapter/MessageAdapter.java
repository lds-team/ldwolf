package bingo.com.screen.numberreport.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import bingo.com.R;
import bingo.com.base.baseadapter.BaseCursorRecyclerAdapter;
import bingo.com.base.baseview.BaseRecyclerViewHolder;
import bingo.com.callbacks.UpdateSync;
import bingo.com.customviews.ResultView;
import bingo.com.dialog.EditMessageDialog;
import bingo.com.enumtype.TypeMessage;
import bingo.com.helperdb.MessageDBHelper;
import bingo.com.helperdb.db.MessageDatabase;
import bingo.com.model.ChooseMessage;
import bingo.com.utils.Utils;
import butterknife.Bind;

public class MessageAdapter extends BaseCursorRecyclerAdapter<MessageAdapter.ViewHolder> {

    private Context context;

    public MessageAdapter(Context context, Cursor cursor) {
        super(context, cursor);

        this.context = context;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        final String phone = getCursor().getString(getCursor().getColumnIndex(MessageDatabase.PHONE));
        final String time = cursor.getString(cursor.getColumnIndex(MessageDatabase.TIME));
        final String name = cursor.getString(cursor.getColumnIndex(MessageDatabase.NAME));
        final String rawmes = cursor.getString(cursor.getColumnIndex(MessageDatabase.RAW_CONTENT)).trim();
        final String analyzemes = cursor.getString(cursor.getColumnIndex(MessageDatabase.ANALYZE_CONTENT)).trim();
        final String mesWin = cursor.getString(cursor.getColumnIndex(MessageDatabase.WIN_NUMBER));

        viewHolder.setData(name, phone, String.valueOf(cursor.getPosition() + 1), time, mesWin, rawmes, analyzemes, cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_messageadapter, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(view);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);

        holder.resultView.removeAllViews();
    }

    private void requestChange(String phone) {
        this.swapCursor(MessageDBHelper.getMessageOnly(phone, TypeMessage.SMS, 0));
    }

    class ViewHolder extends BaseRecyclerViewHolder implements UpdateSync {

        @Bind(R.id.item_messageadapter_textname)
        TextView name;

        @Bind(R.id.item_messageadapter_textcount)
        TextView id;

        @Bind(R.id.item_messageadapter_texttime)
        TextView time;

        @Bind(R.id.item_messageadapter_meswin)
        TextView win;

        @Bind(R.id.item_messageadapter_mesraw)
        TextView message;

        @Bind(R.id.item_messageadapter_resultview)
        ResultView resultView;

        private EditMessageDialog dialog;

        private String phone;

        public ViewHolder(View itemView) {
            super(itemView);

            resultView.setUp(ResultView.TYPE_5_ITEM);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    getCursor().moveToPosition(getLayoutPosition());

                    String name = getCursor().getString(getCursor().getColumnIndex(MessageDatabase.NAME));
                    String phone = getCursor().getString(getCursor().getColumnIndex(MessageDatabase.PHONE));
                    String time = getCursor().getString(getCursor().getColumnIndex(MessageDatabase.TIME));
                    String mes = getCursor().getString(getCursor().getColumnIndex(MessageDatabase.CONTENT));
                    String type = getCursor().getString(getCursor().getColumnIndex(MessageDatabase.TYPE));

                    dialog = new EditMessageDialog(context, false);
                    dialog.setData(new ChooseMessage(name, phone, time, mes, type));
                    dialog.setOnUpdateOnDismissListner(ViewHolder.this);
                    dialog.show();

                    ViewHolder.this.phone = phone;

                    return true;
                }
            });
        }

        public void setData(String name, String phone, String id, String time, String mesWin, String mesRaw, String mes, Cursor cursor) {
            this.name.setText(name);
            this.id.setText(id);
            this.time.setText(Utils.convertTime(time));
            this.win.setText(mesRaw.trim());

            /*Spanned text = Formats.formatStringWin(mes, mesWin);*/
            this.message.setText(Html.fromHtml(mes));

            addToResult(cursor);
        }

        public void addToResult(Cursor cursor) {

            resultView.removeAllViews();

            int sumde = cursor.getInt(cursor.getColumnIndex("de"));
            int sumlo = cursor.getInt(cursor.getColumnIndex("lo"));
            int sumxien2 = cursor.getInt(cursor.getColumnIndex("xien2"));
            int sumxien3 = cursor.getInt(cursor.getColumnIndex("xien3"));
            int sumxien4 = cursor.getInt(cursor.getColumnIndex("xien4"));
            int sumbacang = cursor.getInt(cursor.getColumnIndex("bacang"));

            double actDe = cursor.getDouble(cursor.getColumnIndex("actualcollectde"));
            double actLo = cursor.getDouble(cursor.getColumnIndex("actualcollectlo"));
            double actXien2 = cursor.getDouble(cursor.getColumnIndex("actualcollectxien2"));
            double actXien3 = cursor.getDouble(cursor.getColumnIndex("actualcollectxien3"));
            double actXien4 = cursor.getDouble(cursor.getColumnIndex("actualcollectxien4"));
            double actBacang = cursor.getDouble(cursor.getColumnIndex("actualcollectbacang"));

            int winDe = cursor.getInt(cursor.getColumnIndex("dewin"));
            int winLo = cursor.getInt(cursor.getColumnIndex("lowin"));
            int winXien2 = cursor.getInt(cursor.getColumnIndex("xien2win"));
            int winXien3 = cursor.getInt(cursor.getColumnIndex("xien3win"));
            int winXien4 = cursor.getInt(cursor.getColumnIndex("xien4win"));
            int winBacang = cursor.getInt(cursor.getColumnIndex("bacangwin"));
            int winPointLo = cursor.getInt(cursor.getColumnIndex("lopointwin"));

            this.resultView.add("de",
                    new double[]{
                            sumde,
                            winDe,
                            actDe,
                            (winDe - actDe)
                    });

            this.resultView.add("lo",
                    new double[]{
                            sumlo,
                            winPointLo,
                            actLo,
                            (winLo - actLo)
                    });

            this.resultView.add("xien",
                    new double[]{
                            sumxien2 + sumxien3 + sumxien4,
                            winXien2 + winXien3 + winXien4,
                            actXien2 + actXien3 + actXien4,
                            (winXien2 + winXien3 + winXien4 - actXien2 - actXien3 - actXien4)
                    });

            this.resultView.add("bacang",
                    new double[]{
                            sumbacang,
                            winBacang,
                            actBacang,
                            (winBacang - actBacang)
                    });

            double sum = winDe - actDe + winLo - actLo + winXien2 + winXien3 + winXien4 - actXien2 - actXien3 - actXien4 + winBacang - actBacang;
            double sum2 = actDe + actLo + actXien2 + actXien3 + actXien4 + actBacang;
            resultView.addBottom(sum2, sum);

        }

        public void clearDialog() {
            dialog.setOnUpdateOnDismissListner(null);
            dialog.releaseViews();
        }

        @Override
        public void onUpdate() {
            requestChange(phone);
            clearDialog();
        }
    }
}
