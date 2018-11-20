package com.imge.bus2.myTools;

import android.os.Message;
import com.imge.bus2.TimeActivity;

public class TimeCountDown extends CountDown {
    @Override
    public void excute() {
        TimeActivity.handler.sendEmptyMessage(1);
    }

    @Override
    public void perSecond() {
        // 顯示當前秒數
        Message msg = new Message();
        msg.what = 2;
        msg.arg1 = count;
        TimeActivity.handler.sendMessage(msg);
    }


}
