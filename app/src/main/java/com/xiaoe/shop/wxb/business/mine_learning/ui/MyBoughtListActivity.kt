package com.xiaoe.shop.wxb.business.mine_learning.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.xiaoe.shop.wxb.R
import com.xiaoe.shop.wxb.base.XiaoeActivity
import com.xiaoe.shop.wxb.utils.StatusBarUtil
import kotlinx.android.synthetic.main.activity_my_bought.*
import kotlinx.android.synthetic.main.common_title_view.*

/**
 * Date: 2018/12/25 15:55
 * Author: hans yang
 * Description: 我的已购页面
 */
class MyBoughtListActivity : XiaoeActivity() {

    private val mTitles : List<String> by lazy {
        listOf(getString(R.string.recently_in_learning),
                getString(R.string.purchase_records))
    }
    private val mFragments = listOf<Fragment>(
            RecentlyLearningFragment(),
            RecentlyLearningFragment())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBar()
        setContentView(R.layout.activity_my_bought)

        title_content.text = getString(R.string.my_already_bought)
        title_back.setOnClickListener {
            onBackPressed()
        }

        with(viewPager){
            adapter = ViewPagerAdapter(supportFragmentManager,mFragments,mTitles)
            offscreenPageLimit = mTitles.size
            setScroll(true)
        }

        tabLayout.setViewPager(viewPager,mTitles.toTypedArray())
        initTitleBar()
    }

    // 沉浸式初始化
    private fun initTitleBar() {
        StatusBarUtil.setRootViewFitsSystemWindows(this, false)
        val statusBarHeight = StatusBarUtil.getStatusBarHeight(this)
        common_title_wrap.setPadding(0, statusBarHeight, 0, 0)
    }

    class ViewPagerAdapter(fm: FragmentManager, private val list: List<Fragment>,
                           private val titles: List<String>): FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return list[position]
        }

        override fun getPageTitle(position: Int): CharSequence {
            return titles[position]
        }

        override fun getCount(): Int {
            return titles.size
        }
    }

}

