package bingo.com.screen.numberreport;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;

import bingo.com.R;
import bingo.com.async.CommonTask;
import bingo.com.base.Basev4Fragment;
import bingo.com.customviews.RangeSeekBar;
import bingo.com.dialog.GiuSoDialog;
import bingo.com.dialog.XuatSoDialog;
import bingo.com.enumtype.LotoType;
import bingo.com.enumtype.TableType;
import bingo.com.enumtype.TypeMessage;
import bingo.com.helperdb.db2.FollowDBHelper2;
import bingo.com.model.MessageForm;
import bingo.com.screen.numberreport.adapter.NumberAdapter;
import bingo.com.utils.Utils;
import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class ManagerNumberFragment extends Basev4Fragment implements View.OnClickListener {

    public static final String TAG = "ManagerNumberFragment";

    @Bind(R.id.managernumber_list)
    RecyclerView lstNum;

    @Bind(R.id.fam)
    FloatingActionsMenu fam;

    private NumberAdapter adapter;

    @Bind(R.id.managernumber_sort_received)
    TextView vNhan;

    @Bind(R.id.managernumber_sort_keep)
    TextView vGiu;

    @Bind(R.id.managernumber_sort_sent)
    TextView vChuyen;

    @Bind(R.id.managernumber_sort_other)
    TextView vTon;

    @Bind(R.id.managernumber_xuathet)
    EditText editTextXuatTien;

    @Bind(R.id.managernumber_xiengroup)
    LinearLayout checkGroup;

    @Bind(R.id.managernumber_checkxien2)
    CheckBox xien2;

    @Bind(R.id.managernumber_checkxien3)
    CheckBox xien3;

    @Bind(R.id.managernumber_checkxien4)
    CheckBox xien4;

    @Bind(R.id.rangeSeekbar)
    RangeSeekBar<Integer> seekBar;

    @Bind(R.id.managernumber_menu)
    Button showMenu;

    RadioGroup group;

    private String currentTypeChoose;

    private String sXuatTien = "";

    private String[] groupQueryXien;

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        registerObs(this);

        return inflater.inflate(R.layout.fragment_managernummber, container, false);
    }

    @Override
    public void onViewFragmentCreated(View view, Bundle savedInstanceState) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        lstNum.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(lstNum.getContext(),
                layoutManager.getOrientation());
        lstNum.addItemDecoration(dividerItemDecoration);

        currentTypeChoose = LotoType.de.name();
        adapter = new NumberAdapter(getActivity(), null, seekBar);
        lstNum.setAdapter(adapter);

        initChooser(view);
        requestUpdate();

        //Notify when task is running.
        int taskcount = CommonTask.AsyncSingleThread.get().getCacheTaskCount();
        if (taskcount < 0)
        {
            Utils.showAlert(getActivity(), null, "Hệ thống đang trong quá trình cập nhật dữ liệu.\nCòn " + taskcount + " task.", "Ok", null);
        }
    }

    private void initChooser(View view) {
        group = (RadioGroup) view.findViewById(R.id.number_filter_group);

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                currentTypeChoose = getChooseType(checkedId);
                setTypeViewChange(checkedId);
                resetSeekbar();
                requestChangeNumber(currentTypeChoose);
            }
        });

    }

    @OnTextChanged(R.id.managernumber_xuathet)
    public void afterTextChanged(Editable s) {
        if (!sXuatTien.equals(s.toString()))
        {
            String temp = s.toString();
            if (temp.contains(".") || temp.matches(".*[=>%]{2}.*") || temp.matches(".*[^\\s=>%0-9].*"))
            {
                Utils.showAlert(getActivity(), "Lỗi ký tự", "Chỉ chấp nhận > = %.", "Ok", null);
                s.delete(temp.length() - 1, temp.length());
            }
            else
            {
                sXuatTien = temp.replaceAll("\\s", "");
            }
        }
    }

    private void requestChangeNumber(String type) {
        adapter.swapCursor(queryNumber(type, null));
    }

    private String getChooseType(int id) {
        switch (id) {
            case R.id.number_filter_dedit:
                return LotoType.de.name();

            case R.id.number_filter_xemlo:
                return LotoType.lo.name();

            case R.id.number_filter_xemxien:
                return LotoType.xien.name();

            case R.id.number_filter_dedau:
                return LotoType.daudb.name();

            case R.id.number_filter_dedaunhat:
                return LotoType.daunhat.name();

            case R.id.number_filter_deditnhat:
                return LotoType.ditnhat.name();

            case R.id.number_filter_bacang:
                return LotoType.bacang.name();

            default:
                return null;
        }
    }

    private void setTypeViewChange(int checkId) {
        if (checkId == R.id.number_filter_xemxien)
        {
            checkGroup.setVisibility(View.VISIBLE);
            checkGroupXienForQuery();
        }
        else
        {
            checkGroup.setVisibility(View.GONE);
            groupQueryXien = null;
        }
    }

    @OnClick({R.id.managernumber_checkxien2, R.id.managernumber_checkxien3, R.id.managernumber_checkxien4})
    public void checkGroupXienForQuery() {
        resetSeekbar();
        ArrayList<String> checked = new ArrayList<>();
        if (xien2.isChecked())
        {
            checked.add("xien2");
        }

        if (xien3.isChecked())
        {
            checked.add("xien3");
        }

        if (xien4.isChecked())
        {
            checked.add("xien4");
        }

        if (checked.size() == 0)
        {
            groupQueryXien = null;

            xien2.setChecked(true);
            xien3.setChecked(true);
            xien4.setChecked(true);
        }
        else
        {
            groupQueryXien = checked.toArray(new String[checked.size()]);
        }

        adapter.swapCursor(queryNumber(currentTypeChoose, TypeMessage.RECEIVED));
    }

    private Cursor queryNumber(String type, TypeMessage sortType) {
        if (type == null)
            return FollowDBHelper2.getDatabase().getAllNumberFilterByType("de", sortType);
        else
            return FollowDBHelper2.getDatabase().getAllNumberFilterByType(TableType.getTableName(type).name(), sortType, groupQueryXien);
    }

    @Override
    public void onPause() {
        super.onPause();
        resetSeekbar();
    }

    @OnClick({R.id.managernumber_xuatdan, R.id.managernumber_xuatso, R.id.managernumber_giuso,
            R.id.managernumber_sort_received, R.id.managernumber_sort_sent, R.id.managernumber_sort_keep, R.id.managernumber_sort_other})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.managernumber_xuatdan:
            case R.id.managernumber_xuatso:
                showXuatDanDialog();
                break;

            case R.id.managernumber_giuso:
                showGiusoDialog();
                break;

            //Sort
            case R.id.managernumber_sort_received:
                resetSeekbar();

                vNhan.setTextColor(Color.RED);
                vGiu.setTextColor(Color.BLACK);
                vChuyen.setTextColor(Color.BLACK);
                vTon.setTextColor(Color.BLACK);

                adapter.swapCursor(queryNumber(currentTypeChoose, TypeMessage.RECEIVED));
                break;

            case R.id.managernumber_sort_sent:
                resetSeekbar();

                vNhan.setTextColor(Color.BLACK);
                vGiu.setTextColor(Color.BLACK);
                vChuyen.setTextColor(Color.RED);
                vTon.setTextColor(Color.BLACK);

                adapter.swapCursor(queryNumber(currentTypeChoose, TypeMessage.SENT));
                break;

            case R.id.managernumber_sort_keep:
                resetSeekbar();

                vNhan.setTextColor(Color.BLACK);
                vGiu.setTextColor(Color.RED);
                vChuyen.setTextColor(Color.BLACK);
                vTon.setTextColor(Color.BLACK);

                adapter.swapCursor(queryNumber(currentTypeChoose, TypeMessage.KEEP));
                break;

            case R.id.managernumber_sort_other:
                resetSeekbar();

                vNhan.setTextColor(Color.BLACK);
                vGiu.setTextColor(Color.BLACK);
                vChuyen.setTextColor(Color.BLACK);
                vTon.setTextColor(Color.RED);

                adapter.swapCursor(queryNumber(currentTypeChoose, TypeMessage.SMS));
                break;

            default:
                break;
        }

        fam.collapse();
    }

    @OnClick(R.id.managernumber_menu)
    public void showPopupMenu() {
        PopupMenu menu = new PopupMenu(getActivity(), showMenu);
        MenuInflater inflater = menu.getMenuInflater();
        inflater.inflate(R.menu.menu_managernumber, menu.getMenu());

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.showgiuso:
                        showGiusoDialog();
                        break;

                    case R.id.showrange:
                        seekBar.setVisibility(seekBar.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                        break;

                    case R.id.support:
                        Utils.showAlert(getActivity(), "Hướng dẫn tiền xuất",
                                "Tiền xuất 1000: Phần mềm sẽ tạo 1 tin nhắn có số tồn từ 1000 trong khoảng chọn." +
                                        "\nTiền xuất >1000: Phần mềm sẽ tạo 1 tin nhắn có số tồn với phần thừa >1000 trong khoảng chọn." +
                                        "\nTiền xuất 20%: Phần mềm sẽ tạo 1 tin nhắn với 20% giá trị tồn của các số trong khoảng chọn." +
                                        "\nTiền xuất để trống: Xuất hết.",
                                "Ok", null);
                        break;
                }
                return false;
            }
        });

        menu.show();
    }

    private void showXuatDanDialog() {
        if (!sXuatTien.matches("\\s*[>=]?\\s*[0-9]+\\s*") && !sXuatTien.matches("\\s*[0-9]+\\s*[%]?\\s*") && !sXuatTien.isEmpty())
        {
            Utils.showAlert(getActivity(), "Lỗi ký tự", "Chỉ chấp nhận > = %.", "Ok", null);
            return;
        }

        try {
            int fromNumber = seekBar.getSelectedMinValue();
            int toNumber = seekBar.getSelectedMaxValue();

            int tienXuat;
            XuatSoDialog.TYPE_TIEN_XUAT type_tien_xuat;
            if (sXuatTien.isEmpty())
            {
                tienXuat = 0;
                type_tien_xuat = XuatSoDialog.TYPE_TIEN_XUAT.XUAT_HET;
            }
            else if (sXuatTien.startsWith(">"))
            {
                tienXuat = Integer.parseInt(sXuatTien.replaceAll("\\W", ""));
                type_tien_xuat = XuatSoDialog.TYPE_TIEN_XUAT.XUAT_LON_HON;
            }
            else if (sXuatTien.endsWith("%"))
            {
                tienXuat = Integer.parseInt(sXuatTien.replaceAll("\\W", ""));
                type_tien_xuat = XuatSoDialog.TYPE_TIEN_XUAT.XUAT_PHAN_TRAM;
            }
            else if (sXuatTien.matches("[0-9]+") || sXuatTien.startsWith("="))
            {
                tienXuat = Integer.parseInt(sXuatTien.replaceAll("\\W", ""));
                type_tien_xuat = XuatSoDialog.TYPE_TIEN_XUAT.XUAT_TIEN;
            }
            else
            {
                throw new Exception("Invalid type string export money.");
            }

            XuatSoDialog dialog = new XuatSoDialog(getActivity(), adapter.getCursor(), false,
                    tienXuat, type_tien_xuat, new int[]{fromNumber, toNumber});
            dialog.show();
        } catch (Exception e) {
            Utils.showAlert(getActivity(), "Xuất không thành công", "Xảy ra lỗi khi xuất tiền.", "Ok", null);
        }

        resetSeekbar();
    }

    private void showGiusoDialog() {
        GiuSoDialog dialog = new GiuSoDialog(getActivity(), false, GiuSoDialog.TYPE_LAYOUT.DELO);
        dialog.show();

        resetSeekbar();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unRegisterObs(this);
    }

    @Override
    public void update() {
        Log.d(TAG, "Request Update On Thread: " + Thread.currentThread().getName());

        if (adapter != null) {
            adapter.swapCursor(queryNumber(currentTypeChoose, null));

            setDefaultSortUI();
        }
    }

    private void setDefaultSortUI() {
        vNhan.setTextColor(Color.RED);
        vGiu.setTextColor(Color.BLACK);
        vChuyen.setTextColor(Color.BLACK);
        vTon.setTextColor(Color.BLACK);
    }

    private void resetSeekbar() {
        seekBar.setVisibility(View.GONE);
        seekBar.resetSelectedValues();
    }
}
