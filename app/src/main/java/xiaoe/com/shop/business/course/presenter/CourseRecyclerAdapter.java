package xiaoe.com.shop.business.course.presenter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.common.entitys.ComponentInfo;
import xiaoe.com.shop.R;

public class CourseRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "CourseRecyclerAdapter";

    public static final int RECENT_UPDATE = 0; // 最新更新组件

    private Context mContext;
    private List<ComponentInfo> mComponentData;

    public CourseRecyclerAdapter(Context context, List<ComponentInfo> componentData) {
        mContext = context;
        mComponentData = componentData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case RECENT_UPDATE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_update, null);
                LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                linearLayoutParams.setMargins(0,0,0,30);
                view.setLayoutParams(linearLayoutParams);
                return new RecentUpdateHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        int viewType = getItemViewType(position);
        RecentUpdateHolder rh = (RecentUpdateHolder) holder;
        rh.mRecentUpdateAvatar.setImageURI("res:///" + R.mipmap.audio_ring);
        rh.mRecentUpdateSubTitle.setText("每天听见吴晓波");
        rh.mRecentUpdateSubDesc.setText("已更新至09-22期");
        rh.mRecentUpdateSubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "点击了按钮", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        // 暂时写死，因为 recyclerview 只有在此方法返回值大于 0 时才会进行渲染
        return mComponentData == null ? 1 : mComponentData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    class RecentUpdateHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.recent_update_sub_list)
        LinearLayout mRecentUpdateSubList;
        @BindView(R.id.recent_update_avatar)
        SimpleDraweeView mRecentUpdateAvatar;
        @BindView(R.id.recent_update_sub_title)
        TextView mRecentUpdateSubTitle;
        @BindView(R.id.recent_update_sub_desc)
        TextView mRecentUpdateSubDesc;
        @BindView(R.id.recent_update_sub_btn)
        Button mRecentUpdateSubBtn;

        RecentUpdateHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
