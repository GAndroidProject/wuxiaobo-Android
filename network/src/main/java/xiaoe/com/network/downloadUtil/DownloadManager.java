package xiaoe.com.network.downloadUtil;

import android.text.TextUtils;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import xiaoe.com.common.entitys.DownloadTableInfo;

/**
 * 下载管理器，断点续传
 *
 * @author Cheny
 */
public class DownloadManager implements DownloadListner {
    private static final String TAG = "DownloadManager";
    private static DownloadManager mInstance;
    private List<DownloadTask> mDownloadTasks;//下载任务
    private List<DownloadTask> mAwaitDownloadTasks;//等待下载任务
//    private List<DownloadInfo> mDownloadInfoList;//下载列表
//    private List<DownloadInfo> mDownloadFinishList;//下载完成列表
    /**
     * ConcurrentHashMap可以高并发操作，线程安全，高效
     */
    private ConcurrentHashMap<String, String> mSaveProgresss;//缓存下载进度集合
    private SaveProgressThread saveProgressThread;
    private boolean isAllDownloadFinish = true;
    private int MAX_DOWNLOAD_COUNT = 2;
    private DownloadEvent downloadEvent;

    private DownloadManager() {
        mDownloadTasks = new ArrayList<DownloadTask>();
        mAwaitDownloadTasks = new ArrayList<DownloadTask>();
        mSaveProgresss = new ConcurrentHashMap<String, String>();
        getInstanceDownloadEvent();
        createSaveProgressThread();
    }

    public static DownloadManager getInstance() {//管理器初始化
        if (mInstance == null) {
            synchronized (DownloadManager.class) {
                if (mInstance == null) {
                    mInstance = new DownloadManager();
                }
            }
        }
        return mInstance;
    }

    private void getInstanceDownloadEvent() {
        if (downloadEvent == null) {
            downloadEvent = new DownloadEvent();
        }
    }

    private void createSaveProgressThread() {
        if (saveProgressThread == null) {
            saveProgressThread = new SaveProgressThread();
        }
    }


    /**
     * 下载文件
     */
    private void download(DownloadTableInfo download) {
        //单任务开启下载或多任务开启下载
        String resourceId = download.getResourceId();
        String columnId = download.getColumnId();
        String bigColumnId = download.getBigColumnId();
        String appId = download.getAppId();
        for (int i = 0; i < mAwaitDownloadTasks.size(); i++) {
            if (mDownloadTasks.size() >= MAX_DOWNLOAD_COUNT) {
                return;
            }
            DownloadTask downloadTask = mAwaitDownloadTasks.remove(i);
            DownloadTableInfo taskDownloadInfo = downloadTask.getDownloadInfo();
            boolean equals = compareResource(appId, resourceId, columnId, bigColumnId, taskDownloadInfo.getAppId(), taskDownloadInfo.getResourceId(), taskDownloadInfo.getColumnId(), taskDownloadInfo.getBigColumnId());
            if (equals) {
                Log.d(TAG, "download: ----------------");
                mDownloadTasks.add(downloadTask);
                downloadTask.start();
                DownloadTableInfo downloadInfo = downloadTask.getDownloadInfo();
                downloadInfo.setDownloadState(0);
                download.setDownloadState(0);
                //数据库更新----
                DownloadFileConfig.getInstance().insertDownloadInfo(downloadInfo);
                break;
            }
        }
    }

    /**
     * 暂停
     */
    public void pause(DownloadTableInfo download) {
        if (download == null) {
            return;
        }

        for (int i = 0; i < mDownloadTasks.size(); i++) {
            DownloadTask task = mDownloadTasks.get(i);
            DownloadTableInfo info = task.getDownloadInfo();
            boolean equals = compareResource(download.getAppId(), download.getResourceId(), download.getColumnId(), download.getBigColumnId(), info.getAppId(), info.getResourceId(), info.getColumnId(), info.getBigColumnId());
            if (equals) {
                info.setDownloadState(1);
                task.pause();
                mDownloadTasks.remove(i);
                break;
            }
        }
        for (int i = 0; i < mAwaitDownloadTasks.size(); i++) {
            DownloadTask task = mAwaitDownloadTasks.get(i);
            DownloadTableInfo info = task.getDownloadInfo();
            boolean equals = compareResource(download.getAppId(), download.getResourceId(), download.getColumnId(), download.getBigColumnId(), info.getAppId(), info.getResourceId(), info.getColumnId(), info.getBigColumnId());
            if (equals) {
                info.setDownloadState(1);
                mAwaitDownloadTasks.remove(i);
                break;
            }
        }
//        download.setDownloadState(1);
//        String key = download.getColumnResourceId() + "_" + download.getResourceId();
//        DownloadFileConfig.putString(key, download.getDownloadFileJson());
        //更新数据库----
    }

