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

        img=  (ImageView) findViewById(R.id.imageView1);
        img.setImageDrawable(getResources().getDrawable(R.drawable.null_map)); //不会变形

        mbutton=(Button)findViewById(R.id.button1) ;
        mbutton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                File f = new File(fileName);
                if (f.exists()) {
                    Bitmap bm = BitmapFactory.decodeFile(fileName);
                    img.setImageBitmap(bm);
                    img.requestLayout();
                    txtv.setText(fileName);
                    
                } else
                    txtv.setText("文件不存在");
                //ImageView imageView = new ImageView(this);
            }
        });
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

