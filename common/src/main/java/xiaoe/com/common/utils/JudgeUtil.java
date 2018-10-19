package xiaoe.com.common.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class JudgeUtil {

    /**
     * 显示错误提示的 View，只适用于手机号输入异常的时候
     * @param context 上下文
     * @param head 手机号
     * @param errorView 需要显示错误信息的 view
     * @param belowError 在错误信息 view 下面的 view
     */
    public static void showErrorViewIfNeed(Context context, String head, View errorView, View belowError) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int top;
        int right;
        int left;
        if (head.equals("13") || head.equals("14") || head.equals("15")
                || head.equals("17") || head.equals("18") || head.equals("19")) {
            left = Dp2Px2SpUtil.dp2px(context, 20);
            right = Dp2Px2SpUtil.dp2px(context, 20);
            top = Dp2Px2SpUtil.dp2px(context, 28);
            layoutParams.setMargins(left, top, right, 0);
            errorView.setVisibility(View.GONE);
            belowError.setLayoutParams(layoutParams);
        } else {
            errorView.setVisibility(View.VISIBLE);
            left = Dp2Px2SpUtil.dp2px(context, 20);
            right = Dp2Px2SpUtil.dp2px(context, 20);
            top = Dp2Px2SpUtil.dp2px(context, 6);
            layoutParams.setMargins(left, top, right, 0);
            belowError.setLayoutParams(layoutParams);
        }
    }

    public static void hideErrorViewIfNeed(Context context, View errorView, View belowError) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int top = Dp2Px2SpUtil.dp2px(context, 28);
        int right = Dp2Px2SpUtil.dp2px(context, 20);
        int left = Dp2Px2SpUtil.dp2px(context, 20);
        layoutParams.setMargins(left, top, right, 0);
        errorView.setVisibility(View.GONE);
        belowError.setLayoutParams(layoutParams);
    }
}
