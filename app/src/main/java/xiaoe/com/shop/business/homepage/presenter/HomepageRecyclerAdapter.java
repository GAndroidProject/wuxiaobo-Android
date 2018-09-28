package xiaoe.com.shop.business.homepage.presenter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.common.entitys.ComponentInfo;
import xiaoe.com.shop.R;

public class HomepageRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "HomepageRecyclerAdapter";

    protected static final int FLOW_INFO_FULL = 0; // 信息流全图组件
    protected static final int FLOW_INFO_TOP = 1; // 信息流上图组件

    private Context mContext;
    private List<ComponentInfo> mComponentData;

    public HomepageRecyclerAdapter(Context context, List<ComponentInfo> listData) {
        mContext = context;
        mComponentData = listData;
    }

    @Override
    public int getItemCount() {
        return mComponentData == null ? 0 : mComponentData.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: viewType --- " + viewType);
        View view;
        switch (viewType) {
            case FLOW_INFO_FULL:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_info_full, null);
                FrameLayout.LayoutParams FrameLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(FrameLayoutParams);
                return new FlowInfoFullHolder(view);
            case FLOW_INFO_TOP:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_info_top, null);
                LinearLayout.LayoutParams LinearLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(LinearLayoutParams);
                return new FlowInfoTopHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemType = getItemViewType(position);
        switch (itemType) {
            case FLOW_INFO_FULL:
                FlowInfoFullHolder fh = (FlowInfoFullHolder)holder;
                fh.flowInfoBg.setImageResource(R.drawable.demo);
                fh.flowInfoTitle.setText(mComponentData.get(position).getTitle());
                fh.flowInfoDesc.setText(mComponentData.get(position).getDesc());
                break;
            case FLOW_INFO_TOP:
                FlowInfoTopHolder th = (FlowInfoTopHolder)holder;
                th.flowInfoBg.setImageResource(R.drawable.demo_top);
                th.flowInfoTitle.setText(mComponentData.get(position).getTitle());
                th.flowInfoDesc.setText(mComponentData.get(position).getTitle());
                th.flowInfoPrice.setText(mComponentData.get(position).getPrice());
                break;
            default:
                Log.d(TAG, "onBindBaseHolder: run default.");
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mComponentData.get(position).getType();
    }

    class HomepageHolder extends RecyclerView.ViewHolder {

        HomepageHolder(View itemView) {
            super(itemView);
        }
    }

    class FlowInfoFullHolder extends HomepageHolder {

        @BindView(R.id.flow_info_full_bg)
        ImageView flowInfoBg;
        @BindView(R.id.flow_info_full_title)
        TextView flowInfoTitle;
        @BindView(R.id.flow_info_full_desc)
        TextView flowInfoDesc;

        FlowInfoFullHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class FlowInfoTopHolder extends HomepageHolder {

        @BindView(R.id.flow_info_top_bg)
        ImageView flowInfoBg;
        @BindView(R.id.flow_info_top_title)
        TextView flowInfoTitle;
        @BindView(R.id.flow_info_top_desc)
        TextView flowInfoDesc;
        @BindView(R.id.flow_info_top_price)
        TextView flowInfoPrice;

        FlowInfoTopHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
