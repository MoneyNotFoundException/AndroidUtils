package com.zhouzining.mylibraryingithub;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Walter on 2018/1/2.
 */

public class HttpUtils {
    //    这里的doAsk是get请求和post请求都可以用的
    public static void doAsk(final String url, final String param, final HttpListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("123", "run");
                PrintWriter out = null;
                BufferedReader in = null;
                String result = "";
                try {
                    URL realUrl = new URL(url);
                    // 打开和URL之间的连接
                    URLConnection conn = realUrl.openConnection();
                    // 设置通用的请求属性
                    conn.setRequestProperty("accept", "*/*");
                    conn.setRequestProperty("connection", "Keep-Alive");
                    conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
                    // conn.setConnectTimeout(timeoutMillis);
                    // 发送POST请求必须设置如下两行
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    // 获取URLConnection对象对应的输出流
                    out = new PrintWriter(conn.getOutputStream());
                    // 发送请求参数
                    out.print(param);
                    // flush输出流的缓冲
                    out.flush();
                    // 定义BufferedReader输入流来读取URL的响应
                    in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    while ((line = in.readLine()) != null) {
                        result += line;
                    }
                    Log.e("123", "result" + result);
                } catch (Exception e) {
                    listener.error(e.toString());
                    e.printStackTrace();
                }
                // 使用finally块来关闭输出流、输入流
                finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                        if (in != null) {
                            in.close();
                        }
                    } catch (IOException ex) {
                        listener.error(ex.toString());
                    }
                }
                listener.success(result);
            }
        }).start();
    }

    public interface HttpListener {
        void success(String result);

        void error(String result);
    }
}
