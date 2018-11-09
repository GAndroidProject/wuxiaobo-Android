package xiaoe.com.shop.interfaces;

import xiaoe.com.common.entitys.DownloadTableInfo;

public interface OnDownloadListListener {
    void downloadItem(DownloadTableInfo download, int position, int type);
}
