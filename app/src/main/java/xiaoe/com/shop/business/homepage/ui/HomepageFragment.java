package xiaoe.com.shop.business.homepage.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import xiaoe.com.common.entitys.ComponentInfo;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseFragment;
import xiaoe.com.shop.business.homepage.presenter.HomepagePresenter;
import xiaoe.com.shop.business.homepage.presenter.HomepageRecyclerAdapter;

public class HomepageFragment extends BaseFragment {

    private static final String TAG = "HomepageFragment";

    private Unbinder unbinder;
    private Context mContext;

    private HomepagePresenter homePagePresenter;

    private boolean destroyView = false;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null, false);
        unbinder = ButterKnife.bind(this, view);
        mContext = getContext();
        destroyView = false;
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
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayout.VERTICAL);
        List<ComponentInfo> tempData = new ArrayList<ComponentInfo>();
        ComponentInfo componentInfo_1 = new ComponentInfo(0, "第一个", "第一个内容嘀嘀嘀嘀嘀嘀嘀");
        ComponentInfo componentInfo_2 = new ComponentInfo(0, "第二个", "第二个内容啊啊啊啊啊啊啊");
        ComponentInfo componentInfo_3 = new ComponentInfo(1, "新的组件", "新的组件的内容应该不显示", "￥299.00");
        tempData.add(componentInfo_1);
        tempData.add(componentInfo_2);
        tempData.add(componentInfo_3);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(new HomepageRecyclerAdapter(getActivity(), tempData));
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
//        Log.d(TAG, "onMainThreadResponse: isSuccess --- " + success);
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
