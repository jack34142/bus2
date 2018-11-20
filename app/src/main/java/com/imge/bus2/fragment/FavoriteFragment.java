package com.imge.bus2.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.imge.bus2.R;
import com.imge.bus2.adapter.FavoriteRecyclerViewAdapter;
import com.imge.bus2.adapter.TimeRecyclerViewAdapter;
import com.imge.bus2.myTools.TimeSort;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends TimeFragment {

    public FavoriteFragment() {
        super();
    }

    @Override
    public void updateAdapter(List<List<String>> routeList) {
        // 依照抵達時間重新排序
        TimeSort timeSort = new TimeSort(routeList, goBack);
        routeList = timeSort.group();

        if(adapter == null){
            adapter = new FavoriteRecyclerViewAdapter(activity, routeList, goBack);
            timeRecyclerView.setAdapter(adapter);
        }else{
            adapter.updateData(routeList);
            adapter.notifyDataSetChanged();
        }
    }
}
