package bingo.com.screen.congno.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import bingo.com.R;
import bingo.com.base.baseadapter.BaseCursorRecyclerAdapter;
import bingo.com.base.baseview.BaseRecyclerViewHolder;
import bingo.com.customviews.HistoryView;
import bingo.com.dialog.IncreaseCharge;
import bingo.com.helperdb.CongnoDBHelper;
import bingo.com.helperdb.ContactDBHelper;
import bingo.com.helperdb.db.CongnoDatabase;
import bingo.com.model.ContactModel;
import butterknife.Bind;

public class HistoryAdapter extends BaseCursorRecyclerAdapter<HistoryAdapter.ViewHolder> {

    private Context context;

    private ArrayList<Double> listTotalDay = new ArrayList<>();

    private HashMap<String, ContactModel> contacts;

    private double sumAll = 0;

    public HistoryAdapter(Context context, Cursor cursor) {
        super(context, cursor);

        this.context = context;
        contacts = ContactDBHelper.getFullContact();
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor != null && newCursor.moveToFirst())
        {
            listTotalDay.clear();
            sumAll = 0;

            while (!newCursor.isAfterLast())
            {
                double money = newCursor.getDouble(newCursor.getColumnIndex("summoney"));
                listTotalDay.add(money);
                sumAll += money;

                newCursor.moveToNext();
            }
        }

        return super.swapCursor(newCursor);
    }

    private double getRecursiveMoney(int index) {
        if (index >= listTotalDay.size()) return 0;

        double sum = sumAll;
        for (int i = 0; i < index; i++)
        {
            sum -= listTotalDay.get(i);
        }

        return sum;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        String date = cursor.getString(cursor.getColumnIndex(CongnoDatabase.DETAIL_DATE));
        String phone = cursor.getString(cursor.getColumnIndex(CongnoDatabase.DETAIL_PHONE));

        Cursor c = CongnoDBHelper.getCongNoHistory(phone, date);
        ContactModel contactModel = contacts.get(phone);
        boolean isChu = contactModel != null && contactModel.getType().equals("1");

        try
        {
            viewHolder.setData(contactModel, isChu, date, c, CongnoDatabase.DETAIL_TYPE, CongnoDatabase.DETAIL_POINT, CongnoDatabase.DETAIL_MONEY, CongnoDatabase.DETAIL_POINT_WIN);
        }
        finally {
            c.close();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_history, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));

        return new ViewHolder(view);
    }

    class ViewHolder extends BaseRecyclerViewHolder implements View.OnLongClickListener {

        @Bind(R.id.item_history_date)
        TextView date;

        @Bind(R.id.item_history_view)
        HistoryView history;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnLongClickListener(this);
        }

        public void setData(ContactModel contactModel, boolean isChuNhanSo, String date, Cursor cursor, String... column) {
            double sumRecursive = getRecursiveMoney(getLayoutPosition());

            this.date.setText(date);
            this.history.addAlls(contactModel, isChuNhanSo, cursor, isChuNhanSo ? 0 - sumRecursive : sumRecursive, column);
        }

        @Override
        public boolean onLongClick(View v) {
            getCursor().moveToPosition(getLayoutPosition());
            String name = getCursor().getString(getCursor().getColumnIndex(CongnoDatabase.DETAIL_NAME));
            String phone = getCursor().getString(getCursor().getColumnIndex(CongnoDatabase.DETAIL_PHONE));
            String date = getCursor().getString(getCursor().getColumnIndex(CongnoDatabase.DETAIL_DATE));
            boolean isChu = !getCursor().getString(getCursor().getColumnIndex(CongnoDatabase.DETAIL_CUSTOMER)).equals("0");

            IncreaseCharge dialog = new IncreaseCharge(context, false, name, phone, date, isChu);
            dialog.show();

            return false;
        }
    }
}
