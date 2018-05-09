package com.lizhivscaomei.myapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lizhivscaomei.myapp.fragments.FindFragment;
import com.lizhivscaomei.myapp.fragments.HomeFragment;
import com.lizhivscaomei.myapp.fragments.NotificationFragment;

import java.util.ArrayList;
import java.util.List;

public class BottomNavigationFragmentPagerAdapter extends FragmentPagerAdapter{
    private FragmentManager fm;
    private List<Fragment> fragmentList;
    public BottomNavigationFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        fragmentList=new ArrayList<>();
        fragmentList.add(new HomeFragment());
        fragmentList.add(new NotificationFragment());
        fragmentList.add(new FindFragment());
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public List<Fragment> getFragmentList() {
        return fragmentList;
    }

    public void setFragmentList(List<Fragment> fragmentList) {
        this.fragmentList = fragmentList;
    }
}
