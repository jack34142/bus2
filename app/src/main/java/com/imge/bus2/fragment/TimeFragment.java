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
import com.imge.bus2.adapter.TimeRecyclerViewAdapter;
import com.imge.bus2.myTools.TimeSort;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class TimeFragment extends Fragment {
    protected Activity activity;
    protected List<List<String>> routeList;       // 選擇的路線
    protected int goBack;     // 去或返
    protected RecyclerView timeRecyclerView;      // 列表物件
    protected TimeRecyclerViewAdapter adapter;

    public TimeFragment() {
        // Required empty public constructor
    }

    // 建立物件後, 先執行此方法傳參數
    public void setData(List<List<String>> routeList, int goBack){
        this.routeList = routeList;
        this.goBack = goBack;
    }

    // 更新列表用
    public abstract void updateAdapter(List<List<String>> routeList);

    // initial View
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_time, container, false);
        timeRecyclerView = view.findViewById(R.id.timeRecyclerView);

        return view;
    }

    // set View
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();

        // LinearLayoutManager 類似 ListView
        LinearLayoutManager manager = new LinearLayoutManager(activity);
        // 直列堆疊
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        // 分格線
        timeRecyclerView.addItemDecoration(new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));
        timeRecyclerView.setLayoutManager(manager);
        updateAdapter(routeList);

        // 隱藏 Activity 上的 progressBar
        activity.findViewById(R.id.time_progressBar).setVisibility(View.GONE);
    }

}
