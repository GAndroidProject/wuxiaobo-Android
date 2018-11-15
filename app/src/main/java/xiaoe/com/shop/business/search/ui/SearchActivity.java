package xiaoe.com.shop.business.search.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.common.entitys.SearchHistory;
import xiaoe.com.common.entitys.SearchHistoryEntity;
import xiaoe.com.common.db.SQLiteUtil;
import xiaoe.com.network.NetworkCodes;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.SearchRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.search.presenter.SearchPresenter;
import xiaoe.com.shop.business.search.presenter.SearchSQLiteCallback;
import xiaoe.com.shop.utils.StatusBarUtil;

public class SearchActivity extends XiaoeActivity {

    private static final String TAG = "SearchActivity";

    protected static final String MAIN = "main"; // 搜索主页
    protected static final String CONTENT = "content"; // 搜索内容页
    protected static final String EMPTY = "empty"; // 搜索空白页

    @BindView(R.id.search_wrap)
    LinearLayout searchWrap;
    @BindView(R.id.search_title_wrap)
    FrameLayout searchTitleWrap;
    @BindView(R.id.search_content_et)
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

    SearchPresenter searchPresenter;

    Object dataList; // 搜索结果

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);

        initData();
        initView();
        initListener();
    }

    // 搜索框后面的叉
    Drawable right;

    private void initView() {
        // 先默认显示我的财富计划
        searchContent.setHint("我的财富计划");
        searchContent.setSelection(searchContent.getText().toString().length());
        // 设置删除按钮
        initCloseIcon();
    }

    // 初始化关闭按钮
    private void initCloseIcon() {
        right = SearchActivity.this.getResources().getDrawable(R.mipmap.icon_clear);
        Rect rect = new Rect(0, 0, right.getIntrinsicWidth(), right.getIntrinsicHeight());
        right.setBounds(rect);
        searchContent.setCompoundDrawables(null, null, right, null);
    }

    private void initData() {
        searchPresenter = new SearchPresenter(this);

        // 初始化数据库
        SQLiteUtil.init(this.getApplicationContext(), new SearchSQLiteCallback());
        // 如果表不存在，就去创建
        if(!SQLiteUtil.tabIsExist(SearchSQLiteCallback.TABLE_NAME_CONTENT)){
            SQLiteUtil.execSQL(SearchSQLiteCallback.TABLE_SCHEMA_CONTENT);
        }
        historyList = queryAllData();
        currentFragment = SearchPageFragment.newInstance(R.layout.fragment_search_main);
        ((SearchPageFragment) currentFragment).setHistoryList(historyList);
        getSupportFragmentManager().beginTransaction().add(R.id.search_result_wrap, currentFragment, MAIN).commit();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {
        searchWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSoftKeyboard();
            }
        });

        searchCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragment.getTag().equals(CONTENT) || currentFragment.getTag().equals(EMPTY)) { // 点击取消时，是有搜索内容的话或者是空页面，切换为主页 fragment
                    replaceFragment(MAIN);
                } else {
                    onBackPressed();
                }
            }
        });

        // 输入完成后监听按键盘上的回车键进行搜索
        searchContent.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    // 关闭软件盘
                    toggleSoftKeyboard();
                    String content = searchContent.getText().toString();
                    if (TextUtils.isEmpty(content)) { // 不输入搜索内容，默认搜索最近在搜的第一个，先写死为 list
                        content = "我的财富计划";
                        searchContent.setText(content);
                        searchContent.setSelection(content.length());
                    } else {
                        if (!hasData(content)) { // 不为空并且没有存数据库，就存
                            // 将输入的内容插入到数据库
                            String currentTime = obtainCurrentTime();
                            SearchHistory searchHistory = new SearchHistory(content, currentTime);
                            SQLiteUtil.insert(SearchSQLiteCallback.TABLE_NAME_CONTENT, searchHistory);
                        }
                    }
                    obtainSearchResult(content);
                    return true;
                }
                return false;
            }
        });

        searchContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString();
                if (input.length() == 0) {
                    searchContent.setCompoundDrawables(null, null, null, null);
                } else {
                    searchContent.setCompoundDrawables(null, null, right, null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 拿到画在尾部的 drawable
                Drawable drawable = searchContent.getCompoundDrawables()[2];
                // 如果没有不处理
                if (drawable == null) {
                    return false;
                }
                // 如果不是抬起事件，不处理
                if (event.getAction() != MotionEvent.ACTION_UP) {
                    return false;
                }
                // 点中 drawable
                if (event.getX() > searchContent.getWidth() - searchContent.getPaddingEnd() - drawable.getIntrinsicWidth()) {
                    // 清空操作
                    searchContent.setText("");
                    searchContent.setSelection(searchContent.getText().toString().length());
                    return true;
                }
                return false;
            }
        });
    }

    // 根据搜索内容进行查找
    protected void obtainSearchResult(String content) {
        // 发送搜索请求，避免请求工具为空
        if (searchPresenter == null) {
            searchPresenter = new SearchPresenter(this);
        }
        searchPresenter.requestSearchResult(content);
    }

    // 替换 fragment
    protected void replaceFragment(String tag) {
        if (currentFragment != null) {
            getSupportFragmentManager().beginTransaction().hide(currentFragment).commit();
        }
        currentFragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (currentFragment == null) {
            switch (tag) {
                case MAIN: // 搜索主页
                    currentFragment = SearchPageFragment.newInstance(R.layout.fragment_search_main);
                    break;
                case CONTENT: // 搜索结果页
                    currentFragment = SearchPageFragment.newInstance(R.layout.fragment_search_result);
                    ((SearchPageFragment) currentFragment).setDataList(dataList);
                    break;
                case EMPTY: // 搜索空白页
                    currentFragment = SearchPageFragment.newInstance(R.layout.fragment_search_empty);
                    break;
            }
            if (currentFragment != null) {
                getSupportFragmentManager().beginTransaction().add(R.id.search_result_wrap, currentFragment, tag).commit();
            }
        } else { // fragment 不为空，结果页要显示更新操作
            switch (tag) {
                case MAIN:
                    break;
                case CONTENT:
                    ((SearchPageFragment) currentFragment).setDataList(dataList);
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
        if (this.dataList != null) { // 网络请求回来之后判断一下 dataList 是否为空，不为空则清空
            this.dataList = null;
        }
        JSONObject result = (JSONObject) entity;
        if (success) {
            if (iRequest instanceof SearchRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    JSONObject data = (JSONObject) result.get("data");
                    if (data.get("dataList") != null && ((JSONArray)data.get("dataList")).size() > 0) { // 有内容，就切换到内容页面
                        initResultData(data.get("dataList"));
                    } else { // 否则切换到空页
                        replaceFragment(EMPTY);
                    }
                } else if (code == NetworkCodes.CODE_SEARCH_ERROR) {
                    Log.d(TAG, "onMainThreadResponse: 店铺内搜索商品出错");
                }
            }
        } else {
            Log.d(TAG, "onMainThreadResponse: 请求失败..");
        }
    }

    // 初始化搜索结果
    private void initResultData(Object dataList) {
        this.dataList = dataList;
        replaceFragment(CONTENT);
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
        // 从 search_history 表里面找到 content = tempContent 的那条数据
        List<SearchHistory> lists = SQLiteUtil.query(SearchSQLiteCallback.TABLE_NAME_CONTENT,
                "select * from " + SearchSQLiteCallback.TABLE_NAME_CONTENT + " where " + SearchHistoryEntity.COLUMN_NAME_CONTENT + " = ?", new String[]{tempContent});
        // 已经有一条数据的话就不用再插入
        return lists.size() == 1;
    }

    // 查询最新创建的五条数据
    protected List<SearchHistory> queryAllData() {
        return SQLiteUtil.query(SearchSQLiteCallback.TABLE_NAME_CONTENT,
            "select * from " + SearchSQLiteCallback.TABLE_NAME_CONTENT + " order by " + SearchHistoryEntity.COLUMN_NAME_CREATE + " desc limit 5", null);
    }

    // 获取当前时间
    protected String obtainCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        return sdf.format(new Date());
    }
}
