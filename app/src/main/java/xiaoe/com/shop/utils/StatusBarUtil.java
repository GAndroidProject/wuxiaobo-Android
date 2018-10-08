package xiaoe.com.shop.utils;

import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

public class StatusBarUtil {

    /**
     * 设置状态栏颜色
     * @param window
     * @param barColor
     * @param visibility
     */
    public static void setStatusBarColor(Window window,int barColor, int visibility){
        //状态栏颜色字体(白底黑字)修改 Android5.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(barColor);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.getDecorView().setSystemUiVisibility(visibility);
        }
    }
}
