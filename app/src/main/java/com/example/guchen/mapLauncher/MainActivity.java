package com.example.guchen.mapLauncher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.graphics.Color;
import android.widget.TextView;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private Button mbutton;
    private ImageView img;
    private TextView txtv;
    private String fileName="/storage/emulated/0/logo.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        txtv=(TextView) findViewById(R.id.textView1);

        //img=  (ImageView) findViewById(R.id.imageView1);
        //img.setImageDrawable(getResources().getDrawable(R.drawable.null_map)); //不会变形

        mbutton=(Button)findViewById(R.id.button1) ;
        mbutton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                File f = new File(fileName);
                if (f.exists()) {
                    Bitmap bm = BitmapFactory.decodeFile(fileName);
                    //img.setImageBitmap(bm);
                    //img.requestLayout();
                    txtv.setText(fileName);

                    Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    //Intent intent=new Intent(RecognizerIntent.ACTION_WEB_SEARCH);
                    startActivityForResult(intent, 0);

                    //jumpToLayout2();

                } else
                    txtv.setText("文件不存在");
                //ImageView imageView = new ImageView(this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<String> results = data
                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        Log.i("zpf", results.get(0).toString());

        String output=results.get(0).toString();
        //TextView tv1=(TextView) findViewById(R.id.textview1);
        //tv1.setText(output);

        if(output.contains("打开拍照"))
        {
            Intent intent=new Intent();
            intent.setAction("android.media.action.IMAGE_CAPTURE");//IMAGE for take a picture
            intent.addCategory("android.intent.category.DEFAULT");
            File file=new File(Environment.getExternalStorageDirectory()+"/000.jpg");
            Uri uri= Uri.fromFile(file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
            this.startActivity(intent);
        }
        else if(output.contains("打开导航"))
        {
            jumpToLayout2();
        }
        else if(output.contains("返回主页"))
        {
            jumpToLayout1();
        }
    }

    public void jumpToLayout2()
    {
        setContentView(R.layout.map);
        Button mbutton2=(Button)findViewById(R.id.button2) ;
        mbutton2.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                //Intent intent=new Intent(RecognizerIntent.ACTION_WEB_SEARCH);
                startActivityForResult(intent, 0);
                //jumpToLayout1();
            }
        });

    }

    public void jumpToLayout1()
    {
        setContentView(R.layout.activity_main);
        Button mbutton=(Button)findViewById(R.id.button1) ;
        mbutton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                //Intent intent=new Intent(RecognizerIntent.ACTION_WEB_SEARCH);
                startActivityForResult(intent, 0);

               // jumpToLayout2();
            }
        });

    }

    public void voiceDefine()
    {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

