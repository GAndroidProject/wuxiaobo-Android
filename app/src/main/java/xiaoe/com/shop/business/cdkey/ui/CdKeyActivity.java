package xiaoe.com.shop.business.cdkey.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.cdkey.presenter.CdKeyPresenter;

public class CdKeyActivity extends XiaoeActivity {

    private static final String TAG = "CdKeyActivity";

    @BindView(R.id.cd_key_back)
    ImageView cdBack;
    @BindView(R.id.cd_key_content)
    EditText cdContent;
    @BindView(R.id.cd_key_submit)
    Button cdSubmit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cd_key);
        ButterKnife.bind(this);
        CdKeyPresenter cdKeyPresenter = new CdKeyPresenter(this);
        cdKeyPresenter.requestData();


        initListener();
    }

    private void initListener() {
        cdBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cdContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        Log.d(TAG, "onMainThreadResponse: " + success);
    }
}
