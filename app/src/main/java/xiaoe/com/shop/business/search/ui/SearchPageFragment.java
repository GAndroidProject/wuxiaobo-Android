package xiaoe.com.shop.business.search.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseFragment;
import xiaoe.com.shop.business.search.presenter.OnTabClickListener;
import xiaoe.com.shop.business.search.presenter.SearchMainContentRecyclerAdapter;

public class SearchPageFragment extends BaseFragment implements OnTabClickListener {

    private static final String TAG = "SearchPageFragment";

    private Unbinder unbinder;
    private Context mContext;
    private boolean destroyView = false;
    protected View viewWrap;

    private int layoutId = -1;
    SearchActivity searchActivity;

    // 软键盘
    InputMethodManager imm;

    SearchContentView historyContentView;
    SearchContentView recommendContentView;

    public static SearchPageFragment newInstance(int layoutId) {
        SearchPageFragment searchPageFragment = new SearchPageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("layoutId", layoutId);
        searchPageFragment.setArguments(bundle);
        return searchPageFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            layoutId = bundle.getInt("layoutId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewWrap = inflater.inflate(layoutId, null, false);
        unbinder = ButterKnife.bind(this, viewWrap);
        mContext = getContext();
        searchActivity = (SearchActivity) getActivity();
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        return viewWrap;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (layoutId != -1) {
            initView();
        }
    }

    private void initView() {
        switch (layoutId) {
            case R.layout.fragment_search_main:
                initSearchMainFragment();
                break;
            case R.layout.fragment_search_empty:
                // do nothing;
                break;
        }
    }

    private void initSearchMainFragment() {
        historyContentView = (SearchContentView) viewWrap.findViewById(R.id.history_content);
        historyContentView.setTitleStartText("历史搜索");
        historyContentView.setTitleEndText("清空历史搜索");

        List<String> historyData = new ArrayList<>();
        historyData.add("区块链");
        historyData.add("我的财富计划");
        historyData.add("升职记");
        historyData.add("健康");
        historyData.add("新税收政策");
        SearchMainContentRecyclerAdapter historyAdapter = new SearchMainContentRecyclerAdapter(mContext, historyData);
        historyAdapter.setOnTabClickListener(this);
        historyContentView.setContentAdapter(historyAdapter);

        recommendContentView = (SearchContentView) viewWrap.findViewById(R.id.recommend_content);;
        recommendContentView.setTitleEndVisibility(View.GONE);
        recommendContentView.setTitleStartText("大家都在搜");

        List<String> recommendData = new ArrayList<>();
        recommendData.add("我的财富计划");
        recommendData.add("吴晓波频道");
        recommendData.add("我的职场计划");
        recommendData.add("避免败局");
        
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "onItemClick:  ----------- " + (historyContentView.isMatchRecycler(view.getParent())));
    }
}
