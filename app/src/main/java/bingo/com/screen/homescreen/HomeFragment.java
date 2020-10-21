package bingo.com.screen.homescreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bingo.com.R;
import bingo.com.base.BaseFragment;

public class HomeFragment extends BaseFragment<HomeFragment> {

    public static final String TAG = "HomeFragment";

    @Override
    public String initTag() {
        return TAG;
    }

    @Override
    public String screenNane() {
        return "Home";
    }

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewFragmentCreated(View view, Bundle savedInstanceState) {
//        requestUpdate();
    }

    @Override
    public HomeFragment instance() {
        return this;
    }


    @Override
    public void update() {

    }
}
