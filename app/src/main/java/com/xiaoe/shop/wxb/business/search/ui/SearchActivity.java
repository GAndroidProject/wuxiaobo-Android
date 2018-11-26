package com.xiaoe.shop.wxb.business.search.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
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
import com.xiaoe.common.app.Global;
import com.xiaoe.common.db.SQLiteUtil;
import com.xiaoe.common.entitys.SearchHistory;
import com.xiaoe.common.entitys.SearchHistoryEntity;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.SearchRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.business.search.presenter.SearchPresenter;
import com.xiaoe.shop.wxb.business.search.presenter.SearchSQLiteCallback;
import com.xiaoe.shop.wxb.events.OnClickEvent;
import com.xiaoe.shop.wxb.utils.StatusBarUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    // 搜索框后面的叉
    Drawable right;

    Object dataList; // 搜索结果
    private SQLiteUtil sqLiteUtil;
    protected boolean hasDecorate; // 已经渲染
    protected int pageIndex = 1; // 默认页码
    protected int pageSize = 10; // 默认每页大小
    protected String searchKeyword; // 搜索内容

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        //状态栏颜色字体(白底黑字)修改 Android6.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setStatusBarColor(getWindow(), Color.parseColor(Global.g().getGlobalColor()), View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);

        initData();
        initView();
        initListener();
    }

    private void initView() {
        // 先默认显示我的财富计划
        searchContent.setHint("我的财富计划");
        searchContent.setSelection(searchContent.getText().toString().length());
    }

    private void initData() {
        searchPresenter = new SearchPresenter(this);

        // 初始化数据库
        sqLiteUtil = SQLiteUtil.init(this.getApplicationContext(), new SearchSQLiteCallback());
        // 如果表不存在，就去创建
        if(!sqLiteUtil.tabIsExist(SearchSQLiteCallback.TABLE_NAME_CONTENT)){
            sqLiteUtil.execSQL(SearchSQLiteCallback.TABLE_SCHEMA_CONTENT);
        }
        historyList = queryAllData();
        currentFragment = SearchPageFragment.newInstance(R.layout.fragment_search_main);
        ((SearchPageFragment) currentFragment).setHistoryList(historyList);
        getSupportFragmentManager().beginTransaction().add(R.id.search_result_wrap, currentFragment, MAIN).commit();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {
        searchWrap.setOnClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
                toggleSoftKeyboard();
            }
        });

        searchCancel.setOnClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
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
                    }
                    if (!hasDbData(content)) { // 不为空并且没有存数据库，就存
                        // 将输入的内容插入到数据库
                        String currentTime = obtainCurrentTime();
                        SearchHistory searchHistory = new SearchHistory(content, currentTime);
                        sqLiteUtil.insert(SearchSQLiteCallback.TABLE_NAME_CONTENT, searchHistory);

                        // 刷新界面
                        if (((SearchPageFragment) currentFragment).historyData != null && ((SearchPageFragment) currentFragment).historyAdapter != null) {
                            if (((SearchPageFragment) currentFragment).historyData.size() == 5) {
                                ((SearchPageFragment) currentFragment).historyData.add(0, searchHistory);
                                ((SearchPageFragment) currentFragment).historyData.remove(((SearchPageFragment) currentFragment).historyData.size() - 1);
                            } else if (((SearchPageFragment) currentFragment).historyData.size() < 5)  { // 否则直接添加
                                ((SearchPageFragment) currentFragment).historyData.add(0, searchHistory);
                            }
                            ((SearchPageFragment) currentFragment).historyAdapter.notifyDataSetChanged();
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
                if (right == null) { // 如果关闭图片为 null 的话就进行初始化
                    initCloseIcon();
                }
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

    // 初始化关闭按钮
    private void initCloseIcon() {
        right = SearchActivity.this.getResources().getDrawable(R.mipmap.icon_clear);
        Rect rect = new Rect(0, 0, right.getIntrinsicWidth(), right.getIntrinsicHeight());
        right.setBounds(rect);
//        searchContent.setCompoundDrawables(null, null, right, null);
    }

    // 根据搜索内容进行查找
    protected void obtainSearchResult(String content) {
        // 发送搜索请求，避免请求工具为空
        if (searchPresenter == null) {
            searchPresenter = new SearchPresenter(this);
        }
        // searchPresenter.requestSearchResult(content);
        searchKeyword = content;
        searchPresenter.requestSearchResultByPage(content, pageIndex, pageSize);
        getDialog().showLoadDialog(false);
    }

    // 替换 fragment
    protected void replaceFragment(String tag) {
        if (currentFragment != null) {
            if (MAIN.equals(tag)) {
                if (((SearchPageFragment) currentFragment).itemJsonList != null &&
                    ((SearchPageFragment) currentFragment).groupJsonList != null &&
                    ((SearchPageFragment) currentFragment).decorateRecyclerAdapter != null) {
                    ((SearchPageFragment) currentFragment).itemJsonList.clear();
                    ((SearchPageFragment) currentFragment).groupJsonList.clear();
                    ((SearchPageFragment) currentFragment).decorateRecyclerAdapter.notifyDataSetChanged();
                } else {
                    hasDecorate = false;
                }
            }
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
        getDialog().dismissDialog();
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
    protected boolean hasDbData(String tempContent) {
        // 从 search_history 表里面找到 content = tempContent 的那条数据
        List<SearchHistory> lists = sqLiteUtil.query(SearchSQLiteCallback.TABLE_NAME_CONTENT,
                "select * from " + SearchSQLiteCallback.TABLE_NAME_CONTENT + " where " + SearchHistoryEntity.COLUMN_NAME_CONTENT + " = ?", new String[]{tempContent});
        // 已经有一条数据的话就不用再插入
        return lists.size() == 1;
    }

    // 查询最新创建的五条数据
    protected List<SearchHistory> queryAllData() {
        return sqLiteUtil.query(SearchSQLiteCallback.TABLE_NAME_CONTENT,
            "select * from " + SearchSQLiteCallback.TABLE_NAME_CONTENT + " order by " + SearchHistoryEntity.COLUMN_NAME_CREATE + " desc limit 5", null);
    }

    // 获取当前时间
    protected String obtainCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        return sdf.format(new Date());
    }
}
