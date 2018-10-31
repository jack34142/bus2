package com.imge.bus2.sharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

public class MyLog {

    // 檢查是否下載過必要資料
    public static boolean getIsDownload(Context context){
        SharedPreferences sp = context.getSharedPreferences("myLog", Context.MODE_PRIVATE);
        return sp.getBoolean("isDownload", false);
    }

    // 記錄已經下載過必要資料
    public static void setIsDownload(Context context){
        SharedPreferences sp = context.getSharedPreferences("myLog", Context.MODE_PRIVATE);
        sp.edit().putBoolean("isDownload", true).commit();
    }



}
