package com.imge.bus2.myTools;

import android.os.Message;
import com.imge.bus2.TimeActivity;
import com.imge.bus2.config.MyConfig;

public abstract class CountDown extends Thread{
    protected int count = 0;      // 計時用
    protected boolean isPause = false;     // 判斷是否暫停
    protected boolean isClose = false;

    @Override
    public void run() {
        super.run();

        while ( !isClose ){
            if( !isPause ){     // 運行中

                // 每1秒執行一次的方法
                perSecond();

                if(count == 0){     // 數到 0 更新
                    // 每20秒執行一次的方法
                    excute();
                    try{
                        Thread.sleep(Long.MAX_VALUE);
                    }catch (Exception e){}
                }else{      // 睡個1秒, 然後 count - 1
                    try{
                        Thread.sleep(1000);
                    }catch (Exception e){}
                    count--;
                }
            }else{      // 暫停
                try{
                    Thread.sleep(Long.MAX_VALUE);
                }catch (Exception e){}
            }
        }
    }

    public void resetCount(){
        count = MyConfig.update_frq;     // gq
        this.interrupt();       // 喚醒
    }

    // 更新用，count 不設定成 0 ，主要是因為這個線程大部份的時間都在sleep，
    // 醒來第一件事，就是 count-- ，為了不讓 count 跑去 -1 以下，所以設成 1。
    // 還能順便防止手賤仔，狂按更新的那種，反正 count == 0 才會開始更新。
    public void updateNow(){
        count = 1;
    }

    public void pause(){
        isPause = true;        // 設成 true 就會暫停
    }

    public void go(){
        isPause = false;
        this.interrupt();
    }

    public void close(){
        isClose = true;
    }

    // 每20秒執行一次的方法
    public abstract void excute();

    // 每1秒執行一次的方法 (倒數)
    public abstract void perSecond();

}
