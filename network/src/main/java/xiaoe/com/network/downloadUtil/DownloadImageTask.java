package xiaoe.com.network.downloadUtil;


import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import xiaoe.com.common.entitys.DownloadInfo;

/**
 * Created by Administrator on 2018/1/9.
 */

public class DownloadImageTask {
    private static final String TAG = "DownloadImageTask";
    private File mTmpFile;//临时占位文件
    private boolean isDownloading = false;
    private DownloadInfo mDownloadInfo ;//下载资源信息
    private HttpUtil mHttpUtil;
    private String mUrl;
    private String mFilePath;
    private String mFileName;
    private int mType = -1;//0代表是专栏，1代表是课程

    public DownloadImageTask(DownloadInfo downloadInfo, String url,String filePath, String fileName, int type){
        mDownloadInfo = downloadInfo;
        mUrl = url;
        mFilePath = filePath;
        mFileName = fileName;
        mType = type;
        this.mHttpUtil = HttpUtil.getInstance();
    }

    public synchronized void start(){
        try {
            if(isDownloading){
                return;
            }
            File file = new File(mFilePath, mFileName);
            if(file.exists()){
                if(mType == 0){
                    mDownloadInfo.setLocalColumnImageUrl(mFilePath+mFileName);
                }else if(mType == 1){
                    mDownloadInfo.setLocalResourceImageUrl(mFilePath+mFileName);
                }
                String key = mDownloadInfo.getColumnResourceId()+"_"+mDownloadInfo.getResourceId();
                //更新数据库
                return;
            }
            isDownloading = true;
            mHttpUtil.getContentLength(mUrl, new Callback() {

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    // 获取资源大小
                    long fileLength = response.body().contentLength();
                    // 在本地创建一个与资源同样大小的文件来占位
                    mTmpFile = new File(mFilePath, mFileName + ".tmp");
                    if (!mTmpFile.getParentFile().exists()) mTmpFile.getParentFile().mkdirs();
                    RandomAccessFile tmpAccessFile = new RandomAccessFile(mTmpFile, "rw");
                    tmpAccessFile.setLength(fileLength);
                    tmpAccessFile.seek(0);// 文件写入的开始位置.
                    InputStream is = response.body().byteStream();// 获取流
                      /*  将网络流中的文件写入本地*/
                    byte[] buffer = new byte[1024 << 2];
                    int length = -1;
                    while ((length = is.read(buffer)) > 0) {
                        tmpAccessFile.write(buffer, 0, length);
                    }
                    if(mType == 0){
                        mDownloadInfo.setLocalColumnImageUrl(mFilePath+mFileName);
                    }else if(mType == 1){
                        mDownloadInfo.setLocalResourceImageUrl(mFilePath+mFileName);
                    }
                    String key = mDownloadInfo.getColumnResourceId()+"_"+mDownloadInfo.getResourceId();
                    //更新数据库
                    mTmpFile.renameTo(new File(mFilePath, mFileName));//下载完毕后，重命名目标文件名
                    close(tmpAccessFile,is,response.body());
                }
                @Override
                public void onFailure(Call call, IOException e) {
                    isDownloading = false;
                }
            });
        }catch (IOException e) {
            isDownloading = false;
            e.printStackTrace();
        }
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
}
