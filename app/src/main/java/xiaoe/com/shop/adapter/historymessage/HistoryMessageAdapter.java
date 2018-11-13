package xiaoe.com.shop.adapter.historymessage;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import xiaoe.com.common.entitys.HistoryMessageEntity;
import xiaoe.com.shop.R;

/**
 * @author flynnWang
 * @date 2018/11/13
 * <p>
 * 描述：
 */
public class HistoryMessageAdapter extends BaseQuickAdapter<HistoryMessageEntity, BaseViewHolder> {

    public HistoryMessageAdapter() {
        super(R.layout.item_history_message);
    }

    @Override
    protected void convert(BaseViewHolder helper, HistoryMessageEntity item) {

    }
}
