package xiaoe.com.shop.business.setting.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.common.entitys.SettingItemInfo;
import xiaoe.com.common.interfaces.OnItemClickWithPosListener;
import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.setting.presenter.LinearDividerDecoration;
import xiaoe.com.shop.business.setting.presenter.SettingRecyclerAdapter;

public class SettingPersonActivity extends XiaoeActivity implements OnItemClickWithPosListener {

    private static final String TAG = "SettingPersonActivity";

    @BindView(R.id.setting_back)
    ImageView settingBack;
    @BindView(R.id.setting_person_content)
    RecyclerView settingContentView;

    SettingRecyclerAdapter settingRecyclerAdapter;
    // 从我的页面获取图片路径
    Intent intent;
    List<SettingItemInfo> dataList;
    private static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_person);
        ButterKnife.bind(this);

        intent = getIntent();

        // 网络请求
        // SettingPresenter settingPresenter = new SettingPresenter(this);
        // settingPresenter.requestSearchResult();

        initData();
        initListener();
    }

    private void initData() {
        dataList = new ArrayList<>();
        String imgUrl;
        if (TextUtils.isEmpty(intent.getStringExtra("avatar"))) {
            // 默认图片
            imgUrl = "http://pic24.nipic.com/20121008/6298817_164144357169_2.jpg";
        } else {
            imgUrl = intent.getStringExtra("avatar");
        }
        SettingItemInfo avatar = new SettingItemInfo("头像", imgUrl, "");
        SettingItemInfo nickname = new SettingItemInfo("昵称", "", "木木");
        SettingItemInfo name = new SettingItemInfo("真实姓名", "", "李铭淋");
        SettingItemInfo gender = new SettingItemInfo("性别", "", "男");
        SettingItemInfo birthday = new SettingItemInfo("生日", "", "1994-06-07");
        SettingItemInfo phone = new SettingItemInfo("手机", "", "18814182578");
        SettingItemInfo address = new SettingItemInfo("地址", "", "深圳市南山区科兴科学园 A2 单元 803");
        SettingItemInfo company = new SettingItemInfo("公司", "", "深圳市小鹅网络通信技术有限公司");
        SettingItemInfo level = new SettingItemInfo("职位", "", "前端开发工程师");

        dataList.add(avatar);
        dataList.add(nickname);
        dataList.add(name);
        dataList.add(gender);
        dataList.add(birthday);
        dataList.add(phone);
        dataList.add(address);
        dataList.add(company);
        dataList.add(level);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        settingContentView.setLayoutManager(llm);
        settingRecyclerAdapter = new SettingRecyclerAdapter(this, dataList);
        settingRecyclerAdapter.setOnItemClickWithPosListener(this);
        settingContentView.setAdapter(settingRecyclerAdapter);
        settingContentView.addItemDecoration(new LinearDividerDecoration(this, LinearLayout.VERTICAL, R.drawable.recycler_divider_line, Dp2Px2SpUtil.dp2px(this, 20)));
    }

    private void initListener() {
        settingBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        Log.d(TAG, "onMainThreadResponse: isSuccess --- " + success);
    }

    @Override
    public void onItemClick(View view, int position) {
        if (position != 0) {
            Intent intent = new Intent(this, SettingPersonItemActivity.class);
            intent.putExtra("title", dataList.get(position).getItemTitle());
            intent.putExtra("content", dataList.get(position).getItemContent());
            intent.putExtra("position", position);
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                int position = data.getIntExtra("position", -1);
                String content = data.getStringExtra("content");
                if (position != -1) {
                    dataList.get(position).setItemContent(content);
                    settingRecyclerAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}
