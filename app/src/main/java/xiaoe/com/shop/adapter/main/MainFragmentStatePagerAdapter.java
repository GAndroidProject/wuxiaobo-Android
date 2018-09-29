package xiaoe.com.shop.adapter.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import xiaoe.com.shop.base.BaseFragment;
import xiaoe.com.shop.business.homepage.ui.HomepageFragment;

/**
 * Created by Administrator on 2017/7/17.
 */

public class MainFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "MainFragmentStatePagerAdapter";
    private List<BaseFragment> list = new ArrayList<BaseFragment>();
    public MainFragmentStatePagerAdapter(FragmentManager fm) {
        super(fm);
        for (int i = 0; i < 3; i++){
            HomepageFragment homepageFragment = new HomepageFragment();
            list.add(homepageFragment);
        }
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
