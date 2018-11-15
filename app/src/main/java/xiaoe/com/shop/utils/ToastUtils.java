package xiaoe.com.shop.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * @author wecodexyz@gmail.com
 * @date 2017/10/12 下午3:14
 * GitHub - https://github.com/wecodexyz
 * Description:
 */
public class ToastUtils {

    private ToastUtils() {
    }

    public static void show(@NonNull Context context, @NonNull String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showL(@NonNull Context context, @NonNull String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void show(@NonNull Context context, @StringRes int msg) {
        show(context, context.getString(msg));
    }
}