    /**
     * 开始下载
     *
     * @param download
     */
    public void start(DownloadTableInfo download) {
        //开始前查询等待下载列表中是否已存在该下载任务，如果没有存在则添加
        boolean isExistAwaitList = false;
        String resourceId = download.getResourceId();
        String columnId = download.getColumnId();
        String bigColumnId = download.getBigColumnId();
        for (int i = 0; i < mAwaitDownloadTasks.size(); i++) {
            //等待下载任务
            DownloadTask downloadTask = mAwaitDownloadTasks.get(i);
            DownloadTableInfo downloadTableInfo = downloadTask.getDownloadInfo();
            boolean equals = compareResource(download.getAppId(), resourceId, columnId, bigColumnId, downloadTableInfo.getAppId(), downloadTableInfo.getResourceId(), downloadTableInfo.getColumnId(), downloadTableInfo.getBigColumnId());
            if (equals) {
                Log.d(TAG, "start: ++++++++");
                isExistAwaitList = true;
                break;
            }
        }
        //判断是否正在下载，如果是，则不做操作
        for (int i = 0; i < mDownloadTasks.size(); i++) {
            //正在下载任务
            DownloadTask downloadTask = mDownloadTasks.get(i);
            DownloadTableInfo downloadTableInfo = downloadTask.getDownloadInfo();
            boolean equals = compareResource(download.getAppId(), resourceId, columnId, bigColumnId, downloadTableInfo.getAppId(), downloadTableInfo.getResourceId(), downloadTableInfo.getColumnId(), downloadTableInfo.getBigColumnId());
            if (equals) {
                Log.d(TAG, "start: ***********");
                return;
            }
        }
        if (!isExistAwaitList) {
            mAwaitDownloadTasks.add(new DownloadTask(download, this));
        }
        download(download);
//        startDownloadImage(download);
//        if (isAllDownloadFinish) {
//            isAllDownloadFinish = false;
//            saveProgressThread.start();
//        }
    }

    //比较两个资源是否相同
    private boolean compareResource(String appId, String resourceId, String columnId, String bigColumnId, String newAppId, String newResId, String newColumnId, String newBigColumnId) {
        return appId.equals(newAppId) && resourceId.equals(newResId) && columnId.equals(newColumnId) && bigColumnId.equals(newBigColumnId);
    }

    /**
     * 是否存在下载列表
     *
     * @param download
     * @return
     */
    public DownloadTableInfo isExistInDownloadList(DownloadTableInfo download) {
        DownloadTableInfo existDownloadInfo = null;
        //直接查询数据库是否存在
        return existDownloadInfo;
    }

    /**
     * 是否有效下载 false=无效，true=有效
     */
    private boolean isValidDownload(DownloadTableInfo download) {
        if (download == null) {
            return false;
        }
        String url = download.getFileDownloadUrl();
        if (TextUtils.isEmpty(url.trim())) {
            return false;
        }
        return true;
    }

    /**
     * 移除下载
     *
     * @param download
     */
    public void removeDownload(DownloadTableInfo download) {
        String resourceId = download.getResourceId();
        String columnId = download.getColumnId();
        String bigColumnId = download.getBigColumnId();
        for (int i = 0; i < mDownloadTasks.size(); i++) {
            DownloadTask task = mDownloadTasks.get(i);
            DownloadTableInfo info = task.getDownloadInfo();
            boolean equals = compareResource(download.getAppId(), resourceId, columnId, bigColumnId, info.getAppId(), info.getResourceId(), info.getColumnId(), info.getBigColumnId());
            if (equals) {
                task.pause();
                task.cancel();
                mDownloadTasks.remove(i);
                //更新数据库
                break;
            }
        }
        for (int i = 0; i < mAwaitDownloadTasks.size(); i++) {
            DownloadTask task = mAwaitDownloadTasks.get(i);
            DownloadTableInfo info = task.getDownloadInfo();
            boolean equals = compareResource(download.getAppId(), resourceId, columnId, bigColumnId, info.getAppId(), info.getResourceId(), info.getColumnId(), info.getBigColumnId());
            if (equals) {
                //更新数据库
                mAwaitDownloadTasks.remove(i);
                break;
            }
        }
    }

