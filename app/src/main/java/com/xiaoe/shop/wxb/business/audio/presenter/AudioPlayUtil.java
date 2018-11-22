package com.xiaoe.shop.wxb.business.audio.presenter;

import android.text.TextUtils;

import com.xiaoe.common.app.XiaoeApplication;
import com.xiaoe.common.db.SQLiteUtil;
import com.xiaoe.common.entitys.AudioPlayEntity;
import com.xiaoe.common.entitys.AudioPlayTable;

import java.util.ArrayList;
import java.util.List;

public class AudioPlayUtil {
    private static final String TAG = "AudioPlayUtil";
    private List<AudioPlayEntity> audioList;
    private static AudioPlayUtil audioPlayUtil = null;
    private boolean singleAudio = true; // 是否是单品音频，专栏列表中资源一个音频不属于单品，
    private String fromTag = "";

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

    public void setAudioList(List<AudioPlayEntity> audioList) {
        this.audioList = audioList;
        deleteCache();
        addCache();
    }

    public void addAudio(AudioPlayEntity audio){
        if(this.audioList == null){
            audioList = new ArrayList<AudioPlayEntity>();
        }
        this.audioList.add(audio);
    }
    public void addAudio(AudioPlayEntity audio, int index){
        if(this.audioList == null){
            audioList = new ArrayList<AudioPlayEntity>();
        }
        this.audioList.add(index, audio);
    }

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
        return singleAudio;
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

        boolean resourceEquals = resourceId.equals(playResourceId) && columnId.equals(playColumnId) && bigColumnId.equals(playBigColumnId);
        return  resourceEquals;
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
}
