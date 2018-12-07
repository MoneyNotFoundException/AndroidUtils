package com.zhouzining.mylibraryingithub;


import android.util.Log;

/**
 * Created by zhouzining on 2017/9/12.
 */

public class LogUtils {
    public static final int LOG_LEVEL_NONE = 0;     //不输出任和log
    public static final int LOG_LEVEL_DEBUG = 1;    //调试 蓝色
    public static final int LOG_LEVEL_INFO = 2;     //提现 绿色
    public static final int LOG_LEVEL_WARN = 3;     //警告 橙色
    public static final int LOG_LEVEL_ERROR = 4;    //错误 红色
    public static final int LOG_LEVEL_ALL = 5;      //输出所有等级
    private String tag = "LogUtils";

    /**
     * 允许输出的log日志等级
     * 当出正式版时,把mLogLevel的值改为 LOG_LEVEL_NONE,
     * 就不会输出任何的Log日志了.
     */
    private static int mLogLevel = LOG_LEVEL_ALL;

    /**
     * 获取Log等级
     *
     * @return
     */
    public static int getLogLevel() {
        return mLogLevel;
    }

    /**
     * 给输出的Log等级赋值
     *
     * @param level
     */
    public static void setLogLevel(int level) {
        LogUtils.mLogLevel = level;
    }

    /**
     * 以级别为 d 的形式输出LOG,输出debug调试信息
     */
    public static void d(String msg) {
        if (getLogLevel() >= LOG_LEVEL_DEBUG) {
            Log.d("LogUtils", msg);
        }
    }

    public static void d(String tag, String msg) {
        if (getLogLevel() >= LOG_LEVEL_DEBUG) {
            d("LogUtils", msg);
        }
    }

    /**
     * 以级别为 i 的形式输出LOG,一般提示性的消息information
     */
    public static void i(String tag, String msg) {
        if (getLogLevel() >= LOG_LEVEL_INFO) {
            Log.i(tag, msg);
        }
    }

    public static void i(String msg) {
        if (getLogLevel() >= LOG_LEVEL_INFO) {
            i("LogUtils", msg);
        }
    }

    /**
     * 以级别为 w 的形式输出LOG,显示warning警告，一般是需要我们注意优化Android代码
     */
    public static void w(String tag, String msg) {
        if (getLogLevel() >= LOG_LEVEL_WARN) {
            Log.w(tag, msg);
        }
    }

    public static void w(String msg) {
        if (getLogLevel() >= LOG_LEVEL_WARN) {
            w("LogUtils", msg);
        }
    }

    /**
     * 以级别为 e 的形式输出LOG ，红色的错误信息，查看错误源的关键
     */
    public static void e(String tag, String msg) {
        if (getLogLevel() >= LOG_LEVEL_ERROR) {
            Log.e(tag, msg);
        }
    }

    public static void e(String msg) {
        if (getLogLevel() >= LOG_LEVEL_ERROR) {
            e("LogUtils", msg);
        }
    }

    /**
     * 以级别为 v 的形式输出LOG ，verbose啰嗦的意思
     */
    public static void v(String tag, String msg) {
        if (getLogLevel() >= LOG_LEVEL_ALL) {
            Log.v(tag, msg);
        }
    }

    public static void v(String msg) {
        if (getLogLevel() >= LOG_LEVEL_ALL) {
            v("LogUtils", msg);
        }
    }
}
