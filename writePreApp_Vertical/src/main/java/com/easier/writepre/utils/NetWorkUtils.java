package com.easier.writepre.utils;

import com.easier.writepre.app.WritePreApp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

public class NetWorkUtils {

    public static boolean isNetworkConnected() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) WritePreApp
                .getApp().getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null && mNetworkInfo.isConnected()) {
            if (mNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
                return true;
            }
        }
        return false;
    }

    public static String GetNetworkType() {
        String strNetworkType = "";

        ConnectivityManager mConnectivityManager = (ConnectivityManager) WritePreApp
                .getApp().getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                strNetworkType = "0";
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String _strSubTypeName = networkInfo.getSubtypeName();

                LogUtils.e("Network getSubtypeName : "
                        + _strSubTypeName);

                // TD-SCDMA networkType is 17
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: // api<8 : replace
                        // by 11
                        strNetworkType = "1";
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: // api<9 :
                        // replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD: // api<11 :
                        // replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP: // api<13 :
                        // replace by 15
                        strNetworkType = "2";
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE: // api<11 : replace
                        // by 13
                        strNetworkType = "3";
                        break;
                    default:
                        // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信
                        // 三种3G制式
                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA")
                                || _strSubTypeName.equalsIgnoreCase("WCDMA")
                                || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                            strNetworkType = "2";
                        } else {
                            strNetworkType = _strSubTypeName;
                        }

                        break;
                }

                LogUtils.e(
                        "Network getSubtype : "
                                + Integer.valueOf(networkType).toString());
            }
        }

        LogUtils.e("Network Type : " + strNetworkType);

        return strNetworkType;
    }
}
