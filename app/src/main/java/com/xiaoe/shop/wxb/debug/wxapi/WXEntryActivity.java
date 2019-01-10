package com.xiaoe.shop.wxb.debug.wxapi;

import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.weixin.view.WXCallbackActivity;
import com.xiaoe.common.app.Constants;
import com.xiaoe.common.entitys.HadSharedEvent;
import com.xiaoe.common.utils.SharedPreferencesUtil;
import com.xiaoe.shop.wxb.R;

import org.greenrobot.eventbus.EventBus;


/**
 * WXEntryActivity 是微信固定的Activiy、 不要改名字、并且放到你对应的项目报名下面、
 * 例如： ....(package报名).wxapi.WXEntryActivity
 * 不然无法回调、切记...
 * Wx  回调接口 IWXAPIEventHandler
 * <p/>
 * 关于WXEntryActivity layout。 我们没给页面、而是把Activity  主题 android:theme="@android:style/Theme.Translucent" 透明、
 * <p/>
 * User: MoMo - Nen
 * Date: 2015-10-24
 */
public class WXEntryActivity extends WXCallbackActivity implements IWXAPIEventHandler {

    private static final String TAG = "WXEntryActivity";

    private IWXAPI mApi;

    boolean hasObtainShareCallback; // 已经拿到分享回调，用于处理分享回调两次的情况

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        mApi = WXAPIFactory.createWXAPI(this, Constants.getWXAppId(), true);
        mApi.handleIntent(this.getIntent(), this);
    }

    //微信发送的请求将回调到onReq方法
    @Override
    public void onReq(BaseReq baseReq) {
        Log.d(TAG, "onReq: ....");
    }

    //发送到微信请求的响应结果

    @Override
    public void onResp(BaseResp resp) {
        String code = resp.errCode+"";
        String msg = "";
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                //发送成功
                if(resp instanceof SendAuth.Resp){
                    SendAuth.Resp sendResp = (SendAuth.Resp) resp;
                    code = sendResp.code;
                }
                msg = getString(R.string.send_successfully);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                //发送取消
                msg = getString(R.string.send_to_cancel);
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                //发送被拒绝
                msg = getString(R.string.send_rejected);
                break;
            default:
                //发送返回
                break;
        }
        if(resp.getType() == ConstantsAPI.COMMAND_SENDAUTH){
            Log.d(TAG, "onResp: code --- " + code);
//            SharedPreferencesUtil.getInstance(this, SharedPreferencesUtil.FILE_NAME);
            boolean hasUpdate = SharedPreferencesUtil.putData("wx_code", code);
//            WXEntry wxEntry = WXEntry.getInstance();
//            wxEntry.setCode(code);
//            wxEntry.setMsg(msg);
//            wxEntry.setLogin(true);
//            Intent intent = WXLoginActivity.wxLoginActivity.getIntent();
//            intent.putExtra("code", code);
//            intent.putExtra("msg", msg);
//            intent.putExtra("isLogin", true);
            if (hasUpdate) { // 更新后回去
                finish();
                return;
            }
        } else if (resp.getType() == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX && !hasObtainShareCallback) { // 分享到微信后的回调
            hasObtainShareCallback = true;
            if (getString(R.string.send_successfully).equals(msg)) {
                EventBus.getDefault().post(new HadSharedEvent(true));
            } else if (getString(R.string.send_to_cancel).equals(msg)) {
                EventBus.getDefault().post(new HadSharedEvent(false));
            }
        }
        finish();
    }


}
