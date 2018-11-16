package com.xiaoe.shop.wxb.adapter.column;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import com.xiaoe.shop.wxb.base.BaseFragment;
import com.xiaoe.shop.wxb.business.column.ui.ColumnDetailFragment;
import com.xiaoe.shop.wxb.business.column.ui.ColumnDirectoryFragment;
import com.xiaoe.shop.wxb.business.column.ui.LittleColumnDirectoryFragment;

/**
 * Created by Administrator on 2017/7/17.
 */

public class ColumnFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    private final String TAG = "ColumnStatePagerAdapter";
    private List<BaseFragment> list = new ArrayList<BaseFragment>();
    public ColumnFragmentStatePagerAdapter(FragmentManager fm, boolean bigColumn, String resourceId) {
        super(fm);

        ColumnDetailFragment columnDetailFragment = new ColumnDetailFragment();
        list.add(columnDetailFragment);
        if(bigColumn){
            ColumnDirectoryFragment columnDirectoryFragment = new ColumnDirectoryFragment();
            columnDirectoryFragment.setResourceId(resourceId);
            list.add(columnDirectoryFragment);
        }else{
            LittleColumnDirectoryFragment littleColumnDirectoryFragment = new LittleColumnDirectoryFragment();
            littleColumnDirectoryFragment.setResourceId(resourceId);
            list.add(littleColumnDirectoryFragment);
        }

    }

    @Override
    public BaseFragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
