package xiaoe.com.network.requests;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;

public class AudioContentRequest extends IRequest {
    public AudioContentRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd,iBizCallback);
    }
    public void sendRequest(){
        NetworkEngine.getInstance().sendRequest(this);
    }
}
