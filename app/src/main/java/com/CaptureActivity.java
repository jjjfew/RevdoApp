package com;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.guchen.mapLauncher.R;

import com.camera.CameraManager;
import com.decoding.CaptureActivityHandler;
import com.decoding.InactivityTimer;
import com.view.ViewfinderView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.io.IOException;
import java.util.Vector;


public class CaptureActivity extends AppCompatActivity implements SurfaceHolder.Callback{

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    //private Button btnBack;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        CameraManager.init(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        /*
        btnBack = (Button)findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface)
        {
            initCamera(surfaceHolder);
        }
        else
        {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;
        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
        {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }
    @Override
    protected void onPause()
    {
        super.onPause();
        if (handler != null)
        {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }
    @Override
    protected void onDestroy()
    {
        inactivityTimer.shutdown();
        super.onDestroy();
    }
    private void initCamera(SurfaceHolder surfaceHolder)
    {
        try
        {
            CameraManager.get().openDriver(surfaceHolder);
        }
        catch (IOException ioe)
        {
            finish();
            return;
        }
        catch (Exception e)
        {
            finish();
            return;
        }
        if (handler == null)
        {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        if (!hasSurface)
        {
            hasSurface = true;
            initCamera(holder);
        }
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        hasSurface = false;
    }
    public ViewfinderView getViewfinderView()
    {
        return viewfinderView;
    }
    public Handler getHandler()
    {
        return handler;
    }
    public void drawViewfinder()
    {
        viewfinderView.drawViewfinder();
    }
    public void handleDecode(final Result obj, Bitmap barcode)
    {
	   /* System.out.println("QR code : " + obj.getText());
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();*/
        String result = obj.getText();
        if (!TextUtils.isEmpty(result)) {
            Intent intent = new Intent();
            intent.putExtra("scan_result", obj.getText());
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }
    public void showQRCodeInfoDlg(final String accNo, final String name, final String amount) {
        StringBuffer sb = new StringBuffer();
        sb.append("1").append(": ").append(accNo).append('\n');
        sb.append("2").append(": ").append(name).append('\n');
        sb.append("3").append(": ").append(amount);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setMessage(sb);
        dialog.setNegativeButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                /*Intent intent = new Intent(CaptureActivity.this, TransferInnerBank1Activity.class);
                intent.putExtra("accNo", accNo);
                intent.putExtra("name", name);
                intent.putExtra("amount", amount);
                startActivity(intent);
                finish();*/
            }
        });
        dialog.setPositiveButton("确定1", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                finish();
            }
        });
        dialog.create().show();
    }
    private void initBeepSound()
    {
		/*if (playBeep && mediaPlayer == null)
		{
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);
			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
			try
			{
				mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			}
			catch (IOException e)
			{
				mediaPlayer = null;
			}
		}*/
    }
    private static final long VIBRATE_DURATION = 200L;
    private void playBeepSoundAndVibrate()
    {
        if (playBeep && mediaPlayer != null)
        {
            mediaPlayer.start();
        }
        if (vibrate)
        {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }
    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener()
    {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer)
        {
            mediaPlayer.seekTo(0);
        }
    };
}
