package xiaoe.com.shop.business.search.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.common.app.Global;
import xiaoe.com.common.entitys.SearchHistory;
import xiaoe.com.common.entitys.SearchHistoryEntity;
import xiaoe.com.common.utils.Dp2Px2SpUtil;
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

    @BindView(R.id.search_wrap)
    LinearLayout searchWrap;
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
                onBackPressed();
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
                        content = "list";
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

        // editText 文字长度大于 0 后显示 drawable
        searchContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString();
                if (input.length() > 0) {
                    Drawable right = SearchActivity.this.getResources().getDrawable(R.mipmap.icon_clear);
                    Rect rect = new Rect(0, 0, right.getIntrinsicWidth(), right.getIntrinsicHeight());
                    right.setBounds(rect);
                    searchContent.setCompoundDrawables(null, null, right, null);
                } else {
                    searchContent.setCompoundDrawables(null, null, null, null);
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
                    return true;
                }
                return false;
            }
        });
    }

    // 根据搜索内容进行查找
    private void obtainSearchResult(String content) {
        // TODO: 请求搜索接口，拿到搜索结果，先写死搜索结果
        List<String> tempData = new ArrayList<>();
        tempData.add("group");
        tempData.add("list");
        if (tempData.contains(content)) { // 包含搜索结果
            replaceFragment(CONTENT);
        } else { // 否则
            replaceFragment(EMPTY);
        }
    }

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
