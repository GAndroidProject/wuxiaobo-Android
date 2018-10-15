package xiaoe.com.network;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import xiaoe.com.common.app.XiaoeApplication;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.utils.ThreadPoolUtils;

/**
 * Created by Administrator on 2018/9/26.
 */

public class NetworkEngine {
    private static final String TAG = "NetworkEngine";
    //测试环境url http://demo.h5.inside.xiaoe-tech.com/openapp/login
    private final static String TEST_URL = "http://api.inside.xiaoe-tech.com/";
    //正式环境url http://api.inside.xiaoe-tech.com
    private final static String FORMAL_URL = "https://api.xiaoe-tech.com/";
    //    private final static String BASE_URL = url;
//    private final static String BASE_URL = XiaoeApplication.isFormalCondition() ? FORMAL_URL : TEST_URL;
//    private final static String BASE_URL = "http://134.175.39.17:12242/";
    private final static String BASE_URL = "http://134.175.39.17:9380/";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client;
    private static NetworkEngine networkEngine;

    public static NetworkEngine getInstance() {
        if (networkEngine == null) {
            synchronized (NetworkEngine.class) {
                if (networkEngine == null) {
                    networkEngine = new NetworkEngine();
                }
            }
        }
        return networkEngine;
    }

    private NetworkEngine() {
        client = new OkHttpClient.Builder()
                .connectTimeout(8, TimeUnit.SECONDS)
                .writeTimeout(8, TimeUnit.SECONDS)
                .build();
    }

    public void sendRequest(final IRequest iRequest) {


//        UserInfo userInfo = StatusKeepUtil.getUserInfo();
//        if (userInfo == null) {
//            if (!(iRequest instanceof LoginRequest)) {
//                iRequest.onResponse(false, "not login");
//                return;
//            }
//        }
        ThreadPoolUtils.runTaskOnThread(new Runnable() {
            @Override
            public void run() {
                if (iRequest.isPOST()) {
                    POST(BASE_URL + iRequest.getCmd(), iRequest);
                } else {
                    GET(BASE_URL + iRequest.getCmd(), iRequest);
                }
            }
        });

    }
    private void GET(String url, IRequest iRequest) {
        Request request = new Request.Builder()
                .get()
                .url(url)
                .tag(iRequest)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                IRequest mRequest = (IRequest) call.request().tag();
                mRequest.onResponse(false, null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                IRequest mRequest = (IRequest) call.request().tag();
                ResponseBody body = response.body();
                if (body == null) {
                    mRequest.onResponse(false, null);
                    return;
                }
                String jsonString = body.string();
                JSONObject jsonObject = null;
                String code = "";
                try {
                    jsonObject = new JSONObject(jsonString);
                    code = jsonObject.getString("code");
                } catch (JSONException e) {
                    e.printStackTrace();
                    mRequest.onResponse(false, null);
                    return;
                }

                if ("0".equals(code)) {
                    Gson gson = new Gson();
                    try {
                        Object entityObj = gson.fromJson(jsonObject.getString("data"), mRequest.getEntityClass());
                        mRequest.onResponse(true, entityObj);
                    } catch (Exception e) {
                        e.printStackTrace();
                        mRequest.onResponse(false, null);
                    }
                } else {
                    mRequest.onResponse(false, null);
                }

            }
        });
    }

    private void POST(String url, IRequest iRequest) {
        String formBodyString = iRequest.getWrapedFormBody();
        if (formBodyString == null || TextUtils.isEmpty(formBodyString)) {
            iRequest.onResponse(false, "param error");
            return;
        }
        RequestBody formBody = RequestBody.create(JSON, formBodyString);
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .tag(iRequest)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                IRequest mRequest = (IRequest) call.request().tag();
                mRequest.onResponse(false, null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                IRequest mRequest = (IRequest) (call.request().tag());
                ResponseBody body = response.body();
                if (body == null) {
                    body.close();
                    mRequest.onResponse(false, null);
                    return;
                }
                //登录请求，后续写登录逻辑可放开注释
//                if (mRequest instanceof LoginRequest) {
//                    StatusKeepUtil.clearUserInfo();
//                }
                String jsonString = body.string();
                Log.d(TAG, "onResponse: body --- " + jsonString);
                JSONObject jsonObject = null;
                String code = "";
                try {
                    jsonObject = new JSONObject(jsonString);
                    code = jsonObject.getString("code");
                    Log.d(TAG, "onResponse: code --- " + code);
                } catch (JSONException e) {
                    e.printStackTrace();
                    body.close();
                    mRequest.onResponse(false, null);
                    return;
                }

                if ("0".equals(code)) {
                    Gson gson = new Gson();
                    Object entityObj = null;

                    try {
                        String strData = jsonObject.getString("data");
//                        if ("true".equals(strData) || "false".equals(strData)) {
//                            strData = jsonString;
//                        }
                        if(!TextUtils.isEmpty(strData) && !"null".equals(strData)){
                            entityObj = analyticalJSON(strData, gson, mRequest.getEntityClass());
                        }
                    } catch (JSONException e) {
//                        e.printStackTrace();
                        body.close();
                        mRequest.onResponse(false, null);
                        return;
                    }
                    body.close();
                    mRequest.onResponse(true, entityObj);
                } else if ("2".equals(code)) {
                    body.close();
                    //未登录清除缓存，后续放开注释
//                    StatusKeepUtil.clearUserInfo();
                    mRequest.onResponse(false, "not login");
                }else if("3".equals(code)){
                    body.close();
                    //登录超时清除缓存，后续放开注释
//                    StatusKeepUtil.clearUserInfo();
                    mRequest.onResponse(false, "user login time out");
                } else if("40006".equals(code) || "40007".equals(code)){
                    body.close();
                    mRequest.onResponse(false,"user repeat");
                }else{
                    body.close();
                    mRequest.onResponse(false, null);
                }
            }
        });
    }

    private Object analyticalJSON(String strJSON, Gson gson, Class clazz) throws JSONException {
        Object json = new JSONTokener(strJSON).nextValue();
        //json是jsonObject对象
        if (json instanceof JSONObject) {
            Object objectJSON = null;
            try {
                objectJSON = gson.fromJson(strJSON, clazz);
            } catch (JsonSyntaxException e) {
//                Log.d(TAG, "analyticalJSON: "+e.toString());
                e.printStackTrace();
            }
            return objectJSON;
        } //否则 json是josnArray对象(json instanceof JSONArray)
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(strJSON).getAsJsonArray();
        ArrayList<Object> arrayList = new ArrayList<Object>();

        for (JsonElement jsonElement : jsonArray) {
            Object obj = gson.fromJson(jsonElement, clazz);
            arrayList.add(obj);
        }
        return arrayList;
    }
}
