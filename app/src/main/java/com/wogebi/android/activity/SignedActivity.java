package com.wogebi.android.activity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
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
import com.dougfii.android.core.entity.ResultEntity;
import com.dougfii.android.core.entity.SimpleEntity;
import com.dougfii.android.core.log.L;
import com.dougfii.android.core.utils.DateTimeUtils;
import com.dougfii.android.core.utils.HardwareUtils;
import com.dougfii.android.core.utils.HttpUtils;
import com.dougfii.android.core.utils.ServiceUtils;
import com.wogebi.android.AppApplication;
import com.wogebi.android.R;
import com.wogebi.android.entity.ResolveEntity;
import com.wogebi.android.listener.LocationListener;
import com.wogebi.android.listener.SignedListener;
import com.wogebi.android.model.Constants;
import com.wogebi.android.model.Model;
import com.wogebi.android.service.WalkwayService;
import com.wogebi.android.view.Topbar;

public class SignedActivity extends BaseActivity<AppApplication> {
    private static final String TAG = "SignedActivity";

    private static final String URL = Constants.URL_SERVER + Constants.MODEL_SIGNED_ADD;
    private ResultEntity<SimpleEntity> ret;

    private static final int DELAY_TIME = 0;//定位时间间隔, 0:仅一次

    private MapView mapView = null;
    private BaiduMap map;

    private Topbar topbar;
    private Button sign;
    private Button loc;
    private Button walkway;

    private int code = 0;
    private double lat = 0;
    private double lng = 0;

    private LocationClient location;
    private SignedListener listener;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WalkwayService.MSG_SEND_CLIENT:

                    Bundle bundle = msg.getData();
                    code = bundle.getInt("code");
                    lat = bundle.getDouble("lat");
                    lng = bundle.getDouble("lng");

                    loc();

                    break;

                case LocationListener.MSG_LOCATION_SERVER_ERROR:
                case LocationListener.MSG_LOCATION_NETWORK_EXCEPTION:
                case LocationListener.MSG_LOCATION_CRITERIA_EXCEPTION:
                case LocationListener.MSG_LOCATION_OFFILINE_FIAL:
                case LocationListener.MSG_LOCATION_NETWORK_CONNECT_FAIL:

                    showToast("网络错误，无法定位");

                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_signed);

        mapView = (MapView) findViewById(R.id.signed_map);
        map = mapView.getMap();
        map.setMapType(BaiduMap.MAP_TYPE_NORMAL);//普通地图

        topbar = (Topbar) findViewById(R.id.signed_topbar);
        topbar.setLogo(true);
        topbar.setLine(true);
        topbar.setTitle(getString(R.string.title_signed));

        sign = (Button) findViewById(R.id.signed_sign);
        loc = (Button) findViewById(R.id.signed_loc);
        walkway = (Button) findViewById(R.id.signed_walkway);

        //start();
        // loc();
    }

    @Override
    protected void initEvents() {
        MyOnClickListener my = new MyOnClickListener();
        sign.setOnClickListener(my);
        loc.setOnClickListener(my);
        walkway.setOnClickListener(my);
    }

    private void init() {
        L.i(TAG, "onCreate - 初始化定位服务");
        initLocationService();// 初始化定位服务，配置相应参数

        start();
        loc();
    }

    private void initLocationService() {
        location = new LocationClient(this);
        listener = new SignedListener(this, handler);
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
        option.setScanSpan(DELAY_TIME);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
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

    private void loc() {
        if (location != null && location.isStarted()) {
            location.stop();
        }

        if (lat > 0 && lng > 0) {
            LatLng point = new LatLng(lat, lng);//定义Maker坐标点
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.loc);//构建Marker图标
            OverlayOptions options = new MarkerOptions().position(point).icon(bitmap);//构建MarkerOption，用于在地图上添加Marker
            map.clear();//清除先前所有overlay
            map.addOverlay(options);

            MapStatus status = new MapStatus.Builder().target(point).zoom(19).build();
            MapStatusUpdate update = MapStatusUpdateFactory.newMapStatus(status);
            map.setMapStatus(update);//改变地图状态
        }
    }

    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.signed_sign:

                    loc();
                    signed();

                    break;

                case R.id.signed_loc:

                    loc();

                    break;

                case R.id.signed_walkway:

                    //startActivity(WalkwayActivity.class);

                    break;
            }
        }
    }

    private void start() {
        boolean run = ServiceUtils.isServiceRunning(this, "com.baidu.location.f");//判断服务是否已开启
        if (!run || (location != null && !location.isStarted())) {
            L.i(TAG, "onStartCommand - 开启定位服务");
            location.start();//服务没启动,开启定位服务
        }
    }

    private void signed() {
        if (!Model.IsLogin()) {
            showToast(R.string.no_login);
            return;
        }

        if (!(lat > 0 && lng > 0)) {
            showToast(R.string.no_location);
            return;
        }

        if (!HardwareUtils.isNetworkAvailable(this)) {
            showToast(R.string.no_network);
            return;
        }

        if (!Model.IsLogin()) {
            showToast(R.string.no_login);
            return;
        }

        addTask(new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoading(getString(R.string.loading_load));
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    String url = URL + "&uid=" + Model.My.getId() + "&code=" + code + "&lat=" + lat + "&lng=" + lng + "&date=" + DateTimeUtils.getCurrentDate();
                    L.i(TAG, url);

                    HttpUtils http = new HttpUtils();
                    String json = http.get(url);
                    L.i(TAG, json);

                    ret = (new ResolveEntity()).doSignedAdd(json);
                    if (ret != null) {
                        return true;
                    }
                } catch (Exception ex) {
                    //
                }

                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                dismissLoading();

                if (result && ret.getCode() == 1) {
                    showToast(getString(R.string.title_signed) + "成功");
                } else if (result && ret.getCode() == 0) {
                    showToast(ret.getMsg());
                } else {
                    showToast(R.string.submit_error);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (location != null && location.isStarted()) {
            location.stop();
        }

        super.onDestroy();
    }
}