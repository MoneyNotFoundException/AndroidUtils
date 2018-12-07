package com.zhouzining.myutilsingithub;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/11/6.
 */

public class MessageEvent {
    private HashMap<Integer, PostAsk> mMsg;
    private String complete1;
    private String complete2;

    public MessageEvent(HashMap<Integer, PostAsk> msg, String complete1, String complete2) {
        // TODO Auto-generated constructor stub
        mMsg = msg;
        this.complete1 = complete1;
        this.complete2 = complete2;

    }

    public String getComplete1() {
        return complete1;
    }

    public String getComplete2() {
        return complete2;
    }

    public HashMap<Integer, PostAsk> getMsg() {
        return mMsg;
    }
}
