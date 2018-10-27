package xiaoe.com.shop.events;

public class AudioPlayEvent {
    public static final int LOADING = 1001;
    public static final int PLAY = 1002;
    public static final int PAUSE = 1003;
    public static final int STOP = 1004;
    public static final int PROGRESS = 1005;
    public static final int PREPARE = 1006;
    public static final int NEXT = 1007;
    public static final int LAST = 1008;
    private int state;
    private int progress;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