    /**
     * 添加下载任务
     */
    public void add(DownloadTableInfo download) {
        //是否存在下载列表，如果为空则不存在
        DownloadTableInfo existDownloadInfo = isExistInDownloadList(download);
        if (existDownloadInfo != null) {
            Log.d(TAG, "add: exist download list");
            start(existDownloadInfo);
            return;
        }
        if (new File(download.getLocalFilePath()+"/"+download.getFileName()).exists()) {
            //如果文件已经存在，说明已经下载过，并已下载完
            Log.d(TAG, "add:is download exist and download finish");
            return;
        }
        Log.d(TAG, "add: add finish");
//        DownloadFileConfig.putString(key, download.getDownloadFileJson());
//        DownloadFileConfig.saveDetails(key, download.getResourceDetails());
        //更新数据库
        mAwaitDownloadTasks.add(new DownloadTask(download, this));
        start(download);
        sendDownlaodEvent(download, 0, 1101);
    }


    /**
     * 获取下载任务
     *
     * @return
     */
    public List<DownloadTask> getDownloadTasks() {
        return mDownloadTasks;
    }




    @Override
    public void onDownloadFinished(DownloadTableInfo downloadInfo) {
        Log.d(TAG, "onDownloadFinished: ");
        sendDownlaodEvent(downloadInfo, 1, 3);
    }

    @Override
    public void onDownloadProgress(DownloadTableInfo downloadInfo, float progress) {
        Log.d(TAG, "onDownloadProgress: ");
        sendDownlaodEvent(downloadInfo, progress, 0);
    }

    @Override
    public void onDownloadPause(DownloadTableInfo downloadInfo, float progress) {
        Log.d(TAG, "onDownloadPause: ");
        sendDownlaodEvent(downloadInfo, progress, 1);
    }

    @Override
    public void onDownloadCancel(DownloadTableInfo downloadInfo) {
        Log.d(TAG, "onDownloadCancel: ");
        sendDownlaodEvent(downloadInfo, 0, -1);
    }

    @Override
    public void onDownloadError(DownloadTableInfo downloadInfo, int errorStatus) {
        Log.d(TAG, "onDownloadError: ");
        sendDownlaodEvent(downloadInfo, 0, errorStatus);
    }

    /**
     * 发送下载事件
     *
     * @param downloadInfo
     * @param progress
     * @param status
     */
    private void sendDownlaodEvent(DownloadTableInfo downloadInfo, float progress, int status) {
        getInstanceDownloadEvent();
        downloadEvent.setDownloadInfo(downloadInfo);
        downloadEvent.setProgress(progress);
        downloadEvent.setStatus(status);

        String resourceId = downloadInfo.getResourceId();
        String columnId = downloadInfo.getColumnId();
        String bigColumnId = downloadInfo.getBigColumnId();
        String key = columnId + "_" + resourceId;
        if (status == 0) {
            //正在下载
            Log.d(TAG, "sendDownlaodEvent: 正在下载");
        } else if (status == 1) {
            //下载暂停
            Log.d(TAG, "sendDownlaodEvent: 下载暂停");
        } else if (status == 3) {
            //下载完成
            Log.d(TAG, "sendDownlaodEvent: 完成");

        } else if (status == -1) {
            //取消下载
//            DownloadFileConfig.remove(resourceId);
//            DownloadFileConfig.removeProgress(resourceId);
        } else if (status == -40004 || status == -60000) {
            //-40004:请求失败
            //-60000:网络异常
//            pause(downloadInfo);
            Log.d(TAG, "sendDownlaodEvent: 请求失败");
        }
        EventBus.getDefault().post(downloadEvent);
    }







    /**
     * 自动下载下一个等待下载任务
     */
    private void autoDownloadNextAwaitTask() {
        Log.d(TAG, "autoDownloadNextAwaitTask: " + mAwaitDownloadTasks.size());
        if (mAwaitDownloadTasks.size() > 0) {
            start(mAwaitDownloadTasks.get(0).getDownloadInfo());
        }
    }

    class SaveProgressThread extends Thread {
        //保存下载进度线程
        @Override
        public void run() {
            while (mDownloadTasks.size() > 0) {
                for (Map.Entry<String, String> entry : mSaveProgresss.entrySet()) {
//                    DownloadFileConfig.updataProgress(entry.getKey(), entry.getValue());
//                    mSaveProgresss.remove(entry.getKey());
                }
            }
            isAllDownloadFinish = true;
            Log.d(TAG, "SaveProgressThread: all download finish");
        }
    }
}
