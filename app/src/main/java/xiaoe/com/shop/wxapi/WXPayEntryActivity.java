package xiaoe.com.shop.wxapi;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import xiaoe.com.common.app.Constants;
import xiaoe.com.common.utils.SharedPreferencesUtil;


public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
	
	private static final String TAG = "WXPayEntryActivity";
	
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.pay_result);
    	api = WXAPIFactory.createWXAPI(this, Constants.getWXAppId(),true);
        api.handleIntent(this.getIntent(), this);
		Log.d(TAG, "onCreate: ");
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			Log.e(TAG, "onResp: ------");
			SharedPreferencesUtil.getInstance(this, SharedPreferencesUtil.FILE_NAME);
			SharedPreferencesUtil.putData(SharedPreferencesUtil.KEY_WX_PLAY_CODE, resp.errCode);
		}
		finish();
	}
}