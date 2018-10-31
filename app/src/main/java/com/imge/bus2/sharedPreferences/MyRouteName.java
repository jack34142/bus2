package com.imge.bus2.sharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.imge.bus2.bean.RouteNameBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyRouteName {

    // Map<routeId, routeName routeNameZh>
    public static Map<String, String> getRouteName(Context context){
        SharedPreferences sp = context.getSharedPreferences("myRouteName", Context.MODE_PRIVATE);
        return (Map<String, String>)sp.getAll();
    }

    // Map<routeId, routeName routeNameZh>
    public static void setRouteName(Context context, List<RouteNameBean> list){
        SharedPreferences sp = context.getSharedPreferences("myRouteName", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        Map<String, String> map = new HashMap<>();
        RouteNameBean routeNameBean;
        int list_len = list.size();
        for(int i=0; i<list_len; i++){
            routeNameBean = list.get(i);
            editor.putString(routeNameBean.getID(), routeNameBean.getNameZh()+" "+routeNameBean.getDdesc());
        }

        editor.commit();
    }

}
