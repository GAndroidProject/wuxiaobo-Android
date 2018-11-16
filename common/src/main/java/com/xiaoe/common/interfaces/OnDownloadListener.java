package com.xiaoe.common.interfaces;

import com.xiaoe.common.entitys.DownloadTableInfo;

public interface OnDownloadListener {
    void onDownload(DownloadTableInfo downloadInfo, float progress, int status);
}
