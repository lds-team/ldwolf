package bingo.com.screen.numberreport.adapter;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;

import bingo.com.R;
import bingo.com.async.CommonTask;
import bingo.com.async.TaskAnalyze;
import bingo.com.async.TaskHelper;
import bingo.com.base.BaseActivity;
import bingo.com.base.baseadapter.BaseCursorRecyclerAdapter;
import bingo.com.base.baseview.BaseRecyclerViewHolder;
import bingo.com.controller.DeleteController;
import bingo.com.customviews.ResultView;
import bingo.com.enumtype.LotoType;
import bingo.com.enumtype.TypeMessage;
import bingo.com.helperdb.CongnoDBHelper;
import bingo.com.helperdb.ContactDBHelper;
import bingo.com.helperdb.db.CongnoDatabase;
import bingo.com.model.ContactModel;
import bingo.com.screen.configscreen.SettingsHelper;
import bingo.com.utils.Keysaved;
import bingo.com.utils.Utils;
import butterknife.Bind;

public class CustomerAdapter extends BaseCursorRecyclerAdapter<CustomerAdapter.ViewHolder> {

    private Context context;

    private HashMap<String, ContactModel> contacts;

    public CustomerAdapter(Context context, Cursor cursor) {
        super(context, cursor);

        this.context = context;
        contacts = ContactDBHelper.getFullContact();
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        viewHolder.setValue(
                cursor
        );
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_customadapter, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));

        return new ViewHolder(view);
    }

    class ViewHolder extends BaseRecyclerViewHolder implements View.OnClickListener {

        @Bind(R.id.customadapter_name)
        TextView name;

        @Bind(R.id.customadapter_result)
        ResultView result;

        private Dialog chooser;

        public ViewHolder(View itemView) {
            super(itemView);

            initChooser();

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    chooser.show();

                    return true;
                }
            });
        }

        private void setValue(Cursor cursor) {
            this.name.setText(cursor.getString(2));
            String type = cursor.getString(3);
            String phone = cursor.getString(1);

            if (type.equals("RECEIVED"))
            {
                this.name.setTextColor(Color.DKGRAY);
            }
            else
            {
                this.name.setTextColor(Color.BLUE);
            }

            addToResult(cursor, type, phone);
        }

        private void initChooser() {
            chooser = Utils.createDialog(context, R.layout.dialog_choosermenu, false);

            chooser.findViewById(R.id.choosermenu_1).setOnClickListener(this);
            chooser.findViewById(R.id.choosermenu_2).setOnClickListener(this);
            chooser.findViewById(R.id.choosermenu_3).setOnClickListener(this);
            chooser.findViewById(R.id.choosermenu_4).setOnClickListener(this);
            chooser.findViewById(R.id.choosermenu_5).setOnClickListener(this);
        }

        public void addToResult(Cursor cursor, String type, String phone) {

            result.removeAllViews();

            if (cursor != null)
            {
                double HS_THANG_DE = 1;
                double HS_THANG_BACANG = 1;
                ContactModel contact = contacts.get(phone);
                if (contact != null)
                {
                    HS_THANG_DE = contact.getDITDB_LANAN() / 1000;
                    HS_THANG_BACANG = contact.getBACANG_LANAN() / 1000;
                }

                int sumde = cursor.getInt(cursor.getColumnIndex("sumde"));
                int sumlo = cursor.getInt(cursor.getColumnIndex("sumlo"));
                int sumxien2 = cursor.getInt(cursor.getColumnIndex("sumxien2"));
                int sumxien3 = cursor.getInt(cursor.getColumnIndex("sumxien3"));
                int sumxien4 = cursor.getInt(cursor.getColumnIndex("sumxien4"));
                int sumbacang = cursor.getInt(cursor.getColumnIndex("sumbacang"));

                double actDe = cursor.getDouble(cursor.getColumnIndex("sumactde"));
                double actLo = cursor.getDouble(cursor.getColumnIndex("sumactlo"));
                double actXien2 = cursor.getDouble(cursor.getColumnIndex("sumactxien2"));
                double actXien3 = cursor.getDouble(cursor.getColumnIndex("sumactxien3"));
                double actXien4 = cursor.getDouble(cursor.getColumnIndex("sumactxien4"));
                double actBacang = cursor.getDouble(cursor.getColumnIndex("sumactbacang"));

                int winDe = cursor.getInt(cursor.getColumnIndex("sumwinde"));
                int winLo = cursor.getInt(cursor.getColumnIndex("sumwinlo"));
                int winXien2 = cursor.getInt(cursor.getColumnIndex("sumwinxien2"));
                int winXien3 = cursor.getInt(cursor.getColumnIndex("sumwinxien3"));
                int winXien4 = cursor.getInt(cursor.getColumnIndex("sumwinxien4"));
                int winBacang = cursor.getInt(cursor.getColumnIndex("sumwinbacang"));
                int winPointLo = cursor.getInt(cursor.getColumnIndex("sumpointwinlo"));

                if (type.equals("SENT"))
                {
                    this.result.add("de",
                            new double[]{
                                    sumde,
                                    HS_THANG_DE == 0 ? 0 : (winDe / HS_THANG_DE),
                                    -(winDe - actDe)
                            });

                    this.result.add("lo",
                            new double[]{
                                    sumlo,
                                    winPointLo,
                                    -(winLo - actLo)
                            });

                    this.result.add("xien",
                            new double[]{
                                    sumxien2 + sumxien3 + sumxien4,
                                    winXien2 + winXien3 + winXien4,
                                    -(winXien2 + winXien3 + winXien4 - actXien2 - actXien3 - actXien4)
                            });

                    this.result.add("bacang",
                            new double[]{
                                    sumbacang,
                                    HS_THANG_BACANG == 0 ? 0 : (winBacang / HS_THANG_BACANG),
                                    -(winBacang - actBacang)
                            });

                    double sum = -(winDe - actDe + winLo - actLo + winXien2 + winXien3 + winXien4 - actXien2 - actXien3 - actXien4 + winBacang - actBacang);
                    result.addBottom(sum);
                }
                else
                {
                    this.result.add("de",
                            new double[]{
                                    sumde,
                                    (winDe / HS_THANG_DE),
                                    (winDe - actDe)
                            });

                    this.result.add("lo",
                            new double[]{
                                    sumlo,
                                    winPointLo,
                                    (winLo - actLo)
                            });

                    this.result.add("xien",
                            new double[]{
                                    sumxien2 + sumxien3 + sumxien4,
                                    winXien2 + winXien3 + winXien4,
                                    (winXien2 + winXien3 + winXien4 - actXien2 - actXien3 - actXien4)
                            });

                    this.result.add("bacang",
                            new double[]{
                                    sumbacang,
                                    (winBacang / HS_THANG_BACANG),
                                    (winBacang - actBacang)
                            });

                    double sum = (winDe - actDe + winLo - actLo + winXien2 + winXien3 + winXien4 - actXien2 - actXien3 - actXien4 + winBacang - actBacang);
                    result.addBottom(sum);
                }
            }

        }

        @Override
        public void onClick(View v) {
            chooser.dismiss();

            getCursor().moveToPosition(getLayoutPosition());
            final String phone = getCursor().getString(1);
            String name = getCursor().getString(2);
            String type = getCursor().getString(3);

            switch (v.getId())
            {
                case R.id.choosermenu_1:
                    CongnoDBHelper.deleteCongNo(phone);
                    new TaskHelper.TaskCalculateResult(context, phone).execute();
                    break;

                case R.id.choosermenu_2:
                    String mes = getMessageChotTien(phone);
                    sendMessage(phone, mes);
                    break;

                case R.id.choosermenu_3:
                    String message = getMessageChotTien(phone);
                    copyMessage(message);
                    break;

                case R.id.choosermenu_4:
                    CommonTask.AsyncSingleThread.get().execute(context, new Runnable() {
                        @Override
                        public void run() {
                            DeleteController.deleteDataUserHasAnalyze(phone, Utils.getCurrentDate(), true);

                            CommonTask.AsyncSingleThread.get().executeOnMain(new Runnable() {
                                @Override
                                public void run() {
                                    requestUpdate();
                                    Toast.makeText(context, "Xóa thành công.", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                    break;

                case R.id.choosermenu_5:
                    CongnoDBHelper.deleteCongNo(phone);
                    reloadnewConfig(phone, TypeMessage.convert(type));
                    break;

                default:break;
            }
        }

        private void reloadnewConfig(String phone, TypeMessage typeMessage) {
            new TaskAnalyze.TaskReloadNewConfig(context, phone, typeMessage).execute();
        }

        private void sendMessage(final String phone, final String mes) {
            if (!TextUtils.isEmpty(mes))
            {
                //Send direct.
                Utils.showAlertTwoButton(context, "Tin nhắn gửi đến " + phone, mes, "Cancel", "Gửi", null, new Runnable() {
                    @Override
                    public void run() {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(phone, null, mes, null, null);
                        Toast.makeText(context, "Message Sent", Toast.LENGTH_LONG).show();
                    }
                });
            }
            else
            {
                Utils.showAlert(context, "Lỗi tin nhắn đến " + phone, "Chưa tính tiền ngày hôm nay.", "Ok", null);
            }
        }

        private void copyMessage(String mes) {
            if (!TextUtils.isEmpty(mes))
            {
                ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData data = ClipData.newPlainText(Keysaved.CLIPBOARD_BINGO, mes);
                manager.setPrimaryClip(data);
            }
        }

        private String getMessageChotTien(String phone) {

            double HS_THANG_DE = 1;
            double HS_THANG_BACANG = 1;
            boolean isCHu = false;
            ContactModel contact = contacts.get(phone);
            if (contact != null)
            {
                HS_THANG_DE = contact.getDITDB_LANAN() / 1000;
                HS_THANG_BACANG = contact.getBACANG_LANAN() / 1000;
                isCHu = contact.getType().equals("1");
            }
            else
            {
                return null;
            }

            String date = Utils.getCurrentDate();

            Cursor cursor = CongnoDBHelper.getCongNoByPhone(phone);

            try {

                if (cursor != null && cursor.moveToFirst())
                {
                    String mes = date.concat("\n");
                    int sum = 0;

                    while (!cursor.isAfterLast())
                    {
                        String type = cursor.getString(cursor.getColumnIndex(CongnoDatabase.DETAIL_TYPE));
                        double point = cursor.getDouble(cursor.getColumnIndex(CongnoDatabase.DETAIL_POINT));
                        int pointWin = cursor.getInt(cursor.getColumnIndex(CongnoDatabase.DETAIL_POINT_WIN));
                        if (type.equals(LotoType.de.name()))
                        {
                            pointWin = HS_THANG_DE == 0 ? 0 : (int) (pointWin / HS_THANG_DE);
                        }
                        else if (type.equals(LotoType.bacang.name()))
                        {
                            pointWin = HS_THANG_BACANG == 0 ? 0 : (int) (pointWin / HS_THANG_BACANG);
                        }

                        int money = cursor.getInt(cursor.getColumnIndex(CongnoDatabase.DETAIL_MONEY));
                        sum += money;

                        if (contact != null)
                        {
                            mes = mes.concat(type).concat(": ").concat(Utils.getDotNumberText(point)).concat("(").concat(Utils.getDotNumberText(pointWin))
                                    .concat(")").concat(" = ").concat(Utils.getDotNumberText(money)).concat("\n");
                        }

                        cursor.moveToNext();
                    }

                    sum = isCHu ? 0 - sum : sum;

                    mes = mes.concat("Tong ngay: ").concat(String.valueOf(sum));

                    int type = SettingsHelper.getChotSoDuConfig(((BaseActivity) context).getSaved());

                    if (type == SettingsHelper.CHOT_SO_DU_VA_CONG_NO_CU)
                    {
                        double congnocu = CongnoDBHelper.getNoCuTodate(phone, date);
                        congnocu = isCHu ? 0 - congnocu : congnocu;
                        mes = mes.concat("\nSo cu: ").concat(Utils.getDotNumberText(congnocu));
                        mes = mes.concat("\nTong: ").concat(Utils.getDotNumberText(sum + congnocu));
                    }

                    Log.d("CustomerAdapter", "MessageChot: " + mes);

                    return mes;
                }

            } finally {
                cursor.close();
            }

            return null;
        }
    }
}
