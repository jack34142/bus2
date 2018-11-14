package com.imge.bus2.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.imge.bus2.R;
import com.imge.bus2.TimeActivity;
import com.imge.bus2.adapter.TimeRecyclerViewAdapter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimeFragment extends Fragment {
    private Activity activity;
    private List<List<String>> routeList;
    private int goBack;


    public TimeFragment() {
        // Required empty public constructor
    }

    public void setData(List<List<String>> routeList, int goBack){
        this.routeList = routeList;
        this.goBack = goBack;
    }

    // initial View
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_time, container, false);
    }

    // set View
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();
        View view = getView();

        RecyclerView timeRecyclerView = view.findViewById(R.id.timeRecyclerView);

        // LinearLayoutManager 類似 ListView
        LinearLayoutManager manager = new LinearLayoutManager(activity);
        // 直列堆疊
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        // 分格線
        timeRecyclerView.addItemDecoration(new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));
        timeRecyclerView.setLayoutManager(manager);

        TimeRecyclerViewAdapter timeRecyclerViewAdapter = new TimeRecyclerViewAdapter(activity, routeList, goBack);
        timeRecyclerView.setAdapter(timeRecyclerViewAdapter);

        activity.findViewById(R.id.time_progressBar).setVisibility(View.GONE);

    }







}
