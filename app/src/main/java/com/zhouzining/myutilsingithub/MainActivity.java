package com.zhouzining.myutilsingithub;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.zhouzining.mylibraryingithub.ZNUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ZNUtils znUtils = ZNUtils.getInstance(this);
        znUtils.setString("znUtils", "1");
        znUtils.setBoolean("znUtils", true);
        znUtils.setInt("znUtils", 3);
        znUtils.getString("znUitls");
        znUtils.getBoolean("znUitls");
        znUtils.getInt("znUitls");

        ZNUtils.doAsk("", null, new ZNUtils.HttpListener() {
            @Override
            public void success(String result) {

            }

            @Override
            public void error(String result) {

            }
        });

        ZNUtils.e("123","123");
        ZNUtils.MD5Encode("1231231");
    }
}
