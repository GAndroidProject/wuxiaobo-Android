package xiaoe.com.shop.adapter.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import xiaoe.com.shop.base.BaseFragment;
import xiaoe.com.shop.business.main.ui.MicroPageFragment;
import xiaoe.com.shop.business.mine.ui.MineFragment;

/**
 * Created by Administrator on 2017/7/17.
 */

public class MainFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "MainFragmentStatePagerAdapter";
    private List<BaseFragment> list = new ArrayList<BaseFragment>();
    public MainFragmentStatePagerAdapter(FragmentManager fm) {
        super(fm);
        // TODO: 拿到微页面 id
        // 首页微页面
        MicroPageFragment homePageFragment = MicroPageFragment.newInstance(0);
        // 课程页微页面
        MicroPageFragment coursePageFragment = MicroPageFragment.newInstance(6232);
        MineFragment mineFragment = new MineFragment();
        list.add(homePageFragment);
        list.add(coursePageFragment);
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
