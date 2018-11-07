package xiaoe.com.shop.business.main.presenter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ScholarshipFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = "ScholarshipFragmentStat";

    public ScholarshipFragmentStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }
}
