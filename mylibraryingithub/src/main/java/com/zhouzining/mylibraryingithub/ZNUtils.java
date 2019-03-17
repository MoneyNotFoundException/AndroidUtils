package com.zhouzining.mylibraryingithub;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Walter on 2018/12/7.
 */

public class ZNUtils<T> {
    public static ZNUtils znUtils;
    private SharedPreferences sp;
    public static final int LOG_LEVEL_NONE = 0;     //不输出任和log
    public static final int LOG_LEVEL_DEBUG = 1;    //调试 蓝色
    public static final int LOG_LEVEL_INFO = 2;     //提现 绿色
    public static final int LOG_LEVEL_WARN = 3;     //警告 橙色
    public static final int LOG_LEVEL_ERROR = 4;    //错误 红色
    public static final int LOG_LEVEL_ALL = 5;      //输出所有等级
    private static int mLogLevel = LOG_LEVEL_ALL;
    private static Context mContext;
    private SQLiteDatabase db;
    private NoteDataHelper helper;


    protected ZNUtils(Context context) {
        this.mContext = context;
        if (getBoolean("isFirstIn", true))
            setBoolean("isFirstIn", true);
        sp = context.getSharedPreferences("ZNUtils", Context.MODE_PRIVATE);
    }

    public static ZNUtils getInstance(Context context) {
        return znUtils == null ? new ZNUtils(context) : znUtils;
    }


    //    网络请求

