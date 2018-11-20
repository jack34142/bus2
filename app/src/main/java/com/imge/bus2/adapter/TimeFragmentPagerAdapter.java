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

public abstract class TimeFragmentPagerAdapter extends FragmentStatePagerAdapter {
    protected List<List<String>> routeList;
    protected Map<Integer, TimeFragment> myFragment = new HashMap<>();

    public TimeFragmentPagerAdapter(FragmentManager fm, List<List<String>> routeList) {
        super(fm);
        this.routeList = routeList;
    }

    @Override
    public Fragment getItem(int position) {
        return createItem(position);
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

    public abstract Fragment createItem(int position);

}
