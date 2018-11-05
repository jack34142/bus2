package com.imge.bus2.mySQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDBHelper extends SQLiteOpenHelper {

    // 資料庫名稱
    private static final String DATABASE_NAME = "mydata.db";
    // 資料庫版本，資料結構改變的時候要更改這個數字，通常是加一
    private static final int VERSION = 1;
    // 資料庫物件，固定的欄位變數
    private static SQLiteDatabase db;

    // 建構子，在一般的應用都不需要修改
    private MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // 需要資料庫的元件呼叫這個方法，這個方法在一般的應用都不需要修改
    public static SQLiteDatabase getDatabase(Context context) {
        synchronized (MyDBHelper.class){
            if (db == null || !db.isOpen()) {
                db = new MyDBHelper(context, DATABASE_NAME,null, VERSION).getWritableDatabase();
            }
        }

        return db;
    }

    // 關閉資料庫
    public static void closeDB(){
        if (db != null && db.isOpen()) {
            db.close();
            db = null;
        }
    }

    // 如果db文件不存在，SQLiteOpenHelper 將會自動創建db文件，並調用 onCreate()方法
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 建立應用程式需要的表格
        db.execSQL(BusStopDAO.CREATE_TABLE);
        db.execSQL(RouteNameDAO.CREATE_TABLE);
    }

    // 如果db文件存在，並且當前版本號高於上次創建或升級時的版本號，會調用onUpgrade()方法
    // 由於原本的資料表存在，所以要先刪除再創建
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 刪除原有的表格
        db.execSQL("DROP TABLE IF EXISTS " + BusStopDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RouteNameDAO.TABLE_NAME);
        // 呼叫onCreate建立新版的表格
        onCreate(db);
    }
}
