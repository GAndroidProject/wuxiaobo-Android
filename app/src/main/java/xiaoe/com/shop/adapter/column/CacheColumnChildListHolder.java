package xiaoe.com.shop.adapter.column;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;
import xiaoe.com.shop.business.column.ui.CacheColumnActivity;

public class CacheColumnChildListHolder extends BaseViewHolder implements View.OnClickListener {
    private static final String TAG = "CacheColumnChildListHol";
    private final View rootView;
    private RelativeLayout btnChildSelect;
    private TextView childClassTitle;
    private TextView classPlayDuration;
    private ImageView classPlayIcon;

    public CacheColumnChildListHolder(View itemView) {
        super(itemView);
        rootView = itemView;

        initView();
    }

    private void initView() {
        btnChildSelect = (RelativeLayout) rootView.findViewById(R.id.btn_child_select);
        btnChildSelect.setOnClickListener(this);
        childClassTitle = (TextView) rootView.findViewById(R.id.child_class_title);
        childClassTitle.setText("2019买房还是投资的最佳选择吗？");
        classPlayDuration = (TextView) rootView.findViewById(R.id.class_play_duration);
        classPlayDuration.setText("123:01");
        classPlayIcon = (ImageView) rootView.findViewById(R.id.class_play_icon);
    }

    public void bindView(){
        setSelectDelete();
    }

    private void setSelectDelete() {
        if(CacheColumnActivity.isBatchDelete){
            classPlayIcon.setVisibility(View.GONE);
            btnChildSelect.setVisibility(View.VISIBLE);
        }else{
            classPlayIcon.setVisibility(View.VISIBLE);
            btnChildSelect.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {

    }
}
