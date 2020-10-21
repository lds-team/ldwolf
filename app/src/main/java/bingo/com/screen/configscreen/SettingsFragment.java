package bingo.com.screen.configscreen;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bingo.com.R;
import bingo.com.base.BaseFragment;
import butterknife.Bind;

public class SettingsFragment extends BaseFragment<SettingsFragment> {

    public static final String TAG = "SettingsFragment";

    @Bind(android.R.id.tabhost)
    FragmentTabHost mTabHost;

    @Override
    public String initTag() {
        return TAG;
    }

    @Override
    public String screenNane() {
        return "Settings";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 23)
        {
            String permission = Manifest.permission.READ_CONTACTS;
            int grant = ContextCompat.checkSelfPermission(getActivity(), permission);
            if (grant != PackageManager.PERMISSION_GRANTED) {
                String[] permission_list = new String[1];
                permission_list[0] = permission;
                ActivityCompat.requestPermissions(getActivity(), permission_list, 1);
            }
        }
    }

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewFragmentCreated(View view, Bundle savedInstanceState) {
        mTabHost.setup(context, context.getSupportFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(
                mTabHost.newTabSpec(ContactFragment.TAG).setIndicator("KHÁCH HÀNG", null),
                ContactFragment.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec(ConfigFragment.TAG).setIndicator("CÀI ĐẶT", null),
                ConfigFragment.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec(OtherFragment.TAG).setIndicator("KHÁC", null),
                OtherFragment.class, null);
    }

    @Override
    public SettingsFragment instance() {
        return this;
    }

    @Override
    public void update() {

    }
}
