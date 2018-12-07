package com.zhouzining.myutilsingithub;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.zhouzining.mylibraryingithub.LogUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Administrator on 2017/11/4.
 */

public class HttpAskActivity extends AppCompatActivity {


    @butterknife.BindView(R.id.complete_et)
    EditText completeEt;
    @butterknife.BindView(R.id.http_head)
    EditText httpHead;
    @butterknife.BindView(R.id.http_cut1)
    EditText httpCut1;
    @butterknife.BindView(R.id.http_cut2)
    EditText httpCut2;
    @butterknife.BindView(R.id.http_askbody)
    EditText httpAskbody;
    @butterknife.BindView(R.id.post_layout)
    LinearLayout postLayout;
    @butterknife.BindView(R.id.http_scroll)
    ScrollView httpScroll;
    @butterknife.BindView(R.id.http_addpost_btn)
    Button httpAddpostBtn;
    @butterknife.BindView(R.id.http_start)
    Button httpStart;
    private String complete1, complete2, head, cut1, cut2, askbody;
    private ArrayList<PostAsk> post = new ArrayList<>();
    private HashMap<Integer, PostAsk> postAskHashMap = new HashMap<>();
    private HashMap<Integer, View> postAskViewHashMap = new HashMap<>();
    private HashMap<Integer, String> postAskTypeHashMap = new HashMap<>();
    private HashMap<Integer, String> postAskNameHashMap = new HashMap<>();
    private int postCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http);
        butterknife.ButterKnife.bind(this);
        initView();
    }

    public void initView() {
        httpAddpostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postCount++;
                postLayout.addView(addView());

            }
        });
        httpStart.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                head = httpHead.getText().toString();
                cut1 = httpCut1.getText().toString();
                cut2 = httpCut2.getText().toString();
                askbody = httpAskbody.getText().toString();

                for (Integer i = 1; i <= postAskViewHashMap.size(); i++) {
                    EditText http_choose_type_et = postAskViewHashMap.get(i).findViewById(R.id.http_choose_type_et);
                    postAskTypeHashMap.put(i, http_choose_type_et.getText().toString());
                    EditText http_choose_name_et = postAskViewHashMap.get(i).findViewById(R.id.http_choose_name_et);
                    postAskNameHashMap.put(i, http_choose_name_et.getText().toString());
                    EditText http_post = postAskViewHashMap.get(i).findViewById(R.id.http_post);
                    if (postAskHashMap.containsKey(i)) {
                        postAskHashMap.put(i, new PostAsk(postAskHashMap.get(i).getType()
                                , postAskHashMap.get(i).getName()
                                , http_post.getText().toString()));
                    } else {
                        postAskHashMap.put(i, new PostAsk(postAskTypeHashMap.get(i)
                                , postAskNameHashMap.get(i)
                                , http_post.getText().toString()));
                    }
                }
                complete1 = completeEt.getText().toString();
                complete2 = getGet(head, cut1, cut2, askbody);

                for (int i = 1; i <= postAskViewHashMap.size(); i++) {
                    postAskHashMap.put(i, new PostAsk(postAskTypeHashMap.get(i),
                            postAskNameHashMap.get(i),
                            postAskHashMap.get(i).getData()));
                    LogUtils.e("log--> ", postAskHashMap.get(i).toString());
                }

                EventBus.getDefault().postSticky(
                        new MessageEvent(postAskHashMap, complete1, complete2));

                Intent intent = new Intent();
                intent.setClass(HttpAskActivity.this, ResponseActivity.class);
                startActivity(intent);
            }
        });
    }


    private String getGet(String head, String cut1, String cut2, String askbody) {
        StringBuilder sb = new StringBuilder();
        sb.append(head).append(cut1);
        String[] bodys;
        if (askbody.contains(",")) {
            bodys = askbody.split(",");
        } else {
            bodys = askbody.split("，");
        }
        sb.append(bodys[0]);
        for (int i = 1; i < bodys.length; i++) {
            sb.append(cut2).append(bodys[i]);
        }
//        String get = chineseToEnglish(sb.toString());
        return sb.toString();
    }

    private String chineseToEnglish(String str) {
        String newStr = "";
        if (str != null) {
            if (str.contains("，")) {
                str = str.replace("，", ",");
            }
            if (str.contains("【")) {
                str = str.replace("【", "[");
            }
            if (str.contains("】")) {
                str = str.replace("】", "]");
            }
            if (str.contains("：")) {
                str = str.replace("：", ":");
            }
            if (str.contains("（")) {
                str = str.replace("（", "(");
            }
            if (str.contains("）")) {
                str = str.replace("）", ")");
            }
            if (str.contains("｛")) {
                str = str.replace("｛", "{");
            }
            if (str.contains("｝")) {
                str = str.replace("｝", "}");
            }
            if (str.contains("；")) {
                str = str.replace("；", ";");
            }
            if (str.contains("‘")) {
                str = str.replace("‘", "'");
            }
            if (str.contains("’")) {
                str = str.replace("’", "'");
            }
            if (str.contains("？")) {
                newStr = str.replace("？", "?");
            }
        }
        return newStr;
    }


    private View addView() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LayoutInflater inflater3 = LayoutInflater.from(HttpAskActivity.this);
        View view = inflater3.inflate(R.layout.item_http_post, null);
        view.setLayoutParams(lp);

        postAskViewHashMap.put(postCount, view);
        return view;

    }
}
