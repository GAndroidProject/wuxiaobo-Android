package xiaoe.com.shop.business.homepage.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import xiaoe.com.common.entitys.ComponentInfo;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.decorate.DecorateRecyclerAdapter;
import xiaoe.com.shop.base.BaseFragment;
import xiaoe.com.shop.business.homepage.presenter.HomepagePresenter;
import xiaoe.com.shop.business.homepage.presenter.HomepageRecyclerAdapter;

public class HomepageFragment extends BaseFragment {

    private static final String TAG = "HomepageFragment";

    private Unbinder unbinder;
    private Context mContext;

    private HomepagePresenter homePagePresenter;

    private boolean destroyView = false;

    @BindView(R.id.home_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.home_title_time)
    TextView homeTitleTime;
    @BindView(R.id.home_title_desc)
    TextView homeTitleDesc;
    @BindView(R.id.home_title_icon)
    ImageView homeTitleIcon;
    @BindView(R.id.home_title_icon_desc)
    TextView homeTitleIconDesc;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null, false);
        unbinder = ButterKnife.bind(this, view);
        mContext = getContext();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 网络请求数据代码
//        HomepagePresenter hp = new HomepagePresenter(this);
//        hp.requestData();

//        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
//        toolbar.setNavigationIcon(R.mipmap.ic_launcher);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                HomepageFragment.this.Toast("弹出一个吐司");
//            }
//        });
//        toolbar.setLogo(R.mipmap.ic_launcher);
//        toolbar.setTitle("我是标题");
//        toolbar.setSubtitle("这是子标题");
    }

    public void init() {
        homeTitleTime.setText("9月4日 星期二");
        homeTitleDesc.setText("今天");
        homeTitleIcon.setImageResource(R.drawable.icon_taday_learning);
        homeTitleIconDesc.setText("我正在学");
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayout.VERTICAL);
        List<ComponentInfo> tempData = new ArrayList<ComponentInfo>();
        ComponentInfo componentInfo_1 = new ComponentInfo();
        componentInfo_1.setType("flow_info");
        componentInfo_1.setSubType("imgText");
        componentInfo_1.setTitle("今日精选");
        componentInfo_1.setDesc("我的财富计划 新中产必修财富课程");
        componentInfo_1.setPrice("9.9");
        componentInfo_1.setHasBuy(false);
        componentInfo_1.setImgUrl("http://img.zcool.cn/community/0117e2571b8b246ac72538120dd8a4.jpg@1280w_1l_2o_100sh.jpg");
        ComponentInfo componentInfo_2 = new ComponentInfo();
        componentInfo_2.setType("flow_info");
        componentInfo_2.setSubType("audio");
        componentInfo_2.setTitle("音频组件");
        componentInfo_2.setDesc("我的财富计划 新中产必修财富课程");
        componentInfo_2.setPrice("999.99");
        componentInfo_2.setJoinedDesc("10928");
        componentInfo_2.setHasBuy(false);
        ComponentInfo componentInfo_3 = new ComponentInfo();
        componentInfo_3.setType("flow_info");
        componentInfo_3.setSubType("video");
        componentInfo_3.setTitle("视频组件");
        componentInfo_3.setDesc("每天看见吴晓波");
        componentInfo_3.setPrice("199.99");
        componentInfo_3.setImgUrl("http://pic14.nipic.com/20110605/1369025_165540642000_2.jpg");
        componentInfo_3.setHasBuy(true);
        tempData.add(componentInfo_1);
        tempData.add(componentInfo_2);
        tempData.add(componentInfo_3);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(new DecorateRecyclerAdapter(getActivity(), tempData));
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
//        Log.d(TAG, "onMainThreadResponse: isSuccess --- " + success);
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        destroyView = true;
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
