package bingo.com.screen.numberreport;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bingo.com.R;
import bingo.com.base.BaseFragment;
import butterknife.Bind;

public class NumberReportFragment extends BaseFragment<NumberReportFragment> {

    public static final String TAG = "NumberReportFragment";

    @Bind(android.R.id.tabhost)
    FragmentTabHost mTabHost;

    @Override
    public String initTag() {
        return TAG;
    }

    @Override
    public String screenNane() {
        return "Number Report";
    }

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_numberreport, container, false);
    }

    @Override
    public void onViewFragmentCreated(View view, Bundle savedInstanceState) {
        mTabHost.setup(context, context.getSupportFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(
                mTabHost.newTabSpec(ManagerNumberFragment.TAG).setIndicator("Quản lý số", null),
                ManagerNumberFragment.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec(CustomerFragment.TAG).setIndicator("Khách", null),
                CustomerFragment.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec(MessageFragment.TAG).setIndicator("Tin nhắn", null),
                MessageFragment.class, null);
    }

    @Override
    public NumberReportFragment instance() {
        return this;
    }

    @Override
    public void update() {

    }
}
