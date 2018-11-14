package com.imge.bus2.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.imge.bus2.fragment.TimeFragment;

import java.util.List;

public class TimeFragmentPagetAdapter extends FragmentStatePagerAdapter {
    List<List<String>> routeList;

    public TimeFragmentPagetAdapter(FragmentManager fm, List<List<String>> routeList) {
        super(fm);
        this.routeList = routeList;
    }

    @Override
    public Fragment getItem(int position) {
        TimeFragment fragment = new TimeFragment();
        fragment.setData(routeList, position+1 );
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "去程";
            case 1:
                return "返程";
            default:
                return null;
        }
    }

}
