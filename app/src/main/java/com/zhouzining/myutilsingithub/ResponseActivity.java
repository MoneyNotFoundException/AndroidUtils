package com.zhouzining.myutilsingithub;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.zhouzining.mylibraryingithub.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Administrator on 2017/11/6.
 */

public class ResponseActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.response_get1_btn)
    Button responseGet1Btn;
    @BindView(R.id.response_get1)
    TextView responseGet1;
    @BindView(R.id.response_get2_btn)
    Button responseGet2Btn;
    @BindView(R.id.response_get2)
    TextView responseGet2;
    @BindView(R.id.response_post1_btn)
    Button responsePost1Btn;
    @BindView(R.id.response_post1)
    TextView responsePost1;
    @BindView(R.id.response_post2_btn)
    Button responsePost2Btn;
    @BindView(R.id.response_post2)
    TextView responsePost2;
    private String complete1, complete2;
    private String resGet1, resGet2, resPost1, resPost2;
    private HashMap<String, Object> params;
    private UpgradeUiHandler handler = new UpgradeUiHandler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response);
        ButterKnife.bind(this);
        EventBus.getDefault().register(ResponseActivity.this);
        responseGet1Btn.setOnClickListener(this);
        responseGet2Btn.setOnClickListener(this);
        responsePost1Btn.setOnClickListener(this);
        responsePost2Btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        String msg = "";
        // 将文本内容放到系统剪贴板里。
        switch (view.getId()) {
            case R.id.response_get1_btn:
                msg = responseGet1.getText().toString();
                break;
            case R.id.response_get2_btn:
                msg = responseGet2.getText().toString();
                break;
            case R.id.response_post1_btn:
                msg = responsePost1.getText().toString();
                break;
            case R.id.response_post2_btn:
                msg = responsePost2.getText().toString();
                break;
            default:
                break;
        }
        LogUtils.e(msg);
        ClipData mClipData = ClipData.newPlainText("Label", msg);
        cm.setPrimaryClip(mClipData);
    }

    public class UpgradeUiHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    responseGet1.setText(resGet1);
                    break;
                case 2:
                    responseGet2.setText(resGet2);
                    break;
                case 3:
                    responsePost1.setText(resPost1);
                    break;
                case 4:
                    responsePost2.setText(resPost2);
                    break;
                case 5:
                    responseGet1.setText(resGet1);
                    responseGet1.setTextColor(Color.RED);
                    break;
                case 6:
                    responseGet2.setText(resGet2);
                    responseGet2.setTextColor(Color.RED);
                    break;
                case 7:
                    responsePost1.setText(resPost1);
                    responsePost1.setTextColor(Color.RED);
                    break;
                case 8:
                    responsePost2.setText(resPost2);
                    responsePost2.setTextColor(Color.RED);
                    break;
                default:
                    break;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMainThread(MessageEvent event) {
        HashMap<Integer, PostAsk> msg = event.getMsg();
        params = new HashMap<String, Object>();
        for (int i = 1; i <= msg.size(); i++) {
            if (msg.get(i).getType().toLowerCase().equals("string")) {
                params.put(msg.get(i).getName().toString(), msg.get(i).getData());

            } else if (msg.get(i).getType().toLowerCase().equals("int")) {
                params.put(msg.get(i).getName().toString(), Integer.valueOf(msg.get(i).getData()));

            } else if (msg.get(i).getType().toLowerCase().equals("boolean")) {
                if (msg.get(i).getType().toLowerCase().equals("true")) {
                    params.put(msg.get(i).getName().toString(), true);
                } else {
                    params.put(msg.get(i).getName().toString(), false);
                }

            } else if (msg.get(i).getType().toLowerCase().equals("double")) {
                params.put(msg.get(i).getName().toString(), Double.valueOf(msg.get(i).getData()));

            } else if (msg.get(i).getType().toLowerCase().equals("float")) {
                params.put(msg.get(i).getName().toString(), Float.valueOf(msg.get(i).getData()));

            } else if (msg.get(i).getType().toLowerCase().equals("long")) {
                params.put(msg.get(i).getName().toString(), Long.valueOf(msg.get(i).getData()));

            } else if (msg.get(i).getType().toLowerCase().equals("short")) {
                params.put(msg.get(i).getName().toString(), Short.valueOf(msg.get(i).getData()));

            } else if (msg.get(i).getType().toLowerCase().equals("byte")) {
                try {
                    params.put(msg.get(i).getName().toString(), msg.get(i).getData().getBytes("UTF8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            } else if (msg.get(i).getType().toLowerCase().equals("char")) {
                params.put(msg.get(i).getName().toString(), msg.get(i).getData().toCharArray());

            } else {
                params.put(msg.get(i).getName().toString(), msg.get(i).getData());
            }
            LogUtils.e("response--> ", msg.get(i).toString());
        }
        complete1 = event.getComplete1();
        complete2 = event.getComplete2();
        get_get(complete1, complete2);
        get_post(complete1, complete2, params);
    }

    private void get_get(String complete1, String complete2) {
        RequestQueue mQueue = Volley.newRequestQueue(ResponseActivity.this);
        StringRequest url_get1 = new StringRequest
                (complete1, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        resGet1 = response;
                        handler.sendEmptyMessage(1);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        resGet1 = error.getMessage();
                        handler.sendEmptyMessage(5);
                    }
                });
        StringRequest url_get2 = new StringRequest
                (complete2, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        resGet2 = response;
                        handler.sendEmptyMessage(2);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        resGet2 = error.getMessage();
                        handler.sendEmptyMessage(6);
                    }
                });

        mQueue.add(url_get1);
        mQueue.add(url_get2);

    }

    private void get_post(String complete1, String complete2, HashMap<String, Object> params) {
        RequestQueue mQueue = Volley.newRequestQueue(ResponseActivity.this);

        JsonObjectRequest url_post1 = new JsonObjectRequest(
                1, complete1, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                resPost1 = response.toString();
                handler.sendEmptyMessage(3);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resPost1 = error.getMessage();
                handler.sendEmptyMessage(7);
            }
        });
        JsonObjectRequest url_post2 = new JsonObjectRequest(
                1, complete2, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                resPost2 = response.toString();
                handler.sendEmptyMessage(4);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resPost2 = error.getMessage();
                handler.sendEmptyMessage(8);
            }
        });
        mQueue.add(url_post1);
        mQueue.add(url_post2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
