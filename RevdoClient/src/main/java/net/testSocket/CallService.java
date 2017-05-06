package net.testSocket;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

import java.io.IOException;

public class CallService extends Service {
    String rmaddr;
    int calls=0;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.w("XXX","onCreate");
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        Log.w("XXX","onStartCommand");

        rmaddr=intent.getStringExtra("ipaddr");

       /* 添加自己实现的PhoneStateListener */
        exPhoneCallListener myPhoneCallListener =  new exPhoneCallListener();
    /* 取得电话服务 */
        TelephonyManager tm =
                (TelephonyManager) this.getSystemService
                        (Context.TELEPHONY_SERVICE);
    /* 注册电话通信Listener */
        tm.listen
                (
                        myPhoneCallListener,
                        PhoneStateListener.LISTEN_CALL_STATE
                );

        return START_STICKY;
    }
    @Override
    public void onDestroy(){
        super.onCreate();
        Log.w("XXX","onDestroy");
    }

    /* 内部class继承PhoneStateListener */
    public class exPhoneCallListener extends PhoneStateListener
    {
        /* 重写onCallStateChanged
        当状态改变时改变myTextView1的文字及颜色 */
        @Override
        public void onCallStateChanged(int state, String incomingNumber)
        {
            switch (state)
            {
        /* 无任何状态时 */
                case TelephonyManager.CALL_STATE_IDLE:
                    if(calls>0) {  //防止第一次没电话时就发状态信息
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                SocketClient socketClient = new SocketClient(rmaddr);
                                socketClient.sendToServer("call:通话结束");
                                Log.w("xxx", "电话断开");
                            }
                        }).start();

                    }
                    break;
        /* 接起电话时 */
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SocketClient socketClient=new SocketClient(rmaddr);
                            socketClient.sendToServer("call:通话中");
                            Log.w("xxx","通话中");
                        }
                    }).start();

                    break;
        /* 电话进来时 */
                case TelephonyManager.CALL_STATE_RINGING:
                    final String contatName=query_Phone_Name(incomingNumber);
                    calls++;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SocketClient socketClient=new SocketClient(rmaddr);
                            socketClient.sendToServer("call:来电中: "+contatName );
                        }
                    }).start();
                    Log.w("xxx",query_Phone_Name(incomingNumber));

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SystemClock.sleep(3000);
                            //自动接听电话
                            Intent intent=new Intent(CallService.this,AutoAnswerActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }).start();


                    break;
                default:
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    /* 用来电电话号码去找该联系人 */
    String query_Phone_Name(String incomingNumber){
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query
                (
                        //ContactsContract.Contacts.CONTENT_URI, //不能查到号码
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.NUMBER+ "=?",
                        new String[]
                                {
                                        incomingNumber
                                },
                        null
                );

        if (cursor.getCount() == 0)
        {   /* 找不到联系人 */
            //myTextView1.setText("unknown Number:" + incomingNumber);
            // return incomingNumber;
        }
        else if (cursor.getCount() > 0)
        {
            cursor.moveToFirst();
      /* 在projection这个数组里名字是放在第1个位置 */
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            return name;
        }
        return incomingNumber;
    }

}
