package com.wogebi.android.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.dougfii.android.core.base.BaseService;
import com.dougfii.android.core.entity.ResultEntity;
import com.dougfii.android.core.entity.SimpleEntity;
import com.dougfii.android.core.log.L;
import com.dougfii.android.core.utils.DateTimeUtils;
import com.dougfii.android.core.utils.HardwareUtils;
import com.dougfii.android.core.utils.HttpUtils;
import com.dougfii.android.core.utils.NotificationUtils;
import com.dougfii.android.core.utils.ServiceUtils;
import com.dougfii.android.core.utils.Utils;
import com.wogebi.android.AppApplication;
import com.wogebi.android.R;
import com.wogebi.android.activity.MainActivity;
import com.wogebi.android.db.WalkwayDBHelper;
import com.wogebi.android.entity.ResolveEntity;
import com.wogebi.android.entity.WalkwayEntity;
import com.wogebi.android.listener.LocationListener;
import com.wogebi.android.listener.WalkwayListener;
import com.wogebi.android.model.Constants;

public class WalkwayService extends BaseService<AppApplication> {
    public static final String TAG = "WalkwayService";

    public static final String PROCESS_NAME_WALKWAY = "com.wogebi.com.wogebi.android:walkway";
    public static final String PROCESS_NAME_LOCATION = "com.wogebi.com.wogebi.android:location";


    public static final String ACTION_WALKWAY_SERVICE = "com.wogebi.com.wogebi.android.service.WalkwayService";

    private static final int NOTIFICATION_ID = 1; // 如果id设置为0,会导致不能设置为前台service

    // 0~1000 留给百度定位使用
    public static final int MSG_CLIENT_REGISTER = 1106; //客户端绑定Service
    public static final int MSG_CLIENT_UNREGISTER = 1107; //客户端解绑Service
    public static final int MSG_SEDN_COMMOND = 1108; //服务发送指令，可以在客户端和服务直接交流
    public static final int MSG_SEND_CLIENT = 1003; //发送到客户端
    public static final int MSG_SEND_SERVICE = 1001; //发送给本地服务
    public static final int MSG_LOCATOR_CHECK = 1000; //第一次检查网络完毕
    public static final int MSG_SAVE_DATABASE = 1002; //保存到数据库

    public static final String START_MODE = "StartMode"; //服务的启动方式，开机自启动/手动启动
    public static final int START_UNKNOWN = 0;
    public static final int START_MANUAL = 1;
    public static final int START_BOOT = 2;
    public static final int START_ALARM = 3;
    public static final int START_RESTART = 4;
    public static final String EXIT_MODE = "ExitMode";
    public static final int EXIT_UNKNOWN = 0;
    public static final int EXIT_MANUAL = 1;
    public static final int EXIT_SHUTDOWN = 2;
    private int start = START_UNKNOWN;

    private static final int DELAY_LOCATION = 5 * 1000; //定位时间间隔
    private static final int DELAY_BOOT = 60 * 1000;//开机一分钟后开始检测网络
    private static final int DELAY_PUSH = 60 * 60 * 1000;//推送数据到服务器的周期

    private static final String URL = Constants.URL_SERVER + Constants.MODEL_WALKWAY_ADD;
    private ResultEntity<SimpleEntity> ret;

    private PowerManager.WakeLock wake;

    private LocationClient location;
    private WalkwayListener listener;
    private NotificationUtils notification;

    private boolean isLocator = false;
    private int num = 0;
    private int max = 3;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LocationListener.MSG_LOCATION_SERVER_ERROR:
                case LocationListener.MSG_LOCATION_NETWORK_EXCEPTION:
                case LocationListener.MSG_LOCATION_CRITERIA_EXCEPTION:
                case LocationListener.MSG_LOCATION_OFFILINE_FIAL:
                case LocationListener.MSG_LOCATION_NETWORK_CONNECT_FAIL:
                    notification.sendNetworkNotification(R.mipmap.logo, getString(R.string.notification_network_title), getString(R.string.notification_network_title), getString(R.string.notification_network_content));
                    break;

                case MSG_CLIENT_REGISTER: //绑定客户端
                    clients.add(msg.replyTo);
                    break;

                case MSG_CLIENT_UNREGISTER://解绑客户端
                    clients.remove(msg.replyTo);
                    break;

                case MSG_SEDN_COMMOND://客户端的通信数据
                    int last = msg.arg1; //保留最后一次跟服务器连接的客户端的标识
                    for (int i = clients.size() - 1; i >= 0; i--) {
                        try {
                            clients.get(i).send(Message.obtain(null, MSG_SEDN_COMMOND, last, 0));//发送消息给客户端
                        } catch (RemoteException e) {
                            clients.remove(i);//远程客户端出错，从列表中移除，遍历列表以保证内部循环安全运行
                        }
                    }
                    break;

                case MSG_SEND_SERVICE://发送给本地服务
                    update(msg);
                    break;

                case MSG_LOCATOR_CHECK://检查网络状态
                    checkLocatorTimer();//定时检查网络状态
                    break;

