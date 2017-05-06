package net.testSocket;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

public class QRCreateActivity extends AppCompatActivity implements SurfaceHolder.Callback{
    private String TAG="XXX";
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder01;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcreate);

        textView=(TextView)findViewById(R.id.textViewQR);

        mSurfaceView=(SurfaceView)findViewById(R.id.surfaceView);
        mSurfaceHolder01 = mSurfaceView.getHolder();
       /* Activity必须实现SurfaceHolder.Callback */
        mSurfaceHolder01.addCallback(this);

        Random rand=new Random();
        final int a=rand.nextInt(9000); //产生0-9000之间的随机数
        Log.w("xxx","random "+a);
        WifiClass wifiClass=new WifiClass(QRCreateActivity.this);
        wifiClass.setWifiAp(true,"Revdo_"+a,"12345678");
        textView.setText("请在头盔说指令：连接手机\n网络名:Revdo_"+a+" 密码:12345678");
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(1000); //延时1秒才能显示
                AndroidQREncode("wifi:"+"Revdo_"+a+":"+"12345678", 4);  // 传入setQrcodeVersion为4，仅能接受62个字符
            }
        }).start();

    }

    /* 自定义产生QR Code的函数 */
    public void AndroidQREncode(String strEncoding, int qrcodeVersion)
    {
        try
        {
      /* 建构QRCode编码对象 */
            com.swetake.util.Qrcode testQrcode =
                    new com.swetake.util.Qrcode();
      /* L','M','Q','H' */
            testQrcode.setQrcodeErrorCorrect('M');
      /* "N","A" or other */
            testQrcode.setQrcodeEncodeMode('B');
      /* 0-20 */
            testQrcode.setQrcodeVersion(qrcodeVersion);
            // getBytes
            byte[] bytesEncoding = strEncoding.getBytes("utf-8");
            if (bytesEncoding.length>0 && bytesEncoding.length <120)
            {
        /* 将字符串通过calQrcode函数转换成boolean数组 */
                boolean[][] bEncoding = testQrcode.calQrcode(bytesEncoding);
        /* 依据编码后的boolean数组，绘图 */
                drawQRCode
                        (bEncoding, Color.BLACK);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /* 在SurfaceView上绘制QR Code条形码 */
    private void drawQRCode(boolean[][] bRect, int colorFill)
    {
    /* test Canvas*/
        int intPadding = 20;
    /* 欲在SurfaceView上绘图，需先lock锁定SurfaceHolder */
        Canvas mCanvas01 = mSurfaceHolder01.lockCanvas();
    /* 设置画布绘制颜色 */
        mCanvas01.drawColor(Color.WHITE);    //白色画布底色
    /* 创建画笔 */
        Paint mPaint01 = new Paint();
    /* 设置画笔颜色及模式 */
        mPaint01.setStyle(Paint.Style.FILL);
        mPaint01.setColor(colorFill);  //用黑色画
        mPaint01.setStrokeWidth(1.0F);
    /* 逐一加载2维boolean数组 */
        for (int i=0;i<bRect.length;i++)
        {
            for (int j=0;j<bRect.length;j++)
            {
                if (bRect[j][i])
                {
          /* 依据数组值，绘出条形码方块 */
                    mCanvas01.drawRect
                            (
                                    new Rect
                                            (
                                                    /*
                                                    intPadding+j*3+2, //起点x坐标
                                                    intPadding+i*3+2, //起点y坐标
                                                    intPadding+j*3+2+3,//x坐标+框宽
                                                    intPadding+i*3+2+3 //y坐标+框高
                                                    //等比例将图扩大8倍
                                                    intPadding+j*24+16,
                                                    intPadding+i*24+16,
                                                    intPadding+j*24+16+24,
                                                    intPadding+i*24+16+24
                                                    */
                                                    //等比例将图扩大6倍
                                                    intPadding+j*18+12,
                                                    intPadding+i*18+12,
                                                    intPadding+j*18+12+18,
                                                    intPadding+i*18+12+18
                                            ), mPaint01
                            );
                }
            }
        }
    /* 解锁SurfaceHolder，并绘图 */
        mSurfaceHolder01.unlockCanvasAndPost(mCanvas01);
    }

    @Override
    public void surfaceChanged
            (SurfaceHolder surfaceholder, int format, int w, int h)
    {
        // TODO Auto-generated method stub
        Log.i(TAG, "Surface Changed");
    }
    @Override
    public void surfaceCreated(SurfaceHolder surfaceholder)
    {
        // TODO Auto-generated method stub
        Log.i(TAG, "Surface Changed");
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceholder)
    {
        // TODO Auto-generated method stub
        Log.i(TAG, "Surface Destroyed");
    }
}
