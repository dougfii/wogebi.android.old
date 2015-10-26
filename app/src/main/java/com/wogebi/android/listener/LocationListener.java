package com.wogebi.android.listener;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;

//    public int getLocType ( )
//    返回值：
//    61 ： GPS定位结果，GPS定位成功。
//    62 ： 无法获取有效定位依据，定位失败，请检查运营商网络或者wifi网络是否正常开启，尝试重新请求定位。
//    63 ： 网络异常，没有成功向服务器发起请求，请确认当前测试手机网络是否通畅，尝试重新请求定位。
//    65 ： 定位缓存的结果。
//    66 ： 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果。
//    67 ： 离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果。
//    68 ： 网络连接失败时，查找本地离线定位时对应的返回结果。
//    161： 网络定位结果，网络定位定位成功。
//    162： 请求串密文解析失败。
//    167： 服务端定位失败，请您检查是否禁用获取位置信息权限，尝试重新请求定位。
//    502： key参数错误，请按照说明文档重新申请KEY。
//    505： key不存在或者非法，请按照说明文档重新申请KEY。
//    601： key服务被开发者自己禁用，请按照说明文档重新申请KEY。
//    602： key mcode不匹配，您的ak配置过程中安全码设置有问题，请确保：sha1正确，“;”分号是英文状态；且包名是您当前运行应用的包名，请按照说明文档重新申请KEY。
//    501～700：key验证失败，请按照说明文档重新申请KEY。

public abstract class LocationListener implements BDLocationListener
{
    public static final int MSG_LOCATION_GPS = BDLocation.TypeGpsLocation;//GPS定位结果 61
    public static final int MSG_LOCATION_NETWORK = BDLocation.TypeNetWorkLocation;//网络定位结果 161
    public static final int MSG_LOCATION_OFFLINE = BDLocation.TypeOffLineLocation;//离线定位结果 66
    public static final int MSG_LOCATION_SERVER_ERROR = BDLocation.TypeServerError;//服务端网络定位失败 167
    public static final int MSG_LOCATION_NETWORK_EXCEPTION = BDLocation.TypeNetWorkException;//网络异常 63
    public static final int MSG_LOCATION_CRITERIA_EXCEPTION = BDLocation.TypeCriteriaException;//无法获取有效定位依据导致定位失败 62
    public static final int MSG_LOCATION_OFFILINE_FIAL = BDLocation.TypeOffLineLocationFail;//离线定位失败 67
    public static final int MSG_LOCATION_NETWORK_CONNECT_FAIL = BDLocation.TypeOffLineLocationNetworkFail;//网络连接失败 68

    protected Context context;
    protected Handler handler;

    protected Local local;

    public LocationListener(Context context, Handler handler)
    {
        this.context = context;
        this.handler = handler;
        local = new Local();
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation)
    {
        if (bdLocation == null)
        {
            return;
        }

        local.code = bdLocation.getLocType();
        local.lat = bdLocation.getLatitude();
        local.lng = bdLocation.getLongitude();
        local.radius = bdLocation.getRadius();
        local.time = bdLocation.getTime();

        process();
    }

    //定位有效时的处理
    protected abstract void process();

    protected boolean isLocationAvailable(int code)
    {
        return code == MSG_LOCATION_GPS || code == MSG_LOCATION_NETWORK || code == MSG_LOCATION_OFFLINE;
    }

    protected boolean isLocationUnavailable(int code)
    {
        return code == MSG_LOCATION_SERVER_ERROR || code == MSG_LOCATION_NETWORK_EXCEPTION || code == MSG_LOCATION_NETWORK_CONNECT_FAIL || code == MSG_LOCATION_CRITERIA_EXCEPTION || code == MSG_LOCATION_OFFILINE_FIAL;
    }

    //定位信息发送给本地服务
    protected void send(int what)
    {
        //TODO:线程或进程间的序列化，直接用Parcelable或Serializable对象是无法传递的
        //线程间的序列化 http://blog.csdn.net/21cnbao/article/details/8086619
        //bundle.putParcelable("data", entity);

        Message message = handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putInt("code", local.code);
        bundle.putDouble("lat", local.lat);
        bundle.putDouble("lng", local.lng);
        bundle.putFloat("radius", local.radius);
        bundle.putString("time", local.time);
        message.what = what;
        message.setData(bundle);
        handler.sendMessage(message); //传递消息给主线程
    }

    //定位错误发送给本地
    protected void error()
    {
        handler.sendEmptyMessage(local.code); //定位异常
    }

    protected class Local
    {
        public int code;
        protected double lat;
        protected double lng;
        protected float radius;
        protected String time;
    }
}
