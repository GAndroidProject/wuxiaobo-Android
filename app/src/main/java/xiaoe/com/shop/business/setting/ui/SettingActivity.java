package xiaoe.com.shop.business.setting.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.common.entitys.SettingItemInfo;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.XiaoeActivity;

public class SettingActivity extends XiaoeActivity {

    private static final String TAG = "SettingActivity";

    @BindView(R.id.setting_back)
    ImageView settingBack;
    @BindView(R.id.setting_content)
    RecyclerView settingContentView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        // 网络请求
        // SettingPresenter settingPresenter = new SettingPresenter(this);
        // settingPresenter.requestData();

        initData();
        initListener();
    }

    private void initData() {
        List<SettingItemInfo> personalList = new ArrayList<>();
        SettingItemInfo avatar = new SettingItemInfo("头像", "http://pic24.nipic.com/20121008/6298817_164144357169_2.jpg", "");
        SettingItemInfo nickname = new SettingItemInfo("昵称", "", "木木");
        SettingItemInfo name = new SettingItemInfo("真实姓名", "", "李铭淋");
        SettingItemInfo gender = new SettingItemInfo("性别", "", "男");
        SettingItemInfo birthday = new SettingItemInfo("生日", "", "1994-06-07");
        personalList.add(avatar);
        personalList.add(nickname);
        personalList.add(name);
        personalList.add(gender);
        personalList.add(birthday);
        List<SettingItemInfo> workList = new ArrayList<>();
        SettingItemInfo phone = new SettingItemInfo("手机", "", "18814182578");
        SettingItemInfo address = new SettingItemInfo("地址", "", "深圳市南山区科兴科学园 A2 单元 803");
        SettingItemInfo company = new SettingItemInfo("公司", "", "深圳市小鹅网络通信技术有限公司");
        SettingItemInfo level = new SettingItemInfo("职位", "", "前端开发工程师");
        workList.add(phone);
        workList.add(address);
        workList.add(company);
        workList.add(level);
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
}
