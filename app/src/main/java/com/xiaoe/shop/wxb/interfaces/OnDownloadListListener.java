package com.xiaoe.shop.wxb.interfaces;

import com.xiaoe.common.entitys.DownloadTableInfo;

public interface OnDownloadListListener {
    void downloadItem(DownloadTableInfo download, int position, int type);
}
