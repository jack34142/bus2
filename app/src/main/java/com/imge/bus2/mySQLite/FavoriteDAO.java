package com.imge.bus2.mySQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoriteDAO {

    // 資料庫物件
    private SQLiteDatabase db;
    // 編號表格欄位名稱，固定不變
    public static final String KEY_ID = "_id";
    // 表格名稱
    public static final String TABLE_NAME = "favorite";

    // 其它表格欄位名稱
    public static final String ROUTEID_COLUMN = "route_id";
    public static final String GO_COLUMN = "go";
    public static final String BACK_COLUMN = "back";

    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ROUTEID_COLUMN + " TEXT, " +
                    GO_COLUMN + " TEXT DEFAULT '-1', " +
                    BACK_COLUMN + " TEXT DEFAULT '-1')";

    // 建構子，一般的應用都不需要修改
    public FavoriteDAO(Context context) {
        db = MyDBHelper.getDatabase(context);
    }

    // 關閉資料庫，一般的應用都不需要修改
    public void close() {
        if(db != null && db.isOpen()){
            db.close();
        }
    }

    // 插入 routeId, go or back 設為 1 ( 另一個預設為0 )
    public boolean insert(String routeId, int goBack, String arriveStop){
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(ROUTEID_COLUMN, routeId);

        switch (goBack){
            case 1:
                cv.put(GO_COLUMN, arriveStop);
                break;
            case 2:
                cv.put(BACK_COLUMN, arriveStop);
                break;
        }

        // 新增一筆資料，回傳值為 id ( id < 0 失敗)
        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        return db.insert(TABLE_NAME, null, cv) >= 0;

    }

    // arriveStop = "" 即取消最愛
    public boolean update(String routeId, int goBack, String arriveStop){
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(ROUTEID_COLUMN, routeId);

        switch (goBack){
            case 1:
                cv.put(GO_COLUMN, arriveStop);
                break;
            case 2:
                cv.put(BACK_COLUMN, arriveStop);
                break;
        }

        // 設定修改資料的條件
        // 格式為「欄位名稱＝資料」
        String where = ROUTEID_COLUMN + "=\""  + routeId + "\"";

        // 新增一筆資料，回傳值為 id ( id < 0 失敗)
        // 第一個參數是表格名稱
        // 第二個參數是包裝新增資料的ContentValues物件
        // 第三個參數是 where 的條件
        // 第四個參數如果你的 where = "_id = ?" ，那可以用一個 array 放 ? 的值，沒有就放 null
        return db.update(TABLE_NAME, cv, where, null) >= 0;

    }


    public void delete(String routeId){
        // 設定條件，格式為「欄位名稱=資料」
        String where = ROUTEID_COLUMN + "=\""  + routeId + "\"";
        // 刪除指定編號資料並回傳刪除是否成功
        db.delete(TABLE_NAME, where , null);
    }

    // key = stop name, value = 路線編號 + 路線名稱
    public Set<String> getAll(){
        Set<String> favorite = new HashSet<>();

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
            if( !cursor.getString(cursor.getColumnIndex(GO_COLUMN)).equals("-1") ){
                favorite.add( cursor.getString(cursor.getColumnIndex(ROUTEID_COLUMN)) );
            }
            if( !cursor.getString(cursor.getColumnIndex(BACK_COLUMN)).equals("-1") ){
                favorite.add( cursor.getString(cursor.getColumnIndex(ROUTEID_COLUMN)) );
            }
        }
        cursor.close();
        return favorite;
    }

    // return 路線編號 + 路線名稱
    public String get(String routeId, int goBack){
        String arriveStop = "-2";

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
            switch (goBack){
                case 1:
                    // 讀取包裝一筆資料的物件
                    arriveStop = cursor.getString(cursor.getColumnIndex(GO_COLUMN));
                    break;
                case 2:
                    arriveStop = cursor.getString(cursor.getColumnIndex(BACK_COLUMN));
                    break;
            }
        }

        cursor.close();
        return arriveStop;
    }

}
