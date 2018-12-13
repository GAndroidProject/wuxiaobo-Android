package com.xiaoe.shop.wxb.adapter.column;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.xiaoe.shop.wxb.base.BaseFragment;
import com.xiaoe.shop.wxb.business.column.ui.ColumnActivity;
import com.xiaoe.shop.wxb.business.column.ui.ColumnDetailFragment;
import com.xiaoe.shop.wxb.business.column.ui.LittleColumnDirectoryFragment;
import com.xiaoe.shop.wxb.business.column.ui.MemberFragment;
import com.xiaoe.shop.wxb.business.column.ui.NewColumnDirectoryFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/17.
 */

public class ColumnFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    private final String TAG = "ColumnStatePagerAdapter";
    private List<BaseFragment> list = new ArrayList<BaseFragment>();
    public ColumnFragmentStatePagerAdapter(FragmentManager fm, int resourceType, String resourceId) {
        super(fm);

        ColumnDetailFragment columnDetailFragment = new ColumnDetailFragment();
        list.add(columnDetailFragment);
        if(resourceType == ColumnActivity.RESOURCE_TYPE_TOPIC){
//            ColumnDirectoryFragm columnDirectoryFragment = new ColumnDirectoryFragment();
            NewColumnDirectoryFragment columnDirectoryFragment = new NewColumnDirectoryFragment();
            columnDirectoryFragment.setResourceId(resourceId);
            list.add(columnDirectoryFragment);
        }else if(resourceType == ColumnActivity.RESOURCE_TYPE_MEMBER){
            MemberFragment memberFragment = new MemberFragment();
            memberFragment.setResourceId(resourceId);
            list.add(memberFragment);
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
