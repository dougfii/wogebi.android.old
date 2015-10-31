package com.dougfii.android.core.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;

import java.util.List;
import java.util.Locale;

/**
 * Created by momo on 15/10/31.
 */
public class AppUtils {
    // 获取电话状态
    // CALL_STATE_IDLE = 0; 无活动
    // CALL_STATE_RINGING = 1; 响铃
    // CALL_STATE_OFFHOOK = 2; 摘机
    public static int getCallState(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getCallState();
    }

    // 获取电话方位
    public static CellLocation getCellLocation(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getCellLocation();
    }

    // 获得唯一设备ID, GSM手机的 IMEI和 CDMA手机的 MEID, 返回NULL则无效
    public static String getDeviceId(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    // 获得设备的软件版本号, 返回NULL则无效; 例IMEI/SV(software version) for GSM phones
    public static String getDeviceSoftwareVersion(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceSoftwareVersion();
    }

    // 获取手机号, 返回NULL则无效; GSM手机的 MSISDN
    public static String getLine1Number(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
    }

    // 获取附近的电话的信息, 返回NULL则无效; 需要权限: ACCESS_COARSE_UPDATES
    public static List<NeighboringCellInfo> getNeighboringCellInfo(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getNeighboringCellInfo();
    }

    // 获取ISO标准的国家码, 即国际长途区号; 注意: 仅当用户已在网络注册后有效; 在CDMA网络中结果也许不可靠
    public static String getNetworkCountryIso(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkCountryIso();
    }

    // 获取MCC+MNC(mobile country code + mobile network code); 注意: 仅当用户已在网络注册时有效;
    // 在CDMA网络中结果也许不可靠
    public static String getNetworkOperator(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkOperator();
    }

    // 获取按照字母次序的current registered operator(当前已注册的用户)的名字; 注意: 仅当用户已在网络注册时有效;
    // 在CDMA网络中结果也许不可靠
    public static String getNetworkOperatorName(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkOperatorName();
    }

    // 获取当前使用的网络类型
    // NETWORK_TYPE_UNKNOWN = 0; 网络类型未知
    // NETWORK_TYPE_GPRS = 1; GPRS网络
    // NETWORK_TYPE_EDGE = 2; EDGE网络
    // NETWORK_TYPE_UMTS = 3; UMTS网络
    // NETWORK_TYPE_CDMA = 4; CDMA网络, IS95A 或 IS95B
    // NETWORK_TYPE_EVDO_0 = 5; EVDO网络, revision 0
    // NETWORK_TYPE_EVDO_A = 6; EVDO网络, revision A
    // NETWORK_TYPE_1xRTT = 7; 1xRTT网络
    // NETWORK_TYPE_HSDPA = 8; HSDPA网络
    // NETWORK_TYPE_HSUPA = 9; HSUPA网络
    // NETWORK_TYPE_HSPA = 10; HSPA网络
    // NETWORK_TYPE_IDEN = 11; iDen网络
    // NETWORK_TYPE_EVDO_B = 12; EVDO网络, revision B
    // NETWORK_TYPE_LTE = 13; LTE网络
    // NETWORK_TYPE_EHRPD = 14; eHRPD网络
    // NETWORK_TYPE_HSPAP = 15; HSPA+网络
    public static int getNetworkType(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkType();
    }

    // 获取手机类型
    // PHONE_TYPE_NONE 无信号
    // PHONE_TYPE_GSM GSM信号
    // PHONE_TYPE_CDMA CDMA信号
    // PHONE_TYPE_SIP SIP信号
    public static int getPhoneType(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getPhoneType();
    }

    // 获取ISO国家码; 相当于提供SIM卡的国家码
    public static String getSimCountryIso(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getSimCountryIso();
    }

    // 获取SIM卡提供的移动国家码和移动网络码; 5或6位的十进制数字; SIM卡的状态必须是
    // SIM_STATE_READY(使用getSimState()判断)
    public static String getSimOperator(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getSimOperator();
    }

    // 获取服务商名称; SIM卡的状态必须是 SIM_STATE_READY(使用getSimState()判断); 例: 中国移动、联通
    public static String getSimOperatorName(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getSimOperatorName();
    }

    // 获取SIM卡的序列号; 需要权限: READ_PHONE_STATE
    public static String getSimSerialNumber(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getSimSerialNumber();
    }

    // 获取SIM的状态信息
    // SIM_STATE_UNKNOWN = 0; 未知状态
    // SIM_STATE_ABSENT = 1; 没插卡
    // SIM_STATE_PIN_REQUIRED = 2; 锁定状态, 需要用户的PIN码解锁
    // SIM_STATE_PUK_REQUIRED = 3; 锁定状态, 需要用户的PUK码解锁
    // SIM_STATE_NETWORK_LOCKED = 4; 锁定状态, 需要网络的PIN码解锁
    // SIM_STATE_READY = 5; 就绪状态
    public static int getSimState(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getSimState();
    }

    // 获取唯一的用户ID; 需要权限: READ_PHONE_STATE; 例: IMSI(国际移动用户识别码) for a GSM phone
    public static String getSubscriberId(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getSubscriberId();
    }

    // 取得和语音邮件相关的标签,即为识别符 ; 需要权限: READ_PHONE_STATE
    public static String getVoiceMailAlphaTag(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getVoiceMailAlphaTag();
    }

    // 获取语音邮件号码; 需要权限: READ_PHONE_STATE
    public static String getVoiceMailNumber(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getVoiceMailNumber();
    }

    // 获取ICC卡是否存在
    public static boolean hasIccCard(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).hasIccCard();
    }

    // 获取是否漫游(在GSM用途下)
    public static boolean isNetworkRoaming(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).isNetworkRoaming();
    }

    // 获得当前程序版本信息
    public static String getVersionName(Context context) {
        try {
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionName;
        } catch (Exception e) {
            return "";
        }
    }

    // 获得当前程序版本代码
    public static int getVersionCode(Context context) {
        try {
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionCode;
        } catch (Exception e) {
            return 0;
        }
    }

    // 获取系统版本
    public static String getClientOs() {
        return android.os.Build.ID;
    }

    // 获取系统版本号
    public static String getClientOsVer() {
        return android.os.Build.VERSION.RELEASE;
    }

    // 获取系统语言包
    public static String getLanguage() {
        return Locale.getDefault().getLanguage();
    }

    // 获取国家代码
    public static String getCountry() {
        return Locale.getDefault().getCountry();
    }
}