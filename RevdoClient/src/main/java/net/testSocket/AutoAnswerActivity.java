package net.testSocket;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

import java.io.IOException;
import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;


public class AutoAnswerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_answer);

        TelephonyManager tm = (TelephonyManager) this
                .getSystemService(Service.TELEPHONY_SERVICE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON); //能在锁屏界面显示

        autoAnswerCall();// 自动接听
        //autoEndCall(this,tm);

        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(1000);
                finish();
            }
        }).start();

    }

    void autoAnswerCall(){
        try {
            Runtime.getRuntime().exec("input keyevent " +
                    Integer.toString(KeyEvent.KEYCODE_HEADSETHOOK));
        } catch (IOException e) {
            // Runtime.exec(String) had an I/O problem, try to fall back
            String enforcedPerm = "android.permission.CALL_PRIVILEGED";
            Intent btnDown = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                    Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN,
                            KeyEvent.KEYCODE_HEADSETHOOK));
            Intent btnUp = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                    Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP,
                            KeyEvent.KEYCODE_HEADSETHOOK));

            sendOrderedBroadcast(btnDown, enforcedPerm);
            sendOrderedBroadcast(btnUp, enforcedPerm);
        }
    }

    //自动挂断
    public static void autoEndCall(Context c, TelephonyManager tm) {
        try {
            Log.w("xxx", "endPhone");
            ITelephony iTelephony;
            Method getITelephonyMethod = TelephonyManager.class
                    .getDeclaredMethod("getITelephony", (Class[]) null);
            getITelephonyMethod.setAccessible(true);
            iTelephony = (ITelephony) getITelephonyMethod.invoke(tm,
                    (Object[]) null);
            // 挂断电话
            iTelephony.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
