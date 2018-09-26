package xiaoe.com.common.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

import java.util.LinkedHashMap;
import java.util.Map;

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
        this.isFormalCondition = true;
        this.mContext = getApplicationContext();
        //初始化Fresco图片加载库
        ImagePipelineConfig imagePipelineConfig = ImagePipelineConfig.newBuilder(mContext).setDownsampleEnabled(true).build();
        Fresco.initialize(this,imagePipelineConfig);

    }
    public static Context getmContext() {
        return mContext;
    }

    public static boolean isFormalCondition() {
        return isFormalCondition;
    }
}
