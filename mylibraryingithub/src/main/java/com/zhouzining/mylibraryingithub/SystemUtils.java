package com.zhouzining.mylibraryingithub;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Walter on 2018/1/2.
 */

public class SystemUtils {
    //得到当前手机型号  e.g. vivo x9
    public static String getPhoneType() {
        String Client_type = android.os.Build.MODEL;
        return Client_type == null ? "未知" : Client_type;
    }

    //得到当前手机唯一编号，输出结果为 863807032616751
    public static String getPid(Context context) {
        TelephonyManager tm = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String deviceId = tm.getDeviceId();
        return deviceId == null ? "987654321" : deviceId;

    }

    public static String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss ");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        return str;
    }

    //输出数据为 {"name":"zzn","1":"1"}
    public static String mapToJson(Map<String, ?> map) {
        String result = "";
        JSONObject object = new JSONObject(map);
        result = object.toString();
        return result;
    }

    //输出数据为 [{"name":"zzn","1":"1"},{"name":"zzn","2":"2"},{"name":"zzn","3":"3"}]
    public static String mapsToJson(List<Map<String, ?>> lists) {
        StringBuffer result = new StringBuffer();
        result.append("[");
        for (Map<String, ?> map :
                lists) {
            String mapStr = mapToJson(map);
            result.append(mapStr);
            result.append(",");
        }
        result.deleteCharAt(result.length() - 1);
        result.append("]");
        return result.toString();
    }

    //输出结果为 name=zzn&1=1
    public static String mapToUrl(Map<String, ?> map) {
        StringBuffer result = new StringBuffer();
        if (map.size() > 0) {
            for (String key : map.keySet()) {
                result.append(key + "=");
                if (map.get(key).equals("")) {
                    result.append("&");
                } else {
                    String value = (String) map.get(key);
                    try {
                        value = URLEncoder.encode(value, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    result.append(value + "&");
                }
            }
            result.deleteCharAt(result.length() - 1);

        }
        return result.toString();
    }

}
