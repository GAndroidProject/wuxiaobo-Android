package xiaoe.com.shop.adapter.historymessage;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import xiaoe.com.common.entitys.HistoryMessageEntity;
import xiaoe.com.shop.R;
import xiaoe.com.shop.widget.GlideCircleTransform;

/**
 * @author flynnWang
 * @date 2018/11/13
 * <p>
 * 描述：
 */
public class HistoryMessageAdapter extends BaseQuickAdapter<HistoryMessageEntity.ListBean, BaseViewHolder> {

    private Context mContext;

    public HistoryMessageAdapter(Context context) {
        super(R.layout.item_history_message);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, HistoryMessageEntity.ListBean item) {
        ImageView ivAvatar = helper.getView(R.id.iv_avatar);
        Glide.with(mContext)
                .load(item.getWx_avatar())
                .transform(new GlideCircleTransform(mContext))
                .crossFade()
                .placeholder(R.mipmap.default_avatar)
                .error(R.mipmap.default_avatar)
                .into(ivAvatar);
        helper.setText(R.id.tv_username, item.getSend_nick_name())
                .setText(R.id.tv_message_date, item.getCreated_at())
                .setText(R.id.tv_title, item.getTitle())
                .setText(R.id.tv_content, item.getContent());
    }
}
