package bingo.com.screen.configscreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import bingo.com.R;
import bingo.com.base.Basev4Fragment;
import butterknife.Bind;

public class OtherFragment extends Basev4Fragment {

    public static final String TAG = "OtherFragment";

    @Bind(R.id.other_user)
    EditText user;

    @Bind(R.id.other_pass1)
    EditText pass1;

    @Bind(R.id.other_pass2)
    EditText pass2;

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_other, container, false);
    }

    @Override
    public void onViewFragmentCreated(View view, Bundle savedInstanceState) {

    }

    @Override
    public void update() {

    }
}
