package xiaoe.com.shop.business.setting.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.XiaoeActivity;

public class SettingPersonItemActivity extends XiaoeActivity {

    private static final String TAG = "PersonItemActivity";
    
    @BindView(R.id.person_edit_back)
    ImageView personEditBack;
    @BindView(R.id.person_edit_title)
    TextView personEditTitle;
    @BindView(R.id.person_edit_content)
    EditText personEditContent;
    @BindView(R.id.person_edit_submit)
    Button personEditSubmit;

    Intent intent;
    int position;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_person_item);
        ButterKnife.bind(this);
        intent = getIntent();

        initData();
        initListener();
        // 网络请求
        // SettingPresenter settingPresenter = new SettingPresenter(cmd, this);
        // settingPresenter.requestData();
    }

    private void initData() {
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        position = intent.getIntExtra("position", 0);
        Log.d(TAG, "initData: position ----- " + position);
        personEditTitle.setText(title);
        if (TextUtils.isEmpty(content)) {
            personEditContent.setHint("请输入" + title);
        } else {
            personEditContent.setText(content);
        }
        personEditContent.setSelection(personEditContent.getText().length());
    }

    private void initListener() {
        personEditBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        personEditSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 请求保存接口
                Intent intent = new Intent();
                intent.putExtra("content", personEditContent.getText().toString());
                intent.putExtra("position", position);
                SettingPersonItemActivity.this.setResult(RESULT_OK, intent);
                SettingPersonItemActivity.this.finish();
            }
        });
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
    }
}
