package com.xiaoe.shop.wxb.adapter.download;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import com.xiaoe.shop.wxb.base.BaseFragment;
import com.xiaoe.shop.wxb.business.download.ui.DownloadProceedFragment;
import com.xiaoe.shop.wxb.business.download.ui.FinishDownloadFragment;

public class CacheViewPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "CacheViewPagerAdapter";

    List<BaseFragment> baseFragmentList;

    public CacheViewPagerAdapter(FragmentManager fm) {
        super(fm);
        baseFragmentList = new ArrayList<BaseFragment>();
        baseFragmentList.add(new FinishDownloadFragment());
        baseFragmentList.add(new DownloadProceedFragment());
    }

    @Override
    public Fragment getItem(int position) {
        return baseFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return baseFragmentList.size();
    }
}
