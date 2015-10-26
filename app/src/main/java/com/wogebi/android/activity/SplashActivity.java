package com.wogebi.android.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import com.wogebi.android.BaseActivity;
import com.wogebi.android.R;
import com.wogebi.android.dialog.BaseDialog;
import com.wogebi.android.dialog.DownloadDialog;
import com.wogebi.android.entity.ResolveEntity;
import com.wogebi.android.entity.ResultEntity;
import com.wogebi.android.entity.UserEntity;
import com.wogebi.android.log.L;
import com.wogebi.android.model.Constants;
import com.wogebi.android.model.Model;
import com.wogebi.android.update.UpdateManager;
import com.wogebi.android.utils.HttpUtils;
import com.wogebi.android.utils.HardwareUtils;
import com.wogebi.android.utils.Utils;

public class SplashActivity extends BaseActivity
{
    private static final String TAG = "SplashActivity";

    private static final String URL = Constants.URL_SERVER + Constants.MODEL_LOGIN;
    private ResultEntity<UserEntity> ret;

    private View loading;
    private ProgressBar loadingBar;
    private TextView loadingMessage;

    private LinearLayout login;
    private TextView username;
    private TextView password;
    private CheckBox save;
    private CheckBox auto;
    private Button button;

    private String user;
    private String pass;
    private UpdateManager updater;

    private BaseDialog updateDialog;
    private DownloadDialog downloadDialog;

