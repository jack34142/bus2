package com.imge.bus2.myTools;

import android.util.Log;

import com.google.gson.Gson;
import com.imge.bus2.bean.BusDetailsBean;

import org.json.JSONArray;

import java.util.ArrayList;

public class DataDeal {

    public void dealBusDetails(String response){
        try{
            Gson gson = new Gson();
            BusDetailsBean busDetailsBean = new BusDetailsBean();
            ArrayList<BusDetailsBean> list = new ArrayList<>();

            JSONArray jsonArray = new JSONArray(response);
            int ary_len = jsonArray.length();
            for (int i=0; i<ary_len; i++){
                busDetailsBean = gson.fromJson(jsonArray.getJSONObject(i).toString(), BusDetailsBean.class);
                list.add(busDetailsBean);
                Log.d("test", busDetailsBean.getNameZh());
            }

        }catch (Exception e){
            Log.e("DataDeal", "dealBusDetails 解析 json 失敗");
            e.printStackTrace();
        }

    }


}
