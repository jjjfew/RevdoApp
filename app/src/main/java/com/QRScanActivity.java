package com;

import android.app.Activity;
import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.camera.CameraManager;
import com.example.guchen.mapLauncher.R;

import com.view.WifiClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QRScanActivity extends Activity {
    public static final int SCAN_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);

        Intent intent = new Intent(this, CaptureActivity.class);
        startActivityForResult(intent, SCAN_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        TextView textView = (TextView) this.findViewById(R.id.textViewQR);
        switch (requestCode) {
            case SCAN_CODE:
                if (resultCode == RESULT_OK) {
                    String result = data.getStringExtra("scan_result");
                    textView.setText("result:" + result);
                    Log.w("xxx",result);
                    if(result!=null) {
                        if (eregi("wifi:", result)) {
                          //连接wifi
                            String[] aryTemp01 = result.split(":");
                            WifiClass wifiAdmin = new WifiClass(QRScanActivity.this);
                            wifiAdmin.connect(aryTemp01[1],aryTemp01[2],3);
                            finish();  //连接后关闭当前显示activity
                        }
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    textView.setText("fail to scan!");
                }
                break;
            default:
                break;
        }
    }

    /* 自定义比较字符串函数 */
    public static boolean eregi(String strPat, String strUnknow)
    {
        String strPattern = "(?i)"+strPat;
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(strUnknow);
        return m.find();
    }
}
