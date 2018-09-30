package xiaoe.com.shop.business.course.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseFragment;
import xiaoe.com.shop.business.course.presenter.CoursePresenter;
import xiaoe.com.shop.interfaces.OnBottomTabSelectListener;
import xiaoe.com.shop.widget.BottomBarButton;
import xiaoe.com.shop.widget.BottomTabBar;
import xiaoe.com.shop.widget.CourseTitleView;

public class CourseFragment extends BaseFragment implements OnBottomTabSelectListener {

    private static final String TAG = "CourseFragment";

    private Unbinder unbinder;
    private Context mContext;

    @BindView(R.id.course_title_container)
    CourseTitleView courseTitleContainer;
    @BindView(R.id.course_title_bottom)
    BottomTabBar bottomTabBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course, null, false);
        unbinder = ButterKnife.bind(this, view);
        mContext = getContext();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    public void init() {
        mContext = getActivity();

        // 暂时写死三张图片
        List<String> imageList = new ArrayList<>();
        imageList.add("http://pic.58pic.com/58pic/15/63/07/42Q58PIC42U_1024.jpg");
        imageList.add("http://img.zcool.cn/community/0125fd5770dfa50000018c1b486f15.jpg@1280w_1l_2o_100sh.jpg");
        imageList.add("res:///" + R.drawable.audio);

        courseTitleContainer.setImageList(imageList);

        bottomTabBar.setBottomTabBarOrientation(LinearLayout.HORIZONTAL);
        bottomTabBar.setTabBarWeightSum(4);
        bottomTabBar.setBottomTabSelectListener(this);
        for (int i = 0; i < 4; i++){
            BottomBarButton radioButton = new BottomBarButton(getActivity());
            radioButton.setButtonText("按钮"+(i + 1));
            bottomTabBar.addTabButton(radioButton.getTabButton());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 网络请求数据代码
//        CoursePresenter cp = new CoursePresenter(this);
//        cp.requestData();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (courseTitleContainer != null) {
            courseTitleContainer.startTurning(2000);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (courseTitleContainer != null) {
            courseTitleContainer.stopTurning();
        }
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        Log.d(TAG, "onMainThreadResponse: isSuccess --- " + success);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onCheckedTab(int index) {
        Log.d(TAG, "onCheckedTab: index ---- " + index);
    }
}
