package xiaoe.com.shop.business.search.ui;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.common.app.Global;
import xiaoe.com.common.entitys.SearchHistory;
import xiaoe.com.common.entitys.SearchHistoryEntity;
import xiaoe.com.common.utils.SQLiteUtil;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.search.presenter.SearchSQLiteCallback;
import xiaoe.com.shop.utils.StatusBarUtil;

public class SearchActivity extends XiaoeActivity {

    private static final String TAG = "SearchActivity";

    protected static final String MAIN = "main"; // 搜索主页
    protected static final String CONTENT = "content"; // 搜索内容页
    protected static final String EMPTY = "empty"; // 搜索空白页

    @BindView(R.id.search_content)
    EditText searchContent;
    @BindView(R.id.search_cancel)
    TextView searchCancel;
    @BindView(R.id.search_result_wrap)
    LinearLayout searchResultWrap;

    Fragment currentFragment;

    // 软键盘
    InputMethodManager imm;

    // 历史列表
    List<SearchHistory> historyList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //状态栏颜色字体(白底黑字)修改 Android6.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setStatusBarColor(getWindow(), Color.parseColor(Global.g().getGlobalColor()), View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            Global.g().setGlobalColor("#000000");
            StatusBarUtil.setStatusBarColor(getWindow(), Color.parseColor(Global.g().getGlobalColor()), View.SYSTEM_UI_FLAG_VISIBLE);
        }

        currentFragment = SearchPageFragment.newInstance(R.layout.fragment_search_main);
        getSupportFragmentManager().beginTransaction().add(R.id.search_result_wrap, currentFragment, EMPTY).commit();

        initData();
        initView();
        initListener();
    }

    private void initView() {

    }

    private void initData() {
        // 初始化数据库
        SQLiteUtil.init(this.getApplicationContext(), new SearchSQLiteCallback());
        historyList = queryAllData();
        for(SearchHistory searchHistory : historyList) {
            Log.d(TAG, "initData: id ---- " + searchHistory.getmId());
            Log.d(TAG, "initData: content ---- " + searchHistory.getmContent());
            Log.d(TAG, "initData: create --- " + searchHistory.getmCreate());
        }
    }

    private void initListener() {
        searchCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // 输入完成后监听按键盘上的回车键进行搜索
        searchContent.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    // TODO: 写入搜索操作的数据库搜索记录操作
                    toggleSoftKeyboard();
                    String content = searchContent.getText().toString();
                    if (!hasData(content)) { // 没有存数据库，就存
                        // 将输入的内容插入到数据库
                        String currentTime = obtainCurrentTime();
                        SearchHistory searchHistory = new SearchHistory(content, currentTime);
                        Log.d(TAG, "onKey: searchHistory --- " + searchHistory.toString());
                        SQLiteUtil.insert(SearchSQLiteCallback.TABLE_NAME_CONTENT, searchHistory);
                    }
                    // TODO: 请求搜索结构，拿到搜索结果
                    return true;
                }
                return false;
            }
        });
    }

    protected void replaceFragment(String tag) {
        if (currentFragment != null) {
            getSupportFragmentManager().beginTransaction().hide(currentFragment).commit();
        }
        currentFragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (currentFragment == null) {
            switch (tag) {
                case MAIN: // 搜索主页
                    break;
                case CONTENT: // 搜索结果页
                    break;
                case EMPTY: // 搜索空白页
                    currentFragment = SearchPageFragment.newInstance(R.layout.fragment_search_empty);
                    break;
            }
            if (currentFragment != null) {
                getSupportFragmentManager().beginTransaction().add(R.id.search_result_wrap, currentFragment, tag).commit();
            }
        } else {
            switch (tag) {
                case MAIN:
                    break;
                case CONTENT:
                    break;
                case EMPTY:
                    break;
            }
            getSupportFragmentManager().beginTransaction().show(currentFragment).commit();
        }
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        Log.d(TAG, "onMainThreadResponse: success --- " + success);
    }

    /**
     * 如果软键盘弹出，就关闭软键盘
     */
    private void toggleSoftKeyboard() {
        if (imm != null && imm.isActive()) {
            View view = getCurrentFocus();
            if (view != null) {
                IBinder iBinder = view.getWindowToken();
                imm.hideSoftInputFromWindow(iBinder, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    // 判断数据库是否已经存了这条数据
    private boolean hasData(String tempContent) {
        // 从 search_history 表里面找到 content = tempContent 的 id
        List<SearchHistory> lists = SQLiteUtil.query(SearchSQLiteCallback.TABLE_NAME_CONTENT,
                "select id as " + SearchHistoryEntity.COLUMN_NAME_ID + ", " +
                        SearchHistoryEntity.COLUMN_NAME_CONTENT + " from " +
                        SearchSQLiteCallback.TABLE_NAME_CONTENT + " where content = ?", new String[]{tempContent});
        // 已经有一条数据的话就不用再插入
        return lists.size() == 1;
    }

    // 查询数据库中全部数据
    protected List<SearchHistory> queryAllData() {
        return SQLiteUtil.query(SearchSQLiteCallback.TABLE_NAME_CONTENT,
            "select * from " + SearchSQLiteCallback.TABLE_NAME_CONTENT, null);
    }

    // 获取当前时间
    protected String obtainCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        return sdf.format(new Date());
    }
}
