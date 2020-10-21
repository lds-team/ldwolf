package bingo.com.screen.congno.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import bingo.com.R;
import bingo.com.base.baseadapter.BaseCursorRecyclerAdapter;
import bingo.com.base.baseview.BaseRecyclerViewHolder;
import bingo.com.customviews.TextViewDegit;
import bingo.com.helperdb.CongnoDBHelper;
import bingo.com.helperdb.ContactDBHelper;
import bingo.com.helperdb.db.CongnoDatabase;
import bingo.com.utils.Utils;
import butterknife.Bind;

public class ChargeAdapter extends BaseCursorRecyclerAdapter<ChargeAdapter.ViewHolder> {

    private Context context;

    public ChargeAdapter(Context context, Cursor cursor) {
        super(context, cursor);

        this.context = context;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        String phone = cursor.getString(cursor.getColumnIndex(CongnoDatabase.DETAIL_PHONE));

        String name = cursor.getString(cursor.getColumnIndex(CongnoDatabase.DETAIL_NAME));
        double sum = cursor.getDouble(cursor.getColumnIndex("sum"));

        double nocu = CongnoDBHelper.getNoCu(phone);

        double phatsinh = sum - nocu;

        boolean isChu = !cursor.getString(cursor.getColumnIndex("type_customer")).equals("0");
        if (isChu)
        {
            sum = 0 - sum;
        }

        viewHolder.setData(name, nocu, phatsinh, sum, isChu);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_charge, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));

        return new ViewHolder(view);
    }

    class ViewHolder extends BaseRecyclerViewHolder implements View.OnLongClickListener {

        @Bind(R.id.item_charge_name)
        TextView name;

        @Bind(R.id.item_charge_nocu)
        TextViewDegit nocu;

        @Bind(R.id.item_charge_phatsinh)
        TextViewDegit phatsinh;

        @Bind(R.id.item_charge_socuoi)
        TextViewDegit socuoi;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnLongClickListener(this);
        }

        public void setData(String name, double nocu, double phatsinh, double socuoi, boolean isChu) {
            this.name.setText(name);
            this.nocu.setResult(nocu);
            this.phatsinh.setResult(phatsinh);
            this.socuoi.setResult(socuoi);
            if (isChu)
                this.name.setTextColor(Color.BLUE);
            else
                this.name.setTextColor(Color.BLACK);
        }

        @Override
        public boolean onLongClick(View v) {
            getCursor().moveToPosition(getLayoutPosition());
            String name = getCursor().getString(getCursor().getColumnIndex("name"));
            final String phone = getCursor().getString(getCursor().getColumnIndex("phone"));
            Utils.showAlertTwoButton(context, "Xóa dữ liệu?", "Xóa dữ liệu của khách hàng:\n" + name + " : " + phone,
                    "Xóa", "Cancel", new Runnable() {
                        @Override
                        public void run() {
                            CongnoDBHelper.deleteAllCustomerCongNo(phone);
                            requestUpdate();
                        }
                    }, null);
            return false;
        }
    }
}
