package xiaoe.com.common.app;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.tencent.smtt.sdk.QbSdk;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

import xiaoe.com.common.utils.SharedPreferencesUtil;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2017/7/17.
 */

public class XiaoeApplication extends Application {
    private static final String TAG = "XiaoeApplication";
    private static Context mContext;
    //是否正式环境
    private static boolean isFormalCondition;

    @Override
    public void onCreate() {
        super.onCreate();
        Global.g().setApplication(this);
        this.isFormalCondition = false;
        this.mContext = getApplicationContext();
        //初始化Fresco图片加载库
        ImagePipelineConfig imagePipelineConfig = ImagePipelineConfig.newBuilder(mContext).setDownsampleEnabled(true).build();
        Fresco.initialize(this,imagePipelineConfig);
        //腾讯x5内核初始化
        QbSdk.initX5Environment(mContext, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
            }

            @Override
            public void onViewInitFinished(boolean b) {
            }
        });
        //友盟集成初始化
        UMConfigure.setLogEnabled(true);
        UMConfigure.init(this, Constants.getUMAppId()
                ,"umeng",UMConfigure.DEVICE_TYPE_PHONE,"");
        PlatformConfig.setWeixin(Constants.getWXAppId(), Constants.getWxSecret());
        SharedPreferencesUtil.getInstance(mContext, SharedPreferencesUtil.FILE_NAME);
        SharedPreferencesUtil.putData(SharedPreferencesUtil.KEY_WX_PLAY_CODE, -100);

        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
    }
    public static Context getmContext() {
        return mContext;
    }

    public static boolean isFormalCondition() {
        return isFormalCondition;
    }
}