    public static boolean isNetWork(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        @SuppressLint("MissingPermission") NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }
        return true;
    }

    public static void doAsk(final String url, final String param, final HttpListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PrintWriter out = null;
                BufferedReader in = null;
                String result = "";
                try {
                    URL realUrl = new URL(url);
                    URLConnection conn = realUrl.openConnection();
                    conn.setRequestProperty("accept", "*/*");
                    conn.setRequestProperty("connection", "Keep-Alive");
                    conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    out = new PrintWriter(conn.getOutputStream());
                    out.print(param);
                    out.flush();
                    in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    while ((line = in.readLine()) != null) {
                        result += line;
                    }
                } catch (Exception e) {
                    listener.error(e.toString());
                    e.printStackTrace();
                } finally {
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

    public static void openUrlByBrowser(String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(url);
        if (uri == null || url.equals("") || url.equals("null")) {
            return;
        }
        intent.setData(uri);
        mContext.startActivity(intent);
    }

    public static String downloadFile(String url, String folderPath, String fileName) {
        try {
            //下载路径，如果路径无效了，可换成你的下载路径
            final long startTime = System.currentTimeMillis();
            Log.i("DOWNLOAD", "startTime=" + startTime);
            //下载函数
            if (fileName == null || "".equals(fileName))
                fileName = url.substring(url.lastIndexOf("/") + 1);
            //获取文件名
            URL myURL = new URL(url);
            URLConnection conn = myURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            int fileSize = conn.getContentLength();//根据响应获取文件大小
            if (fileSize <= 0) throw new RuntimeException("无法获知文件大小 ");
            if (is == null) throw new RuntimeException("stream is null");
            File file1 = new File(folderPath);
            if (!file1.exists()) {
                file1.mkdirs();
            }
            //把数据存入路径+文件名
            FileOutputStream fos = new FileOutputStream(folderPath + "/" + fileName);
            byte buf[] = new byte[1024];
            int downLoadFileSize = 0;
            do {
                //循环读取
                int numread = is.read(buf);
                if (numread == -1) {
                    break;
                }
                fos.write(buf, 0, numread);
                downLoadFileSize += numread;
                //更新进度条
            } while (true);
            is.close();
            return folderPath + "/" + fileName;
        } catch (Exception ex) {
            Log.e("DOWNLOAD", "error: " + ex.getMessage(), ex);
        }
        return "error";
    }
    //    dialog 和 popwindow类

    //    文件类


    public static boolean deleteFolder(String path) {
        File dir = new File(path);
        if (!dir.exists())
            return false;
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteFolder(new File(dir, children[i]).getAbsolutePath());
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public static long getFolderSize(String path) {
        File file = new File(path);
        if (!file.exists())
            return 0l;
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i].getAbsolutePath());
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    public static String getSizeStrByDouble(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return "0K";
        }
        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "K";
        }
        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "M";
        }
        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }

    public static String saveBitmap(View view, String folderPath, String fileName) {
        if (view == null)
            return "";
        view.setDrawingCacheEnabled(true);
        Bitmap secondBitmap = getBitmapByView(view);
        view.setDrawingCacheEnabled(false);
        Bitmap bitmap = Bitmap.createBitmap(secondBitmap.getWidth()
                , secondBitmap.getHeight(), secondBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(secondBitmap, new Matrix(), null);

        File appDir = new File(Environment.getExternalStorageDirectory(), folderPath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    public static String saveBitmap(ScrollView view, String folderPath, String fileName) {
        view.setDrawingCacheEnabled(true);
        Bitmap secondBitmap = getBitmapByView(view);
        view.setDrawingCacheEnabled(false);
        Bitmap bitmap = Bitmap.createBitmap(secondBitmap.getWidth()
                , secondBitmap.getHeight(), secondBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(secondBitmap, new Matrix(), null);
        File appDir = new File(Environment.getExternalStorageDirectory(), folderPath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }


    //    sp存储
    public boolean isFirstIn() {
        return getBoolean("isFirstIn", false);
    }

    public void setString(String key, String value) {
        sp.edit().putString(key, value).apply();
    }

    public String getString(String key) {
        return getString(key, "error");
    }

    public String getString(String key, String defaultStr) {
        return sp.getString(key, defaultStr);
    }

    public void setBoolean(String key, boolean defaultboo) {
        sp.edit().putBoolean(key, defaultboo).apply();
    }

    public boolean getBoolean(String key, boolean ble) {
        return sp.getBoolean(key, ble);
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public void setInt(String key, int value) {
        sp.edit().putInt(key, value).apply();
    }

    public int getInt(String key, int defaultValue) {
        return sp.getInt(key, defaultValue);
    }

    public int getInt(String key) {
        return getInt(key, -1);
    }


    //    数据库
    public static void initConfig(String dbName, HashMap<String, String> dbMap) {
        SpUtils utils = SpUtils.getInstance();
        JSONObject jsonObject = new JSONObject(dbMap);
        utils.setDB(dbName, jsonObject.toString());
    }

    public HashMap<String, String> getDBMapByDBName(String dbName) {
        HashMap<String, String> dbMap = new HashMap<>();
        SpUtils utils = SpUtils.getInstance();
        String dbMapStr = utils.getDB(dbName);
        dbMap = NoteDataHelper.getMap(dbMapStr);

        if (dbMap == null || dbMap.size() <= 0)
            return null;
        return dbMap;
    }

    public boolean createDB(String dbName) {
        return createDB(dbName, getDBMapByDBName(dbName));
    }

    public boolean createDB(String dbName, HashMap<String, String> dbMap) {
//        如果没有传dbmap，通过dbName对数据库进行初始化
        if (dbMap == null || dbMap.size() < 1) {
//            if (dbName.equals("")) {
//                HashMap<String, String> dbMapNew = new HashMap<>();
//                dbMapNew.put("dbName", dbName);
//                dbMapNew.put("dbKey", "id");
//                dbMapNew.put("dbType", "int");
//                helper = new NoteDataHelper(context, dbName, 1, dbMapNew);
//                db = helper.getDb(helper);
//            }
            return false;
        } else {
            helper = new NoteDataHelper(mContext, dbName, 1, dbMap);
            db = helper.getDb(helper);
            return true;
        }
    }

    public void saveData(HashMap<String, String> dataMap) {
        helper.insert(db, dataMap);
    }

    public List<T> getData(HashMap<String, String> whereMap, Class cla) {
        ArrayList<Object> list = helper.select(db, whereMap, cla, false);
        ArrayList<T> newCla = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            newCla.add((T) list.get(i));
        }
        return newCla;
    }

    public List<T> getAllData(Class cla) {
        ArrayList<Object> list = helper.select(db, null, cla, true);
        ArrayList<T> newCla = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            newCla.add((T) list.get(i));
        }
        return newCla;
    }

    public void upDate(HashMap<String, String> upDateMap, HashMap<String, String> whereMap) {
        helper.update(db, upDateMap, whereMap);
    }

    public void deleteData(HashMap<String, String> whereMap) {
        helper.delete(db, whereMap);
    }

    public T getNewData(Class cla) {
        List<T> lists = getAllData(cla);
        return lists.get(lists.size() - 1);
    }


    //    系统数据获取
    public static String getDeviceType() {
        String Client_type = android.os.Build.MODEL;
        return Client_type == null ? "未知" : Client_type;
    }

    //得到当前手机唯一编号，输出结果为 863807032616751
    public static String getDeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String deviceId = tm.getDeviceId();
        return deviceId == null ? "987654321" : deviceId;

    }

    public static String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss ");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        return str;
    }

    public static long getCurrentStamp() {
        return System.currentTimeMillis();
    }

    public static String getTimeByStamp(long stamp, String pattern) {
        Date date = new Date(stamp);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    public static long getStampByTime(String time, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = new Date();
        try {
            date = dateFormat.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static int getAppVersionCode(Context context) {
        int localVersion = 0;
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            localVersion = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    public static String getAppVersionName(Context context) {
        String localVersion = "";
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            localVersion = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    public static synchronized String getCurrentPkg(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isPhoneNum(String tel) {
        if (tel.length() != 11) {
            return false;
        }
        String str = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(tel);
        return m.matches();
    }

    public static void showShareDialog(Activity activity, Uri imageUri, String provideName) {
        if (imageUri == null || !new File(imageUri.getPath()).exists())
            return;

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/*");

        activity.startActivity(Intent.createChooser(shareIntent, "分享到"));
    }

    public static void openAppOrMarket(String targetPkg) {
        final PackageManager packageManager = mContext.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        List<String> packageNames = new ArrayList<String>();

        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        if (packageNames.contains(targetPkg)) {
            Intent LaunchIntent = mContext.getPackageManager()
                    .getLaunchIntentForPackage(targetPkg);
            mContext.startActivity(LaunchIntent);
        } else {
            try {
                Uri uri = Uri.parse("market://details?id=" + targetPkg);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            } catch (Exception e) {
                toastS("您的手机没有安装Android应用市场");
                e.printStackTrace();
            }

        }
    }

    /**
     * @param videoPath 视频的url地址或者本地地址
     * @param kind      传1或者3 具体是啥不记得了
     * @return
     */
    public static Bitmap getBitMapByVideo(String videoPath, int kind) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            if (videoPath.startsWith("http://")
                    || videoPath.startsWith("https://")
                    || videoPath.startsWith("widevine://")) {
                retriever.setDataSource(videoPath, new Hashtable<String, String>());
            } else {
                retriever.setDataSource(videoPath);
            }
            bitmap = retriever.getFrameAtTime(-1);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }
        }
        if (bitmap == null) return null;
        if (kind == MediaStore.Images.Thumbnails.MINI_KIND) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int max = Math.max(width, height);
            if (max > 512) {
                float scale = 512f / max;
                int w = Math.round(scale * width);
                int h = Math.round(scale * height);
                bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
            }
        } else if (kind == MediaStore.Images.Thumbnails.MICRO_KIND) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, 96, 96, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }

    /**
     * @param videoPath 视频本地地址
     * @return 格式为毫秒，需要自己转换
     */
    public static int getVideoTime(String videoPath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(videoPath);
        // 播放时长单位为毫秒
        int videoSumTime = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        mmr.release();
        // return GeneralUtils.millSecondToTime(videoSumTime);
        return videoSumTime;
    }

    public static String getCatchSize() {
        long cacheSize = getFolderSize(mContext.getCacheDir().getPath());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheSize += getFolderSize(mContext.getExternalCacheDir().getPath());
        }
        return getSizeStrByDouble(cacheSize);
    }

    public static void cleanCatch() {
        deleteFolder(mContext.getCacheDir().getPath());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            deleteFolder(mContext.getExternalCacheDir().getPath());
        }
    }

    public static Bitmap getBitmapByView(View view) {
        int h = 0;
        Bitmap bitmap;
        h += view.getHeight();
        bitmap = Bitmap.createBitmap(view.getWidth(), h,
                Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }


//    private long mExitTime;
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            //与上次点击返回键时刻作差
//            if ((System.currentTimeMillis() - mExitTime) > 2000) {
//                //大于2000ms则认为是误操作，使用Toast进行提示
//                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
//                //并记录下本次点击“返回键”的时刻，以便下次进行判断
//                mExitTime = System.currentTimeMillis();
//            } else {
//                //小于2000ms则认为是用户确实希望退出程序-调用System.exit()方法进行退出
//                System.exit(0);
//            }
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    //输出数据为 {"name":"zzn","1":"1"}
    public static String mapToJson(Map<String, ?> map) {
        String result = "";
        JSONObject object = new JSONObject(map);
        result = object.toString();
        return result;
    }

    //输出数据为 [{"name":"zzn","1":"1"},{"name":"zzn","2":"2"},{"name":"zzn","3":"3"}]
    public static String mapsToJson(List<Map<String, ?>> lists) {
        StringBuffer result = new StringBuffer();
        result.append("[");
        for (Map<String, ?> map :
                lists) {
            String mapStr = mapToJson(map);
            result.append(mapStr);
            result.append(",");
        }
        result.deleteCharAt(result.length() - 1);
        result.append("]");
        return result.toString();
    }

    //输出结果为 name=zzn&1=1
    public static String mapToUrl(Map<String, ?> map) {
        StringBuffer result = new StringBuffer();
        if (map.size() > 0) {
            for (String key : map.keySet()) {
                result.append(key + "=");
                if (map.get(key).equals("")) {
                    result.append("&");
                } else {
                    String value = (String) map.get(key);
                    try {
                        value = URLEncoder.encode(value, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    result.append(value + "&");
                }
            }
            result.deleteCharAt(result.length() - 1);

        }
        return result.toString();
    }

    public static Map<String, String> urlToMap(String paramsStr) {
        Map<String, String> mapParams = new HashMap<String, String>();
        if (paramsStr == null) {
            return mapParams;
        }
        String[] paramsArr = paramsStr.split("[&]");
        if (paramsArr != null && paramsArr.length > 0) {
            for (String string : paramsArr) {
                String[] kvParams = string.split("[=]");
                if (kvParams != null && kvParams.length > 1) {
                    mapParams.put(kvParams[0], kvParams[1]);
                } else {
                    if (!"".equals(kvParams[0])) {
                        mapParams.put(kvParams[0], "");
                    }
                }
            }
        }
        return mapParams;
    }

    /***
     * dp单位转换为params里面的px单位
     * @param dpStr
     * @return
     */
    public static int dpToPx(String dpStr) {
        int dp = Integer.parseInt(dpStr);
        dp = dp / 2;
        final float scale = mContext.getResources().getDisplayMetrics().density;
        int px = (int) (dp * scale + 0.5f);
        return px;
    }

    //    MD5类
    public static String MD5Encode(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] result = digest.digest(text.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                int number = b & 0xff;
                String hex = Integer.toHexString(number);
                if (hex.length() == 1) {
                    sb.append("0" + hex);
                } else {
                    sb.append(hex);
                }
            }
            return sb.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    //    base64加密类
    public static String base64Encode(String oldWord) {
        String encodeWord = "error";
        try {
            encodeWord = Base64.encodeToString(oldWord.getBytes("utf-8"), Base64.NO_WRAP);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodeWord;
    }

    public static String base64Decode(String encodeWord) {
        String decodeWord = "error";
        try {
            decodeWord = new String(Base64.decode(encodeWord, Base64.NO_WRAP), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return decodeWord;
    }

    //    Toast类
    public static void toastL(String text) {
        Toast.makeText(MyApplication.getInstance(), text, Toast.LENGTH_LONG).show();
    }

    public static void toastS(String text) {
        Toast.makeText(MyApplication.getInstance(), text, Toast.LENGTH_SHORT).show();
    }

    //    Log打印类

    /**
     * 设置Log等级
     *
     * @return
     */
    public static void setLogLevel(int level) {
        mLogLevel = level;
    }

    /**
     * 获取Log等级
     *
     * @return
     */
    public static int getLogLevel() {
        return mLogLevel;
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
