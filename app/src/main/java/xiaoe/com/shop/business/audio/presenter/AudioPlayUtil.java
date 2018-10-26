package xiaoe.com.shop.business.audio.presenter;

import java.util.ArrayList;
import java.util.List;

import xiaoe.com.common.entitys.AudioPlayEntity;

public class AudioPlayUtil {
    private static final String TAG = "AudioPlayUtil";
    private List<AudioPlayEntity> audioList;
    private static AudioPlayUtil audioPlayUtil = null;

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

    }

}
