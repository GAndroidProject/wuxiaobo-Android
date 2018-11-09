package xiaoe.com.network.downloadUtil;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import xiaoe.com.common.app.Global;
import xiaoe.com.common.app.XiaoeApplication;
import xiaoe.com.common.entitys.ColumnDirectoryEntity;
import xiaoe.com.common.entitys.ColumnSecondDirectoryEntity;
import xiaoe.com.common.entitys.DownloadResourceTableInfo;
import xiaoe.com.common.entitys.DownloadTableInfo;
import xiaoe.com.common.utils.DateFormat;
import xiaoe.com.common.utils.MD5Utils;
import xiaoe.com.common.utils.SQLiteUtil;

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
//    private SaveProgressThread saveProgressThread;
    private boolean isAllDownloadFinish = true;
    private int MAX_DOWNLOAD_COUNT = 1;
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
            DownloadTask downloadTask = mAwaitDownloadTasks.remove(i);
            DownloadTableInfo taskDownloadInfo = downloadTask.getDownloadInfo();
            boolean equals = compareResource(appId, resourceId, taskDownloadInfo.getAppId(), taskDownloadInfo.getResourceId());
            if (equals) {
                mDownloadTasks.add(downloadTask);
                downloadTask.start();
//                DownloadTableInfo downloadInfo = downloadTask.getDownloadInfo();
//                downloadInfo.setDownloadState(0);
//                download.setDownloadState(0);
                //数据库更新----
//                DownloadFileConfig.getInstance().insertDownloadInfo(downloadInfo);
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
                break;
            }
        }
        DownloadFileConfig.getInstance().updateDownloadInfo(download);
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
        DownloadFileConfig.getInstance().execSQL(delSQL);
        File file = new File(download.getLocalFilePath());
        if(file.exists() && file.isFile()){
            file.delete();
        }
        SQLiteUtil.init(XiaoeApplication.getmContext(), new RelationTable());
        String delRelaSQL = "DELETE FROM "+RelationTable.TABLE_NAME+" where app_id='"+download.getAppId()+"' and id='"+md5Code+"'";
        SQLiteUtil.execSQL(delRelaSQL);

        SQLiteUtil.init(XiaoeApplication.getmContext(), new DownloadResourceTable());
        String delResSQL = "DELETE FROM "+DownloadResourceTable.TABLE_NAME+" where app_id='"+download.getAppId()+"' and resource_id='"+download.getResourceId()+"'";
        SQLiteUtil.execSQL(delResSQL);
    }

    /**
     * 移除下载中的
     * @param download
     */
    public void removeDownloading(DownloadTableInfo download) {
        removeDownloadTasks(download);

        String md5Code = MD5Utils.encrypt(download.getResourceId());
        String delSQL = "DELETE FROM "+DownloadFileConfig.TABLE_NAME+" where app_id='"+download.getAppId()+"' and id='"+md5Code+"'";
        DownloadFileConfig.getInstance().execSQL(delSQL);
        File file = new File(download.getLocalFilePath());

        deleteDirWihtFile(file);


        SQLiteUtil.init(XiaoeApplication.getmContext(), new RelationTable());
        String delRelaSQL = "DELETE FROM "+RelationTable.TABLE_NAME+" where app_id='"+download.getAppId()+"' and id='"+md5Code+"'";
        SQLiteUtil.execSQL(delRelaSQL);

        SQLiteUtil.init(XiaoeApplication.getmContext(), new DownloadResourceTable());
        String delResSQL = "DELETE FROM "+DownloadResourceTable.TABLE_NAME+" where app_id='"+download.getAppId()+"' and resource_id='"+download.getResourceId()+"'";
        SQLiteUtil.execSQL(delResSQL);
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
    /**
     * 添加下载任务
     */
    public void addLittleColumn(ColumnDirectoryEntity column, String appId){
//        SQLiteUtil.init(XiaoeApplication.getmContext(), new DownloadResourceTable());
//        if(!SQLiteUtil.tabIsExist(DownloadResourceTable.TABLE_NAME)){
//            SQLiteUtil.execSQL(DownloadResourceTable.CREATE_TABLE_SQL);
//        }
//        String querySQL ="select * from "+DownloadResourceTable.TABLE_NAME+" where resource_id=? limit 1";
//        if(littleColumn != null){
//            //小专栏
//            List<DownloadResourceTableInfo> tableInfos = SQLiteUtil.query(DownloadResourceTable.TABLE_NAME, querySQL, new String[]{littleColumn.getResource_id()});
//            //数据不存在，则插入一条数据
//            if(tableInfos == null || tableInfos.size() <= 0){
//                DownloadResourceTableInfo table = new DownloadResourceTableInfo();
//                table.setAppId(littleColumn.getApp_id());
//                table.setResourceId(littleColumn.getResource_id());
//                table.setTitle(littleColumn.getTitle());
//                table.setResourceType(3);
//                table.setImgUrl(littleColumn.getImg_url());
//                table.setDesc("");
//                SQLiteUtil.insert(DownloadResourceTable.TABLE_NAME, table);
//            }
//        }

        //专栏
        if(column != null){
            String queryResourceSQL ="select * from "+DownloadResourceTable.TABLE_NAME+" where app_id=? and resource_id=? limit 1";
            List<DownloadResourceTableInfo> dbResourceList = SQLiteUtil.query(DownloadResourceTable.TABLE_NAME, queryResourceSQL, new String[]{appId, column.getResource_id()});
            if(dbResourceList == null || dbResourceList.size() <= 0){
                //没有对应的资源，则插入一条数据
                DownloadResourceTableInfo resourceInfo = new DownloadResourceTableInfo();
                resourceInfo.setAppId(appId);
                resourceInfo.setResourceId(column.getResource_id());
                resourceInfo.setTitle(column.getTitle());
                resourceInfo.setDesc("");
                resourceInfo.setImgUrl(column.getImg_url());
                resourceInfo.setResourceType(3);
                resourceInfo.setDepth(1);
                resourceInfo.setCreateAt(DateFormat.currentTime());
                resourceInfo.setUpdateAt(DateFormat.currentTime());
                SQLiteUtil.insert(DownloadResourceTable.TABLE_NAME, resourceInfo);
            }
        }
    }

    public void addBigColumn(ColumnDirectoryEntity topic, String appId){
        //大专栏
        if(topic != null){
            String queryResourceSQL ="select * from "+DownloadResourceTable.TABLE_NAME+" where app_id=? and resource_id=? limit 1";
            List<DownloadResourceTableInfo> dbResourceList = SQLiteUtil.query(DownloadResourceTable.TABLE_NAME, queryResourceSQL, new String[]{appId, topic.getResource_id()});
            if(dbResourceList == null || dbResourceList.size() <= 0){
                //没有对应的资源，则插入一条数据
                DownloadResourceTableInfo resourceInfo = new DownloadResourceTableInfo();
                resourceInfo.setAppId(appId);
                resourceInfo.setResourceId(topic.getResource_id());
                resourceInfo.setTitle(topic.getTitle());
                resourceInfo.setDesc("");
                resourceInfo.setImgUrl(topic.getImg_url());
                resourceInfo.setResourceType(4);
                resourceInfo.setDepth(2);
                resourceInfo.setCreateAt(DateFormat.currentTime());
                resourceInfo.setUpdateAt(DateFormat.currentTime());
                SQLiteUtil.insert(DownloadResourceTable.TABLE_NAME, resourceInfo);
            }
        }
    }

    public void addDownload(String tId, String cId, ColumnSecondDirectoryEntity resource){
        String topicId = TextUtils.isEmpty(cId) ? "" : tId;
        String columnId = TextUtils.isEmpty(cId) ? "" : cId;
        String resourceId = resource.getResource_id();
        String appId = resource.getApp_id();

        String md5Code = MD5Utils.encrypt(topicId+columnId+resourceId);
        //查询下载列表中是否存在
        String querySQL ="select * from "+DownloadFileConfig.TABLE_NAME+" where app_id=? and resource_id=? limit 1";
        List<DownloadTableInfo> tableInfos = DownloadFileConfig.getInstance().query(DownloadFileConfig.TABLE_NAME, querySQL, new String[]{appId, resourceId});
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
//                downloadTableInfo.setFileDownloadUrl(resource.get);
            }
            downloadTableInfo.setCreateAt(DateFormat.currentTime());
            downloadTableInfo.setUpdateAt(DateFormat.currentTime());
            DownloadFileConfig.getInstance().insertDownloadInfo(downloadTableInfo);
        }
        //关系表
        SQLiteUtil.init(XiaoeApplication.getmContext(), new RelationTable());
        if(!SQLiteUtil.tabIsExist(RelationTable.TABLE_NAME)){
            SQLiteUtil.execSQL(RelationTable.CREATE_TABLE_SQL);
        }
        String queryRelationSQL ="select * from "+RelationTable.TABLE_NAME+" where app_id=? and id=? and resource_id=? limit 1";
        List<RelationTableInfo> dbRelation = SQLiteUtil.query(RelationTable.TABLE_NAME, queryRelationSQL, new String[]{appId, md5Code, resourceId});
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
            SQLiteUtil.insert(RelationTable.TABLE_NAME, relationTableInfo);
        }

        //资源表
        SQLiteUtil.init(XiaoeApplication.getmContext(), new DownloadResourceTable());
        if(!SQLiteUtil.tabIsExist(DownloadResourceTable.TABLE_NAME)){
            SQLiteUtil.execSQL(DownloadResourceTable.CREATE_TABLE_SQL);
        }
        String queryResourceSQL ="select * from "+DownloadResourceTable.TABLE_NAME+" where app_id=? and resource_id=? limit 1";
        List<DownloadResourceTableInfo> dbResourceList = SQLiteUtil.query(DownloadResourceTable.TABLE_NAME, queryResourceSQL, new String[]{appId, resourceId});
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
            SQLiteUtil.insert(DownloadResourceTable.TABLE_NAME, resourceInfo);
        }

    }

    public void addDownload(List<ColumnSecondDirectoryEntity> resourceList) {
        String querySQL ="select * from "+DownloadFileConfig.TABLE_NAME+" where resource_id=? limit 1";
        for (ColumnSecondDirectoryEntity resInfo : resourceList) {
//            List<DownloadResourceTableInfo> tableInfos = SQLiteUtil.query(DownloadResourceTable.TABLE_NAME, querySQL, new String[]{resInfo.getResource_id()});
            List<DownloadTableInfo> tableInfos = DownloadFileConfig.getInstance().query(DownloadFileConfig.TABLE_NAME, querySQL, new String[]{resInfo.getResource_id()});
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
            DownloadFileConfig.getInstance().insertDownloadInfo(downloadTableInfo);
        }
    }


    /**
     * 获取下载任务
     *
     * @return
     */
    public List<DownloadTask> getDownloadTasks() {
        return mDownloadTasks;
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
        Log.d(TAG, "onDownloadProgress: ");
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
    private void sendDownloadEvent(DownloadTableInfo downloadInfo, float progress, int status) {
        getInstanceDownloadEvent();
        downloadEvent.setDownloadInfo(downloadInfo);
        downloadEvent.setProgress(progress);
        downloadEvent.setStatus(status);

        downloadInfo.setUpdateAt(DateFormat.currentTime());
        //0-等待，1-下载中，2-暂停，3-完成
        if (status == 0) {
            //等待
            Log.d(TAG, "sendDownlaodEvent: 等待");
            //更新数据库进度
            downloadInfo.setDownloadState(0);
        } else if (status == 1) {
            //下载
            Log.d(TAG, "sendDownlaodEvent: 下载");
            downloadInfo.setDownloadState(1);
        } else if(status == 2){
            //暂停
            Log.d(TAG, "sendDownlaodEvent: 暂停");
            downloadInfo.setDownloadState(2);
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
        DownloadFileConfig.getInstance().updateDownloadInfo(downloadInfo);
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

    /**
     * 设置下载状态未暂停
     */
    public void setDownloadPause(){
        String querySQL = "update "+DownloadFileConfig.TABLE_NAME+" SET download_state = 2 where download_state!=3";
        DownloadFileConfig.getInstance().execSQL(querySQL);
    }

    /**
     * 获取下载中的列表
     */
    public List<DownloadTableInfo> getDownloadingList(){
        //获取下载资源
        String querySQL = "select * from "+DownloadFileConfig.TABLE_NAME+" where download_state!=3 order by create_at desc";
        List<DownloadTableInfo> dbDownloadingList = DownloadFileConfig.getInstance().query(DownloadFileConfig.TABLE_NAME, querySQL ,null);

        return dbDownloadingList;
    }

    public List<DownloadResourceTableInfo> getDownloaFinishList(){
        //获取已下载完成的资源
        String querySQL = "select * from "+DownloadFileConfig.TABLE_NAME+" where download_state=3 order by create_at desc";
        List<DownloadTableInfo> dbDownloadList = DownloadFileConfig.getInstance().query(DownloadFileConfig.TABLE_NAME, querySQL ,null);

        if(dbDownloadList == null || dbDownloadList.size() <= 0){
            return null;
        }
        SQLiteUtil.init(XiaoeApplication.getmContext(), new RelationTable());
        SQLiteUtil.init(XiaoeApplication.getmContext(), new DownloadResourceTable());

        HashMap<String,DownloadResourceTableInfo> tempFinishList = new HashMap<String,DownloadResourceTableInfo>();
//        HashMap<String, DownloadResourceTableInfo> tempTopic = new HashMap<String, DownloadResourceTableInfo>();
        HashMap<String, DownloadResourceTableInfo> tempColumn = new HashMap<String, DownloadResourceTableInfo>();


        String queryRelaSQL = "select * from "+RelationTable.TABLE_NAME+" where app_id=? and resource_id=?";
        for (DownloadTableInfo dbItem : dbDownloadList) {
            List<RelationTableInfo> relaList = SQLiteUtil.query(RelationTable.TABLE_NAME, queryRelaSQL, new String[]{dbItem.getAppId(), dbItem.getResourceId()});
            if(relaList == null || relaList.size() <= 0){
                continue;
            }
            DownloadResourceTableInfo single = getSingleResource(dbItem);
            if(single == null){
                continue;
            }
            for (RelationTableInfo dbRelaItem : relaList){
                String queryResSQL = "select * from "+DownloadResourceTable.TABLE_NAME+" where app_id=? and resource_id=?";
                if(TextUtils.isEmpty(dbRelaItem.getPath())){
//                    finishList.add(single);
                    tempFinishList.put(dbRelaItem.getResourceId(), single);
                    //单品
                }else{
                    JSONObject jsonObject = JSONObject.parseObject(dbRelaItem.getPath());
                    String columnId = jsonObject.getString("column");
                    if(TextUtils.isEmpty(columnId)){
                        //如果专栏id都不存在，则说明路径不存在了
                        continue;
                    }
                    String topicId = jsonObject.getString("topic");
                    if(!TextUtils.isEmpty(topicId)){
                        //查询大专栏
                        DownloadResourceTableInfo topic = null;
                        if(tempFinishList.containsKey(topicId)){
                            //查询的大专栏已经存在
                            topic = tempFinishList.get(topicId) ;
                        }else{
                            List<DownloadResourceTableInfo> topicList = SQLiteUtil.query(DownloadResourceTable.TABLE_NAME, queryResSQL, new String[]{dbRelaItem.getAppId(), topicId});
                            if(topicList != null && topicList.size() > 0){
                                topic = topicList.get(0);
                                topic.setChildList(new ArrayList<DownloadResourceTableInfo>());
                                tempFinishList.put(topicId, topic);
                            }
                        }
                        //查询专栏
                        DownloadResourceTableInfo column = null;
                        if(tempColumn.containsKey(columnId)){
                            //查询的大专栏已经存在
                            column = tempColumn.get(columnId) ;
                        }else{
                            List<DownloadResourceTableInfo> columnList = SQLiteUtil.query(DownloadResourceTable.TABLE_NAME, queryResSQL, new String[]{dbRelaItem.getAppId(), columnId});
                            if(columnList != null && columnList.size() > 0){
                                column = columnList.get(0);
                                column.setChildList(new ArrayList<DownloadResourceTableInfo>());
                                tempColumn.put(columnId, column);
                            }
                        }
                        //将商品归类
                        if(topic != null && column != null){
                            boolean exist = false;
                            for (DownloadResourceTableInfo childItem : topic.getChildList()){
                                if(columnId.equals(childItem.getResourceId())){
                                    //专栏已经存在，则直接添加商品到给专栏
                                    exist = true;
                                    childItem.getChildList().add(single);
                                    break;
                                }
                            }
                            if(!exist){
                                //专栏不存在，则直接添加商品到给专栏
                                column.getChildList().add(single);
                                topic.getChildList().add(column);
                            }
                        }
                    }else{
                        //查询专栏
                        DownloadResourceTableInfo column = null;
                        if(tempFinishList.containsKey(columnId)){
                            //查询的专栏已经存在
                            column = tempFinishList.get(columnId) ;
                        }else{
                            List<DownloadResourceTableInfo> columnList = SQLiteUtil.query(DownloadResourceTable.TABLE_NAME, queryResSQL, new String[]{dbRelaItem.getAppId(), columnId});
                            if(columnList != null && columnList.size() > 0){
                                column = columnList.get(0);
                                column.setChildList(new ArrayList<DownloadResourceTableInfo>());
                                tempFinishList.put(columnId, column);
                            }
                        }
                        if(column == null){
                            continue;
                        }
                        boolean exist = false;
                        for (DownloadResourceTableInfo childItem : column.getChildList()){
                            if(columnId.equals(childItem.getResourceId())){
                                //专栏已经存在，则直接添加商品到给专栏
                                exist = true;
                                childItem.getChildList().add(single);
                                break;
                            }
                        }
                        if(!exist){
                            //专栏不存在，则直接添加商品到给专栏
                            column.getChildList().add(single);
                        }
                    }
                }
            }
        }
        List<DownloadResourceTableInfo> finishList = new ArrayList<DownloadResourceTableInfo>();
        for (Map.Entry<String, DownloadResourceTableInfo> item : tempFinishList.entrySet()){
            finishList.add(item.getValue());
        }

        return finishList;
    }
    private DownloadResourceTableInfo getSingleResource(DownloadTableInfo dbItem){
        String queryResSQL = "select * from "+DownloadResourceTable.TABLE_NAME+" where app_id=? and resource_id=?";
        List<DownloadResourceTableInfo> singleList = SQLiteUtil.query(DownloadResourceTable.TABLE_NAME, queryResSQL, new String[]{dbItem.getAppId(), dbItem.getResourceId()});
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

    public boolean isDownload(String appId, String resourceId){
        String querySQL ="select * from "+DownloadFileConfig.TABLE_NAME+" where app_id=? and resource_id=? limit 1";
        List<DownloadTableInfo> tableInfos = DownloadFileConfig.getInstance().query(DownloadFileConfig.TABLE_NAME, querySQL, new String[]{appId, resourceId});
        if(tableInfos != null && tableInfos.size() > 0){
            return true;
        }
        return false;
    }
}
