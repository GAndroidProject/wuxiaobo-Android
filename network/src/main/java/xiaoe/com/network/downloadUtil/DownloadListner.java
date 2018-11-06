package xiaoe.com.network.downloadUtil;


import xiaoe.com.common.entitys.DownloadTableInfo;

/**
 * 下载监听
 *
 * @author Cheny
 */
public interface DownloadListner {
    void onDownloadFinished(DownloadTableInfo downloadInfo);
    void onDownloadProgress(DownloadTableInfo downloadInfo, float progress);
    void onDownloadPause(DownloadTableInfo downloadInfo, float progress);
    void onDownloadCancel(DownloadTableInfo downloadInfo);
    void onDownloadError(DownloadTableInfo downloadInfo, int errorStatus);
}
