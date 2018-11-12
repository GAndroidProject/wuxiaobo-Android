package xiaoe.com.network.downloadUtil;

import android.text.TextUtils;
import android.util.Log;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Call;
import okhttp3.Response;
import xiaoe.com.common.entitys.DownloadTableInfo;
import xiaoe.com.network.utils.ThreadPoolUtils;

/**
 * Created by Cheny on 2017/4/29.
 */

public class DownloadTask{
    private static final String TAG = "DownloadTask";
    private final int MSG_WAIT= 0;//
    private final int MSG_PROGRESS = 1;//进度
    private final int MSG_FINISH = 2;//完成下载
    private final int MSG_PAUSE = 3;//暂停
    private final int MSG_CANCEL = 4;//暂停
    private final int MSG_REQUEST_FAILURE = -40004;//请求失败
    private final int MSG_NETWORK_ERROR = -60000;//网络异常
    private final int THREAD_COUNT = 1;//线程数
    private long mFileLength;


    private boolean isDownloading = false;
    private int childCanleCount;//子线程取消数量
    private int childPauseCount;//子线程暂停数量
    private int childFinshCount;
    private HttpUtil mHttpUtil;
    private long[] mProgress;
    private File[] mCacheFiles;
    private File mTmpFile;//临时占位文件
    private boolean pause;//是否暂停
    private boolean cancel;//是否取消下载

    private DownloadListner mListner;//下载回调监听
    private DownloadTableInfo mDownloadInfo ;//下载资源信息

    private long lastProgress = 0;//上一次的进度

    private int currentDownloadState = MSG_WAIT;

    private DownloadListenerThread downloadListenreThread;
    /**
     * 任务管理器初始化数据
     * @param downloadInfo
     * @param l
     */
    DownloadTask(DownloadTableInfo downloadInfo , DownloadListner l) {
        this.mListner = l;
        this.mProgress = new long[THREAD_COUNT];
        this.mCacheFiles = new File[THREAD_COUNT];
        this.mHttpUtil = HttpUtil.getInstance();
        this.mDownloadInfo = downloadInfo;
    }

    private long getCurrentProgress(){
        long progress = 0;
        for (int i = 0, length = mProgress.length; i < length; i++) {
            progress += mProgress[i];
        }
        return progress;
    }

    public synchronized void start() {
        currentDownloadState = MSG_WAIT;
        if(downloadListenreThread == null){
            downloadListenreThread = new DownloadListenerThread();
        }
        try {
            if(TextUtils.isEmpty(mDownloadInfo.getFileDownloadUrl())){
//                sendEmptyMessage(MSG_NETWORK_ERROR);
                currentDownloadState = MSG_NETWORK_ERROR;
                downloadListenreThread.start();
                return;
            }
            childCanleCount = 0;
            childPauseCount = 0;
            childFinshCount = 0;
            if (isDownloading) return;
            Log.d(TAG, "start: ---------");
            downloadListenreThread.start();
            isDownloading = true;
            mHttpUtil.getContentLength(mDownloadInfo.getFileDownloadUrl(), new okhttp3.Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.code() != 200) {
                        close(response.body());
                        resetStutus();
                        currentDownloadState = MSG_REQUEST_FAILURE;
                        return;
                    }
                    // 获取资源大小
                    mFileLength = response.body().contentLength();
                    mDownloadInfo.setTotalSize(mFileLength);
                    DownloadFileConfig.getInstance().updateDownloadInfo(mDownloadInfo);
                    close(response.body());
                    // 在本地创建一个与资源同样大小的文件来占位
                    mTmpFile = new File(mDownloadInfo.getLocalFilePath(), mDownloadInfo.getFileName() + ".tmp");
                    if (!mTmpFile.getParentFile().exists()){
                        mTmpFile.getParentFile().mkdirs();
                    }
                    RandomAccessFile tmpAccessFile = new RandomAccessFile(mTmpFile, "rw");
                    tmpAccessFile.setLength(mFileLength);
                    /*将下载任务分配给每个线程*/
                    long blockSize = mFileLength / THREAD_COUNT;// 计算每个线程理论上下载的数量.

                    /*为每个线程配置并分配任务*/
                    for (int threadId = 0; threadId < THREAD_COUNT; threadId++) {
                        final long startIndex = threadId * blockSize; // 线程开始下载的位置
                        long endIndex = (threadId + 1) * blockSize - 1; // 线程结束下载的位置
                        if (threadId == (THREAD_COUNT - 1)) { // 如果是最后一个线程,将剩下的文件全部交给这个线程完成
                            endIndex = mFileLength - 1;
                        }
                        final long finalEndIndex = endIndex;
                        final int finalThreadId = threadId;
                        ThreadPoolUtils.runTaskOnThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    download(startIndex, finalEndIndex, finalThreadId);// 开启线程下载
                                } catch (IOException e) {
//                                    e.printStackTrace();
                                    e.printStackTrace();
//                                    sendEmptyMessage(MSG_REQUEST_FAILURE);
                                    currentDownloadState = MSG_REQUEST_FAILURE;
                                }
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
//                    sendEmptyMessage(MSG_REQUEST_FAILURE);
                    currentDownloadState = MSG_REQUEST_FAILURE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
//            sendEmptyMessage(MSG_REQUEST_FAILURE);
            currentDownloadState = MSG_REQUEST_FAILURE;
        }
    }

