package com.imge.bus2.model;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

public class KeyBoard {

    // 手機鍵盤關閉
    public static void hideKeyBoard(Context context){
        Activity activity = (Activity) context;
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

}
