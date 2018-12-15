package com.xiaoe.shop.wxb.adapter.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.xiaoe.shop.wxb.base.BaseFragment;
import com.xiaoe.shop.wxb.business.main.ui.MainActivity;
import com.xiaoe.shop.wxb.business.main.ui.MicroPageFragment;
import com.xiaoe.shop.wxb.business.main.ui.NewScholarshipFragment;
import com.xiaoe.shop.wxb.business.mine.ui.MineFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @date 2017/7/17
 */
public class MainFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

    private List<BaseFragment> list = new ArrayList<>();

    public MainFragmentStatePagerAdapter(FragmentManager fm, boolean needShowScholarship) {
        super(fm);
        // 首页微页面
        list.add(MicroPageFragment.newInstance(MainActivity.MICRO_PAGE_MAIN, 0));
        // 课程页微页面
        list.add(MicroPageFragment.newInstance(MainActivity.MICRO_PAGE_COURSE, 1));
        // 任务奖学金页面
        if (needShowScholarship) {
            list.add(NewScholarshipFragment.newInstance(2));
        }
        // 我的页面
        list.add(MineFragment.newInstance(needShowScholarship ? 3 : 2));
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
