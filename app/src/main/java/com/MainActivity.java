package com;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guchen.mapLauncher.R;
import com.view.WifiClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private ImageView img;
    private TextView txtv,textViewRadar,textViewRadar2,tvCall;
    private int SPEECHREQUEST=123;
    //private String httpUrl = "http://apis.baidu.com/heweather/weather/free";
    //private String httpArg = "city=beijing";
    String httpUrl = "http://apis.baidu.com/heweather/pro/attractions";
    String httpArg = "cityid=CN10101010018A";
    private String resultJson=null;
    private String tempt=null;
    Handler handler;
    private ServiceConnection mMyConn;
    static public Handler handlerRadar,handlerCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtv=(TextView) findViewById(R.id.textViewWeather);
        textViewRadar=(TextView) findViewById(R.id.textViewRadar); //L雷达
        textViewRadar2=(TextView) findViewById(R.id.textViewRadar2);//R雷达
        tvCall=(TextView) findViewById(R.id.textViewCall);

        Button button=(Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Intent intent1 = new Intent();
                //intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent1.setClass(MainActivity.this, QRScanActivity.class);
                //startActivity(intent1);

                //启动语音唤醒后台服务
                Intent intent=new Intent(MainActivity.this,WakeService.class);
                startService(intent);
            }
        });


       //联网显示温度
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(isNetwork()) {
                    resultJson = requestWeather(httpUrl, httpArg);
                    Log.w("xxx",resultJson);
                    if(resultJson!=null) {  //不判断是否得到数据，解析错误会闪退
                        try {
                            JSONObject jsonParser = new JSONObject(resultJson);
                            JSONArray jsonArray = jsonParser.getJSONArray("HeWeather data service 3.0");
                            JSONObject today = jsonArray.getJSONObject(0).getJSONArray("daily_forecast").getJSONObject(0);
                            tempt= today.getJSONObject("tmp").getString("max");  //今天的最高温度
                            //Log.v("XXX", now.getString("tmp"));
                            handler.sendEmptyMessage(1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();

       handler=new Handler(){
          @Override
           public void handleMessage(Message msg){
              super.handleMessage(msg);
                txtv.setText("当前温度："+tempt);
          }
        };


        handlerRadar = new Handler(){
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 222:// L雷达
                        textViewRadar.setText((String)msg.obj);
                        break;
                    case 333:// R雷达
                        textViewRadar2.setText((String)msg.obj);
                        break;
                    default: break;
                }}
        };

        handlerCall = new Handler(){
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 222:// 服务器消息
                        tvCall.setText((String)msg.obj);
                        break;
                    case 333:// 扫描完毕消息
                    default: break;
                }}
        };


        //启动网络连接后台服务
        if (mMyConn==null)mMyConn=new MyConn();
        Intent intent1=new Intent(MainActivity.this,SocketsService.class);
        //参数1表示Intent对象，参数2表示连接对象，参数3表示如果服务不存在则创建服务
        bindService(intent1,mMyConn,BIND_AUTO_CREATE);

        //启动语音唤醒后台服务
        Intent intent=new Intent(this,WakeService.class);
        startService(intent);

        //启动雷达测距后台服务
        Intent intent2=new Intent(this,BluetoothService.class);
        startService(intent2);

    }

    public static String requestWeather(String httpUrl, String httpArg) {
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        String result=null;
        httpUrl = httpUrl + "?" + httpArg;
        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            // 填入apikey到HTTP header
            connection.setRequestProperty("apikey",  "eb85b80f6388242d239b31fc3a1d888b");
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    boolean isNetwork(){
        ConnectivityManager mConnectivityManager;
         NetworkInfo mNetworkInfo;
        boolean ret=false;
        try {
            mConnectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo!=null) {
                if (mNetworkInfo.isAvailable())
                    Log.w("XXX", "当前网络可用");
                    ret= true;
            } else
                Log.w("XXX", "当前网络不可用");
        }catch (Exception e){}
        return ret;
    }

    class MyConn implements ServiceConnection{
        @Override
        public void onServiceConnected(ComponentName name, IBinder service){
            Log.w("xxx","服务成功绑定");
        }
        //当服务失去连接时
        @Override
        public void onServiceDisconnected(ComponentName name){
            Log.w("xxx","服务异常失去连接");
        }
    }
}

