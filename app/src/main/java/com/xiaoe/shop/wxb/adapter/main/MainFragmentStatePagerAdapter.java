package com.xiaoe.shop.wxb.adapter.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import com.xiaoe.shop.wxb.base.BaseFragment;
import com.xiaoe.shop.wxb.business.main.ui.MainActivity;
import com.xiaoe.shop.wxb.business.main.ui.MicroPageFragment;
import com.xiaoe.shop.wxb.business.main.ui.NewScholarshipFragment;
import com.xiaoe.shop.wxb.business.mine.ui.MineFragment;

/**
 * Created by Administrator on 2017/7/17.
 */

public class MainFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "MainFragmentStatePagerAdapter";
    private List<BaseFragment> list = new ArrayList<BaseFragment>();
    public MainFragmentStatePagerAdapter(FragmentManager fm, boolean needShowScholarship) {
        super(fm);
        // 拿到微页面 id
        // 首页微页面
        MicroPageFragment homePageFragment = MicroPageFragment.newInstance(MainActivity.MICRO_PAGE_MAIN);
        // 课程页微页面
        MicroPageFragment coursePageFragment = MicroPageFragment.newInstance(MainActivity.MICRO_PAGE_COURSE);
        NewScholarshipFragment scholarshipFragment = null;
        if (needShowScholarship) {
            // 任务奖学金页面
            scholarshipFragment = new NewScholarshipFragment();
        }
        // 我的页面
        MineFragment mineFragment = new MineFragment();
        list.add(homePageFragment);
        list.add(coursePageFragment);
        if (needShowScholarship) {
            list.add(scholarshipFragment);
        }
        list.add(mineFragment);
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
