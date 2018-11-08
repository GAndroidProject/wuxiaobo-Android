package xiaoe.com.shop.business.upgrade;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Haley.Yang on 2018/5/31.
 * <p>
 * 描述：
 */
public class DownLoadRunnable implements Runnable {
    private String url;
    private Handler handler;
    private Context mContext;
    private String appName;
    public long requestId = -1;
    private int lastProgress = 0;

    public long getRequestId() {
        return requestId;
    }

    public DownLoadRunnable(Context context, String url, String appName) {
        this(context,url,null,appName);
    }

    public DownLoadRunnable(Context context, String url, Handler handler, String appName) {
        this.mContext = context;
        this.url = url;
        this.handler = handler;
        this.appName = appName;
        lastProgress = 0;
    }

    @Override
    public void run() {
        //设置线程优先级为后台，这样当多个线程并发后很多无关紧要的线程分配的CPU时间将会减少，有利于主线程的处理
//        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        //具体下载方法
        startDownload();
    }

    private long startDownload() {
        //获得DownloadManager对象
        DownloadManager downloadManager=(DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        //获得下载id，这是下载任务生成时的唯一id，可通过此id获得下载信息
        try {
            requestId = downloadManager.enqueue(CreateRequest(url));
            //查询下载信息方法
            queryDownloadProgress(requestId, downloadManager);
        }catch (Exception e){
            e.printStackTrace();
        }
        return requestId;
    }

    private void queryDownloadProgress(long requestId, DownloadManager downloadManager) {
        DownloadManager.Query query=new DownloadManager.Query();
        //根据任务编号id查询下载任务信息
        query.setFilterById(requestId);
        try {
            boolean isStarted = false;
            boolean isGoing=true;
            while (isGoing) {
                Cursor cursor = downloadManager.query(query);
                if (cursor != null && cursor.moveToFirst()) {

                    //获得下载状态
                    int state = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    switch (state) {
                        case DownloadManager.STATUS_SUCCESSFUL://下载成功
                            isGoing = false;
                            if (handler != null)
                                handler.obtainMessage(downloadManager.STATUS_SUCCESSFUL).sendToTarget();//发送到主线程，更新ui
                            else    EventBus.getDefault().post(new UpgradeProgressUpdateEvent(downloadManager.STATUS_SUCCESSFUL));
                            break;
                        case DownloadManager.STATUS_FAILED://下载失败
                            isGoing = false;
                            if (handler != null) {
                                handler.obtainMessage(downloadManager.STATUS_FAILED).sendToTarget();//发送到主线程，更新ui
                            }else   EventBus.getDefault().post(new UpgradeProgressUpdateEvent(downloadManager.STATUS_FAILED));
                            break;
                        case DownloadManager.STATUS_RUNNING://下载中
                            /**
                             * 计算下载下载率；
                             */
                            int totalSize = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                            int currentSize = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                            int progress = (int) (((float) currentSize) / ((float) totalSize) * 100);
                            if (progress - lastProgress >= 1){
                                if (handler != null)
                                    handler.obtainMessage(downloadManager.STATUS_RUNNING, progress).sendToTarget();//发送到主线程，更新ui
                                else    EventBus.getDefault().post(new UpgradeProgressUpdateEvent(downloadManager.STATUS_RUNNING, progress));
                                lastProgress = progress;
                            }
                            break;
                        case DownloadManager.STATUS_PAUSED://下载停止
                            isGoing = false;
                            if (handler != null)
                                handler.obtainMessage(DownloadManager.STATUS_PAUSED).sendToTarget();
                            else    EventBus.getDefault().post(new UpgradeProgressUpdateEvent(downloadManager.STATUS_PAUSED));
                            break;
                        case DownloadManager.STATUS_PENDING://准备下载
                            if (isStarted)  break;
                            if (handler != null)
                                handler.obtainMessage(DownloadManager.STATUS_PENDING).sendToTarget();
                            else    EventBus.getDefault().post(new UpgradeProgressUpdateEvent(downloadManager.STATUS_PENDING));
                            isStarted = true;
                            lastProgress = 0;
                            break;
                    }
                }
                if(cursor!=null){
                    cursor.close();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private DownloadManager.Request CreateRequest(String url) {
        DownloadManager.Request  request=new DownloadManager.Request(Uri.parse(url));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);// 隐藏notification
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);//设置下载网络环境为wifi
        request.setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_DOWNLOADS,appName);//指定apk缓存路径，默认是在SD卡中的Download文件夹
        return  request;
    }
}
