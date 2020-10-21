package bingo.com.base;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bingo.com.obs.SyncObs;
import butterknife.ButterKnife;

public abstract class BaseFragment<FM extends BaseFragment> extends Fragment implements SyncObs {

    public abstract String initTag();

    public abstract String screenNane();

    public abstract View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    public abstract void onViewFragmentCreated(View view, Bundle savedInstanceState);

    public abstract FM instance();

    protected BaseActivity context;

    protected FragmentManager fm;

    private SharedPreferences pref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.context = (BaseActivity) getActivity();
        fm = this.context.fm;
        pref = ((BaseActivity) getActivity()).getSaved();
    }

    protected void registerObs(SyncObs obs) {
        ((BaseActivity) getActivity()).registerObs(obs);
    }

    protected void unRegisterObs(SyncObs obs) {
        ((BaseActivity) getActivity()).unRegisterObs(obs);
    }

    protected void requestUpdate() {
        try {
            ((BaseActivity) getActivity()).requestUpdate();
        } catch (Exception e) {}
    }

    public SharedPreferences getSaved() {
        return pref;
    }

    public String getCurrenConfigDate() {
        return ((BaseActivity) getActivity()).getCurrentDate();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = onCreateFragmentView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        onViewFragmentCreated(view, savedInstanceState);
    }

    protected void showLoading(String message) {
        context.getLoadingAnimation(message).show();
    }

    protected void dismissLoading() {
        context.dismissLoading();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d("OnDestroyView: " + getClass(), instance().screenNane() + ": isDestroyView.");

        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected void unBindview(ViewGroup viewGroup) {
        if (viewGroup != null) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                viewGroup.getChildAt(i).setOnClickListener(null);
                viewGroup.getChildAt(i).setTag(null);
                if (viewGroup.getChildAt(i) instanceof ViewGroup)
                    unBindview((ViewGroup) viewGroup.getChildAt(i));
            }

            viewGroup.destroyDrawingCache();
        }

    }

}
