package xiaoe.com.shop.business.login.presenter;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import xiaoe.com.shop.R;

public class LoginTimeCount extends CountDownTimer {

    private Context mContext;
    private View container;
    private TextView tvObtain;
    private TextView tvSecond;

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public LoginTimeCount(Context context, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.mContext = context;
    }

    public LoginTimeCount(Context context, long millisInFuture, long countDownInterval, View viewWrap) {
        super(millisInFuture, countDownInterval);
        this.mContext = context;
        this.container = viewWrap;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        tvObtain = (TextView) container.findViewById(R.id.login_register_code_obtain);
        tvSecond = (TextView) container.findViewById(R.id.login_register_code_second);
        tvSecond.setVisibility(View.VISIBLE);
        tvObtain.setEnabled(false);
        tvObtain.setTextColor(mContext.getResources().getColor(R.color.knowledge_item_desc_color));
        int second = (int) (millisUntilFinished / 1000);
        tvSecond.setText(String.format(mContext.getResources().getString(R.string.login_code_desc_content), second));
    }

    @Override
    public void onFinish() {
        tvSecond.setVisibility(View.GONE);
        tvObtain.setClickable(true);
        tvObtain.setTextColor(mContext.getResources().getColor(R.color.edit_cursor));
    }
}
