package com;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothService extends Service {
    public BluetoothService() {
    }

    private static final String TAG = "THINBTCLIENT";
    private static final boolean D = true;

    private BluetoothAdapter mBluetoothAdapter = null;

    private BluetoothSocket btSocket = null;

    private OutputStream outStream = null;
    private InputStream inputStream=null;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //private static String address = "00:11:02:29:01:07"; // <==要连接的蓝牙设备MAC地址,old
    private static String address = "20:16:10:24:73:41";

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.w("XXX","bluetooth onCreate");

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            //Toast.makeText(this,"蓝牙设备不可用，请打开蓝牙！", Toast.LENGTH_LONG).show();
            Toast.makeText(this,"bluetooth is null,pls open bluetooth", Toast.LENGTH_SHORT).show();
            Log.w("xxx","蓝牙设备不可用，请打开蓝牙！");
            //finish();
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            //Toast.makeText(this,  "请打开蓝牙并重新运行程序！", Toast.LENGTH_LONG).show();
            Toast.makeText(this,"pls enable bluetooth", Toast.LENGTH_SHORT).show();
            Log.w("xxx","请打开蓝牙并重新运行程序！");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                //DisplayToast("正在尝试连接智能小车，请稍后・・・・");
                //DisplayToast("connecting to bluetooth,pls wait....");
                //Step1. 创建bluetooth devie
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);


                try {
                    //Step2. 创建bluetooth socket(根据UUID)
                    btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                } catch (IOException e) {

                    //DisplayToast("套接字创建失败！");
                    //DisplayToast("fail to create bluetooth socket");
                }
                //DisplayToast("成功连接智能小车！可以开始操控了~~~");
                //DisplayToast("successful connect to bluetooth,pls control");

                //停掉搜索设备，否则连接可能会变得非常慢并且容易失败
                mBluetoothAdapter.cancelDiscovery();
                try {
                    //Step3. 建立bluetooth socket连接
                    btSocket.connect();
                    //Toast.makeText(BluetoothService.this,"连接成功建立，数据连接打开", Toast.LENGTH_LONG).show();
                    //DisplayToast("连接成功建立，数据连接打开！");
                    //DisplayToast("successful establish data connection");
                    Message message=new Message();
                    message.what = 333;
                    message.obj = "连接成功建立，数据连接打开";
                    MainActivity.handlerRadar.sendMessage(message);
                } catch (IOException e) {
                    try {
                        btSocket.close();
                    } catch (IOException e2) {
                        //   DisplayToast("连接没有建立，且无法关闭套接字！");
                        //DisplayToast("connection fail, can't close socket");
                    }
                }

                read_From_Server2();
            }
        }).start();


    }
    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        Log.w("XXX","bluetooth onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onCreate();
        Log.w("XXX","onDestroy");

        if (D)
            Log.e(TAG, "- ON PAUSE -");
        if (outStream != null) {
            try {
                outStream.flush();  //在close前将内存中可能留存的数据强制输出，防止丢数据
            } catch (IOException e) {
                Log.e(TAG, "ON PAUSE: Couldn't flush output stream.", e);
            }
        }

        try {
            btSocket.close();
        } catch (IOException e2) {
            //  DisplayToast("套接字关闭失败！");
            //DisplayToast("fail to close socket");
        }
    }
    /*
        public void DisplayToast(String str)
        {
            Toast toast=Toast.makeText(this, str, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 220);
            toast.show();

        }
    */
    public void read_From_Server()
    {
        try {
            inputStream = btSocket.getInputStream();
            int data1,data2,data3,data4,distance;
            while (true) {
                try {
                    Log.w("xxx","data1 before");
                    data1 = inputStream.read();
                    Log.w("xxx","data1 after");
                    //Log.w("xxx","data1 "+data1+"");
                } catch (IOException e) {
                    Log.e("TAG", e.toString());
                    break;
                }

                    Log.w("xxx","distance "+data1+"");
                    Message message=new Message();
                    message.what=222;
                    message.obj=data1+"";
                    MainActivity.handlerRadar.sendMessage(message);

            }
        } catch (IOException e) {
            Log.e("TAG", e.toString());
        }
    }

    public void read_From_Server2()
    {
        try {
            inputStream = btSocket.getInputStream();
            int data1,data2,data3,data4,data5,data6,distance;
            while (true) {
                try {
                    data1 = inputStream.read();
                    //Log.w("xxx","data1 "+data1+"");
                } catch (IOException e) {
                    Log.e("TAG", e.toString());
                    break;
                }
                if(data1==0xaa){
                    data2 = inputStream.read();
                  //  if(data2==0x55) {
                        //Log.w("xxx","data2 "+data2+"");
                        data3 = inputStream.read();   //L和R
                        //Log.w("xxx","data3 "+data3+"");
                        data4 = inputStream.read(); //高8位
                        data5 = inputStream.read(); //低8位
                        //Log.w("xxx","data4 "+data4+"");
                        data6 = inputStream.read();  //校验值
                        distance = (data4 << 8) + data5;
                        distance = distance / 10; //厘米

                        Message message = new Message();
                        if(data3=='L') {
                            Log.w("xxx", "L: " + distance + "");
                            message.what = 222;
                            message.obj = distance + "";
                            MainActivity.handlerRadar.sendMessage(message);
                        }
                        else if(data3=='R') {
                            message.what = 333;
                            Log.w("xxx", "R: " + distance + "");
                            message.obj = distance + "";
                            MainActivity.handlerRadar.sendMessage(message);
                        }

                //    }
                }
            }
        } catch (IOException e) {
            Log.e("TAG", e.toString());
        }
    }
}
