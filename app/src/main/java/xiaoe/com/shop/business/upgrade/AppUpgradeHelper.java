package xiaoe.com.shop.business.upgrade;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.io.File;
import xiaoe.com.shop.BuildConfig;
import xiaoe.com.shop.R;

/**
 * Created by Haley.Yang on 2018/5/31.
 * <p>
 * 描述：
 */
public class AppUpgradeHelper {

    final String TAG = "AppUpgradeHelper";

    private static class Holder {
        private static final AppUpgradeHelper INSTANCE = new AppUpgradeHelper();
    }

    public static AppUpgradeHelper getInstance() {
        return Holder.INSTANCE;
    }

    Context mContext;
    Activity mActivity;
    ProgressDialog mProgressDialog;
    DownLoadRunnable mDownLoadRunnable;
//    MyHandler mHandler;
    String versionName;
    boolean isForceUpdate = false;
    boolean isDownloading = false;
    boolean isRequesting = false;
    private boolean hasUpgradeCurrentApp = false;
    private String appName;
    AlertDialog mAlertDialog;
    UpgradeResult.Data mData;

    public boolean isHasUpgradeCurrentApp() {
        return hasUpgradeCurrentApp;
    }

    public void setHasUpgradeCurrentApp(boolean hasUpgradeCurrentApp) {
        this.hasUpgradeCurrentApp = hasUpgradeCurrentApp;
    }

    public void setDownloading(boolean downloading) {
        isDownloading = downloading;
    }

    public void registerEventBus(){
        isDownloading = false;
        hasUpgradeCurrentApp = false;
//        EventBus.getDefault().register(this);
    }

    public void unregisterEventBus(){
        isDownloading = false;
        hasUpgradeCurrentApp = false;
//        if (EventBus.getDefault().isRegistered(this))
//            EventBus.getDefault().unregister(this);
    }

    /**
     * 检查更新
     */
    public void checkUpgrade(final boolean isManual, Activity activity){
//        RequestService service = null;
//        Object object = ApiManager.INSTANCE.getApiService(API_TAG,RequestService.class);
//        if (object != null && object instanceof RequestService)
//            service = (RequestService)object;
//        if (isRequesting || service == null)   return;//是正在请求
        mActivity = activity;
        mContext = activity.getApplicationContext();
        if (isDownloading){
            if (isManual)
                Toast(mContext, mContext.getString(R.string.upgrade_downloading));
            return;
        }
        isRequesting = true;
        versionName = getVersionName(mContext);

//        final int code = 121212;
//        AsyncTaskManager.getInstance(mContext).request(code, new OnDataListener() {
//            @Override
//            public Object doInBackground(int requestCode, String parameter) throws HttpException {
//                return new SealAction(mContext).checkAppUpgrade("android",versionName,UtilsKt.getChannel(mContext),10120);
//            }
//
//            @Override
//            public void onSuccess(int requestCode, Object result) {
//                if (code == requestCode && result instanceof UpgradeResult){
//                    requestHandle((UpgradeResult) result, isManual);
//                }
//            }
//
//            @Override
//            public void onFailure(int requestCode, int state, Object result) {
//                isRequesting = false;
//            }
//        });

//        service.checkAppUpgrade("android",versionName, UtilsKt.getChannel(mContext),10120)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(result -> requestHandle(result, isManual),
//                        throwable -> {
//                            isRequesting = false;
//                            LogUtils.e("checkUpgrade error -> " + throwable);
//                        });
    }

