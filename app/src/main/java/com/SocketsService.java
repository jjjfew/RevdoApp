package com;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketsService extends Service {
    private String str;
    private ServerSocket serverSocket;

    public SocketsService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.w("XXX","socket_onBind");

        new Thread() {   //用到网络的功能，必须使用thread，否则会闪退
            @Override
            public void run() {
                startSocketServer();
            }
        }.start();

        IBinder result = null;
        if ( null == result ) result = new MyBinder() ;
        return result;

        //return null;
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onUnbind(Intent intent){
        Log.w("XXX","socket_onUnBind");
        //Toast.makeText(this,"结束服务程序",Toast.LENGTH_SHORT);
        return super.onUnbind(intent);
    }
    @Override
    public void onCreate(){
        super.onCreate();
        Log.w("XXX","socket_onCreate");
    }
    @Override
    public void onDestroy(){
        super.onCreate();
        Log.w("XXX","socket_onDestroy");
    }

    public void startSocketServer(){
        try {
            serverSocket = new ServerSocket(54321);
            while (true) {
                Log.w("xxx", "wait client to connect");
                //等待client连接
                Socket client = serverSocket.accept();
                try {
                    Log.w("xxx", "start to read");
                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    str = in.readLine();  //在这里会阻塞
                    Message message=new Message();
                    message.what=111;
                    message.obj=str;
                    Log.w("xxx", str);

                    remote_Control(); //执行远程控制命令
                    //向clinet返回刚才传来的数据
                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
                    out.println(str);
                    in.close();
                    out.close();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    ex.printStackTrace();
                } finally {
                    client.close();
                    System.out.println("close");
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
/*
    public void sendToClient(){
        try {
                Log.w("xxx", "wait client to connect");
                //等待client连接
                Socket client = serverSocket.accept();
                try {
                    Log.w("xxx", "start to read");
                    //下面的getInputStream不会阻塞，没有会一直往下执行
                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    str = in.readLine();
                    Message message=new Message();
                    message.what=111;
                    message.obj=str;
                    Log.w("xxx", str);

                    remote_Control(); //执行远程控制命令
                    //向clinet返回刚才传来的数据
                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
                    out.println(str);
                    in.close();
                    out.close();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    ex.printStackTrace();
                } finally {
                    client.close();
                    System.out.println("close");
                }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
*/
    public void remote_Control() {
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        if (str.equals("home")) {
            Intent intent1 = new Intent();
            intent1.setAction(Intent.ACTION_MAIN);
            intent1.addCategory(Intent.CATEGORY_HOME);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);
        } else if (str.equals("return")) {
            //onBackPressed();
        }else if (str.equals("menu")) {

        }else if (str.contains("vol:")) {
            int volset=7;
            if(str.length()==5) volset=Integer.valueOf(str.substring(4,5));
            else volset=Integer.valueOf(str.substring(4,6));
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volset, 0);
        }else if (str.equals("reboot")) {
            PowerManager pm = (PowerManager)getApplicationContext().getSystemService(Context.POWER_SERVICE);
            pm.reboot(null);
        }else if (str.equals("mouse")) {
            //启动语音唤醒后台服务
            Intent intent=new Intent(this,WakeService.class);
            startService(intent);
        }else if (str.equals("keyboard")) {
            //启动语音唤醒后台服务
            //Intent intent=new Intent(this,WakeService.class);
            //startService(intent);
        }else if (str.equals("activity_navi")) {
            Intent intent1=new Intent();
            intent1.setClass(this,BaiduHudActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);
        }else if (str.contains("call:")) {
            String callState=str.substring(5,str.length());
            Message message=new Message();
            message.what=222;
            message.obj=callState;
            MainActivity.handlerCall.sendMessage(message);
        }
    }

    public class MyBinder extends Binder {
        //此方法是为了可以在Acitity中获得服务的实例
        public SocketsService getService() {
            return SocketsService.this;
        }

    }
}
