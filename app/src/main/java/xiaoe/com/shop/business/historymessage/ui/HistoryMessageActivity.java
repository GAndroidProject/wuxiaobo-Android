package xiaoe.com.shop.business.historymessage.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import xiaoe.com.common.entitys.HistoryMessageEntity;
import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.historymessage.HistoryMessageAdapter;
import xiaoe.com.shop.base.XiaoeActivity;

/**
 * @author flynnWang
 * @date 2018/11/13
 * <p>
 * 描述：我的-历史消息页面
 */
public class HistoryMessageActivity extends XiaoeActivity {

    private EmptyView mEmptyView;
    private SwipeRefreshLayout refreshLayout;

    private List<HistoryMessageEntity> transOrderList = new ArrayList<>();
    private HistoryMessageAdapter historyMessageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_message);
    }
}
