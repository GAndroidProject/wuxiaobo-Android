package xiaoe.com.shop.business.homepage.presenter;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.common.entitys.ComponentInfo;
import xiaoe.com.shop.R;

public class HomepageRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "HomepageRecyclerAdapter";

    public static final int FLOW_INFO_IMG_TEXT = 0; // 信息流图文组件
    public static final int FLOW_INFO_VIDEO = 1; // 信息流视频组件
    public static final int FLOW_INFO_AUDIO = 2; // 信息流音频组件

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
            case FLOW_INFO_IMG_TEXT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_info_img_text, null);
                FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                frameLayoutParams.setMargins(0,0,0,30);
                view.setLayoutParams(frameLayoutParams);
                return new FlowInfoImgTextHolder(view);
            case FLOW_INFO_VIDEO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_info_video, null);
                LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                linearLayoutParams.setMargins(0,0,0,30);
                view.setLayoutParams(linearLayoutParams);
                return new FlowInfoVideoHolder(view);
            case FLOW_INFO_AUDIO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_info_audio, null);
                RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                relativeLayoutParams.setMargins(0,0,0,30);
                view.setLayoutParams(relativeLayoutParams);
                return new FlowInfoAudioHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemType = getItemViewType(position);
        switch (itemType) {
            case FLOW_INFO_IMG_TEXT:
                FlowInfoImgTextHolder fh = (FlowInfoImgTextHolder)holder;
                fh.flowInfoBg.setImageURI("http://img.zcool.cn/community/0125fd5770dfa50000018c1b486f15.jpg@1280w_1l_2o_100sh.jpg");
                fh.flowInfoTitle.setText(mComponentData.get(position).getTitle());
                fh.flowInfoDesc.setText(mComponentData.get(position).getDesc());
                break;
            case FLOW_INFO_VIDEO:
                FlowInfoVideoHolder vh = (FlowInfoVideoHolder)holder;
                vh.flowInfoBg.setImageURI("http://pic.58pic.com/58pic/15/63/07/42Q58PIC42U_1024.jpg");
                vh.flowInfoTitle.setText(mComponentData.get(position).getTitle());
                vh.flowInfoDesc.setText(mComponentData.get(position).getDesc());
                vh.flowInfoPrice.setText(mComponentData.get(position).getPrice());
                break;
            case FLOW_INFO_AUDIO:
                FlowInfoAudioHolder ah = (FlowInfoAudioHolder)holder;
                Uri audioBgUri = Uri.parse("res:///" + R.drawable.audio);
                Uri audioRingUri = Uri.parse("res:///" + R.drawable.ring);
                ah.flowInfoBg.setImageURI(audioBgUri);
                ah.flowInfoAvatar.setImageURI(audioRingUri);
                ah.flowInfoJoined.setText(mComponentData.get(position).getJoined());
                ah.flowInfoTitle.setText(mComponentData.get(position).getTitle());
                ah.flowInfoDesc.setText(mComponentData.get(position).getDesc());
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

    class FlowInfoImgTextHolder extends HomepageHolder {

        @BindView(R.id.flow_info_img_text_bg)
        SimpleDraweeView flowInfoBg;
        @BindView(R.id.flow_info_img_text_title)
        TextView flowInfoTitle;
        @BindView(R.id.flow_info_img_text_desc)
        TextView flowInfoDesc;

        FlowInfoImgTextHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class FlowInfoVideoHolder extends HomepageHolder {

        @BindView(R.id.flow_info_video_bg)
        SimpleDraweeView flowInfoBg;
        @BindView(R.id.flow_info_video_title)
        TextView flowInfoTitle;
        @BindView(R.id.flow_info_video_desc)
        TextView flowInfoDesc;
        @BindView(R.id.flow_info_video_price)
        TextView flowInfoPrice;

        FlowInfoVideoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class FlowInfoAudioHolder extends HomepageHolder {

        @BindView(R.id.flow_info_audio_bg)
        SimpleDraweeView flowInfoBg;
        @BindView(R.id.flow_info_audio_avatar)
        SimpleDraweeView flowInfoAvatar;
        @BindView(R.id.flow_info_audio_joined)
        TextView flowInfoJoined;
        @BindView(R.id.flow_info_audio_title)
        TextView flowInfoTitle;
        @BindView(R.id.flow_info_audio_desc)
        TextView flowInfoDesc;

        FlowInfoAudioHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
