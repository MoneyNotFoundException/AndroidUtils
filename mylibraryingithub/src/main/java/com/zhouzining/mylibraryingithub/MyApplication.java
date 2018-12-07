package com.zhouzining.mylibraryingithub;

import android.app.Application;

/**
 * Created by Walter on 2018/1/2.
 * 在application里面应该初始化所有全局变量
 * 包括友盟统计，后台所要求的激活，分渠道打包时需要的数据
 */

public abstract class MyApplication extends Application {
    public static MyApplication application;

    public static MyApplication getInstance() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        HashMap<String, String> dbMapNew = new HashMap<>();
//        dbMapNew.put("dbName", "adc");
//        dbMapNew.put("dbKey", "id");
//        dbMapNew.put("dbType", "int");
//        SaveAndGetUtils.initConfig("adc", dbMapNew);
        myOnCreate();

    }

    public abstract void myOnCreate();

    public abstract void initData();

    public abstract void initConfig();
}
