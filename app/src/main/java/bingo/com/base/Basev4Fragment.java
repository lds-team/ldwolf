package bingo.com.base;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bingo.com.obs.SyncObs;
import butterknife.ButterKnife;

public abstract class Basev4Fragment extends Fragment implements SyncObs {

    public abstract View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    public abstract void onViewFragmentCreated(View view, Bundle savedInstanceState);

    private SharedPreferences pref;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = ((BaseActivity) getActivity()).getSaved();
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        onViewFragmentCreated(view, savedInstanceState);
    }

    protected void showLoading() {
        ((BaseActivity) getActivity()).getLoadingAnimation("Loading...").show();
    }

    protected void dismissLoading() {
        ((BaseActivity) getActivity()).dismissLoading();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.unbind(this);
    }

    protected void registerObs(SyncObs obs) {
        ((BaseActivity) getActivity()).registerObs(obs);
    }

    protected void unRegisterObs(SyncObs obs) {
        ((BaseActivity) getActivity()).unRegisterObs(obs);
    }

    protected void requestUpdate() {
        ((BaseActivity) getActivity()).requestUpdate();
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
