package com.zhouzining.mylibraryingithub;

import android.content.Context;


/**
 * Created by Administrator on 2017/10/19.
 */
public class PreferenceUtils extends BasePreference {
    private static PreferenceUtils preferenceUtils;
    private int collectCount = 0;
    private int recordCount = 0;

    /**
     * 需要增加key就在这里新建
     */


    private PreferenceUtils(Context context) {
        super(context);
    }

    /**
     * 这里我通过自定义的Application来获取Context对象，所以在获取preferenceUtils时不需要传入Context。
     *
     * @return
     */
    public synchronized static PreferenceUtils getInstance() {
        if (null == preferenceUtils) {
            preferenceUtils = new PreferenceUtils(MyApplication.getInstance());
        }
        return preferenceUtils;
    }

    public boolean isFirst() {
        return getBoolean("isFirst");
    }

    public void setFirst(Boolean isFirst) {
        setBoolean("isFirst", isFirst);
    }

}