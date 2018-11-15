package xiaoe.com.shop.common.web;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import xiaoe.com.shop.BuildConfig;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseResult;
import xiaoe.com.shop.base.XiaoeActivity;

import static xiaoe.com.shop.common.web.BridgeWebView.CUSTOM_ERROR_PAGE;

/**
 * @author lancelot
 * @date 2018/5/15
 */
public class BrowserActivity extends XiaoeActivity {

    private BridgeWebView mWebView;
    private ProgressBar mProgressBar;

    public static final String WEB_URL = "url";
    public static final String WEB_TITLE = "title";

    private String url;
    private String title;

    private boolean mWebOnPageFinished = false;
    //    private Disposable mDisposable;
    private Uri mCandyShareUri;
    /**
     * 修复：在呼朋唤友页多次点击生成你的好友海报按钮会弹出多个分享弹层
     */
    boolean isGenerateImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_browser);
        getIntentValue();
        initView();
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (mDisposable != null && !mDisposable.isDisposed()) {
//            mDisposable.dispose();
//        }
    }

    public String getUrl() {
        return url;
    }

    private void getIntentValue() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        url = bundle.getString(WEB_URL, "");
        title = bundle.getString(WEB_TITLE, "");
    }

    private void initView() {
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mWebView = (BridgeWebView) findViewById(R.id.web_browser);

        setTitle(title);
    }

    private void init() {
        mWebView.setWebViewClient(new BridgeWebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return myShouldOverrideUrlLoading(Uri.parse(url)) || super.shouldOverrideUrlLoading(view, url);
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return myShouldOverrideUrlLoading(request.getUrl()) || super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mWebOnPageFinished = true;
                if (url == null) {
                    return;
                }
                if (url.contains("m.jinse.com")) {
                    String js = "document.getElementsByClassName(\"appfixed\")[0].remove()";
                    mWebView.loadUrl("javascript:" + js);
                }
            }
        });
        mWebView.setWebChromeClient(new BridgeWebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mProgressBar.setProgress(newProgress);
                mProgressBar.setVisibility(newProgress >= 100 ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                setTitle(title);
            }
        });
        mWebView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
        mWebView.loadUrl(url);
    }

    public boolean loadUrl(String url) {
        if (mWebView == null || TextUtils.isEmpty(url)) {
            return false;
        }
        this.url = url;
        mWebView.loadUrl(url);
        return true;
    }

    private boolean myShouldOverrideUrlLoading(Uri uri) {
//        String scheme = uri.getScheme();
//        // 还需要判断host
//        if (TextUtils.equals(scheme, "tbox")) {
//            String url = uri.toString();
//            LogUtils.d("url = " + url);
//            if (!TextUtils.isEmpty(url) && url.startsWith("tbox://shortMessage?shareText")) {
//                Intent intent = new Intent(this, NewsShareActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putParcelable(NewsShareActivity.Companion.getURI(), uri);
//                intent.putExtras(bundle);
//                startActivity(intent);
//
////                EventReportMgr.onEvent(getParentActivity(), "find_newsFlash_share", getChannel(getParentActivity()));
//                return true;
//            } else if (!TextUtils.isEmpty(url) && url.startsWith("tbox://candy?shareImgUrl")) {
//                if (isGenerateImage) {
//                    return true;
//                }
//                isGenerateImage = true;
//                if (mCandyShareUri == null) {
//                    mProgressView.setVisibility(View.GONE);
//                }
//                mDisposable = Observable.just(uri)
//                        .flatMap(new Function<Uri, ObservableSource<Uri>>() {
//                            @Override
//                            public ObservableSource<Uri> apply(Uri uri) throws Exception {
//                                return Observable.just(generateImage(uri, "candy_share.jpg"));
//                            }
//                        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
//                        .subscribe(new Consumer<Uri>() {
//                            @Override
//                            public void accept(Uri uri) throws Exception {
//                                if (mProgressView == null) {
//                                    return;
//                                }
//                                mProgressView.setVisibility(View.GONE);
//                                Intent intent = new Intent(Intent.ACTION_SEND);
//                                // 设置分享内容的类型
//                                intent.setType("image/*");
//                                if (Build.VERSION.SDK_INT >= 24) {
//                                    // 添加这一句表示对目标应用临时授权该Uri所代表的文件
//                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                                }
//                                intent.putExtra(Intent.EXTRA_STREAM, uri);
//                                // 创建分享的Dialog
//                                intent = Intent.createChooser(intent, getString(R.string.share));
//                                startActivityForResult(intent, 500);
//                                if (mProgressView != null) {
//                                    mProgressView.postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            isGenerateImage = false;
//                                        }
//                                    }, 200);
//                                }
//                            }
//                        }, new Consumer<Throwable>() {
//                            @Override
//                            public void accept(Throwable throwable) throws Exception {
//                                LogUtils.d("throwable = " + throwable.toString());
//                                mProgressView.setVisibility(View.GONE);
//                                isGenerateImage = false;
//                                ToastUtils.show(mContext, mContext.getString(R.string.no_network_at_present));
//                            }
//                        });
//                return true;
//            } /*else if (!TextUtils.isEmpty(url) && url.startsWith("tbox://resolve?domain=")) {
//                Browser.openUrl(mContext, url);
//                return true;
//            }*/
//            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            return true;
//        }
        return false;
    }

    private String getShareImgUrl(Uri uri) {
        String url = "";
        try {
            String data = uri.getQueryParameter("shareImgUrl");
            if (!TextUtils.isEmpty(data)) {
                String dataString = data.replace(" ", "+");
                String dataBase64 = new String(Base64.decode(dataString, Base64.NO_WRAP));
//                LogUtils.d("dataBase64 = " + dataBase64);
                ShareImageUrlData shareImageUrlData = BaseResult.parseResult(dataBase64, ShareImageUrlData.class);
//                LogUtils.d("dataBase64 --- = " + shareImageUrlData.data);
                if (shareImageUrlData != null && !TextUtils.isEmpty(shareImageUrlData.data)) {
                    url = shareImageUrlData.data;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        LogUtils.d("url --- = " + url);
        return url;
    }

    public class ShareImageUrlData {
        String data;
    }

    /**
     * @param uri     base64编码字符串
     * @param picName 图片路径-具体到文件
     * @return
     * @Description: 将base64编码字符串转换为图片
     * @Author:
     * @CreateTime:
     */
    public Uri generateImage(Uri uri, String picName) {
        String imgStr = getShareImgUrl(uri);
        if (imgStr == null) {
            return null;
        }
        try {
            File saveFile = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            File f = new File(saveFile, picName);
            if (!f.exists()) {
                f.getParentFile().mkdirs();
                f.createNewFile();
            } else if (f.isFile()) {
                f.delete();
            }
            // 解密
            byte[] b = Base64.decode(imgStr.split(",")[1], Base64.DEFAULT);
            // 处理数据
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            OutputStream out = new FileOutputStream(f.getAbsolutePath());
            out.write(b);
            out.close();
            if (Build.VERSION.SDK_INT >= 24) {
                mCandyShareUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".FileProvider", f);
            } else {
                mCandyShareUri = Uri.fromFile(f);
            }
            return mCandyShareUri;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 远程图片文件的URL
     * 下载远程图片
     *
     * @param uri
     * @return
     */
    private Uri saveRemoteImage(Uri uri) {
        if (mCandyShareUri != null) return mCandyShareUri;
        String imgUrl = getShareImgUrl(uri);
//        imgUrl = "https://ww1.sinaimg.cn/bmiddle/6834c769jw1djjf4p3p9rj.jpg";
        URL fileURL;
        try {
            fileURL = new URL(imgUrl);
            HttpURLConnection conn = (HttpURLConnection) fileURL.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            int length = conn.getContentLength();
            if (length != -1) {
                byte[] imgData = new byte[length];
                byte[] buffer = new byte[512];
                int readLen = 0;
                int destPos = 0;
                while ((readLen = is.read(buffer)) > 0) {
                    System.arraycopy(buffer, 0, imgData, destPos, readLen);
                    destPos += readLen;
                }
                Bitmap bitmap = BitmapFactory.decodeByteArray(imgData, 0,
                        imgData.length);
                return saveBitmap(bitmap, "candy_share.jpg");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将图片存到本地
     *
     * @param src
     * @param picName
     * @return
     */
    private Uri saveBitmap(Bitmap src, String picName) {
        if (src == null) {
            return null;
        }
        try {
            File saveFile = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            File f = new File(saveFile, picName);
            if (!f.exists()) {
                f.getParentFile().mkdirs();
                f.createNewFile();
            } else if (f.isFile()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            src.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            if (Build.VERSION.SDK_INT >= 24) {
                mCandyShareUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".FileProvider", f);
            } else {
                mCandyShareUri = Uri.fromFile(f);
            }
            return mCandyShareUri;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void openUrl(Context context, Bundle bundle) {
        Intent intent = new Intent(context, BrowserActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void openUrl(Context context, String url, String title) {
        Bundle args = new Bundle();
        args.putString(WEB_URL, url);
        if (!TextUtils.isEmpty(title)) {
            args.putString(WEB_TITLE, title);
        }
        Intent intent = new Intent(context, BrowserActivity.class);
        intent.putExtras(args);
        context.startActivity(intent);
    }

    @Override
    public void onHeadLeftButtonClick(View v) {
        if (isWebGoBack()) {
            return;
        }
        super.onHeadLeftButtonClick(v);
    }

    @Override
    public void onBackPressed() {
        if (isWebGoBack()) {
            return;
        }
        super.onBackPressed();
    }

    private boolean isWebGoBack() {
        if (mWebView.canGoBack() && !mWebView.getUrl().equals(CUSTOM_ERROR_PAGE)) {
            mWebView.goBack();
            return true;
        }
        return false;
    }

}
