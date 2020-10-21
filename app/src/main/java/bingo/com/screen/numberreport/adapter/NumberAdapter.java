package bingo.com.screen.numberreport.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bingo.com.R;
import bingo.com.async.CommonTask;
import bingo.com.base.BaseActivity;
import bingo.com.base.baseadapter.BaseCursorRecyclerAdapter;
import bingo.com.base.baseview.BaseRecyclerViewHolder;
import bingo.com.customviews.RangeSeekBar;
import bingo.com.helperdb.ContactDBHelper;
import bingo.com.helperdb.MessageDBHelper;
import bingo.com.helperdb.db.FollowDatabase;
import bingo.com.utils.JSONAnalyzeBuilder;
import bingo.com.utils.Utils;
import butterknife.Bind;

public class NumberAdapter extends BaseCursorRecyclerAdapter<NumberAdapter.ViewHolder> {

    private Context context;

    private RangeSeekBar<Integer> rangeSeekBar;

    public NumberAdapter(Context context, Cursor cursor, RangeSeekBar<Integer> rangeSeekBar) {
        super(context, cursor);

        this.context = context;
        this.rangeSeekBar = rangeSeekBar;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        String number = cursor.getString(cursor.getColumnIndex("number"));

        //TODO Must to find root cause. Error while import data keep.
        int chuyen;
        int giu;
        int nhan;
        int ton;

//        String type = cursor.getString(cursor.getColumnIndex(FollowDatabase.DETAIL_TYPE));
//
//        chuyen = FollowDBHelper.getSumSentValueOfNumber(number, type);
//        giu = FollowDBHelper.getSumKeepValueOfNumber(number, type);
//        nhan = FollowDBHelper.getSumReceivedValueOfNumber(number, type);

        chuyen = cursor.getInt(cursor.getColumnIndex("sort2"));
        giu = cursor.getInt(cursor.getColumnIndex("sort3"));
        nhan = cursor.getInt(cursor.getColumnIndex("sort1"));
        ton = cursor.getInt(cursor.getColumnIndex("sms"));

        boolean hasWin = cursor.getString(cursor.getColumnIndex("countwin")).trim().equals("true");

//        String ton = String.valueOf(nhan - chuyen - giu);

        viewHolder.setValue(
                String.valueOf(cursor.getPosition() + 1),
                number,
                String.valueOf(nhan),
                String.valueOf(giu),
                String.valueOf(chuyen),
                String.valueOf(ton),
                hasWin
        );
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_numberadapter, null);
        //Hot fix for issue not full screen width. Remove at the feature.
        //TODO : This fix will make slowdown the scrolling RecyclerView.
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));

        return new ViewHolder(view);
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        rangeSeekBar.setRangeValues(1, newCursor.getCount());
        return super.swapCursor(newCursor);
    }

    class ViewHolder extends BaseRecyclerViewHolder {

        @Bind(R.id.item_numberadapter_id)
        TextView id;

        @Bind(R.id.item_numberadapter_so)
        TextView chon;

        @Bind(R.id.item_numberadapter_nhan)
        TextView nhan;

        @Bind(R.id.item_numberadapter_giu)
        TextView giu;

        @Bind(R.id.item_numberadapter_chuyen)
        TextView chuyen;

        @Bind(R.id.item_numberadapter_ton)
        TextView ton;

        public ViewHolder(final View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getCursor().moveToPosition(getLayoutPosition());

                    final String number = getCursor().getString(getCursor().getColumnIndex(FollowDatabase.DETAIL_NUMBER));
                    final String type = getCursor().getString(getCursor().getColumnIndex(FollowDatabase.DETAIL_TYPE));

                    //TODO for fun.
                    CommonTask.AsyncSingleThread.get().execute(new Runnable() {
                        @Override
                        public void run() {
                            final String s = buildNumber(number, type);

                            ((BaseActivity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Snackbar snackbar = Snackbar.make(itemView, "", Snackbar.LENGTH_SHORT);
                                        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();

                                        TextView textView = layout.findViewById(android.support.design.R.id.snackbar_text);
                                        textView.setVisibility(View.INVISIBLE);

                                        View snackView = LayoutInflater.from(context).inflate(R.layout.simple_list_item_1, layout);
                                        snackbar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.black_semi_transparent));

                                        TextView text = snackView.findViewById(android.R.id.text1);
                                        text.setText(s);
                                        text.setTextColor(Color.BLACK);

                                        layout.setPadding(0,0,0,0);

                                        snackbar.show();
                                    } catch (Exception e) {}
                                }
                            });
                        }
                    }).clean();
                }
            });
        }

        private String buildNumber(String reqNumber, String reqType) {
            String build = "";

            Cursor cursor = MessageDBHelper.getDb().getAllReceivedMessage(Utils.getCurrentDate());

            if (cursor.moveToFirst())
            {
                try {
                    int sum = 0;

                    while (!cursor.isAfterLast())
                    {
                        String phone = cursor.getString(cursor.getColumnIndex("phone"));
                        String name = /*cursor.getString(cursor.getColumnIndex("name"))*/ContactDBHelper.getName(phone);

                        String detail = cursor.getString(cursor.getColumnIndex("detail"));
                        JSONArray array = new JSONArray(detail);

                        int size = array.length();
                        for (int i = 0; i < size; i++)
                        {
                            JSONArray jsonArray = array.getJSONArray(i);
                            int sz = jsonArray.length();
                            for (int j = 0; j < sz; j++)
                            {
                                JSONObject object = jsonArray.getJSONObject(j);
                                String number = object.getString(JSONAnalyzeBuilder.KEY_NUMBER);
                                String type = object.getString(JSONAnalyzeBuilder.KEY_TYPE);
                                String price = object.getString(JSONAnalyzeBuilder.KEY_PRICE);

                                if (number.equals(reqNumber) && type.equals(reqType))
                                {
                                    sum += Integer.parseInt(price);
                                }
                            }
                        }

                        if (cursor.moveToNext())
                        {
                            String nextPhone = cursor.getString(cursor.getColumnIndex("phone"));
                            if (!phone.equals(nextPhone) && sum != 0)
                            {
                                build = build.concat(name).concat(":").concat(String.valueOf(sum)).concat("\n");
                                sum = 0;
                            }
                        }
                        else
                        {
                            if (sum != 0)
                            {
                                build = build.concat(name).concat(":").concat(String.valueOf(sum));
                            }
                        }

                    }
                } catch (JSONException e) {
                    cursor.close();
                }
            }

            cursor.close();

            return build;
        }

        private void setValue(String id, String chon, String nhan, String giu, String chuyen, String ton, boolean win) {
            this.id.setText(id);
            this.chon.setText(chon);
            this.nhan.setText(nhan);
            this.giu.setText(giu);
            this.chuyen.setText(chuyen);
            this.ton.setText(ton);

            if (win)
            {
                this.chon.setText(chon.concat("*"));
                this.chon.setTextSize(20);
                this.chon.setTextColor(Color.RED);
            }
            else
            {
                this.chon.setTextSize(16);
                this.chon.setTextColor(Color.BLACK);
            }
        }
    }
}
