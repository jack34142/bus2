package com.imge.bus2.sharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.imge.bus2.config.MyConfig;

public class MyLog {

    // 檢查是否下載過必要資料
    public static int getDownloadVersion(Context context){
        SharedPreferences sp = context.getSharedPreferences("myLog", Context.MODE_PRIVATE);
        return sp.getInt("isDownload", 0);
    }

    // 記錄已經下載過必要資料
    public static void setDownloadVersion(Context context){
        SharedPreferences sp = context.getSharedPreferences("myLog", Context.MODE_PRIVATE);
        sp.edit().putInt("isDownload", MyConfig.download_version).commit();
    }



}
