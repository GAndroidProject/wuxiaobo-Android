package xiaoe.com.shop.business.audio.presenter;

import java.util.ArrayList;
import java.util.List;

public class AudioPlayUtil {
    private static final String TAG = "AudioPlayUtil";
    private List<Audio> audioList;
    private static AudioPlayUtil audioPlayUtil = null;

    private AudioPlayUtil(){
        audioList = new ArrayList<Audio>();
    }

    public static AudioPlayUtil getInstance(){
        if(audioPlayUtil == null){
            audioPlayUtil = new AudioPlayUtil();
        }
        return audioPlayUtil;
    }

    public List<Audio> getAudioList() {
        return audioList;
    }

    public void setAudioList(List<Audio> audioList) {
        this.audioList = audioList;
    }

    public void addAudio(Audio audio){
        if(this.audioList == null){
            audioList = new ArrayList<Audio>();
        }
        this.audioList.add(audio);
    }
    public void addAudio(Audio audio, int index){
        if(this.audioList == null){
            audioList = new ArrayList<Audio>();
        }
        this.audioList.add(index, audio);
    }

    public static class Audio{
        private String url;
        private int index;
        private boolean play;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public boolean isPlay() {
            return play;
        }

        public void setPlay(boolean play) {
            this.play = play;
        }
    }
}
