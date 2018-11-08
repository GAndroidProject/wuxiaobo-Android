package xiaoe.com.network.downloadUtil;


import xiaoe.com.common.entitys.DownloadTableInfo;

/**
 * Created by Administrator on 2018/1/5.
 */

public class DownloadEvent {
    private DownloadTableInfo downloadInfo;
    private float progress;//0～1,0代表下载0%；1代表下载100%。
    private int status;//-1代表取消下载（删除），0代表正在下载，1-暂停下载，3代表完成

    public DownloadTableInfo getDownloadInfo() {
        return downloadInfo;
    }

    public void setDownloadInfo(DownloadTableInfo downloadInfo) {
        this.downloadInfo = downloadInfo;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
