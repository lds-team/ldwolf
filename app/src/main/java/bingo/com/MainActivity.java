package bingo.com;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import bingo.com.async.CommonTask;
import bingo.com.base.BaseActivity;
import bingo.com.base.BaseFragment;
import bingo.com.helperdb.MessageDBHelper;
import bingo.com.screen.configscreen.SettingsFragment;
import bingo.com.screen.congno.CongNoFragment;
import bingo.com.screen.dbscreen.DatabaseFragment;
import bingo.com.screen.editmessagescreen.EditMessageFragment;
import bingo.com.screen.helperscreen.HelperFragment;
import bingo.com.screen.homescreen.HomeFragment;
import bingo.com.screen.modelscreen.ModelFragment;
import bingo.com.screen.numberreport.NumberReportFragment;
import bingo.com.service.MessageChangeService;
import bingo.com.utils.Constant;
import bingo.com.utils.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    private BaseFragment mTarget;

    private boolean hasoverOncreate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hasoverOncreate = true;

        ButterKnife.bind(this);

        showBackButton(0, true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        if (navigationView != null)
        {
            navigationView.setItemIconTintList(null);
            setupDrawerContent(navigationView);
        }

        if (Build.VERSION.SDK_INT >= 23)
        {
            requestInternet();
        }

        firstTransition();

        registerNotifyNumberClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTarget != null)
                {
                    if (!currentFragmentTag.equals(EditMessageFragment.TAG))
                    {
                        BaseFragment f = getExistFragment(EditMessageFragment.class, EditMessageFragment.TAG);
                        transition(f, EditMessageFragment.TAG);
                    }
                    else
                    {
                        transition(mTarget, mTarget.initTag());
                    }
                }
                else
                {
                    BaseFragment f = getExistFragment(EditMessageFragment.class, EditMessageFragment.TAG);
                    transition(f, EditMessageFragment.TAG);
                }
            }
        });

        //Start Observer Message thread change.
