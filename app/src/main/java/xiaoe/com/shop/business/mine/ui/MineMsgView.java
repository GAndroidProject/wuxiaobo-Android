package xiaoe.com.shop.business.mine.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import xiaoe.com.shop.R;

public class MineMsgView extends RelativeLayout {

    private static final String TAG = "MineMsgView";

    private Context mContext;
    private TextView title_nickname;
    private SimpleDraweeView title_avatar;
    private Button title_buy_vip;

    public MineMsgView(Context context) {
        super(context);
        mContext = context;
        init(context);
    }

    public MineMsgView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(context);
    }

    public MineMsgView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(context);
    }

    public MineMsgView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        init(context);
    }

    private void init(Context context) {
        View view = View.inflate(context, R.layout.mine_msg, this);
        title_nickname = (TextView) view.findViewById(R.id.title_nickname);
        title_avatar = (SimpleDraweeView) view.findViewById(R.id.title_avatar);
        title_buy_vip = (Button) view.findViewById(R.id.title_buy_vip);
    }

    public void setAvatar(String imgUrl) {
        if (title_avatar != null) {
            title_avatar.setImageURI(imgUrl);
        }
    }

    public void setNickName(String nickName) {
        if (title_nickname != null) {
            title_nickname.setText(nickName);
        }
    }

    public void setBuyVipVisibility(int visibility) {
        title_buy_vip.setVisibility(visibility);
    }

    public void setBuyVipClickListener(OnClickListener listener) {
        title_buy_vip.setOnClickListener(listener);
    }
}
