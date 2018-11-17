package com.imge.bus2.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;
import com.imge.bus2.fragment.TimeFragment;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeFragmentPagetAdapter extends FragmentStatePagerAdapter {
    List<List<String>> routeList;
    Map<Integer, TimeFragment> myFragment = new HashMap<>();

    public TimeFragmentPagetAdapter(FragmentManager fm, List<List<String>> routeList) {
        super(fm);
        this.routeList = routeList;
    }

    @Override
    public Fragment getItem(int position) {
        if( myFragment.keySet().contains(position) ){
            return myFragment.get(position);
        }else{
            TimeFragment fragment = new TimeFragment();
            fragment.setData(routeList, position+1 );
            myFragment.put(position, fragment);
            return fragment;
        }
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

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        myFragment.remove(position);
    }

    public void updateData(List<List<String>> routeList){
        this.routeList = routeList;

        for(int position : myFragment.keySet()){
            TimeFragment fragment = myFragment.get(position);
            fragment.updateAdapter(routeList);
        }
    }

}
