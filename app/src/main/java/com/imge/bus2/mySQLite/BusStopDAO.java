package com.imge.bus2.mySQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BusStopDAO{

    // 資料庫物件
    private SQLiteDatabase db;
    // 編號表格欄位名稱，固定不變
    public static final String KEY_ID = "_id";
    // 表格名稱
    public static final String TABLE_NAME = "bus_stop";

    // 其它表格欄位名稱
    public static final String STOPNAME_COLUMN = "stop_name";
    public static final String LATITUDE_COLUMN = "latitude";
    public static final String LONGITUDE_COLUMN = "longitude";
    public static final String ROUTEIDS_COLUMN = "route_ids";

    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    STOPNAME_COLUMN + " TEXT, " +
                    LATITUDE_COLUMN + " TEXT, " +
                    LONGITUDE_COLUMN + " TEXT, " +
                    ROUTEIDS_COLUMN + " TEXT)";

    // 建構子，一般的應用都不需要修改
    public BusStopDAO(Context context) {
        db = MyDBHelper.getDatabase(context);
    }

    // 關閉資料庫，一般的應用都不需要修改
    public void close() {
        if(db != null && db.isOpen()){
            db.close();
        }
    }

    public void insert(Map<String, List> map){
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

     /*
                如果調用了 db.setTransactionSuccessful()
                在 db.beginTransaction() 之後的操作全部提交，反之回滾
         */
        db.beginTransaction();
        boolean isSuccess = true;
        for(String stopName : map.keySet()){
            List value = map.get(stopName);

            // 加入ContentValues物件包裝的新增資料
            // 第一個參數是欄位名稱， 第二個參數是欄位的資料
            cv.put(STOPNAME_COLUMN, stopName);
            cv.put(LATITUDE_COLUMN, value.get(1).toString());
            cv.put(LONGITUDE_COLUMN, value.get(2).toString());
            cv.put(ROUTEIDS_COLUMN, ((Set<String>)value.get(0)).toString() );

            // 新增一筆資料，回傳值為 id ( id < 0 失敗)
            // 第一個參數是表格名稱
            // 第二個參數是沒有指定欄位值的預設值
            // 第三個參數是包裝新增資料的ContentValues物件
            if( db.insert(TABLE_NAME, null, cv) < 0){
                isSuccess = false;
                break;
            }
        }
        if (isSuccess){
            db.setTransactionSuccessful();
            db.endTransaction();
        }

    }

    public void update(Map<String, List> map){
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

     /*
                如果調用了 db.setTransactionSuccessful()
                在 db.beginTransaction() 之後的操作全部提交，反之回滾
         */
        db.beginTransaction();
        boolean isSuccess = true;
        for(String stopName : map.keySet()){
            List value = map.get(stopName);

            // 加入ContentValues物件包裝的新增資料
            // 第一個參數是欄位名稱， 第二個參數是欄位的資料
            cv.put(STOPNAME_COLUMN, stopName);
            cv.put(LATITUDE_COLUMN, value.get(1).toString());
            cv.put(LONGITUDE_COLUMN, value.get(2).toString());
            cv.put(ROUTEIDS_COLUMN, ((Set<String>)value.get(0)).toString() );

            // 設定修改資料的條件
            // 格式為「欄位名稱＝資料」
            String where = STOPNAME_COLUMN + "=\""  + stopName + "\"";

            // 新增一筆資料，回傳值為 id ( id < 0 失敗)
            // 第一個參數是表格名稱
            // 第二個參數是包裝新增資料的ContentValues物件
            // 第三個參數是 where 的條件
            // 第四個參數如果你的 where = "_id = ?" ，那可以用一個 array 放 ? 的值，沒有就放 null
            if( db.update(TABLE_NAME, cv, where, null) < 0){
                isSuccess = false;
                break;
            }
        }
        if (isSuccess){
            db.setTransactionSuccessful();
        }

    }

    public void delete(String stopName){
        // 設定條件，格式為「欄位名稱=資料」
        String where = STOPNAME_COLUMN + "=\""  + stopName + "\"";
        // 刪除指定編號資料並回傳刪除是否成功
        db.delete(TABLE_NAME, where , null);
    }

    public Map<String, List> getAll(){
        Map<String, List> map = new HashMap<>();

        Cursor cursor = db.query(
                TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        // 如果有查詢結果
        while (cursor.moveToNext()) {
            String stopName = cursor.getString(cursor.getColumnIndex(STOPNAME_COLUMN));
            map.put(stopName, setList(cursor));
        }

        cursor.close();
        return map;
    }

    public List get(String stopName){
        List list = new ArrayList();

        String where = STOPNAME_COLUMN + "=\""  + stopName + "\"";
        // 執行查詢
        Cursor cursor = db.query(
                TABLE_NAME,
                null,
                where,
                null,
                null,
                null,
                null,
                null
        );

        // 如果有查詢結果
        if (cursor.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            list = setList(cursor);
        }

        cursor.close();
        return list;

    }

    public List setList(Cursor cursor){
        List list = new ArrayList();

        String routeIds = cursor.getString(cursor.getColumnIndex(ROUTEIDS_COLUMN));
        Set<String> set = new HashSet<>(Arrays.asList(routeIds));
        list.add(set);
        list.add( Double.parseDouble(cursor.getString(cursor.getColumnIndex(LATITUDE_COLUMN))) );
        list.add( Double.parseDouble(cursor.getString(cursor.getColumnIndex(LONGITUDE_COLUMN))) );

        return list;
    }




}
