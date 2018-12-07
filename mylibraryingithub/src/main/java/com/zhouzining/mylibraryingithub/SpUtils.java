package com.zhouzining.mylibraryingithub;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Walter on 2018/1/2.
 * 此处是sharepreferences的工具类
 */

public class SpUtils {
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String FILE_NAME = "zhouzining";
    private static SpUtils preferenceUtils;

    protected SpUtils(Context context) {
        sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 这里我通过自定义的Application来获取Context对象，所以在获取preferenceUtils时不需要传入Context。
     *
     * @return
     */
    public synchronized static SpUtils getInstance() {
        if (null == preferenceUtils) {
            preferenceUtils = new SpUtils(MyApplication.getInstance());
        }
        return preferenceUtils;
    }

    public List<Object> getWriteLvBean(String beanName, Class cla) {
        return preferenceUtils.getBean(beanName, cla);
    }

    //    以下是对writelvitem的数据进行存储和提取

    public String getDB(String dbName) {
        return getString(dbName);
    }

    public void setDB(String dbName ,String db) {
        setString(dbName, db);
    }


    /*下面部分是不需要更改的*/
    protected void setString(String key, String value) {
        sp.edit().putString(key, value).apply();
    }

    protected String getString(String key) {
        return sp.getString(key, "");
    }

    protected void setBoolean(String key, boolean value) {
        sp.edit().putBoolean(key, value).apply();
    }

    protected boolean getBoolean(String key, boolean ble) {
        return sp.getBoolean(key, ble);
    }

    protected boolean getBoolean(String key) {
        return getBoolean(key, true);
    }

    protected void setInt(String key, int value) {
        sp.edit().putInt(key, value).apply();
    }

    protected int getInt(String key) {
        return sp.getInt(key, 0);
    }

    protected List<Object> getBean(String beanName, Class cla) {
//        得到存储的拼接后的字符串
        String beanJson = sp.getString(beanName, "null");
        String[] datas = beanJson.split("zzn520dmn");
        List<Object> results = new ArrayList<>();
        for (int i = 0; i < datas.length; i++) {
            results.add(new Gson().fromJson(datas[i], cla));
        }
        return results;
    }

    protected void setBean(String beanName, List<Object> datas) {
//        分隔符
        StringBuffer beanJson = new StringBuffer("zzn520dmn");
        for (int i = 0; i < datas.size(); i++) {
            beanJson.append(new Gson().toJson(datas.get(i)
            ));
        }
        sp.edit().putString(beanName, beanJson.toString());
    }

    protected void addBean(String beanName, Object object, Class cla) {
        List<Object> datas = getBean(beanName, cla);
        datas.add(object);
        setBean(beanName, datas);
    }
}

