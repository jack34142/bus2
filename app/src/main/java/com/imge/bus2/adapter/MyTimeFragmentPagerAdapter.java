package com.imge.bus2.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.imge.bus2.fragment.MyTimeFragment;
import com.imge.bus2.fragment.TimeFragment;
import java.util.List;

public class MyTimeFragmentPagerAdapter extends TimeFragmentPagerAdapter {

    public MyTimeFragmentPagerAdapter(FragmentManager fm, List<List<String>> routeList) {
        super(fm, routeList);
    }

    @Override
    public Fragment createItem(int position) {

        if( myFragment.keySet().contains(position) ){
            return myFragment.get(position);
        }else{
            TimeFragment fragment = new MyTimeFragment();
            fragment.setData(routeList, position+1 );
            myFragment.put(position, fragment);
            return fragment;
        }
    }

}
