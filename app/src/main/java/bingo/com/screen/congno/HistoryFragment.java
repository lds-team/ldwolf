package bingo.com.screen.congno;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import bingo.com.R;
import bingo.com.base.Basev4Fragment;
import bingo.com.helperdb.CongnoDBHelper;
import bingo.com.helperdb.ContactDBHelper;
import bingo.com.helperdb.db.ContactDatabase;
import bingo.com.screen.congno.adapter.HistoryAdapter;
import butterknife.Bind;
import butterknife.OnItemSelected;

public class HistoryFragment extends Basev4Fragment {

    public static final String TAG = "HistoryFragment";

    @Bind(R.id.history_customer)
    Spinner listCustomer;

    @Bind(R.id.history_list)
    RecyclerView list;

    private HistoryAdapter adapter;

    private String phoneSelected;

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        registerObs(this);
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewFragmentCreated(View view, Bundle savedInstanceState) {
        initListContact(listCustomer);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        list.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(list.getContext(),
                layoutManager.getOrientation());
        list.addItemDecoration(dividerItemDecoration);

        adapter = new HistoryAdapter(getActivity(), null);
        list.setAdapter(adapter);
    }

    private void initListContact(Spinner spinner) {
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                CongnoDBHelper.getAllCongno(),
                new String[]{ContactDatabase.NAME},
                new int[]{android.R.id.text1}, 0);

        spinner.setAdapter(adapter);
        spinner.setDropDownVerticalOffset(120);
        spinner.setSelection(0);
    }

    @OnItemSelected(R.id.history_customer)
    public void onChangeCustomer() {
        phoneSelected = ((Cursor) listCustomer.getSelectedItem()).getString(((Cursor) listCustomer.getSelectedItem()).getColumnIndex(ContactDatabase.PHONE));

        if (phoneSelected != null)
        {
            adapter.swapCursor(CongnoDBHelper.getPhoneGroupDate(phoneSelected));
        }
        else
        {
            adapter.swapCursor(null);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unRegisterObs(this);
    }

    @Override
    public void update() {
        if (phoneSelected != null)
        {
            adapter.swapCursor(CongnoDBHelper.getPhoneGroupDate(phoneSelected));
        }
        else
        {
            adapter.swapCursor(null);
        }
    }
}
