package xiaoe.com.shop.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.shop.business.login.ui.LoginActivity;

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
        if (head.equals(LoginActivity.REGISTER_ERROR_TIP)) {
            errorView.setVisibility(View.VISIBLE);
            top = Dp2Px2SpUtil.dp2px(context, 6);
            layoutParams.setMargins(0, top, 0, 0);
            belowError.setLayoutParams(layoutParams);
            return;
        } else {
            ((TextView)errorView).setText("手机号格式错误，请重新输入");
        }
        if (head.length() == 2) {
            if (head.equals("13") || head.equals("14") || head.equals("15")
                    || head.equals("17") || head.equals("18") || head.equals("19")){
                top = Dp2Px2SpUtil.dp2px(context, 28);
                layoutParams.setMargins(0, top, 0, 0);
                errorView.setVisibility(View.GONE);
                belowError.setLayoutParams(layoutParams);
            } else {
                errorView.setVisibility(View.VISIBLE);
                top = Dp2Px2SpUtil.dp2px(context, 6);
                layoutParams.setMargins(0, top, 0, 0);
                belowError.setLayoutParams(layoutParams);
            }
        } else if (head.length() < 2) {
            if (head.length() == 1) {
                if (head.equals("1")) {
                    top = Dp2Px2SpUtil.dp2px(context, 28);
                    layoutParams.setMargins(0, top, 0, 0);
                    errorView.setVisibility(View.GONE);
                    belowError.setLayoutParams(layoutParams);
                } else {
                    errorView.setVisibility(View.VISIBLE);
                    top = Dp2Px2SpUtil.dp2px(context, 6);
                    layoutParams.setMargins(0, top, 0, 0);
                    belowError.setLayoutParams(layoutParams);
                }
            } else { // 没输入不能说是错的..
                top = Dp2Px2SpUtil.dp2px(context, 28);
                layoutParams.setMargins(0, top, 0, 0);
                errorView.setVisibility(View.GONE);
                belowError.setLayoutParams(layoutParams);
            }
        } else {
            if (head.contains("+") || head.contains("-") || head.contains(".") || head.contains(",") || head.contains(";")
                    || head.contains("*") || head.contains("#")) {
                errorView.setVisibility(View.VISIBLE);
                top = Dp2Px2SpUtil.dp2px(context, 6);
                layoutParams.setMargins(0, top, 0, 0);
                belowError.setLayoutParams(layoutParams);
            } else {
                if (head.substring(0, 2).equals("13") || head.substring(0, 2).equals("14") || head.substring(0, 2).equals("15")
                        || head.substring(0, 2).equals("17") || head.substring(0, 2).equals("18") || head.substring(0, 2).equals("19")) {
                    top = Dp2Px2SpUtil.dp2px(context, 28);
                    layoutParams.setMargins(0, top, 0, 0);
                    errorView.setVisibility(View.GONE);
                    belowError.setLayoutParams(layoutParams);
                }
            }
        }
    }

    /**
     * 隐藏错误 tip view
     * @param context
     * @param errorView
     * @param belowError
     */
    public static void hideErrorViewIfNeed(Context context, View errorView, View belowError) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int top = Dp2Px2SpUtil.dp2px(context, 28);
        layoutParams.setMargins(0, top, 0, 0);
        errorView.setVisibility(View.GONE);
        belowError.setLayoutParams(layoutParams);
    }
}