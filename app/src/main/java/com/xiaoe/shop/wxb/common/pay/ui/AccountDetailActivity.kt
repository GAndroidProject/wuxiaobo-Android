package com.xiaoe.shop.wxb.common.pay.ui

import android.os.Bundle
import android.view.View
import com.xiaoe.shop.wxb.R
import com.xiaoe.shop.wxb.base.XiaoeActivity
import com.xiaoe.shop.wxb.utils.StatusBarUtil
import kotlinx.android.synthetic.main.activity_account_detail.*
import kotlinx.android.synthetic.main.common_title_view.*

class AccountDetailActivity : XiaoeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBar()
        setContentView(R.layout.activity_account_detail)

        recordWrap.setPadding(0, StatusBarUtil.getStatusBarHeight(this), 0, 0)
        recordLoading.visibility = View.VISIBLE
        recordLoading.setLoadingState(View.VISIBLE)

        title_content.text = getString(R.string.bo_bi_content_desc)
        title_end.visibility = View.GONE
        title_back.setOnClickListener{ onBackPressed() }
    }
}
