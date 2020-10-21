package bingo.com.screen.configscreen;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bingo.com.R;
import bingo.com.base.Basev4Fragment;
import bingo.com.callbacks.UpdateSync;
import bingo.com.dialog.InfoContactDialog;
import bingo.com.helperdb.ContactDBHelper;
import bingo.com.screen.configscreen.adapter.ContactAdapter;
import bingo.com.utils.Utils;
import butterknife.Bind;
import butterknife.OnClick;

public class ContactFragment extends Basev4Fragment {

    public static final String TAG = "ContactFragment";

    private static final int REQUEST_CONTACT = 100;

    @Bind(R.id.contact_list)
    RecyclerView list;

    private ContactAdapter adapter;

    private InfoContactDialog dialog;

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        registerObs(this);
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }

    @Override
    public void onViewFragmentCreated(View view, Bundle savedInstanceState) {

        initList();
    }

    private void initList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        list.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(list.getContext(),
                layoutManager.getOrientation());
        list.addItemDecoration(dividerItemDecoration);

        adapter = new ContactAdapter(getContext(), getList());
        list.setAdapter(adapter);
    }

    private Cursor getList() {
        return ContactDBHelper.getAlls();
    }

    @OnClick(R.id.contact_add)
    public void requestContact() {
        Intent contactRequest = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactRequest, REQUEST_CONTACT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CONTACT)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                Uri contactUri = data.getData();

                Cursor cPhone = getContext().getContentResolver().query(contactUri,
                        new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);

                String phone = null;
                if (cPhone != null && cPhone.moveToFirst()) //TODO may be more than 1 number. Only get first number?
                {
                    phone = Utils.fixPhone(cPhone.getString(0));

                    cPhone.close();

                    if (checkExist(phone))
                        return;
                }

                Cursor cName = getContext().getContentResolver().query(contactUri,
                        new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME}, null, null, null);

                String name = null;
                if (cName != null && cName.moveToFirst())
                {
                    name = cName.getString(0);

                    cName.close();
                }

                if (phone != null && name != null)
                {
                    showAddContact(name, phone);
                }
            }
        }
    }

    private boolean checkExist(String phone) {
        if (ContactDBHelper.isExist(phone))
        {
            Utils.showAlert(getActivity(), null, "Số này đã có trong danh bạ.", "Ok", null);
            return true;
        }

        return false;
    }

    private void showAddContact(String name, String phone) {
        dialog = new InfoContactDialog(getContext(), false, name, phone);
        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (dialog != null)
            dialog.releaseViews();

        dialog = null;

        unBindview(list);
        unRegisterObs(this);
    }

    @Override
    public void update() {
        adapter.swapCursor(getList());
    }
}
