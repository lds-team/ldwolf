package bingo.com.screen.congno;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bingo.com.R;
import bingo.com.base.BaseFragment;
import butterknife.Bind;

public class CongNoFragment extends BaseFragment<CongNoFragment> {

    public static final String TAG = "CongNoFragment";

    @Bind(android.R.id.tabhost)
    FragmentTabHost mTabHost;

    @Override
    public String initTag() {
        return TAG;
    }

    @Override
    public String screenNane() {
        return "Công nợ";
    }

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_congno, container, false);
    }

    @Override
    public void onViewFragmentCreated(View view, Bundle savedInstanceState) {
        mTabHost.setup(context, context.getSupportFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(
                mTabHost.newTabSpec(ChargeFragment.TAG).setIndicator("Thanh toán", null),
                ChargeFragment.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec(HistoryFragment.TAG).setIndicator("Lịch sử", null),
                HistoryFragment.class, null);
    }

    @Override
    public CongNoFragment instance() {
        return this;
    }

    @Override
    public void update() {

    }
}
