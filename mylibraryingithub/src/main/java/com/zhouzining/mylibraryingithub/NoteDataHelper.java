package com.zhouzining.mylibraryingithub;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Walter on 2018/1/17.
 */

public class NoteDataHelper extends SQLiteOpenHelper {
    //类没有实例化,是不能用作父类构造器的参数,必须声明为静态

    private static String name = "default"; //数据库名称
    private static int version = 1; //数据库版本
    private String dbName;
    private String dbKey;
    private String dbType;
    private String dbProperty;

    public NoteDataHelper(Context context, HashMap<String, String> dbMap) {
        super(context, name, null, version);
        mapToData(dbMap);
    }

    public NoteDataHelper(Context context, String baseName, int baseVersion, HashMap<String, String> dbMap) {
        super(context, baseName, null, baseVersion);
        name = baseName;
        version = baseVersion;
        mapToData(dbMap);
    }

    private void mapToData(HashMap<String, String> dbMap) {
//        取出固定的三个值
        dbName = dbMap.get("dbName");
        dbKey = dbMap.get("dbKey");
        dbType = dbMap.get("dbType");
        dbType = typeToDbType(dbType);
//        删除固定的三个值，把剩下的转为string
        dbMap.remove("dbName");
        dbMap.remove("dbKey");
        dbMap.remove("dbType");
        dbProperty = mapToDbPropertyKeyValue(dbMap);
    }

    //    输出为:name varchar(20), age INTEGER
    private String mapToDbPropertyKeyValue(HashMap<String, String> dbMap) {
        StringBuffer dbString = new StringBuffer();
        if (dbMap.size() > 0) {
            for (String key : dbMap.keySet()) {
                dbString.append(key + " ");
                if (dbMap.get(key).equals("")) {
                    dbString.append("varchar(100)");
                } else {
                    String value = (String) dbMap.get(key);
                    dbString.append(typeToDbType(value) + ", ");
                }
            }
            dbString.deleteCharAt(dbString.length() - 2);
        }
        return dbString.toString();
    }

    //    输出为:(sname,snumber) values('xiaoming','01005')
    private String mapToDbPropertyKeysValues(HashMap<String, String> dbMap) {
        StringBuffer dbString = new StringBuffer();
        StringBuffer dbKey = new StringBuffer("(");
        StringBuffer dbValue = new StringBuffer(" values('");
        if (dbMap.size() > 0) {
            for (String key : dbMap.keySet()) {
                dbKey.append(key + ",");
                String value = dbMap.get(key);
                dbValue.append(value + "','");
            }
            dbKey.deleteCharAt(dbKey.length() - 1);
            for (int i = 0; i < 2; i++) {
                dbValue.deleteCharAt(dbValue.length() - 1);
            }
            dbKey.append(")");
            dbValue.append(")");
            dbString.append(dbKey.toString());
            dbString.append(dbValue.toString());
        }

        return dbString.toString();
    }

    //    输出为:_id = 6 and name = abc
    private String mapToDbPropertyKeyAndValue(HashMap<String, String> dbMap, String connector) {
        StringBuffer dbString = new StringBuffer();
        if (dbMap.size() > 0) {
            for (String key : dbMap.keySet()) {
                dbString.append(" " + key + " = ");
                String value = dbMap.get(key);
                dbString.append("'" + value + "' " + connector);
            }
            for (int i = 0; i < connector.toCharArray().length + 1; i++) {
                dbString.deleteCharAt(dbString.length() - 1);
            }
        }

        return dbString.toString();
    }

    //    输出为:_id like %6% and name like %abc%
    private String mapToDbPropertyKeyAndValueLike(HashMap<String, String> dbMap, String connector) {
        StringBuffer dbString = new StringBuffer();
        if (dbMap.size() > 0) {
            for (String key : dbMap.keySet()) {
                dbString.append(" " + key + " like ");
                String value = dbMap.get(key);
                dbString.append("'%" + value + "%' " + connector);
            }
            for (int i = 0; i < connector.toCharArray().length + 1; i++) {
                dbString.deleteCharAt(dbString.length() - 1);
            }
        }

        return dbString.toString();
    }


//    json 转HashMap格式
    public static HashMap<String, String> getMap(String jsonString)

