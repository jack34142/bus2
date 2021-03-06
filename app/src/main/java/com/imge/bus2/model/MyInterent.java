package com.imge.bus2.model;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class MyInterent {

    // 檢查網路是否啟用
    public static boolean getIsConn(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }
}
