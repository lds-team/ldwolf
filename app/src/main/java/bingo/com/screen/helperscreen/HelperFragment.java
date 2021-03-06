package bingo.com.screen.helperscreen;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import bingo.com.R;
import bingo.com.base.BaseFragment;
import butterknife.Bind;
import butterknife.OnCheckedChanged;

public class HelperFragment extends BaseFragment<HelperFragment> {

    public static final String TAG = "HelperFragment";

    @Bind(R.id.helper_guide1)
    TextView guide1;

    @Bind(R.id.helper_guide2)
    TextView guide2;

    @Bind(R.id.helper_guide3)
    TextView guide3;

    @Bind(R.id.helper_guide4)
    TextView guide4;

    @Bind(R.id.helper_guide5)
    TextView guide5;

    @Override
    public String initTag() {
        return TAG;
    }

    @Override
    public String screenNane() {
        return "Hướng dẫn";
    }

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_helper, container, false);
    }

    @Override
    public void onViewFragmentCreated(View view, Bundle savedInstanceState) {

    }

    @OnCheckedChanged({R.id.helper_switch1, R.id.helper_switch2, R.id.helper_switch3, R.id.helper_switch4, R.id.helper_switch5})
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(TAG, "handleChecked");

        switch (buttonView.getId())
        {
            case R.id.helper_switch1:
                toggleButton(guide1, isChecked);
                break;

            case R.id.helper_switch2:
                toggleButton(guide2, isChecked);
                break;

            case R.id.helper_switch3:
                toggleButton(guide3, isChecked);
                break;

            case R.id.helper_switch4:
                toggleButton(guide4, isChecked);
                break;

            case R.id.helper_switch5:
                toggleButton(guide5, isChecked);
                break;

            default:break;
        }
    }

    private void toggleButton(View v, boolean show) {
        v.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public HelperFragment instance() {
        return this;
    }

    @Override
    public void update() {

    }
}
