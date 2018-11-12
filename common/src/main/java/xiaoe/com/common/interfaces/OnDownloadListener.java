package xiaoe.com.common.interfaces;

import xiaoe.com.common.entitys.DownloadTableInfo;

public interface OnDownloadListener {
    void onDownload(DownloadTableInfo downloadInfo, float progress, int status);
}