    private void Toast(Context context,String msg){
        if (context != null && !TextUtils.isEmpty(msg))
            Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    private void requestHandle(@NonNull UpgradeResult result, boolean isManual) {
        isRequesting = false;
        log("checkUpgrade -> " + result.toString());
        if (mActivity == null)   return;
        if (result != null && 200 == result.code && result.result != null){
            UpgradeResult.Data data = result.result;
            mData = data;

//          data.update_mode = 1;
            if (1 == data.is_update){//有更新
                setHasUpgradeCurrentApp(true);
                EventBus.getDefault().post(new IsHasUpgradeEvent(true));

                StringBuffer content = new StringBuffer();
                if (data.msg != null && data.msg.length > 0)
                    for (int i = 0; i < data.msg.length; i++) {
                        content.append(data.msg[i]);
                        if (i < data.msg.length -1)
                            content.append("<br/>");
                    }

                isForceUpdate = 1 == data.update_mode;
                if (0 == data.update_mode || 1 == data.update_mode || isManual)
                    showUpgradeDialog(data.download_url,data.target_version,content.toString(),mActivity);
            }else if (0 == data.is_update && isManual) {
                setHasUpgradeCurrentApp(false);
                EventBus.getDefault().post(new IsHasUpgradeEvent(false));
                Toast(mContext, mContext.getString(R.string.upgrade_already_latest_version));//你已经是最新版了
            }
        } else if (result != null && !TextUtils.isEmpty(result.msg) && isManual) {
            Toast(mContext, result.msg);
        }
    }

    /**
     * 展示app版本更新提示对话框
     * @param downloadUrl
     * @param versionName
     * @param upgradeContent
     * @param activity
     */
    private void showUpgradeDialog(String downloadUrl,String versionName,String upgradeContent, Activity activity){
        if (TextUtils.isEmpty(downloadUrl) || activity == null || (mAlertDialog != null && mAlertDialog.isShowing()))
            return;
        // 这里的属性可以一直设置，因为每次设置后返回的是一个builder对象
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        mAlertDialog = builder.create();
        mAlertDialog.setCancelable(false);
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //显示对话框
        mAlertDialog.show();
        View view = getUpgradeDialogView(downloadUrl, versionName, upgradeContent, activity);
        mAlertDialog.setContentView(view);
    }

    @NonNull
    private View getUpgradeDialogView(final String downloadUrl, final String versionName, String upgradeContent, final Activity activity) {
//        String content = TextUtils.isEmpty(upgradeContent) ? activity.getString(R.string.upgrade_my_content) : upgradeContent;
//        View view = activity.getLayoutInflater().inflate(R.layout.layout_upgrade,null);
//        ((TextView)view.findViewById(R.id.tv_version_name)).setText("V" + versionName);
//        ((TextView)view.findViewById(R.id.tv_upgrade_feature)).setText(Html.fromHtml(content));
//        TextView cancel = view.findViewById(R.id.btn_cancel);
//        cancel.setVisibility(isForceUpdate ? View.GONE : View.VISIBLE);
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mAlertDialog != null)
//                    mAlertDialog.dismiss();
////                        EventReportMgr.onEvent(mContext, "monitor_updateTip_cancel", getChannel(mContext));
//            }
//        });
//
//        TextView confirm = view.findViewById(R.id.btn_confirm);
//        confirm.setBackgroundResource(isForceUpdate ? R.drawable.btn_bottom_radio : R.drawable.btn_right_bottom_radio);
//        confirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mAlertDialog != null)
//                    mAlertDialog.dismiss();
//                startDownload(activity,downloadUrl,versionName);//下载最新的版本程序
////                        EventReportMgr.onEvent(mContext, "monitor_updateTip_update", getChannel(mContext));
//            }
//        });
        return null;
    }

    /**
     * 开始下载
     * @param activity
     * @param downloadUrl
     * @param versionName
     */
    private void startDownload(Activity activity, String downloadUrl, String versionName){
        if (TextUtils.isEmpty(downloadUrl) || (!downloadUrl.startsWith("http") && !downloadUrl.startsWith("https")))     return;
        appName = "xiaoe_shop" + versionName + ".apk";
        File saveFile = activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(saveFile, AppUpgradeHelper.getInstance().getAppName());
        if (file != null && file.isFile()){
            log("file.delete = " + file.getAbsolutePath() + "--- is file = " + file.isFile());
            file.delete();
        }
//        if (mHandler == null)
//            mHandler = new MyHandler(activity);
        mDownLoadRunnable = new DownLoadRunnable(mContext,downloadUrl,appName);
        new Thread(mDownLoadRunnable).start();
        if (activity != null)
            showDialog(activity);
    }

    /**
     * 初始化百分比下载进度对话框
     * @param context
     */
    private void initProgressDialog(Context context) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setMessage(mContext.getString(R.string.upgrade_downloading));
        }
    }

//    class MyHandler extends Handler {
//        WeakReference<Activity> mActivityWeakReference = null;
//        public MyHandler(Activity activity){
//            mActivityWeakReference = new WeakReference<Activity>(activity);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (mActivityWeakReference == null)
//                return;
//            AppUpgradeHelper.this.handleMessage(msg,mActivityWeakReference.get());
//        }
//    }

    /**
     * 在数据返回到UI线程中处理
     * @param event
     */
    @Subscribe
    public void onEventMainThread(UpgradeProgressUpdateEvent event) {
        if (event != null){
            long id = mDownLoadRunnable != null ? mDownLoadRunnable.requestId : -1;
            if (-1 == id || id != getRequestId())
                return;
            switch (event.state){
                case DownloadManager.STATUS_SUCCESSFUL://下载成功
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.setProgress(100);
                    canceledDialog(true);
                    log("install-Apk-file = 下载任务已经完成！");
//                    Toast.makeText(mContext, "下载任务已经完成！", Toast.LENGTH_SHORT).show();
                    break;

                case DownloadManager.STATUS_RUNNING://下载中
                    //int progress = (int) msg.obj;
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.setProgress(event.progress);
                        isDownloading = true;
                    }
                    //canceledDialog();
                    break;

                case DownloadManager.STATUS_FAILED://下载失败
                    canceledDialog(false);
                    break;
                case DownloadManager.STATUS_PAUSED://下载停止
                    canceledDialog(false);
                    break;
                case DownloadManager.STATUS_PENDING://准备下载
//                showDialog(activity);
                    break;
            }
        }
    }