    {
        JSONObject jsonObject;
        try
        {
            jsonObject = new JSONObject(jsonString);   @SuppressWarnings("unchecked")
        Iterator<String> keyIter = jsonObject.keys();
            String key;
            String value;
            HashMap<String, String> valueMap = new HashMap<String, String>();
            while (keyIter.hasNext())
            {
                key = (String) keyIter.next();
                value = jsonObject.get(key).toString();
                valueMap.put(key, value);
            }
            return valueMap;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return null;

    }


    //    把java里的类型转换为database里面用的类型
    private String typeToDbType(String type) {
        if (type == null || type.equals("")) {
            type = "string";
        }
        type = type.toLowerCase();
        if ("string".equals(type)) {
            type = "varchar(100)";
        } else if ("int".equals(type) || "integer".equals(type)) {
            type = "INTEGER";
        } else if ("text".equals(type) || "test".equals(type)) {
            type = "TEXT";
        } else if ("double".equals(type) || "float".equals(type)) {
            type = "double";
        } else if ("blob".equals(type) || "boolean".equals(type)) {
            type = "BLOB";
        }
        return type;
    }

    //    得到db的对象
    public SQLiteDatabase getDb(NoteDataHelper helper) {
        return helper.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + dbName + " ("
                + dbKey + " " + dbType + " primary key autoincrement, " + dbProperty + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(SQLiteDatabase db, HashMap<String, String> dbMap) {
        db.execSQL("insert into " + dbName + mapToDbPropertyKeysValues(dbMap));
    }

    public void delete(SQLiteDatabase db, HashMap<String, String> dbMap) {
        db.execSQL("delete from " + dbName + " where " + mapToDbPropertyKeyAndValue(dbMap, "and"));
    }

    public ArrayList<Object> select(SQLiteDatabase db, HashMap<String, String> dbMap, Class cla, boolean isAll) {
        Cursor cursor;
        if (isAll) {
            cursor = db.rawQuery("select * from " + dbName, null);
        } else {
            cursor = db.rawQuery("select * from " + dbName + " where "
                    + mapToDbPropertyKeyAndValue(dbMap, "and"), null);
        }

//        db.execSQL("select * from name where "+mapToDbPropertyKeyAndValue(dbMap,"and"));
        StringBuffer stringBuffer = new StringBuffer();
        String[] names = cursor.getColumnNames();
        int mapCount = cursor.getColumnCount();
        HashMap<String, String> jsonMap = new HashMap<>();
        ArrayList<Object> jsonList = new ArrayList<>();
        while (cursor.moveToNext()) {
            for (int i = 0; i < mapCount; i++) {
                jsonMap.put(names[i], cursor.getString(i));
            }
            Gson gson = new Gson();
            Object obj = gson.fromJson(SystemUtils.mapToJson(jsonMap), cla);
            jsonList.add(obj);
        }
        cursor.close();

        return jsonList;
    }

    public ArrayList<Object> selectLike(SQLiteDatabase db, HashMap<String, String> dbMap, Class clas) {
        Cursor cursor = db.rawQuery("select * from " + dbName + " where " + mapToDbPropertyKeyAndValueLike(dbMap, "and"), null);
//        db.execSQL("select * from name where "+mapToDbPropertyKeyAndValue(dbMap,"and"));
        StringBuffer stringBuffer = new StringBuffer();
        String[] names = cursor.getColumnNames();
        int mapCount = cursor.getColumnCount();
        HashMap<String, String> jsonMap = new HashMap<>();
        ArrayList<Object> jsonList = new ArrayList<>();
        while (cursor.moveToNext()) {
            for (int i = 0; i < mapCount; i++) {
                jsonMap.put(names[i], cursor.getString(i));
            }
            Gson gson = new Gson();
            Object obj = gson.fromJson(SystemUtils.mapToJson(jsonMap), clas);
            jsonList.add(obj);
        }
        cursor.close();

        return jsonList;
    }

    public void update(SQLiteDatabase db, HashMap<String, String> dbMapData, HashMap<String, String> dbMapWhere) {
        db.execSQL("update " + dbName + " set " + mapToDbPropertyKeyAndValue(dbMapData, ",") + " where " + mapToDbPropertyKeyAndValue(dbMapWhere, "and"));
    }
}