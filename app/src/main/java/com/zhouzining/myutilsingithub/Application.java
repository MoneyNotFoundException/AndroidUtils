package com.zhouzining.myutilsingithub;

import com.zhouzining.mylibraryingithub.MyApplication;

/**
 * Created by Walter on 2018/1/2.
 * 在application里面应该初始化所有全局变量
 * 包括友盟统计，后台所要求的激活，分渠道打包时需要的数据
 */

public class Application extends MyApplication {
    public static MyApplication application;

    public static MyApplication getInstance() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void myOnCreate() {
        application = this;

//        初始化拍摄的部分需要
//        initVideo();
    }

    @Override
    public void initData() {
        int a = 0;
        double b = 2d;
    }

    @Override
    public void initConfig() {

    }

}
