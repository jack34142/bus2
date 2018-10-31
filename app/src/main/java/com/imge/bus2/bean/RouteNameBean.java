package com.imge.bus2.bean;

import android.util.Log;

public class RouteNameBean {
    /**
     * ID : 3010
     * ProviderId : 1
     * nameZh : 1
     * ddesc : 中壢<->桃園
     * departureZh : 中壢
     * destinationZh : 桃園
     * gxcode : 3010
     * RouteType : RTY
     * MasterRouteName :
     * MasterRouteNo :
     * MasterRouteDesc : RG
     */

    private String ID;      // routeId
    private String nameZh;      // 編號
    private String ddesc;       // 中文名

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getNameZh() {
        return nameZh;
    }

    public void setNameZh(String nameZh) {
        this.nameZh = nameZh;
    }

    public String getDdesc() {
        try {
            ddesc = new String(ddesc.getBytes("iso-8859-1"), "UTF-8");
        }catch (Exception e){
            Log.e("RouteNameBean", "getDdesc 中文轉碼失敗");
        }

        return ddesc;
    }

    public void setDdesc(String ddesc) {
        this.ddesc = ddesc;
    }
}
