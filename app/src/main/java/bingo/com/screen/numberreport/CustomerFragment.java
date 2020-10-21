package bingo.com.screen.numberreport;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bingo.com.R;
import bingo.com.base.Basev4Fragment;
import bingo.com.customviews.ResultView;
import bingo.com.helperdb.MessageDBHelper;
import bingo.com.helperdb.db2.FollowDBHelper2;
import bingo.com.screen.numberreport.adapter.CustomerAdapter;
import butterknife.Bind;

public class CustomerFragment extends Basev4Fragment {

    public static final String TAG = "CustomerFragment";

    @Bind(R.id.customer_list)
    RecyclerView lst;

    @Bind(R.id.customer_resultview)
    ResultView resultView;

    private CustomerAdapter adapter;

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        registerObs(this);
        return inflater.inflate(R.layout.fragment_customer, container, false);
    }

    @Override
    public void onViewFragmentCreated(View view, Bundle savedInstanceState) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        lst.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(lst.getContext(),
                layoutManager.getOrientation());
        lst.addItemDecoration(dividerItemDecoration);

        adapter = new CustomerAdapter(getActivity(), null);
        lst.setAdapter(adapter);

        fillKeepData();

        adapter.swapCursor(MessageDBHelper.getDb().getCustomerDetail());
    }

    private void  fillKeepData() {
        Cursor cursor = MessageDBHelper.getDb().getRemainDetail();
        addToResult(cursor);
        cursor.close();
    }

    public void addToResult(Cursor cursor) {

        resultView.removeAllViews();

        if (cursor.moveToFirst())
        {
            String typeMessage = cursor.getString(cursor.getColumnIndex("typemessage"));

            int[] winFixed = FollowDBHelper2.getDatabase().getSumDeWinToDay();

            int sumde = cursor.getInt(cursor.getColumnIndex("sumde"));
            int sumlo = cursor.getInt(cursor.getColumnIndex("sumlo"));
            int sumxien2 = cursor.getInt(cursor.getColumnIndex("sumxien2"));
            int sumxien3 = cursor.getInt(cursor.getColumnIndex("sumxien3"));
            int sumxien4 = cursor.getInt(cursor.getColumnIndex("sumxien4"));
            int sumbacang = cursor.getInt(cursor.getColumnIndex("sumbacang"));

            double actDe = cursor.getDouble(cursor.getColumnIndex("sumactde"));
            double actLo = cursor.getDouble(cursor.getColumnIndex("sumactlo"));
            double actXien2 = cursor.getDouble(cursor.getColumnIndex("sumactxien2"));
            double actXien3 = cursor.getDouble(cursor.getColumnIndex("sumactxien3"));
            double actXien4 = cursor.getDouble(cursor.getColumnIndex("sumactxien4"));
            double actBacang = cursor.getDouble(cursor.getColumnIndex("sumactbacang"));

            int winDe = cursor.getInt(cursor.getColumnIndex("sumwinde"));
            int winLo = cursor.getInt(cursor.getColumnIndex("sumwinlo"));
            int winXien2 = cursor.getInt(cursor.getColumnIndex("sumwinxien2"));
            int winXien3 = cursor.getInt(cursor.getColumnIndex("sumwinxien3"));
            int winXien4 = cursor.getInt(cursor.getColumnIndex("sumwinxien4"));
            int winBacang = cursor.getInt(cursor.getColumnIndex("sumwinbacang"));
            int winPointLo = cursor.getInt(cursor.getColumnIndex("sumpointwinlo"));

            if (typeMessage.equals("SENT"))
            {
                sumde = 0 - sumde;
                sumlo = 0 - sumlo;
                sumxien2 = 0 - sumxien2;
                sumxien3 = 0 - sumxien3;
                sumxien4 = 0 - sumxien4;
                sumbacang = 0 - sumbacang;

                actDe = 0 - actDe;
                actLo = 0 - actLo;
                actXien2 = 0 - actXien2;
                actXien3 = 0 - actXien3;
                actXien4 = 0 - actXien4;
                actBacang = 0 - actBacang;

                winDe = 0 - winDe;
                winLo = 0 - winLo;
                winXien2 = 0 - winXien2;
                winXien3 = 0 - winXien3;
                winXien4 = 0 - winXien4;
                winBacang = 0 - winBacang;
                winPointLo = 0 - winPointLo;
            }

            if (cursor.moveToNext())
            {
                sumde -= cursor.getInt(cursor.getColumnIndex("sumde"));
                sumlo -= cursor.getInt(cursor.getColumnIndex("sumlo"));
                sumxien2 -= cursor.getInt(cursor.getColumnIndex("sumxien2"));
                sumxien3 -= cursor.getInt(cursor.getColumnIndex("sumxien3"));
                sumxien4 -= cursor.getInt(cursor.getColumnIndex("sumxien4"));
                sumbacang -= cursor.getInt(cursor.getColumnIndex("sumbacang"));

                actDe -= cursor.getDouble(cursor.getColumnIndex("sumactde"));
                actLo -= cursor.getDouble(cursor.getColumnIndex("sumactlo"));
                actXien2 -= cursor.getDouble(cursor.getColumnIndex("sumactxien2"));
                actXien3 -= cursor.getDouble(cursor.getColumnIndex("sumactxien3"));
                actXien4 -= cursor.getDouble(cursor.getColumnIndex("sumactxien4"));
                actBacang -= cursor.getDouble(cursor.getColumnIndex("sumactbacang"));

                winDe -= cursor.getInt(cursor.getColumnIndex("sumwinde"));
                winLo -= cursor.getInt(cursor.getColumnIndex("sumwinlo"));
                winXien2 -= cursor.getInt(cursor.getColumnIndex("sumwinxien2"));
                winXien3 -= cursor.getInt(cursor.getColumnIndex("sumwinxien3"));
                winXien4 -= cursor.getInt(cursor.getColumnIndex("sumwinxien4"));
                winBacang -= cursor.getInt(cursor.getColumnIndex("sumwinbacang"));
                winPointLo -= cursor.getInt(cursor.getColumnIndex("sumpointwinlo"));
            }

            this.resultView.add("de",
                    new double[]{
                            sumde,
                            winFixed[0],
                            (-winDe + actDe)
                    });

            this.resultView.add("lo",
                    new double[]{
                            sumlo,
                            winPointLo,
                            (-winLo + actLo)
                    });

            this.resultView.add("xien",
                    new double[]{
                            sumxien2 + sumxien3 + sumxien4,
                            winXien2 + winXien3 + winXien4,
                            (-winXien2 - winXien3 - winXien4 + actXien2 + actXien3 + actXien4)
                    });

            this.resultView.add("bacang",
                    new double[]{
                            sumbacang,
                            winFixed[1],
                            (-winBacang + actBacang)
                    });

            double sum = -(winDe - actDe + winLo - actLo + winXien2 + winXien3 + winXien4 - actXien2 - actXien3 - actXien4 + winBacang - actBacang);
            resultView.addBottom(sum);
        }
    }

    @Override
    public void update() {
        fillKeepData();

        adapter.swapCursor(MessageDBHelper.getDb().getCustomerDetail());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unRegisterObs(this);
    }
}
