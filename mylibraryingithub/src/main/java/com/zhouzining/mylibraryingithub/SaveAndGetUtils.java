package com.zhouzining.mylibraryingithub;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Walter on 2018/1/27.
 */

public class SaveAndGetUtils<T> {

    private Context context;
    private SQLiteDatabase db;
    private NoteDataHelper helper;
    private String dbName = "";


    public SaveAndGetUtils(Context context) {
        this.context = context;
    }


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

    public static SaveAndGetUtils getInstance(Context context) {
        return new SaveAndGetUtils(context);
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
            helper = new NoteDataHelper(context, dbName, 1, dbMap);
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

}
