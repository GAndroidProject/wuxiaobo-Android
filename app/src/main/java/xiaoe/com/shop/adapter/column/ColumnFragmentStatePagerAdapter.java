package xiaoe.com.shop.adapter.column;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import xiaoe.com.shop.base.BaseFragment;
import xiaoe.com.shop.business.column.ui.ColumnDetailFragment;
import xiaoe.com.shop.business.column.ui.ColumnDirectoryFragment;

/**
 * Created by Administrator on 2017/7/17.
 */

public class ColumnFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    private final String TAG = "ColumnStatePagerAdapter";
    private List<BaseFragment> list = new ArrayList<BaseFragment>();
    public ColumnFragmentStatePagerAdapter(FragmentManager fm) {
        super(fm);
        ColumnDetailFragment columnDetailFragment = new ColumnDetailFragment();
        ColumnDirectoryFragment columnDirectoryFragment = new ColumnDirectoryFragment();
        list.add(columnDetailFragment);
        list.add(columnDirectoryFragment);
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
