package com.xiaoe.shop.wxb.business.audio.presenter;

import android.text.TextUtils;

import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.app.Constants;
import com.xiaoe.common.app.XiaoeApplication;
import com.xiaoe.common.db.SQLiteUtil;
import com.xiaoe.common.entitys.AudioPlayEntity;
import com.xiaoe.common.entitys.AudioPlayTable;

import java.util.ArrayList;
import java.util.List;

public class AudioPlayUtil {
    private static final String TAG = "AudioPlayUtil";
    private List<AudioPlayEntity> audioList,audioListNew;
    private static AudioPlayUtil audioPlayUtil = null;
    private boolean singleAudio = true; // 是否是单品音频，专栏列表中资源一个音频不属于单品，
    private String fromTag = "";
    private boolean isCloseMiniPlayer = true;

    private AudioPlayUtil(){
        audioList = new ArrayList<AudioPlayEntity>();
    }

    public static AudioPlayUtil getInstance(){
        if(audioPlayUtil == null){
            audioPlayUtil = new AudioPlayUtil();
        }
        return audioPlayUtil;
    }

    public List<AudioPlayEntity> getAudioList() {
        return audioList;
    }

    public List<AudioPlayEntity> getAudioListNew() {
        if (audioListNew == null)
            audioListNew = new ArrayList<AudioPlayEntity>();
        return audioListNew;
    }

    public void setAudioList(List<AudioPlayEntity> audioList) {
        this.audioList = audioList;
        deleteCache();
        addCache();
    }

    public void setAudioList2(List<AudioPlayEntity> audioList) {
        this.audioListNew = audioList;
        if (audioList != null && audioList.size() > 0)
            AudioMediaPlayer.mCurrentColumnId = audioList.get(0).getColumnId();
    }

    public void addAudio(AudioPlayEntity audio){
        if(this.audioList == null){
            audioList = new ArrayList<AudioPlayEntity>();
        }
        this.audioList.add(audio);
    }
//    public void addAudio(List<AudioPlayEntity> audioList){
//        if(audioList != null && audioList.size() > 0){
//            this.audioList.addAll(audioList);
//            SQLiteUtil sqLiteUtil = SQLiteUtil.init(XiaoeApplication.getmContext(), new AudioSQLiteUtil());
//            for (AudioPlayEntity audioPlayEntity : audioList){
//                String querySQL = "select * from "+ AudioPlayTable.TABLE_NAME + " where app_id=? and user_id=? and resource_id=?";
//                List list = sqLiteUtil.query(AudioPlayTable.TABLE_NAME, querySQL, new String[]{Constants.getAppId(), CommonUserInfo.getLoginUserIdOrAnonymousUserId(), audioPlayEntity.getResourceId()});
//                if(list != null && list.size() > 0){
//                    String updateWhereSQL = "app_id=? and user_id=? and resource_id=?";
//                    sqLiteUtil.update(AudioPlayTable.TABLE_NAME, audioPlayEntity, updateWhereSQL, new String[]{Constants.getAppId(), CommonUserInfo.getLoginUserIdOrAnonymousUserId(), audioPlayEntity.getResourceId()});
//                }else{
//                    sqLiteUtil.insert(AudioPlayTable.TABLE_NAME, audioPlayEntity);
//                }
//            }
//
//        }
//    }

    public void refreshAudio(AudioPlayEntity audio){
        if(this.audioList == null){
            audioList = new ArrayList<AudioPlayEntity>();
            audioList.add(audio);
        }else{
            audioList.clear();
            audioList.add(audio);
        }
        deleteCache();
        addCache();
    }

    /**
     * 是否是单品音频，专栏列表中资源一个音频不属于单品
     * @return
     */
    public boolean isSingleAudio() {
        return  1 == AudioPlayUtil.getInstance().getAudioList().size();
    }

    /**
     * 是否是单品音频，专栏列表中资源一个音频不属于单品
     * @param singleAudio
     */
    public void setSingleAudio(boolean singleAudio) {
        this.singleAudio = singleAudio;
    }

    public static boolean resourceEquals(String playResourceId, String playColumnId, String playBigColumnId, String resourceId, String columnId, String bigColumnId){
        resourceId = TextUtils.isEmpty(resourceId) ? "" : resourceId;
        columnId = TextUtils.isEmpty(columnId) ? "" : columnId;
        bigColumnId = TextUtils.isEmpty(bigColumnId) ? "" : bigColumnId;
        playResourceId = TextUtils.isEmpty(playResourceId) ? "" : playResourceId;
        playColumnId = TextUtils.isEmpty(playColumnId) ? "" : playColumnId;
        playBigColumnId = TextUtils.isEmpty(playBigColumnId) ? "" : playBigColumnId;

        return  resourceId.equals(playResourceId) && columnId.equals(playColumnId) && bigColumnId.equals(playBigColumnId);
    }

    public String getFromTag() {
        return fromTag;
    }

    public void setFromTag(String fromTag) {
        this.fromTag = fromTag;
    }

    private void deleteCache(){
        SQLiteUtil sqLiteUtil = SQLiteUtil.init(XiaoeApplication.getmContext(), new AudioSQLiteUtil());
        // 如果表不存在，就去创建
        if (!sqLiteUtil.tabIsExist(AudioPlayTable.TABLE_NAME)) {
            sqLiteUtil.execSQL(AudioPlayTable.CREATE_TABLE_SQL);
        }else{
            sqLiteUtil.deleteFrom(AudioPlayTable.TABLE_NAME);
        }
    }
    private void addCache(){
        SQLiteUtil sqLiteUtil = SQLiteUtil.init(XiaoeApplication.getmContext(), new AudioSQLiteUtil());
        // 如果表不存在，就去创建
        if (!sqLiteUtil.tabIsExist(AudioPlayTable.TABLE_NAME)) {
            sqLiteUtil.execSQL(AudioPlayTable.CREATE_TABLE_SQL);
        }else{
            sqLiteUtil.insert(AudioPlayTable.TABLE_NAME, audioList);
        }
    }

    public boolean isCloseMiniPlayer() {
        return isCloseMiniPlayer;
    }

    public void setCloseMiniPlayer(boolean closeMiniPlayer) {
        isCloseMiniPlayer = closeMiniPlayer;
    }
}
