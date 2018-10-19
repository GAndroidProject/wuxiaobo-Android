package xiaoe.com.shop.adapter.download;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;
import xiaoe.com.shop.interfaces.IonSlidingButtonListener;
import xiaoe.com.shop.widget.SlidingButtonView;

public class BatchDownloadViewHolder2 extends BaseViewHolder {
    private static final String TAG = "BatchDownloadHolder";
    private final View mItemView;
    TextView tv_delete;
    TextView mText;
    LinearLayout layout_content;
    private IonSlidingButtonListener mListener;

    public BatchDownloadViewHolder2(View itemView, IonSlidingButtonListener listener) {
        super(itemView);
        mItemView = itemView;
        mListener = listener;
        initView();
    }

    private void initView() {
        tv_delete = (TextView)mItemView.findViewById(R.id.tv_delete);

        mText = (TextView)mItemView.findViewById(R.id.text);

        layout_content = (LinearLayout)mItemView.findViewById(R.id.layout_content);
        ((SlidingButtonView)mItemView).setSlidingButtonListener(mListener);
    }

    public void bindView(int position){

    }
}
