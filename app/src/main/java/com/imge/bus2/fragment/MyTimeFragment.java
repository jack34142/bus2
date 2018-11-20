package com.imge.bus2.fragment;

import com.imge.bus2.adapter.TimeRecyclerViewAdapter;
import com.imge.bus2.myTools.TimeSort;

import java.util.List;

public class MyTimeFragment extends TimeFragment {

    public MyTimeFragment() {
        super();
    }

    @Override
    public void updateAdapter(List<List<String>> routeList) {
        // 依照抵達時間重新排序
        TimeSort timeSort = new TimeSort(routeList, goBack);
        routeList = timeSort.group();

        if(adapter == null){
            adapter = new TimeRecyclerViewAdapter(activity, routeList, goBack);
            timeRecyclerView.setAdapter(adapter);
        }else{
            adapter.updateData(routeList);
            adapter.notifyDataSetChanged();
        }
    }
}
