package bingo.com.screen.congno;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bingo.com.R;
import bingo.com.base.Basev4Fragment;
import bingo.com.dialog.AddChargeDialog;
import bingo.com.helperdb.CongnoDBHelper;
import bingo.com.screen.congno.adapter.ChargeAdapter;
import butterknife.Bind;
import butterknife.OnClick;

public class ChargeFragment extends Basev4Fragment {

    public static final String TAG = "ChargeFragment";

    @Bind(R.id.charge_list)
    RecyclerView list;

    private ChargeAdapter adapter;

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        registerObs(this);
        return inflater.inflate(R.layout.fragment_charge, container, false);
    }

    @Override
    public void onViewFragmentCreated(View view, Bundle savedInstanceState) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        list.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(list.getContext(),
                layoutManager.getOrientation());
        list.addItemDecoration(dividerItemDecoration);

        adapter = new ChargeAdapter(getActivity(), CongnoDBHelper.getAllCongno());
        list.setAdapter(adapter);
    }

    @OnClick(R.id.charge_fam)
    public void famClick() {
        AddChargeDialog dialog = new AddChargeDialog(getActivity(), false, adapter.getCursor());
        dialog.show();
    }

    @Override
    public void update() {
        adapter.swapCursor(CongnoDBHelper.getAllCongno());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unRegisterObs(this);
    }
}
