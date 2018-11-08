package xiaoe.com.shop.business.upgrade;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import java.io.File;
import xiaoe.com.shop.BuildConfig;

/**
 * Created by Haley.Yang on 2018/5/31.
 * <p>
 * 描述：
 */
public class InstallApkBroadcast extends BroadcastReceiver {
    final String TAG = "InstallApkBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        long ID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        if (ID != -1 && ID == AppUpgradeHelper.getInstance().getRequestId())
            install(context);
    }
    private void install(Context context) {
        try {
            File saveFile = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(saveFile, AppUpgradeHelper.getInstance().getAppName());
            Log.d(TAG,"install-Apk-file = " + file.getAbsolutePath() + "--- is file = " + file.isFile());
            if (!file.isFile()){
                AppUpgradeHelper.getInstance().setDownloading(false);
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
            Log.d(TAG,"install-Apk-error");
        }
    }
}
