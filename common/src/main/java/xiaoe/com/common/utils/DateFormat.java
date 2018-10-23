package xiaoe.com.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Administrator on 2016/10/27.
 */

public class DateFormat {
    private static final String TAG = "TimeFormat";
    /**
     *
     * @param ms 毫秒，不能超过24小时，
     */
    public static String longToString (long ms){

        int totalSecond = (int) (ms / 1000);//总时间（单位：秒）
        int h =  totalSecond / 3600;//时
        int m = (totalSecond - h * 3600) / 60;//分
        int s =((totalSecond - h * 3600) - m * 60);//秒
        String strH = ""+h;
        String strM = ""+m;
        String strS = ""+s;
        String time = "";
        if( h < 10){
            strH = "0"+h;
        }if(m < 10){
            strM = "0"+m;
        }if(s < 10){
            strS = "0"+s;
        }
//        Log.d(TAG, "longToString: "+strH+":"+strM+":"+strS);
        if(h <= 0){
            return strM+":"+strS;
        }
        return strH+":"+strM+":"+strS;
    }

    /**
     * 返回时间格式带毫秒
     * @param ms 毫秒，不能超过24小时，
     * @return
     */
    public static String longToStringByms (long ms){
        String strMS = ""+(ms % 1000);
        int totalSecond = (int) (ms / 1000);//总时间（单位：秒）
        int h =  totalSecond / 3600;//时
        int m = (totalSecond - h * 3600) / 60;//分
        int s =((totalSecond - h * 3600) - m * 60);//秒
        String strH = ""+h;
        String strM = ""+m;
        String strS = ""+s;
        if(h < 10){
            strH = "0"+h;
        }if(m < 10){
            strM = "0"+m;
        }if(s < 10){
            strS = "0"+s;
        }
//        Log.d(TAG, "longToString: "+strH+":"+strM+":"+strS);
        return strH+":"+strM+":"+strS+"."+strMS;
    }

    /**
     *
     * @param data 时间格式：HH:mm:ss
     */
    public static long stringToLong(String data){
        int index = data.indexOf(".");
        if(index != -1){
            data = data.substring(0,index);
        }
        String aryData[] = data.split(":");
        int h = Integer.parseInt(aryData[0]);//时
        int m = Integer.parseInt(aryData[1]);//分
        int s = Integer.parseInt(aryData[2]);//秒
        long ms = (h * 3600 + m * 60 + s) * 1000;//毫秒
        return ms;
    }

    public static String currentTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        return sdf.format(new Date());
    }
}
