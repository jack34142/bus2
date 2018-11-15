package com.imge.bus2.mySQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RouteStopsDAO {

    // 資料庫物件
    private SQLiteDatabase db;
    // 編號表格欄位名稱，固定不變
    public static final String KEY_ID = "_id";
    // 表格名稱
    public static final String TABLE_NAME = "route_stops";

    // 其它表格欄位名稱
    public static final String ROUTEID_COLUMN = "route_id";
    public static final String STOPS_COLUMN = "stops";

    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ROUTEID_COLUMN + " TEXT, " +
                    STOPS_COLUMN + " TEXT)";

    // 建構子，一般的應用都不需要修改
    public RouteStopsDAO(Context context) {
        db = MyDBHelper.getDatabase(context);
    }

    // 關閉資料庫，一般的應用都不需要修改
    public void close() {
        if(db != null && db.isOpen()){
            db.close();
        }
    }

    public void insert(Map<String, Set<String>> map){
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

     /*
                如果調用了 db.setTransactionSuccessful()
                在 db.beginTransaction() 之後的操作全部提交，反之回滾
         */
        db.beginTransaction();
        boolean isSuccess = true;
        for(String routeId : map.keySet()){

            // 加入ContentValues物件包裝的新增資料
            // 第一個參數是欄位名稱， 第二個參數是欄位的資料
            cv.put(ROUTEID_COLUMN, routeId);
            cv.put(STOPS_COLUMN, map.get(routeId).toString() );

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

    public void update(Map<String, String> map){
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

     /*
                如果調用了 db.setTransactionSuccessful()
                在 db.beginTransaction() 之後的操作全部提交，反之回滾
         */
        db.beginTransaction();
        boolean isSuccess = true;
        for(String routeId : map.keySet()){

            // 加入ContentValues物件包裝的新增資料
            // 第一個參數是欄位名稱， 第二個參數是欄位的資料
            cv.put(ROUTEID_COLUMN, routeId);
            cv.put(STOPS_COLUMN, map.get(routeId).toString() );

            // 設定修改資料的條件
            // 格式為「欄位名稱＝資料」
            String where = ROUTEID_COLUMN + "=\""  + routeId + "\"";

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


    public void delete(String routeId){
        // 設定條件，格式為「欄位名稱=資料」
        String where = ROUTEID_COLUMN + "=\""  + routeId + "\"";
        // 刪除指定編號資料並回傳刪除是否成功
        db.delete(TABLE_NAME, where , null);
    }

    // key = stop name, value = 路線編號 + 路線名稱
    public Map<String, Set<String>> getAll(){
        Map<String, Set<String>> map = new HashMap<>();

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
            String stops_str = cursor.getString(cursor.getColumnIndex(STOPS_COLUMN));
            stops_str = stops_str.substring(1, stops_str.length()-1);
            String[] stops_ary = stops_str.split(", ");
            Set<String> stops = new HashSet<>(Arrays.asList(stops_ary));

            map.put( cursor.getString(cursor.getColumnIndex(ROUTEID_COLUMN)), stops);
        }

        cursor.close();
        return map;
    }

    // return 路線編號 + 路線名稱
    public Set<String> get(String routeId){
        Set<String> stops = new HashSet<>();

        String where = ROUTEID_COLUMN + "=\""  + routeId + "\"";
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
            String stops_str = cursor.getString(cursor.getColumnIndex(STOPS_COLUMN));
            stops_str = stops_str.substring(1, stops_str.length()-1);
            String[] stops_ary = stops_str.split(", ");
            stops = new HashSet<>(Arrays.asList(stops_ary));
        }

        cursor.close();
        return stops;
    }

}
