package xiaoe.com.shop.events;

public class AudioPlayEvent {
    public static final int LOADING = 1001;
    public static final int PLAY = 1002;
    public static final int PAUSE = 1003;
    public static final int STOP = 1004;
    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