//        startService(new Intent(this, MessageChangeService.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    validateLicense();
                    requestSmsPermission();
                } else {
                    Toast.makeText(MainActivity.this, "Permission denied to read phone state", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            break;
            case 200: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestReadPhoneState();
                } else {
                    Toast.makeText(MainActivity.this, "BingoNew App need permisson to access internet.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            break;
            default:
                break;
        }
    }

    private void validateLicense() {
        new CommonTask.ThreadTask(new Runnable() {
            @Override
            public void run() {
                if (!checkDate())
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Hết hạn")
                                    .setMessage("Vui lòng liên hệ để gia hạn phần mềm.")
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    });
                }
            }
        }).start();
    }

    private boolean checkDate() {
        InputStream stream = null;
        StringBuilder builder = new StringBuilder();

        try {
            URL url = new URL(Constant.HOST + "/json.php?imei=" + getDeviceIMEI());
            stream = url.openConnection().getInputStream();
            InputStreamReader inputreader = new InputStreamReader(stream);
            // read data.
            BufferedReader reader = new BufferedReader(inputreader);

            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line + "\n");
            }
            reader.close();
            String json = builder.toString();

            return !json.contains("false") && json.contains("true");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public String getDeviceIMEI() {
        String deviceUniqueIdentifier = null;
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (null != tm) {
            deviceUniqueIdentifier = tm.getDeviceId();
        }
        if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length()) {
            deviceUniqueIdentifier = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceUniqueIdentifier;
    }

    @Override
    protected int getNotifyError() {
        Log.d("MainActivity", "updated NotifyError");
        return MessageDBHelper.getDb().getCountNoAnalyze();
    }

    private void requestSmsPermission() {
        requestReceiveSMS();
        requestReadSMS();
        requestReadExtenal();
        requestWriteExtenal();
    }

    private void requestReceiveSMS() {
        String permission = Manifest.permission.RECEIVE_SMS;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 1);
        }
    }

    private void requestReadExtenal() {
        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 1);
        }
    }

    private void requestReadContact() {
        String permission = Manifest.permission.READ_CONTACTS;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 1);
        }
    }

    private void requestReadSMS() {
        String permission = Manifest.permission.READ_SMS;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 1);
        }
    }

    private void requestWriteExtenal() {
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 1);
        }
    }

    private void requestSendSMS() {
        String permission = Manifest.permission.SEND_SMS;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 1);
        }
    }

    private void requestInternet() {
        String permission = Manifest.permission.INTERNET;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 200);
        } else {
            requestReadPhoneState();
        }
    }

    private void requestReadPhoneState() {
        String permission = Manifest.permission.READ_PHONE_STATE;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 100);
        } else {
            validateLicense();
            requestSmsPermission();
        }
    }

    private void firstTransition() {
        transition(new HomeFragment(), HomeFragment.TAG);
        setNotifyNumber(getNotifyError());
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        menu.clear();

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Toast.makeText(this, "Giữ BACK để thoát app.", Toast.LENGTH_LONG).show();
        }

        return false;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(final NavigationView navigationView) {

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull final MenuItem menuItem) {

                mTarget = null;

                menuItem.setChecked(true);

                getLoadingAnimation().show();

                mDrawerLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        switch (menuItem.getItemId()) {
                            case R.id.nav_home:

                                mTarget = getExistFragment(HomeFragment.class, HomeFragment.TAG);

                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                break;

                            case R.id.nav_editsms:

                                mTarget = getExistFragment(EditMessageFragment.class, EditMessageFragment.TAG);

                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                break;

                            case R.id.nav_numreport:

                                mTarget = getExistFragment(NumberReportFragment.class, NumberReportFragment.TAG);

                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                break;

                            case R.id.nav_moneyreport:

                                mTarget = getExistFragment(CongNoFragment.class, CongNoFragment.TAG);

                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                break;

                            case R.id.nav_settings:

                                mTarget = getExistFragment(SettingsFragment.class, SettingsFragment.TAG);

                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                break;

                            case R.id.nav_instructions:

                                mTarget = getExistFragment(HelperFragment.class, HelperFragment.TAG);

                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                break;

                            case R.id.nav_template:

                                mTarget = getExistFragment(ModelFragment.class, ModelFragment.TAG);

                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                break;

                            case R.id.nav_database:

                                mTarget = getExistFragment(DatabaseFragment.class, DatabaseFragment.TAG);

                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                break;

                            default:
                                dismissLoading();
                                mTarget = null;
                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                break;

                        }

                    }
                }, 10);


                return true;
            }
        });

        //Listen When Drawer Close to Execute Tasks.
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                executeTrans();
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
    }

    private void executeTrans() {
        if (mTarget != null) {
            transition(mTarget, mTarget.initTag());
        }

        dismissLoading();//TODO remove

//        mTarget = null;
    }

    /*@Override
    protected void onNewIntent(Intent intent) {
        boolean hasExtras = intent != null && intent.hasExtra(Constant.NOTIFY_BUNDLE);

        if (hasExtras)
        {
            BaseFragment f = getExistFragment(EditMessageFragment.class, EditMessageFragment.TAG);
            transition(f, EditMessageFragment.TAG);
        }
        else
        {
            BaseFragment f = getExistFragment(HomeFragment.class, HomeFragment.TAG);
            transition(f, HomeFragment.TAG);
        }
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MainActivity", "onResume");

        if (!hasoverOncreate)
        {
            validatePermisson();
        }

        hasoverOncreate = false;
    }

    private void validatePermisson() {
        String permission = Manifest.permission.INTERNET;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            finish();
        }

        String permission2 = Manifest.permission.READ_PHONE_STATE;
        int grant2 = ContextCompat.checkSelfPermission(this, permission2);
        if (grant2 != PackageManager.PERMISSION_GRANTED) {
            finish();
        }

//        validateLicense();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
