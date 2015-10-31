package com.wogebi.android.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import com.dougfii.android.core.base.BaseActivity;
import com.dougfii.android.core.log.L;
import com.dougfii.android.core.utils.ServiceUtils;
import com.wogebi.android.AppApplication;
import com.wogebi.android.R;
import com.wogebi.android.service.WalkwayService;
import com.wogebi.android.view.Topbar;

/**
 * @deprecated 实现的功能已经移到MainActivity中, 仅作为参考保留
 */
public class WalkwayActivity extends BaseActivity<AppApplication> {
    private static final String TAG = "WalkwayActivity";

    private MapView mapView = null;
    private BaiduMap map;

    private Topbar topbar;
    private Button signed;

    private double lat = 0;
    private double lng = 0;

    private Messenger messengerService;//Messenger Service引用
    private final Messenger messenger = new Messenger(new LocationHandler()); //客户端与服务端的交互，接收Service的信息
    private boolean isBind = false;
    private LocationServiceConnection connection = new LocationServiceConnection();

    //客户端Handler，用来接收服务端信息
    @SuppressLint("HandlerLeak")
    private class LocationHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WalkwayService.MSG_SEDN_COMMOND://交互数据类型
                    //WalkwayEntity entity = new WalkwayEntity();
                    //entity = (WalkwayEntity) bundle.getParcelable("data");

                    Bundle bundle = msg.getData();
                    int codex = bundle.getInt("code");
                    double latitudex = bundle.getDouble("latitude");
                    double longitudex = bundle.getDouble("longitude");
                    float radiusx = bundle.getFloat("radius");
                    String timex = bundle.getString("time");

                    lat = latitudex;
                    lng = longitudex;
                    loc();

                    L.i(TAG, "********************************* code:" + codex);

                    break;

                default:
                    break;
            }
        }
    }

    private class LocationServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //Service连接建立时将调用该方法
            //返回IBinder接口以便我们可以跟Service关联
            //可通过IPC接口来交流
            messengerService = new Messenger(service);//Service的Messenger
            isBind = true;

            try {
                //注册
                Message message = Message.obtain(null, WalkwayService.MSG_CLIENT_REGISTER);
                message.replyTo = messenger;
                messengerService.send(message);

                //例子
                message = Message.obtain(null, WalkwayService.MSG_SEDN_COMMOND, 11111, 0);
                messengerService.send(message);
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even
                // do anything with it; we can count on soon being
                // disconnected (and then reconnected if it can be restarted)
                // so there is no need to do anything here.
            }

            //showToast("Location Service Connected!");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // 当进程崩溃时将被调用，因为运行在同一程序，如果是崩溃将所以永远不会发生
            // 只有异常销毁时才会被调用
            messengerService = null;
            isBind = false;

            //showToast("Location Service Disconnected!");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        bind();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mapView.onPause();
        unbind();
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_walkway);

        mapView = (MapView) findViewById(R.id.walkway_map);
        map = mapView.getMap();
        map.setMapType(BaiduMap.MAP_TYPE_NORMAL);//普通地图

        topbar = (Topbar) findViewById(R.id.walkway_topbar);
        topbar.setLogo(true);
        topbar.setLine(true);
        topbar.setTitle(getString(R.string.title_walkway));

        signed = (Button) findViewById(R.id.walkway_signed);
    }

    @Override
    protected void initEvents() {
        signed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lat > 0 && lng > 0) {
                    loc();
                    showToast("打卡签到成功");
                } else {
                    showToast("无法获得当前位置信息");
                }
            }
        });
    }

    private void start() {
        //如果服务没有开启，开启Service然后绑定
        boolean run = ServiceUtils.isServiceRunning(this, WalkwayService.ACTION_WALKWAY_SERVICE);
        if (!run) {
            Intent intent = new Intent(WalkwayService.ACTION_WALKWAY_SERVICE).setPackage(getPackageName());
            intent.putExtra(WalkwayService.START_MODE, WalkwayService.START_MANUAL);
            startService(intent);
        }
    }

    private void stop() {
        boolean run = ServiceUtils.isServiceRunning(this, WalkwayService.ACTION_WALKWAY_SERVICE);
        if (run) {
            Intent intent = new Intent(WalkwayService.ACTION_WALKWAY_SERVICE).setPackage(getPackageName());
            stopService(intent);
        }
    }

    private void bind() {
        Intent intent = new Intent(WalkwayService.ACTION_WALKWAY_SERVICE).setPackage(getPackageName());
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        isBind = true;
    }

    private void unbind() {
        if (isBind) {
            if (messengerService != null) {
                try {
                    //发送解绑消息
                    Message message = Message.obtain(null, WalkwayService.MSG_CLIENT_UNREGISTER);
                    message.replyTo = messenger;
                    messengerService.send(message);
                } catch (RemoteException e) {
                    //
                }
            }

            unbindService(connection);
            isBind = false;
        }
    }

    private void loc() {
        if (lat > 0 && lng > 0) {
            LatLng point = new LatLng(lat, lng);//定义Maker坐标点
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.loc);//构建Marker图标
            OverlayOptions options = new MarkerOptions().position(point).icon(bitmap);//构建MarkerOption，用于在地图上添加Marker
            map.clear();//清除先前所有overlay
            map.addOverlay(options);

            MapStatus status = new MapStatus.Builder().target(point).zoom(19).build();
            MapStatusUpdate update = MapStatusUpdateFactory.newMapStatus(status);
            map.setMapStatus(update);//改变地图状态
        } else {
            //showToast("无法获得当前位置信息");
        }
    }
}