//    private void handleMessage(Message msg, Activity activity) {
//        long id = mDownLoadRunnable != null ? mDownLoadRunnable.requestId : -1;
//        if (-1 == id || id != getRequestId())
//            return;
//        switch (msg.what){
//            case DownloadManager.STATUS_SUCCESSFUL://下载成功
//                if (mProgressDialog != null && mProgressDialog.isShowing())
//                    mProgressDialog.setProgress(100);
//                canceledDialog(true);
//                log("install-Apk-file = 下载任务已经完成！");
////                    Toast.makeText(mContext, "下载任务已经完成！", Toast.LENGTH_SHORT).show();
//                break;
//
//            case DownloadManager.STATUS_RUNNING://下载中
//                //int progress = (int) msg.obj;
//                if (mProgressDialog != null && mProgressDialog.isShowing())
//                    mProgressDialog.setProgress((int) msg.obj);
//                isDownloading = true;
//                //canceledDialog();
//                break;
//
//            case DownloadManager.STATUS_FAILED://下载失败
//                canceledDialog(false);
//                break;
//            case DownloadManager.STATUS_PAUSED://下载停止
//                canceledDialog(false);
//                break;
//            case DownloadManager.STATUS_PENDING://准备下载
////                showDialog(activity);
//                break;
//        }
//    }

    /**
     * 百分比下载进度对话框消失
     */
    private void canceledDialog(boolean isDownloadSuccess) {
        isDownloading = false;
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        if (isForceUpdate){
            if (mActivity == null)  return;
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle(mActivity.getString(R.string.app_name))
                    .setMessage(isDownloadSuccess ? R.string.upgrade_version_downloaded : R.string.upgrade_version_download_fail)
                    .setNegativeButton(R.string.upgrade_redownload, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mData == null || mActivity == null)  return;
                            startDownload(mActivity,mData.download_url,mData.target_version);
                        }
                    });
            if (isDownloadSuccess)  builder.setPositiveButton(R.string.upgrade_install, null);

            AlertDialog alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    install(mContext);
                }
            });
        }else if (!isDownloadSuccess)
            Toast(mContext,mContext.getString(R.string.upgrade_version_download_fail));
    }

    /**
     * 百分比下载进度对话框展示
     */
    private void showDialog(Context context) {
        initProgressDialog(context);
        mProgressDialog.setCancelable(!isForceUpdate);//不能手动取消下载进度对话框
        mProgressDialog.setCanceledOnTouchOutside(!isForceUpdate);
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
            mProgressDialog.setProgress(0);
        }
        isDownloading = true;
    }

    public String getAppName() {
        return appName;
    }

    public long getRequestId() {
        return mDownLoadRunnable != null ? mDownLoadRunnable.getRequestId() : -1;
    }

    /**
     * 从包信息中获取版本号
     * @param context
     * @return
     */
    private String getVersionName(Context context){
        try{
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        }catch(PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void install(Context context) {
        try {
            File saveFile = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(saveFile, AppUpgradeHelper.getInstance().getAppName());
            log("install-Apk-file = " + file.getAbsolutePath() + "--- is file = " + file.isFile());
            if (!file.isFile()){
                isDownloading = false;
                return;
            }

            Intent installIntent = new Intent();
            installIntent.setAction(Intent.ACTION_VIEW);
            // 在Broadcast中启动活动需要添加Intent.FLAG_ACTIVITY_NEW_TASK
            installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri data;
            if (Build.VERSION.SDK_INT >= 24) {
                installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
                data = FileProvider.getUriForFile(context.getApplicationContext(), BuildConfig.APPLICATION_ID + ".DataProvider", file);
            } else {
                data = Uri.fromFile(file);
            }
            installIntent.setDataAndType(data,"application/vnd.android.package-archive");//存储位置为Android/data/包名/file/Download文件夹
            context.startActivity(installIntent);
        }catch (Exception e){
            e.printStackTrace();
            log("install-Apk-error");
        }
    }

    private void log(String msg){
        Log.d(TAG,msg);
    }

}