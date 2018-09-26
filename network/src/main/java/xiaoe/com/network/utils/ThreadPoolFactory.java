package xiaoe.com.network.utils;

/**
 * Created by Administrator on 2015/12/1.
 */
public class ThreadPoolFactory {
    public static ThreadPoolProxy commonThreadPool;

    public static ThreadPoolProxy getCommonThreadPool(){
        if(commonThreadPool == null){
            synchronized (ThreadPoolFactory.class){
                if(commonThreadPool == null){
                    commonThreadPool = new ThreadPoolProxy(10,20,100);
                }
            }
        }
        return commonThreadPool;
    }

}
