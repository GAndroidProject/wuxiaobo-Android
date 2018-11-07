package xiaoe.com.shop.business.bought_list.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import xiaoe.com.network.NetworkCodes;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.ScholarshipBoughtListRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.main.presenter.ScholarshipPresenter;

public class BoughtListActivity extends XiaoeActivity {

    private static final String TAG = "BoughtListActivity";

    @BindView(R.id.bought_list_back)
    ImageView boughtListBack;
    @BindView(R.id.bought_list_content)
    ListView boughtListContent;

    Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bought_lsit);
        unbinder = ButterKnife.bind(this);
        ScholarshipPresenter scholarshipPresenter = new ScholarshipPresenter(this);
        scholarshipPresenter.requestBoughtList();
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        JSONObject result = (JSONObject) entity;
        if (success) {
            if (iRequest instanceof ScholarshipBoughtListRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
//                    JSONObject data = (JSONObject) ((JSONObject) ((JSONObject) result.get("data")).get("allData")).get("audio");
//                    initPageData(data);
                } else {
                    Log.d(TAG, "onMainThreadResponse: request fail...");
                }
            }
        } else {
            Log.d(TAG, "onMainThreadResponse: request fail, param error maybe");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    private void initPageData(JSONObject data) {

    }
}
