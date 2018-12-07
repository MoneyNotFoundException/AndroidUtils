package com.zhouzining.myutilsingithub;

/**
 * Created by Administrator on 2017/11/4.
 */

public class PostAsk {
    private String type;
    private String name;
    private String data;

    public PostAsk(String type, String name, String data) {
        this.type = type;
        this.name = name;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "PostAsk{" +
                "type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
