package com.xiaoe.network.downloadUtil;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.xiaoe.common.app.Global;
import com.xiaoe.common.app.XiaoeApplication;
import com.xiaoe.common.db.SQLiteUtil;
import com.xiaoe.common.entitys.ColumnSecondDirectoryEntity;
import com.xiaoe.common.entitys.DownloadResourceTableInfo;
import com.xiaoe.common.entitys.DownloadTableInfo;
import com.xiaoe.common.interfaces.OnDownloadListener;
import com.xiaoe.common.utils.DateFormat;
import com.xiaoe.common.utils.MD5Utils;
import com.xiaoe.network.utils.ThreadPoolUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    private HashMap<String, DownloadTableInfo> allDownloadList;//全部下载列表
    private Map<String,OnDownloadListener> onDownloadListenerList;
    private SQLiteUtil relaSQLiteUtil;
    private SQLiteUtil resSQLiteUtil;
    /**
     * ConcurrentHashMap可以高并发操作，线程安全，高效
     */
    private ConcurrentHashMap<String, String> mSaveProgresss;//缓存下载进度集合
//    private SaveProgressThread saveProgressThread;
    private boolean isAllDownloadFinish = true;
    private int MAX_DOWNLOAD_COUNT = 1;
//    private DownloadEvent downloadEvent;

    private DownloadManager() {
        mDownloadTasks = new ArrayList<DownloadTask>();
        mAwaitDownloadTasks = new ArrayList<DownloadTask>();
        mSaveProgresss = new ConcurrentHashMap<String, String>();
        onDownloadListenerList = new HashMap<String, OnDownloadListener>();
        allDownloadList = new HashMap<String, DownloadTableInfo>();
//        getInstanceDownloadEvent();
        relaSQLiteUtil = SQLiteUtil.init(XiaoeApplication.getmContext(), new RelationTable());
        resSQLiteUtil = SQLiteUtil.init(XiaoeApplication.getmContext(), new DownloadResourceTable());
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


    private void createSaveProgressThread() {
//        if (saveProgressThread == null) {
//            saveProgressThread = new SaveProgressThread();
//        }
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
            final DownloadTask downloadTask = mAwaitDownloadTasks.remove(i);
            DownloadTableInfo taskDownloadInfo = downloadTask.getDownloadInfo();
            boolean equals = compareResource(appId, resourceId, taskDownloadInfo.getAppId(), taskDownloadInfo.getResourceId());
            if (equals) {
                mDownloadTasks.add(downloadTask);
                ThreadPoolUtils.runTaskOnThread(new Runnable() {
                    @Override
                    public void run() {
                        downloadTask.start();
                    }
                });
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
        download.setDownloadState(2);
        for (int i = 0; i < mDownloadTasks.size(); i++) {
            DownloadTask task = mDownloadTasks.get(i);
            DownloadTableInfo info = task.getDownloadInfo();
            boolean equals = compareResource(download.getAppId(), download.getResourceId(), info.getAppId(), info.getResourceId());
            if (equals) {
                task.pause();
                mDownloadTasks.remove(i);
                break;
            }
        }
        for (int i = 0; i < mAwaitDownloadTasks.size(); i++) {
            DownloadTask task = mAwaitDownloadTasks.get(i);
            DownloadTableInfo info = task.getDownloadInfo();
            boolean equals = compareResource(download.getAppId(), download.getResourceId(), info.getAppId(), info.getResourceId());
            if (equals) {
                mAwaitDownloadTasks.remove(i);
                //DownloadFileConfig.getInstance().updateDownloadInfo(download);
                break;
            }
        }
//        DownloadFileConfig.getInstance().updateDownloadInfo(download);
        sendDownloadEvent(download, 0, 2);
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
            boolean equals = compareResource(download.getAppId(), resourceId, downloadTableInfo.getAppId(), downloadTableInfo.getResourceId());
            if (equals) {
                isExistAwaitList = true;
                break;
            }
        }
        //判断是否正在下载，如果是，则不做操作
        for (int i = 0; i < mDownloadTasks.size(); i++) {
            //正在下载任务
            DownloadTask downloadTask = mDownloadTasks.get(i);
            DownloadTableInfo downloadTableInfo = downloadTask.getDownloadInfo();
            boolean equals = compareResource(download.getAppId(), resourceId, downloadTableInfo.getAppId(), downloadTableInfo.getResourceId());
            if (equals) {
                return;
            }
        }
        if (!isExistAwaitList) {
            mAwaitDownloadTasks.add(new DownloadTask(download, this));
            download.setDownloadState(0);
            sendDownloadEvent(download, 0, 0);
        }
        download(download);
    }

    //比较两个资源是否相同
    private boolean compareResource(String appId, String resourceId, String newAppId, String newResId) {
//        return appId.equals(newAppId) && resourceId.equals(newResId) && columnId.equals(newColumnId) && bigColumnId.equals(newBigColumnId);
        return appId.equals(newAppId) && resourceId.equals(newResId);
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
     * 移除已下载完成的
     *
     * @param download
     */
    public void removeDownloadFinish(DownloadResourceTableInfo download) {

        String md5Code = MD5Utils.encrypt(download.getResourceId());
        String delSQL = "DELETE FROM "+DownloadFileConfig.TABLE_NAME+" where app_id='"+download.getAppId()+"' and id='"+md5Code+"'";
//        DownloadFileConfig.getInstance().execSQL(delSQL);
        DownloadSQLiteUtil downloadSQLiteUtil = new DownloadSQLiteUtil(XiaoeApplication.getmContext(), DownloadFileConfig.getInstance());
        downloadSQLiteUtil.execSQL(delSQL);
        File file = new File(download.getLocalFilePath());
        if(file.exists() && file.isFile()){
            file.delete();
        }
        String delRelaSQL = "DELETE FROM "+RelationTable.TABLE_NAME+" where app_id='"+download.getAppId()+"' and id='"+md5Code+"'";
        relaSQLiteUtil.execSQL(delRelaSQL);

        String delResSQL = "DELETE FROM "+DownloadResourceTable.TABLE_NAME+" where app_id='"+download.getAppId()+"' and resource_id='"+download.getResourceId()+"'";
        resSQLiteUtil.execSQL(delResSQL);
        if(allDownloadList != null){
            allDownloadList.remove(download.getResourceId());
        }
    }

    /**
     * 移除下载中的
     * @param download
     */
    public void removeDownloading(DownloadTableInfo download) {
        removeDownloadTasks(download);

        String md5Code = MD5Utils.encrypt(download.getResourceId());
        String delSQL = "DELETE FROM "+DownloadFileConfig.TABLE_NAME+" where app_id='"+download.getAppId()+"' and id='"+md5Code+"'";
//        DownloadFileConfig.getInstance().execSQL(delSQL);
        DownloadSQLiteUtil downloadSQLiteUtil = new DownloadSQLiteUtil(XiaoeApplication.getmContext(), DownloadFileConfig.getInstance());
        downloadSQLiteUtil.execSQL(delSQL);
        File file = new File(download.getLocalFilePath());

        deleteDirWihtFile(file);


        String delRelaSQL = "DELETE FROM "+RelationTable.TABLE_NAME+" where app_id='"+download.getAppId()+"' and id='"+md5Code+"'";
        relaSQLiteUtil.execSQL(delRelaSQL);

        String delResSQL = "DELETE FROM "+DownloadResourceTable.TABLE_NAME+" where app_id='"+download.getAppId()+"' and resource_id='"+download.getResourceId()+"'";
        resSQLiteUtil.execSQL(delResSQL);
        if(allDownloadList != null){
            allDownloadList.remove(download.getResourceId());
        }
    }

    private static void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }


    public void addDownload(String tId, String cId, ColumnSecondDirectoryEntity resource){
        String topicId = TextUtils.isEmpty(cId) ? "" : tId;
        String columnId = TextUtils.isEmpty(cId) ? "" : cId;
        String resourceId = resource.getResource_id();
        String appId = resource.getApp_id();

        String md5Code = MD5Utils.encrypt(topicId+columnId+resourceId);
        //查询下载列表中是否存在
        String querySQL ="select * from "+DownloadFileConfig.TABLE_NAME+" where app_id=? and resource_id=? limit 1";
//        List<DownloadTableInfo> tableInfos = DownloadFileConfig.getInstance().query(DownloadFileConfig.TABLE_NAME, querySQL, new String[]{appId, resourceId});
        DownloadSQLiteUtil downloadSQLiteUtil = new DownloadSQLiteUtil(XiaoeApplication.getmContext(), DownloadFileConfig.getInstance());
        List<DownloadTableInfo> tableInfos = downloadSQLiteUtil.query(DownloadFileConfig.TABLE_NAME, querySQL, new String[]{appId, resourceId});
        //如果数据里没有，则添加
        if(tableInfos == null || tableInfos.size() <= 0){
            //实际下载的内容
            DownloadTableInfo downloadTableInfo = new DownloadTableInfo();
            downloadTableInfo.setAppId(resource.getApp_id());
            downloadTableInfo.setId(md5Code);
            downloadTableInfo.setResourceId(resource.getResource_id());
            downloadTableInfo.setColumnId(resource.getColumnId());
            downloadTableInfo.setBigColumnId(resource.getBigColumnId());
            downloadTableInfo.setDownloadState(0);
            downloadTableInfo.setTitle(resource.getTitle());
            downloadTableInfo.setDesc("");
            downloadTableInfo.setResourceType(resource.getResource_type());
            downloadTableInfo.setImgUrl(resource.getImg_url());
            downloadTableInfo.setLocalFilePath(Global.g().getDefaultDirectory()+md5Code);
            if(resource.getResource_type() == 2){
                String audioUrl = resource.getAudio_url();
                downloadTableInfo.setFileName(resource.getTitle()+audioUrl.substring(audioUrl.lastIndexOf(".")));
                downloadTableInfo.setFileDownloadUrl(audioUrl);
            }else if(resource.getResource_type() == 3){
                String videoUrl = resource.getVideo_url();
                downloadTableInfo.setFileName(resource.getTitle()+videoUrl.substring(videoUrl.lastIndexOf(".")));
                downloadTableInfo.setFileDownloadUrl(videoUrl);
            }
            downloadTableInfo.setCreateAt(DateFormat.currentTime());
            downloadTableInfo.setUpdateAt(DateFormat.currentTime());
//            DownloadFileConfig.getInstance().insertDownloadInfo(downloadTableInfo);
            downloadSQLiteUtil.insertDownloadInfo(downloadTableInfo);
            start(downloadTableInfo);
            allDownloadList.put(resource.getResource_id(), downloadTableInfo);
        }
        //关系表
        if(!relaSQLiteUtil.tabIsExist(RelationTable.TABLE_NAME)){
            relaSQLiteUtil.execSQL(RelationTable.CREATE_TABLE_SQL);
        }
        String queryRelationSQL ="select * from "+RelationTable.TABLE_NAME+" where app_id=? and id=? and resource_id=? limit 1";
        List<RelationTableInfo> dbRelation = relaSQLiteUtil.query(RelationTable.TABLE_NAME, queryRelationSQL, new String[]{appId, md5Code, resourceId});
        if(dbRelation == null || dbRelation.size() <= 0){
            //关系不存在，则添加插入一条关系
            RelationTableInfo relationTableInfo = new RelationTableInfo();
            relationTableInfo.setAppId(appId);
            relationTableInfo.setId(md5Code);
            relationTableInfo.setResourceId(resourceId);
            JSONObject jsonObject = new JSONObject();
            if(!TextUtils.isEmpty(columnId)){
                jsonObject.put("column", columnId);
            }
            if(!TextUtils.isEmpty(topicId)){
                jsonObject.put("topic", columnId);
            }
            if(jsonObject.size() > 0){
                relationTableInfo.setPath(jsonObject.toJSONString());
            }
            relaSQLiteUtil.insert(RelationTable.TABLE_NAME, relationTableInfo);
        }

        //资源表
        if(!resSQLiteUtil.tabIsExist(DownloadResourceTable.TABLE_NAME)){
            resSQLiteUtil.execSQL(DownloadResourceTable.CREATE_TABLE_SQL);
        }
        String queryResourceSQL ="select * from "+DownloadResourceTable.TABLE_NAME+" where app_id=? and resource_id=? limit 1";
        List<DownloadResourceTableInfo> dbResourceList = resSQLiteUtil.query(DownloadResourceTable.TABLE_NAME, queryResourceSQL, new String[]{appId, resourceId});
        if(dbResourceList == null || dbResourceList.size() <= 0){
            //没有对应的资源，则插入一条数据
            DownloadResourceTableInfo resourceInfo = new DownloadResourceTableInfo();
            resourceInfo.setAppId(appId);
            resourceInfo.setResourceId(resourceId);
            resourceInfo.setTitle(resource.getTitle());
            resourceInfo.setDesc("");
            resourceInfo.setImgUrl(resource.getImg_url());
            if(resource.getResource_type() == 2){
                resourceInfo.setResourceType(1);
            }else if(resource.getResource_type() == 3){
                resourceInfo.setResourceType(2);
            }
            resourceInfo.setDepth(0);
            resourceInfo.setCreateAt(DateFormat.currentTime());
            resourceInfo.setUpdateAt(DateFormat.currentTime());
            resSQLiteUtil.insert(DownloadResourceTable.TABLE_NAME, resourceInfo);
        }

    }

    public void addDownload(List<ColumnSecondDirectoryEntity> resourceList) {
        String querySQL ="select * from "+DownloadFileConfig.TABLE_NAME+" where resource_id=? limit 1";
        for (ColumnSecondDirectoryEntity resInfo : resourceList) {
//            List<DownloadResourceTableInfo> tableInfos = SQLiteUtil.query(DownloadResourceTable.TABLE_NAME, querySQL, new String[]{resInfo.getResource_id()});
//            List<DownloadTableInfo> tableInfos = DownloadFileConfig.getInstance().query(DownloadFileConfig.TABLE_NAME, querySQL, new String[]{resInfo.getResource_id()});
            DownloadSQLiteUtil downloadSQLiteUtil = new DownloadSQLiteUtil(XiaoeApplication.getmContext(), DownloadFileConfig.getInstance());
            List<DownloadTableInfo> tableInfos = downloadSQLiteUtil.query(DownloadFileConfig.TABLE_NAME, querySQL, new String[]{resInfo.getResource_id()});
            //如果数据里没有，则添加
            if(tableInfos != null && tableInfos.size() > 0){
                //已经存在
                continue;
            }
            //实际下载的内容
            DownloadTableInfo downloadTableInfo = new DownloadTableInfo();
            downloadTableInfo.setAppId(resInfo.getApp_id());
            downloadTableInfo.setResourceId(resInfo.getResource_id());
            downloadTableInfo.setColumnId(resInfo.getColumnId());
            downloadTableInfo.setBigColumnId(resInfo.getBigColumnId());
            downloadTableInfo.setDownloadState(0);
            downloadTableInfo.setTitle(resInfo.getTitle());
            downloadTableInfo.setDesc("");
            downloadTableInfo.setResourceType(resInfo.getResource_type());
            downloadTableInfo.setImgUrl(resInfo.getImg_url());
            downloadTableInfo.setLocalFilePath(Global.g().getDefaultDirectory()+resInfo.getColumnId());
            if(resInfo.getResource_type() == 2){
                String audioUrl = resInfo.getAudio_url();
                downloadTableInfo.setFileName(resInfo.getTitle()+audioUrl.substring(audioUrl.lastIndexOf(".")));
                downloadTableInfo.setFileDownloadUrl(audioUrl);
            }else if(resInfo.getResource_type() == 3){
//                downloadTableInfo.setFileDownloadUrl(resInfo.);
            }
            downloadTableInfo.setCreateAt(DateFormat.currentTime());
            downloadTableInfo.setUpdateAt(DateFormat.currentTime());
//            DownloadFileConfig.getInstance().insertDownloadInfo(downloadTableInfo);
            downloadSQLiteUtil.insertDownloadInfo(downloadTableInfo);
        }
    }



    /**
     * 移除下载任务
     */
    private void removeDownloadTasks(DownloadTableInfo download) {
        for (int i = 0; i < mDownloadTasks.size(); i++) {
            DownloadTask task = mDownloadTasks.get(i);
            DownloadTableInfo info = task.getDownloadInfo();
            if (compareResource(download.getAppId(), download.getResourceId(), info.getAppId(), info.getResourceId())) {
                task.cancel();
                mDownloadTasks.remove(i);
                break;
            }
        }
        for (int i = 0; i < mAwaitDownloadTasks.size(); i++){
            DownloadTask task = mAwaitDownloadTasks.get(i);
            DownloadTableInfo info = task.getDownloadInfo();
            if (compareResource(download.getAppId(), download.getResourceId(), info.getAppId(), info.getResourceId())) {
                task.cancel();
                mAwaitDownloadTasks.remove(i);
                break;
            }
        }

    }

    @Override
    public void onDownloadFinished(DownloadTableInfo downloadInfo) {
        Log.d(TAG, "onDownloadFinished: ");
        sendDownloadEvent(downloadInfo, 1, 3);
        removeDownloadTasks(downloadInfo);
        autoDownloadNextAwaitTask();
    }


    @Override
    public void onDownloadProgress(DownloadTableInfo downloadInfo, float progress) {
        sendDownloadEvent(downloadInfo, progress, 1);
    }

    @Override
    public void onDownloadPause(DownloadTableInfo downloadInfo, float progress) {
        Log.d(TAG, "onDownloadPause: ");
        sendDownloadEvent(downloadInfo, progress, 2);
    }

    @Override
    public void onDownloadCancel(DownloadTableInfo downloadInfo) {
        Log.d(TAG, "onDownloadCancel: ");
        sendDownloadEvent(downloadInfo, 0, -1);
    }

    @Override
    public void onDownloadError(DownloadTableInfo downloadInfo, int errorStatus) {
        Log.d(TAG, "onDownloadError: ");
        sendDownloadEvent(downloadInfo, 0, errorStatus);
    }

    /**
     * 发送下载事件
     *
     * @param downloadInfo
     * @param progress
     * @param status
     */
    private void sendDownloadEvent(final DownloadTableInfo downloadInfo, final float progress, final int status) {
        downloadInfo.setUpdateAt(DateFormat.currentTime());
        //0-等待，1-下载中，2-暂停，3-完成
        if (status == 0) {
            //等待
            Log.d(TAG, "sendDownlaodEvent: 等待");
            //更新数据库进度
            downloadInfo.setDownloadState(0);
        } else if (status == 1) {
            //下载
//            Log.d(TAG, "sendDownlaodEvent: 下载");
            downloadInfo.setDownloadState(1);
        } else if(status == 2){
            //暂停
            Log.d(TAG, "sendDownlaodEvent: 暂停");
            downloadInfo.setDownloadState(2);
            autoDownloadNextAwaitTask();
        }else if (status == 3) {
            //下载完成
            Log.d(TAG, "sendDownlaodEvent: 完成");
            downloadInfo.setDownloadState(3);
        } else if (status == -1) {
            //取消下载
            Log.d(TAG, "sendDownlaodEvent: 取消");
            if(downloadInfo.getDownloadState() != 3){
                downloadInfo.setDownloadState(2);
            }
        } else if (status == -40004 || status == -60000) {
            //-40004:请求失败
            //-60000:网络异常
            pause(downloadInfo);
            Log.d(TAG, "sendDownlaodEvent: 请求失败");
            downloadInfo.setDownloadState(2);
        }
        ThreadPoolUtils.runTaskOnUIThread(new Runnable() {
            @Override
            public void run() {
                for (Map.Entry<String, OnDownloadListener> entry : onDownloadListenerList.entrySet()){
                    if (entry.getValue() != null){
                        entry.getValue().onDownload(downloadInfo, progress, status);
                    }
                }
            }
        });

    }



    /**
     * 自动下载下一个等待下载任务
     */
    private void autoDownloadNextAwaitTask() {
        if (mAwaitDownloadTasks.size() > 0) {
            start(mAwaitDownloadTasks.get(0).getDownloadInfo());
        }
    }

    /**
     * 设置下载状态未暂停
     */
    public void setDownloadPause(){
        String querySQL = "update "+DownloadFileConfig.TABLE_NAME+" SET download_state = 2 where download_state!=3";
//        DownloadFileConfig.getInstance().execSQL(querySQL);

        DownloadSQLiteUtil downloadSQLiteUtil = new DownloadSQLiteUtil(XiaoeApplication.getmContext(), DownloadFileConfig.getInstance());
        downloadSQLiteUtil.execSQL(querySQL);
    }

    /**
     * 获取下载中的列表
     */
    public List<DownloadTableInfo> getDownloadingList(){
        //获取下载资源
        String querySQL = "select * from "+DownloadFileConfig.TABLE_NAME+" where download_state!=3 order by create_at desc";
//        List<DownloadTableInfo> dbDownloadingList = DownloadFileConfig.getInstance().query(DownloadFileConfig.TABLE_NAME, querySQL ,null);
        DownloadSQLiteUtil downloadSQLiteUtil = new DownloadSQLiteUtil(XiaoeApplication.getmContext(), DownloadFileConfig.getInstance());
        List<DownloadTableInfo> dbDownloadingList = downloadSQLiteUtil.query(DownloadFileConfig.TABLE_NAME, querySQL ,null);
        return dbDownloadingList;
    }

    public List<DownloadResourceTableInfo> getDownloadFinishList(){
        //获取已下载完成的资源
        String querySQL = "select * from "+DownloadFileConfig.TABLE_NAME+" where download_state=3 order by create_at desc";
//        List<DownloadTableInfo> dbDownloadList = DownloadFileConfig.getInstance().query(DownloadFileConfig.TABLE_NAME, querySQL ,null);
        DownloadSQLiteUtil downloadSQLiteUtil = new DownloadSQLiteUtil(XiaoeApplication.getmContext(), DownloadFileConfig.getInstance());
        List<DownloadTableInfo> dbDownloadList = downloadSQLiteUtil.query(DownloadFileConfig.TABLE_NAME, querySQL ,null);
        if(dbDownloadList == null || dbDownloadList.size() <= 0){
            return null;
        }
        SQLiteUtil.init(XiaoeApplication.getmContext(), new DownloadResourceTable());
        List<DownloadResourceTableInfo> finishList = new ArrayList<DownloadResourceTableInfo>();
        for (DownloadTableInfo dbItem : dbDownloadList) {
            DownloadResourceTableInfo single = getSingleResource(dbItem);
            if(single != null){
                finishList.add(single);
            }
        }
        return finishList;
    }
    public DownloadResourceTableInfo getDownloadFinish(String appId, String resId){
        //获取已下载完成的资源
        String querySQL = "select * from "+DownloadFileConfig.TABLE_NAME+" where app_id=? and resource_id=? and download_state=3 limit 1";
//        List<DownloadTableInfo> dbDownloadList = DownloadFileConfig.getInstance().query(DownloadFileConfig.TABLE_NAME, querySQL, new String[]{appId, resId});
        DownloadSQLiteUtil downloadSQLiteUtil = new DownloadSQLiteUtil(XiaoeApplication.getmContext(), DownloadFileConfig.getInstance());
        List<DownloadTableInfo> dbDownloadList = downloadSQLiteUtil.query(DownloadFileConfig.TABLE_NAME, querySQL, new String[]{appId, resId});
        if(dbDownloadList == null || dbDownloadList.size() <= 0){
            return null;
        }
        SQLiteUtil.init(XiaoeApplication.getmContext(), new DownloadResourceTable());
        DownloadResourceTableInfo single = getSingleResource(dbDownloadList.get(0));
        if(single != null){
            return single;
        }
        return  null;
    }
    private DownloadResourceTableInfo getSingleResource(DownloadTableInfo dbItem){
        String queryResSQL = "select * from "+DownloadResourceTable.TABLE_NAME+" where app_id=? and resource_id=?";
        List<DownloadResourceTableInfo> singleList = resSQLiteUtil.query(DownloadResourceTable.TABLE_NAME, queryResSQL, new String[]{dbItem.getAppId(), dbItem.getResourceId()});
        DownloadResourceTableInfo single = null;
        if(singleList != null && singleList.size() > 0){
            single = singleList.get(0);
            single.setTotalSize(dbItem.getTotalSize());
            single.setProgress(dbItem.getProgress());
            single.setLocalFilePath(dbItem.getLocalFilePath()+"/"+dbItem.getFileName());
            single.setFileUrl(dbItem.getFileDownloadUrl());
        }
        return single;
    }


    public HashMap<String, DownloadTableInfo> getAllDownloadList() {
        if(allDownloadList == null){
            allDownloadList = new HashMap<String, DownloadTableInfo>();
            String querySQL = "select * from "+DownloadFileConfig.TABLE_NAME+" order by create_at desc";
//            List<DownloadTableInfo> dbDownloadingList = DownloadFileConfig.getInstance().query(DownloadFileConfig.TABLE_NAME, querySQL ,null);
            DownloadSQLiteUtil downloadSQLiteUtil = new DownloadSQLiteUtil(XiaoeApplication.getmContext(), DownloadFileConfig.getInstance());
            List<DownloadTableInfo> dbDownloadingList = downloadSQLiteUtil.query(DownloadFileConfig.TABLE_NAME, querySQL ,null);
            for (DownloadTableInfo download : dbDownloadingList) {
                allDownloadList.put(download.getResourceId(), download);
            }
        }
        return allDownloadList;
    }

    public boolean isDownload(String appId, String resourceId){
//        String querySQL ="select * from "+DownloadFileConfig.TABLE_NAME+" where app_id=? and resource_id=? limit 1";
//        List<DownloadTableInfo> tableInfos = DownloadFileConfig.getInstance().query(DownloadFileConfig.TABLE_NAME, querySQL, new String[]{appId, resourceId});
//        if(tableInfos != null && tableInfos.size() > 0){
//            return true;
//        }
        if(allDownloadList == null || allDownloadList.size() <= 0){
            return false;
        }
        return allDownloadList.containsKey(resourceId);
    }

    public void setOnDownloadListener(String key, OnDownloadListener listener){
        onDownloadListenerList.put(key, listener);
    }
    public void removeOnDownloadListener(String key){
        onDownloadListenerList.remove(key);
    }
}
