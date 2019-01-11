package com.xiaoe.common.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.xiaoe.common.utils.CommonUtils;
import com.xiaoe.common.utils.SharedPreferencesUtil;
import com.xiaoe.common.widget.CommonRefreshHeader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import cn.jpush.android.api.JPushInterface;

/**
 * @author Administrator
 * @date 2017/7/17
 */
public class XiaoeApplication extends MultiDexApplication {

    private static final String TAG = "XiaoeApplication";
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    /**
     * 是否正式环境
     */
    private static boolean isFormalCondition;
    public static volatile Context applicationContext;
    public static volatile Handler applicationHandler;

    static { // SmartRefreshLayout 全局配置，写在这里防止内存泄漏
        //全局设置默认的 Header
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull RefreshLayout layout) {
                //开始设置全局的基本参数（这里设置的属性只跟下面的MaterialHeader绑定，其他Header不会生效，能覆盖DefaultRefreshInitializer的属性和Xml设置的属性）
                // layout.setEnableHeaderTranslationContent(false);
                return new CommonRefreshHeader(mContext);
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 获取当前包名
        String packageName = getPackageName();
        // 获取当前进程名
        String processName = getProcessName(android.os.Process.myPid());
        // 修复application会创建多次，初始化数据多次
        if (!TextUtils.isEmpty(packageName) && !packageName.equals(processName)) {
            return;
        }
        Global.g().setApplication(this);
        isFormalCondition = false;
        mContext = getApplicationContext();
        applicationContext = getApplicationContext();
        applicationHandler = new Handler(applicationContext.getMainLooper());

        ImagePipelineConfig imagePipelineConfig = ImagePipelineConfig.newBuilder(mContext)
                .setDownsampleEnabled(true)
                .build();
        Fresco.initialize(mContext,imagePipelineConfig);

        //初始化一下库，在子线程初始化
        init();
    }
    public static Context getmContext() {
        return mContext;
    }

    public static boolean isFormalCondition() {
        return isFormalCondition;
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }
    private void init(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //↓↓↓↓↓↓↓友盟集成初始化↓↓↓↓↓↓↓
                UMConfigure.setLogEnabled(true);
                UMConfigure.init(mContext, Constants.getUMAppId(), CommonUtils.getChannel(XiaoeApplication.applicationContext), UMConfigure.DEVICE_TYPE_PHONE, "");
                PlatformConfig.setWeixin(Constants.getWXAppId(), Constants.getWxSecret());
                // 初始化 SharedPreference
                SharedPreferencesUtil.getInstance(mContext, SharedPreferencesUtil.FILE_NAME);
                SharedPreferencesUtil.putData(SharedPreferencesUtil.KEY_WX_PLAY_CODE, -100);
                MobclickAgent.setScenarioType(mContext, MobclickAgent.EScenarioType.E_UM_NORMAL);
                //↑↑↑↑↑↑↑友盟集成初始化↑↑↑↑↑↑↑

                // 设置开启日志,发布时请关闭日志
                JPushInterface.setDebugMode(!isFormalCondition);
                // 初始化 JPush
                JPushInterface.init(mContext);

                //↓↓↓↓↓↓↓↓初始化bugly↓↓↓↓↓↓↓
                /**
                 * 如果使用了MultiDex  请参照bugly注意事项https://bugly.qq.com/docs/user-guide/instruction-manual-android/?v=20181014122344#_2
                 *
                 * */
                Context context = getApplicationContext();
                // 获取当前包名
                String packageName = context.getPackageName();
                // 获取当前进程名
                String processName = getProcessName(android.os.Process.myPid());
                // 设置是否为上报进程
                CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
                strategy.setUploadProcess(processName == null || processName.equals(packageName));
                // 初始化Bugly
                CrashReport.initCrashReport(context, Constants.getBuglyAppId(), !isFormalCondition, strategy);
            }
        }).start();
    }
}