                default:
                    break;
            }
        }
    };

    private final Messenger messenger = new Messenger(handler);
    private ArrayList<Messenger> clients = new ArrayList<>();//保存所有跟服务器连接的客户端---每一个客户端绑定就多了一个

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        L.i(TAG, "onBind -----");
        return messenger.getBinder(); //返回值可用来进行Activity与Service之间的交互
    }

    @Override
    public void onCreate() {
        notification = new NotificationUtils(this);

        L.i(TAG, "onCreate - 初始化定位服务");
        initLocationService();// 初始化定位服务，配置相应参数

        //保持cpu一直运行，不管屏幕是否黑屏
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wake = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "CPUKeepRunning");
        wake.acquire();

        //双服务互听，避免被一键清理杀掉
        keep();
    }

    @Override
    public void onLowMemory() {
        L.i(TAG, "onLowMemory");
        keep();
    }

    @Override
    public void onTrimMemory(int level) {
        L.i(TAG, "onTrimMemory");
        keep();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //设置为前台进程，尽量避免被系统干掉
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        Notification foreground = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.logo)
                .setTicker(this.getString(R.string.notification_foreground_content))
                .setWhen(System.currentTimeMillis())
                .setContentTitle(this.getString(R.string.notification_foreground_title))
                .setContentText(this.getString(R.string.notification_foreground_content))
                .setContentIntent(pendingIntent)
                .build();
        startForeground(NOTIFICATION_ID, foreground);

        L.i(TAG, "onStartCommand begin =====");

        if (intent != null) {
            start = intent.getIntExtra(START_MODE, START_UNKNOWN);

            L.i(TAG, "onStartCommand - " + START_MODE + ":" + start);

            if (start == START_MANUAL || start == START_ALARM) {
                //手动启动
//                boolean run = ServiceUtils.isServiceRunning(this, "com.baidu.location.f");//判断服务是否已开启
//                if (!run || (location != null && !location.isStarted()))
//                {
//                    L.i(TAG, "onStartCommand - 开启定位服务");
//                    location.start();//服务没启动,开启定位服务
//                }

                boolean run = ServiceUtils.isProcessRunning(this, WalkwayService.PROCESS_NAME_LOCATION);
                if (!run || (location != null && !location.isStarted())) {
                    L.i(TAG, "onStartCommand - 开启定位服务");
                    location.start();//服务没启动,开启定位服务
                }
            } else {
                L.i(TAG, "onStartCommand - 开机自动启动服务");
                //开机自动启动

                // 关闭手机，再次开启手机。这种情况下，StartMode的值获取不到。
                // 关机重启，这种情况下，StartMode的值可以拿到。
                // if (start == Constant.BOOT_START_SERVICE) {
                num++;

                // 第一次，1分钟后检测网络---这个线程只会执行一次
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //第一次检测网络
                        checkLocator();
                        handler.sendEmptyMessage(MSG_LOCATOR_CHECK);
                    }
                }, DELAY_BOOT);
            }
        }

        send();

        L.i(TAG, "onStartCommand end ==========");

        return Service.START_STICKY;
        //return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        L.i(TAG, "onDestory ==========");

        if (location != null && location.isStarted()) {
            location.stop();
            if (listener != null) {
                location.unRegisterLocationListener(listener);
            }
        }

        SharedPreferences sp = getSharedPreferences(TAG, Context.MODE_PRIVATE);
        int exit = sp.getInt(EXIT_MODE, EXIT_UNKNOWN);

        if (exit == EXIT_SHUTDOWN) {
            sp.edit().putInt(EXIT_MODE, EXIT_MANUAL).commit();
            System.exit(0);
            return;
        }

        if (wake != null && wake.isHeld()) {
            wake.release();
            wake = null;
        }

        //销毁时重新启动Service
        Intent intent = new Intent(ACTION_WALKWAY_SERVICE).setPackage(getPackageName());
        intent.putExtra(START_MODE, start);
        startService(intent);
    }

    private void initLocationService() {
        location = new LocationClient(this);
        listener = new WalkwayListener(this, handler);
        location.registerLocationListener(listener);

        setLocationOption();
    }

    private void setLocationOption() {
        //高精度定位模式：这种定位模式下，会同时使用网络定位和GPS定位，优先返回最高精度的定位结果；
        //低功耗定位模式：这种定位模式下，不会使用GPS，只会使用网络定位（Wi-Fi和基站定位）；
        //仅用设备定位模式：这种定位模式下，不需要连接网络，只使用GPS进行定位，这种模式下不支持室内环境的定位。

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(DELAY_LOCATION);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        //option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        //option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        //option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        //option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        //option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        //option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        //option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        option.setProdName(getString(R.string.loaction_prod_name));

        location.setLocOption(option);
    }

    //检测定位是否可用
    private void checkLocator() {
        if (HardwareUtils.isLocatorAvailable(this)) {
            isLocator = true;
            location.start();
        } else {
            notification.sendLocatorNotification(R.mipmap.logo, getString(R.string.notification_location_title), getString(R.string.notification_location_title), getString(R.string.notification_location_content));
        }
    }

    //定时检测网络是否可用
    private void checkLocatorTimer() {
        // 第一检测网络，直接过了。（已打开） ---检查定位服务
        boolean run = ServiceUtils.isProcessRunning(this, PROCESS_NAME_LOCATION);

        if (!isLocator || !run) {
            // 打开定时器，每个一段时间提醒一次
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    num++;

                    checkLocator();

                    boolean run = ServiceUtils.isProcessRunning(getApplicationContext(), PROCESS_NAME_LOCATION);
                    if (isLocator && run) {
                        notification.cancel(NotificationUtils.LOCATOR_UNAVAILABLE);
                        timer.cancel();
                    } else {
                        if (num == max) {
                            // 检查网络，提醒了用户三次依然未开，退出应用。
                            notification.cancel(NotificationUtils.LOCATOR_UNAVAILABLE);
                            timer.cancel();
                            // System.gc();
                            System.exit(0);
                        }
                    }
                }

            }, 0, DELAY_LOCATION);
        }
    }

    //更新数据，发送给客户端
    private void update(Message msg) {
        msg.what = MSG_SEDN_COMMOND;// 改变消息类型
        // 将MSG发送给已经绑定的客户端
        for (int i = clients.size() - 1; i >= 0; i--) {
            try {
                clients.get(i).send(msg);//将消息发送给客户端
            } catch (RemoteException e) {
                clients.remove(i);//远程客户端出错，从列表中移除，遍历列表以保证内部循环安全运行
            }
        }
        // L.i("定位信息:"+myLocation);
        // txt_LocationView.setText(myLocation);
    }

    //定时推送数据至服务器
    private void send() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                int hour = DateTimeUtils.getCurrentHour();
                if (hour >= 8 && hour <= 18) {
                    push();
                }
            }
        }, DELAY_LOCATION, DELAY_PUSH);
    }

    //推送数据至服务器
    private void push() {
        L.i(TAG, "push ******************** " + DateTimeUtils.getCurrentDateTime());

        SharedPreferences preferences = getSharedPreferences("gerp_login", MODE_PRIVATE);
        final int uid = preferences.getInt("uid", 0);
        if (uid <= 0) {
            L.i(TAG, "push error: uid not exist");
            return;
        }

        if (!HardwareUtils.isNetworkAvailable(getApplicationContext())) {
            L.i(TAG, "push error: network not connect");
            return;
        }

        L.i(TAG, "push process ********** " + DateTimeUtils.getCurrentDateTime());

        addTask(new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        try {
                            int last = 0;
                            StringBuilder sb = new StringBuilder();

                            //重组发送数据格式
                            Map<String, String> locs = new LinkedHashMap<>();
                            List<WalkwayEntity> entities = new ArrayList<>();
                            entities.addAll(WalkwayDBHelper.query(getApplicationContext(), "id ASC"));
                            if (entities.size() > 0) {
                                for (WalkwayEntity entity : entities) {
                                    last = entity.getId();
                                    if (locs.containsKey(entity.getDate())) {
                                        locs.put(entity.getDate(), locs.get(entity.getDate()) + entity.toString() + "|");
                                    } else {
                                        locs.put(entity.getDate(), entity.toString() + "|");
                                    }
                                }

                                if (locs.size() > 0) {
                                    for (Map.Entry<String, String> entry : locs.entrySet()) {
                                        String key = entry.getKey();
                                        String val = entry.getValue();
                                        sb.append(key).append("*").append(val).append("!");
                                    }
                                }
                            }

                            //提交数据
                            String url = URL;
                            L.i(TAG, url);

                            Map<String, String> pl = new HashMap<>();
                            pl.put("uid", Utils.toString(uid));
                            pl.put("last", Utils.toString(last));
                            pl.put("locs", sb.toString());

                            HttpUtils http = new HttpUtils();
                            String json = http.post(url, pl);
                            L.i(TAG, json);

                            ret = (new ResolveEntity()).doWalkwayAdd(json);
                            if (ret != null && ret.getCode() == 1) {
                                int lastid = Utils.toInteger(ret.getMsg());
                                if (lastid > 0 && lastid == last) {
                                    L.i(TAG, "success push data, last id: " + lastid);
                                    WalkwayDBHelper.delete(getApplicationContext(), lastid);
                                } else {
                                    L.i(TAG, "last id return error");
                                }
                            } else if (ret != null && ret.getCode() == 0) {
                                L.i(TAG, ret.getMsg());
                            } else {
                                L.i(TAG, getString(R.string.submit_error));
                            }
                        } catch (Exception ex) {
                            L.e(TAG, ex.getCause());
                        }

                        return null;
                    }
                }
        );
    }

    private IAliveService core = new IAliveService.Stub() {
        @Override
        public void alive() throws RemoteException {
            Intent intent = new Intent(getBaseContext(), CoreService.class);
            getBaseContext().startService(intent);
        }
    };

    private void keep() {
        boolean run = ServiceUtils.isProcessRunning(this, CoreService.PROCESS_NAME_CORE);
        if (!run) {
            try {
                L.i(TAG, "重新启动 CoreService");
                core.alive();
            } catch (RemoteException e) {
                //
            }
        }
    }
}