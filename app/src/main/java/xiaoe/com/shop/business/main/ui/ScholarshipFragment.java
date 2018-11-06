package xiaoe.com.shop.business.main.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import xiaoe.com.common.app.Global;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseFragment;
import xiaoe.com.shop.common.JumpDetail;
import xiaoe.com.shop.utils.StatusBarUtil;
import xiaoe.com.shop.widget.ListBottomLoadMoreView;
import xiaoe.com.shop.widget.ScrollViewPager;

public class ScholarshipFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "ScholarshipFragment";

    private Unbinder unbinder;
    private Context mContext;

    @BindView(R.id.scholarship_rule)
    TextView scholarshipRule;
    @BindView(R.id.scholarship_step_one)
    ImageView scholarshipStepOne;
    @BindView(R.id.scholarship_step_two)
    ImageView scholarshipStepTwo;
    @BindView(R.id.scholarship_step_three)
    ImageView scholarshipStepThree;
    @BindView(R.id.scholarship_divide)
    Button scholarshipDivide;
    @BindView(R.id.scholarship_real_range)
    TextView scholarshipRealRange;
    @BindView(R.id.scholarship_all_range)
    TextView scholarshipAllRange;
    @BindView(R.id.scholarship_view_pager)
    ScrollViewPager scholarshipViewpager;
    @BindView(R.id.scholarship_bottom_load_more)
    ListBottomLoadMoreView scholarshipLoadMore;

    protected static final String RULE = "rule";
    protected static final String GO_BUY = "go_buy";

    Dialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scholarship, null, false);
        unbinder = ButterKnife.bind(this, view);
        mContext = getContext();
        view.setPadding(0, StatusBarUtil.getStatusBarHeight(mContext), 0, 0);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initListener();
    }

    private void initListener() {
        scholarshipRule.setOnClickListener(this);
        scholarshipDivide.setOnClickListener(this);
        scholarshipRealRange.setOnClickListener(this);
        scholarshipAllRange.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.scholarship_rule:
                showDialogByType(RULE);
                break;
            case R.id.scholarship_divide:
                Log.d(TAG, "onClick: ===========================");
                showDialogByType(GO_BUY);
                break;
            case R.id.scholarship_real_range:
                Toast.makeText(getActivity(), "实时榜...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.scholarship_all_range:
                Toast.makeText(getActivity(), "总排行...", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void showDialogByType(String type) {
        Window window;
        View view;
        ImageView dialogClose;
        TextView dialogTitle;
        TextView dialogContent;
        TextView dialogBtn;
        switch (type) {
            case RULE:
                dialog = new Dialog(mContext, R.style.ActionSheetDialogStyle);
                window = dialog.getWindow();
                view = getActivity().getLayoutInflater().inflate(R.layout.radius_dialog, null);

                dialogClose = (ImageView) view.findViewById(R.id.radius_dialog_close);
                dialogTitle = (TextView) view.findViewById(R.id.radius_dialog_title);
                dialogContent = (TextView) view.findViewById(R.id.radius_dialog_content);
                dialogBtn = (TextView) view.findViewById(R.id.radius_dialog_btn);

                dialogClose.setVisibility(View.GONE);
                dialogContent.setTextSize(14);
                dialogContent.setTextColor(getActivity().getResources().getColor(R.color.recent_list_color));
                dialogTitle.setText(mContext.getResources().getString(R.string.scholarship_dialog_rule_title));
                dialogContent.setText(mContext.getResources().getString(R.string.scholarship_dialog_rule_content));
                dialogBtn.setText(mContext.getResources().getString(R.string.scholarship_dialog_rule_btn));
                dialogBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                // 先 show 后才会有宽高
                dialog.show();

                if (window != null) {
                    window.setGravity(Gravity.CENTER);
                    WindowManager.LayoutParams layoutParams = window.getAttributes();
                    Point point = Global.g().getDisplayPixel();
                    layoutParams.width = (int) (point.x * 0.8);
                    window.setAttributes(layoutParams);
                }
                dialog.setContentView(view);
                break;
            case GO_BUY:
                dialog = new Dialog(mContext, R.style.ActionSheetDialogStyle);
                window = dialog.getWindow();
                view = getActivity().getLayoutInflater().inflate(R.layout.radius_dialog, null);

                dialogClose = (ImageView) view.findViewById(R.id.radius_dialog_close);
                dialogTitle = (TextView) view.findViewById(R.id.radius_dialog_title);
                dialogContent = (TextView) view.findViewById(R.id.radius_dialog_content);
                dialogBtn = (TextView) view.findViewById(R.id.radius_dialog_btn);

                dialogClose.setVisibility(View.VISIBLE);
                dialogTitle.setVisibility(View.GONE);
                dialogContent.setText(getActivity().getResources().getString(R.string.scholarship_dialog_go_buy_content));
                dialogContent.setTextSize(16);
                dialogContent.setTextColor(getActivity().getResources().getColor(R.color.main_title_color));
                dialogBtn.setText(getActivity().getResources().getString(R.string.scholarship_dialog_rule_btn));
                dialogBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        JumpDetail.
                        Toast.makeText(getActivity(), "啊哈哈哈", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();

                if (window != null) {
                    window.setGravity(Gravity.CENTER);
                    WindowManager.LayoutParams layoutParams = window.getAttributes();
                    Point point = Global.g().getDisplayPixel();
                    layoutParams.width = (int) (point.x * 0.8);
                    window.setAttributes(layoutParams);
                }
                dialog.setContentView(view);
                break;
        }
    }
}