    private static final int MSG_GPS_AVAILABLE = 1;
    private static final int MSG_GPS_UNAVAILABLE = 2;
    private static final int MSG_NETWORK_AVAILABLE = 3;
    private static final int MSG_NETWORK_UNAVAILABLE = 4;
    private static final int MSG_DOWNLOAD_BEGIN = 5;
    private static final int MSG_DOWNLOAD_COMPLETED = 6;
    private static final int MSG_DOWNLOAD_FAILED = 7;
    private static final int MSG_DOWNLOAD_CANCEL = 8;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_GPS_AVAILABLE:
                    gpsAvailable();
                    break;
                case MSG_GPS_UNAVAILABLE:
                    gpsUnavailable();
                    break;
                case MSG_NETWORK_AVAILABLE:
                    networkAvailable();
                    break;
                case MSG_NETWORK_UNAVAILABLE:
                    networkUnavailable();
                    break;
                case MSG_DOWNLOAD_BEGIN:
                    downloadBegin();
                    break;
                case MSG_DOWNLOAD_COMPLETED:
                    downloadCompleted();
                    break;
                case MSG_DOWNLOAD_FAILED:
                    downloadFailed();
                    break;
                case MSG_DOWNLOAD_CANCEL:
                    downloadCancel();
                    break;
                default:
                    break;
            }
        }
    };

    private UpdateManager.UpdateCallback callback = new UpdateManager.UpdateCallback()
    {
        @Override
        public void onStateChanged(int id)
        {
            switch (id)
            {
                case UpdateManager.MSG_XML_DOWNLOAD_COMPLETED:
                    //showToast("XML下载成功");
                    break;
                case UpdateManager.MSG_XML_DOWNLOAD_FAILED:
                    //showToast("XML下载失败");
                    break;
                case UpdateManager.MSG_UPDATE_PARSE:
                    //showToast("更新分析中");
                    break;
                case UpdateManager.MSG_UPDATE_FORCE:
                    updateForce();
                    break;
                case UpdateManager.MSG_UPDATE_ENABLE:
                    updateEnable();
                    break;
                case UpdateManager.MSG_UPDATE_DISABLE:
                    login();
                    break;
                case UpdateManager.MSG_UPDATE_FAILED:
                    showToast("更新检测失败");
                    break;
                case UpdateManager.MSG_FILE_COMPLETED:
                    handler.sendEmptyMessage(MSG_DOWNLOAD_COMPLETED);
                    break;
                case UpdateManager.MSG_FILE_CANCEL:
                    handler.sendEmptyMessage(MSG_DOWNLOAD_CANCEL);
                    break;
                case UpdateManager.MSG_FILE_SDCARD_UNAVAILABLE:
                case UpdateManager.MSG_FILE_FAILED:
                    handler.sendEmptyMessage(MSG_DOWNLOAD_FAILED);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onProgressChanged(int size, int progress)
        {
            if (downloadDialog != null)
            {
                if (size > 0 && progress == 0)
                {
                    downloadDialog.setMax(size);
                }
                downloadDialog.setProgress(progress);
            }
        }
    };

    @Override
    protected void initViews()
    {
        setContentView(R.layout.activity_splash);

        loading = findViewById(R.id.splash_loading);
        loadingBar = (ProgressBar) loading.findViewById(R.id.loading_bar);
        loadingMessage = (TextView) loading.findViewById(R.id.loading_message);

        login = (LinearLayout) findViewById(R.id.splash_login);
        username = (TextView) findViewById(R.id.splash_login_username);
        password = (TextView) findViewById(R.id.splash_login_password);
        save = (CheckBox) findViewById(R.id.splash_login_save);
        auto = (CheckBox) findViewById(R.id.splash_login_auto);
        button = (Button) findViewById(R.id.splash_login_button);

        updater = new UpdateManager(this, Constants.UPDATE_XML, Constants.DIR_TEMP, Constants.UPDATE_FILENAME, callback);

        welcome();
    }

    @Override
    protected void initEvents()
    {
        MyOnClickListener my = new MyOnClickListener();
        button.setOnClickListener(my);
    }

    private class MyOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.splash_login_button:
                    doLogin();
                    break;
            }
        }
    }

    private void welcome()
    {
        loading.setVisibility(View.VISIBLE);
        loadingMessage.setText("正在启动应用程序检查");

        gps();
    }

    private void gps()
    {
        //GpsUtils.openGPS(getApplicationContext());
        loadingMessage.setText("正在检查GPS");

        final Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                if (HardwareUtils.isGpsAvailable(getApplicationContext()))
                {
                    timer.cancel();
                    handler.sendEmptyMessage(MSG_GPS_AVAILABLE);
                }
                else
                {
                    handler.sendEmptyMessage(MSG_GPS_UNAVAILABLE);
                }

            }
        }, 500, 3000);
    }

    private void gpsAvailable()
    {
        network();
    }

    private void gpsUnavailable()
    {
        loadingMessage.setText(R.string.no_gps);
    }

    private void network()
    {
        loadingMessage.setText("正在检查网络");

        final Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                if (HardwareUtils.isNetworkAvailable(getApplicationContext()))
                {
                    timer.cancel();
                    handler.sendEmptyMessage(MSG_NETWORK_AVAILABLE);
                }
                else
                {
                    handler.sendEmptyMessage(MSG_NETWORK_UNAVAILABLE);
                }

            }
        }, 500, 3000);
    }

    private void networkAvailable()
    {
        update();
    }

    private void networkUnavailable()
    {
        loadingMessage.setText(R.string.no_network);
    }

    private void update()
    {
        loadingMessage.setText("正在检查更新");
        updater.start();
    }

    private void updateForce()
    {
        handler.sendEmptyMessage(MSG_DOWNLOAD_BEGIN);
    }

    private void updateEnable()
    {
        updateDialog = showDialog("版本更新", "发现新版本更新,是否立即更新?", "确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                handler.sendEmptyMessage(MSG_DOWNLOAD_BEGIN);
                updateDialog.dismiss();
            }
        }, "取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                updateDialog.dismiss();
                login();
            }
        });
    }

    private void downloadBegin()
    {
        loadingMessage.setText("正在下载更新");
        downloadDialog = DownloadDialog.getDialog(this, "下载更新", "更新文件正在下载中...", "取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                updater.downloadCancel();
            }
        });
        downloadDialog.show();
        updater.download();
    }

    private void downloadCompleted()
    {
        downloadDialog.dismiss();
        loadingMessage.setText("下载更新完成,正在安装...");

        System.exit(0);
    }

    private void downloadCancel()
    {
        downloadDialog.dismiss();
        loadingMessage.setText("取消下载更新");

        try
        {
            Thread.sleep(1000);
        }
        catch (Exception e)
        {
            //
        }

        welcome();
    }

    private void downloadFailed()
    {
        downloadDialog.dismiss();
        loadingMessage.setText("下载更新失败");

        try
        {
            Thread.sleep(1000);
        }
        catch (Exception e)
        {
            //
        }

        welcome();
    }

    private void login()
    {
        loadingMessage.setText("正在处理登录");

        SharedPreferences preferences = getSharedPreferences("gerp_login", MODE_PRIVATE);
        user = preferences.getString("username", "");
        pass = preferences.getString("password", "");

        boolean save = preferences.getBoolean("save", false);
        boolean auto = preferences.getBoolean("auto", false);
        if (save)
        {
            username.setText(user);
            password.setText(pass);
            this.save.setChecked(true);
        }

        if (auto)
        {
            this.auto.setChecked(true);
            loadingMessage.setText("正在自动登录");
            doLogin();
        }
        else
        {
            loading.setVisibility(View.GONE);
            login.setVisibility(View.VISIBLE);
        }
    }

    private void doLogin()
    {
        if (!HardwareUtils.isNetworkAvailable(this))
        {
            loading.setVisibility(View.GONE);
            login.setVisibility(View.VISIBLE);
            showToast(R.string.no_network);
            return;
        }

        user = username.getText().toString();
        pass = password.getText().toString();

        if (!Utils.isUsername(user))
        {
            showToast("用户错误");
            return;
        }

        if (!Utils.isPassword(pass))
        {
            showToast("密码错误");
            return;
        }

        addTask(new AsyncTask<Void, Void, Boolean>()
        {

            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                showLoading(getString(R.string.loading_load));
            }

            @Override
            protected Boolean doInBackground(Void... params)
            {
                try
                {
                    String url = URL + "&username=" + user + "&password=" + pass;
                    L.i(TAG, url);

                    HttpUtils http = new HttpUtils();
                    String json = http.get(url);
                    L.i(TAG, json);

                    ret = (new ResolveEntity()).getUser(json);
                    if (ret != null && ret.getData() != null)
                    {
                        return true;
                    }
                }
                catch (Exception ex)
                {
                    //
                }

                return false;
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                super.onPostExecute(result);
                dismissLoading();

                if (result && ret.getCode() == 1 && ret.getData() != null)
                {
                    Model.My = ret.getData();

                    SharedPreferences preferences = getSharedPreferences("gerp_login", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("username", username.getText().toString());
                    editor.putString("password", password.getText().toString());
                    editor.putBoolean("save", save.isChecked());
                    editor.putBoolean("auto", auto.isChecked());
                    editor.putInt("uid", Model.My.getId());
                    editor.commit();


                    //setResult(RESULT_OK);
                    startActivity(MainActivity.class);
                    finish();
                }
                else if (result && ret.getCode() == 0)
                {
                    loading.setVisibility(View.GONE);
                    login.setVisibility(View.VISIBLE);
                    showToast(ret.getMsg());
                }
                else
                {
                    loading.setVisibility(View.GONE);
                    login.setVisibility(View.VISIBLE);
                    showToast(R.string.submit_error);
                }
            }
        });
    }

    private void sleep(int second)
    {
        try
        {
            Thread.sleep(second);
        }
        catch (Exception e)
        {
            //
        }
    }
}