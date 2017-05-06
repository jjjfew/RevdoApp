package com;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.guchen.mapLauncher.R;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
/*
        StringBuffer param = new StringBuffer();
        // 设置你申请的应用appid,请勿在'='与appid之间添加空格及空转义符
        param.append("appid="+"58b4904e");
        // 参数间使用半角“,”分隔。
        param.append(",");
        // 设置使用v5+
        param.append(SpeechConstant.ENGINE_MODE+"="+SpeechConstant.MODE_MSC); //不加这个默认会使用讯飞语记SpeechConstant.MODE_PLUS
        param.append(",");
        // SpeechUtility接口在非主进程调用会返回null对象，如需在非主进程使用语音功能，请增加参数：SpeechConstant.FORCE_LOGIN+"=true"
        param.append(SpeechConstant.FORCE_LOGIN+"=true");
        SpeechUtility.createUtility(this, param.toString());
        // 以下语句用于设置日志开关（默认开启），设置成false时关闭语音云SDK日志打印
        // Setting.setShowLog(false);
*/
    }
}

