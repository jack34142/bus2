package com.imge.bus2.myTools;

import android.os.Message;

import com.imge.bus2.FavoriteActivity;
import com.imge.bus2.TimeActivity;

public class FavoriteCountDown extends CountDown {
    @Override
    public void excute() {
        FavoriteActivity.handler.sendEmptyMessage(1);
    }

    @Override
    public void perSecond() {
        // 顯示當前秒數
        Message msg = new Message();
        msg.what = 2;
        msg.arg1 = count;
        FavoriteActivity.handler.sendMessage(msg);
    }


}