    private void download(final long startIndex, final long endIndex, final int threadId) throws IOException {
        long newStartIndex = startIndex;
        // 分段请求网络连接,分段将文件保存到本地.
        // 加载下载位置缓存文件
        final File cacheFile = new File(mDownloadInfo.getLocalFilePath(), "thread" + threadId + "_" + mDownloadInfo.getFileName() + ".cache");
        Log.d(TAG, "download: thread_"+threadId);
        mCacheFiles[threadId] = cacheFile;
        final RandomAccessFile cacheAccessFile = new RandomAccessFile(cacheFile, "rwd");
        if (cacheFile.exists()) {// 如果文件存在
            String startIndexStr = cacheAccessFile.readLine();
            if(!TextUtils.isEmpty(startIndexStr)){
                try {
                    newStartIndex = Integer.parseInt(startIndexStr);//重新设置下载起点
                } catch (NumberFormatException e) {
//                    e.printStackTrace();
                    currentDownloadState = MSG_REQUEST_FAILURE;
                }
            }
        }
        final long finalStartIndex = newStartIndex;
        mHttpUtil.downloadFileByRange(mDownloadInfo.getFileDownloadUrl(), finalStartIndex, endIndex, new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onResponse: "+response.code());
                if (response.code() != 206) {// 206：请求部分资源成功码
                    resetStutus();
                    currentDownloadState = MSG_REQUEST_FAILURE;
                    return;
                }
                InputStream is = response.body().byteStream();// 获取流
                RandomAccessFile tmpAccessFile = new RandomAccessFile(mTmpFile, "rw");// 获取前面已创建的文件.
                tmpAccessFile.seek(finalStartIndex);// 文件写入的开始位置.
                  /*  将网络流中的文件写入本地*/
                byte[] buffer = new byte[1024 << 2];
                int length = -1;
                int total = 0;// 记录本次下载文件的大小
                long progress = 0;
                try {
                    while ((length = is.read(buffer)) > 0) {
                        if (cancel) {
                            //关闭资源
                            close(cacheAccessFile, is, response.body());
                            cleanFile(cacheFile);
//                            sendEmptyMessage(MSG_CANCEL);
                            currentDownloadState = MSG_CANCEL;
                            return;
                        }
                        if (pause) {
                            //关闭资源
                            close(cacheAccessFile, is, response.body());
                            //发送暂停消息
//                            sendEmptyMessage(MSG_PAUSE);
                            currentDownloadState = MSG_PAUSE;
                            return;
                        }
                        tmpAccessFile.write(buffer, 0, length);
                        total += length;
                        progress = finalStartIndex + total;

                        //将当前现在到的位置保存到文件中
                        cacheAccessFile.seek(0);
                        cacheAccessFile.write((progress + "").getBytes("UTF-8"));
                        //发送进度消息
                        mProgress[threadId] = progress - startIndex;
//                        sendEmptyMessage(MSG_PROGRESS);
                        currentDownloadState = MSG_PROGRESS;
                    }
                    //关闭资源
                    close(cacheAccessFile, is, response.body());
                    // 删除临时文件
                    cleanFile(cacheFile);
                    //发送完成消息
//                    sendEmptyMessage(MSG_FINISH);
                    currentDownloadState = MSG_FINISH;
                }catch (IOException e){
//                    sendEmptyMessage(MSG_NETWORK_ERROR);
                    currentDownloadState = MSG_NETWORK_ERROR;
                }

            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: ------");
                isDownloading = false;
//                sendEmptyMessage(MSG_NETWORK_ERROR);
                currentDownloadState = MSG_NETWORK_ERROR;
            }
        });
    }

    /**
     * 关闭资源
     *
     * @param closeables
     */
    private void close(Closeable... closeables) {
        int length = closeables.length;
        try {
            for (int i = 0; i < length; i++) {
                Closeable closeable = closeables[i];
                if (null != closeable)
                    closeables[i].close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            for (int i = 0; i < length; i++) {
                closeables[i] = null;
            }
        }
    }

    /**
     * 删除临时文件
     */
    private void cleanFile(File... files) {
        for (int i = 0, length = files.length; i < length; i++) {
            if (null != files[i])
                files[i].delete();
        }
    }

    /**
     * 暂停
     */
    public void pause() {
        pause = true;

        currentDownloadState = MSG_PAUSE;
    }

    /**
     * 取消
     */
    public void cancel() {
        cancel = true;
        cleanFile(mTmpFile);
        if (!isDownloading) {
            if (null != mListner) {
                cleanFile(mCacheFiles);
                resetStutus();
            }
        }
        currentDownloadState = MSG_CANCEL;
    }

    public DownloadTableInfo getDownloadInfo() {
        return mDownloadInfo;
    }

    public void DownloadTableInfo(DownloadTableInfo mDownloadInfo) {
        this.mDownloadInfo = mDownloadInfo;
    }

    /**
     * 重置下载状态
     */
    private void resetStutus() {
        pause = false;
        cancel = false;
        isDownloading = false;
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    class DownloadListenerThread extends Thread {
        //保存下载进度线程
        @Override
        public void run() {
            boolean isTrue = true;
            Log.d(TAG, "run:  istrue "+isTrue);
            while (isTrue) {
                Log.d(TAG, "run: "+currentDownloadState);
                if(currentDownloadState == MSG_PROGRESS){
                    long progress = getCurrentProgress();
                    if(progress + 10240 > lastProgress){
                        lastProgress = progress;
                        mDownloadInfo.setProgress(progress);
                        mListner.onDownloadProgress(mDownloadInfo,progress * 1.0f / mFileLength);
                    }
                } else if(currentDownloadState == MSG_PAUSE){
                    //暂停
                    Log.d(TAG, "run: 暂停");
                    resetStutus();
                    long cProgress = getCurrentProgress();
                    mListner.onDownloadPause(mDownloadInfo,cProgress * 1.0f / mFileLength);
                    isTrue = false;
                    break;
                }else if(currentDownloadState == MSG_FINISH){
                    Log.d(TAG, "handleMessage: 完成");
                    mTmpFile.renameTo(new File(mDownloadInfo.getLocalFilePath(), mDownloadInfo.getFileName()));//下载完毕后，重命名目标文件名
                    resetStutus();
                    mListner.onDownloadFinished(mDownloadInfo);
                    isTrue = false;
                    break;
                }else if(currentDownloadState == MSG_CANCEL){
                    Log.d(TAG, "run: 取消");
                    resetStutus();
                    mProgress = new long[THREAD_COUNT];
                    mListner.onDownloadCancel(mDownloadInfo);
                    isTrue = false;
                    break;
                }else if(currentDownloadState == MSG_REQUEST_FAILURE || currentDownloadState == MSG_NETWORK_ERROR){
                    Log.d(TAG, "run: 异常");
                    resetStutus();
                    mListner.onDownloadError(mDownloadInfo,  MSG_REQUEST_FAILURE);
                    isTrue = false;
                    break;
                }
            }

            Log.d(TAG, "SaveProgressThread: all download finish");
        }
    }
}
