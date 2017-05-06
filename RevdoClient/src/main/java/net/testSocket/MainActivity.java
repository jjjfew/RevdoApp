package net.testSocket;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import net.testSocket.chat.LoginActivity;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static Handler handler1,handlerConnect;
    private String rmaddr=null;
    public TabHost tabhost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeActionOverflowMenuShown();
        setContentView(R.layout.activity_main);

        //自定义ActionBar
        ActionBar actionBar=getSupportActionBar();
        //actionBar.setCustomView(LayoutInflater.from(this).inflate(R.layout.myactionbar, null));
        actionBar.setCustomView(R.layout.myactionbar);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true) ;
        Button buttonBar=(Button)findViewById(R.id.buttonActionbar) ;
        buttonBar.setTextColor(Color.WHITE);
        buttonBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences mSharedPreferences=null;
                mSharedPreferences=getSharedPreferences("test",MODE_PRIVATE);//仅本程序可访问，生成test.xml
                SharedPreferences.Editor edit=mSharedPreferences.edit();
                edit.putBoolean("isSaved",false);
                edit.commit(); //更新数据

                Intent intent1=new Intent();
                intent1.setClass(MainActivity.this,ConnectActivity.class);
                startActivity(intent1);
            }
        });

        //保存上次连接的server ip,并获取上次保存的server ip
        SharedPreferences mSharedPreferences=null;
        mSharedPreferences=getSharedPreferences("ipText",MODE_PRIVATE);//仅本程序可访问，生成test.xml
        try {
            Bundle bundle = this.getIntent().getExtras();
            rmaddr = bundle.getString("ipaddr");
            Log.w("xxx","ipaddr= "+rmaddr);
            //rmaddr = "192.168.43.221";

            //如果是从搜索界面跳转过来
            if(rmaddr!=null) {
                SharedPreferences.Editor edit = mSharedPreferences.edit();
                edit.putString("ipValue", rmaddr);
                edit.commit(); //更新数据
            }
        }catch (Exception e){
            Log.w("xxx","null ipaddr");
            e.printStackTrace();

            //如果是直接启动的，没有buddle会异常，进行这里
            String temp=null;
            temp=mSharedPreferences.getString("ipValue","");
            if(temp!=null)
            rmaddr=temp;
        }

        //控制左上角的连接状态显示
        final TextView textViewBar=(TextView)findViewById(R.id.textViewActionbar);
        handlerConnect=new Handler(){
            public void handleMessage(Message m){
                textViewBar.setText(m.obj.toString());
            }
        };
        //测试ip连接是否在
        textViewBar.setTextColor(Color.WHITE);
        textViewBar.setText("Revdo：已断开");
        new Thread(new Runnable() {
            @Override
            public void run() {
                SocketClient socketClient = new SocketClient(rmaddr);
                socketClient.sendToServer("hello");
            }
        }).start();

        //启动电话监听服务
        Intent intent=new Intent(this,CallService.class);
        intent.putExtra("ipaddr",rmaddr);
        startService(intent);

        //检查更新
        UpdateClass mUpdate=new UpdateClass(this);
        mUpdate.autoUpdateApp();
        //显示是最新版本（用于右上角手动检查）
        handler1=new Handler(){
            public void handleMessage(Message m){
                new AlertDialog.Builder(MainActivity.this).setTitle("已经是最新版本！").setMessage(m.obj.toString()).
                        setPositiveButton("确定",null).setIcon(R.drawable.icon).show();
            }
        };



        tabhost=(TabHost)findViewById(R.id.tabHost);
        tabhost.setup();

        LayoutInflater i= LayoutInflater.from(this);
        i.inflate(R.layout.activity_show_app, tabhost.getTabContentView());//动态载入XML，而不需要Activity
        i.inflate(R.layout.control, tabhost.getTabContentView());

        TabHost.TabSpec page1 = tabhost.newTabSpec("tabControl")
                .setIndicator("控制")
                .setContent(R.id.tablayoutControl);
        tabhost.addTab(page1);
        TabHost.TabSpec page2 = tabhost.newTabSpec("tabBluetooth")
                .setIndicator("蓝牙")
                .setContent(R.id.linearLayout3);
        tabhost.addTab(page2);
        TabHost.TabSpec page3 = tabhost.newTabSpec("tabFile")
                .setIndicator("文件")
                .setContent(R.id.linearLayout2);
        tabhost.addTab(page3);
        TabHost.TabSpec page4 = tabhost.newTabSpec("tabApp")
                .setIndicator("程序")
                .setContent(R.id.tablayoutApp);
        tabhost.addTab(page4);
        TabHost.TabSpec page5 = tabhost.newTabSpec("tabChat")
                .setIndicator("聊天")
                .setContent(R.id.linearLayout3);
        tabhost.addTab(page5);

        //TabWidget tabWidget=(TabWidget)findViewById(R.id.tabWidget);
        //tabWidget.setBackgroundResource(R.drawable.background);
        tabhost.setCurrentTab(0);

        defaultTabLinstener();  //默认第一个TAB的监听函数

        tabhost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                if (s.equalsIgnoreCase("tabControl")) {
                    defaultTabLinstener();  //默认第一个TAB的监听函数
                } else if (s.equalsIgnoreCase("tabApp")) {
                   // setTab(TAB_2, true);
                    ListView mListView=(ListView)findViewById(R.id.listView);
                    ShowAppClass showAppClass=new ShowAppClass(MainActivity.this,mListView);
                    showAppClass.startShowApp();
                }else if (s.equalsIgnoreCase("tabFile")) {

            }else if (s.equalsIgnoreCase("tabChat")) {
                    Intent intent=new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }else if (s.equalsIgnoreCase("tabBluetooth")) {

             }}});
    }

    void defaultTabLinstener()
    {
        Button btMenu=(Button)findViewById(R.id.buttonMenu);
        Button btHome=(Button)findViewById(R.id.buttonHome);
        Button btReturn=(Button)findViewById(R.id.buttonReturn);
        Button btReboot=(Button)findViewById(R.id.buttonReboot);
        Button btMouse=(Button)findViewById(R.id.buttonMouse);
        Button btKeyboard=(Button)findViewById(R.id.buttonKeyboard);
        Button btNavi=(Button)findViewById(R.id.buttonNavi);
        SeekBar seekBar=(SeekBar)findViewById(R.id.seekBar);
        ArrayList<Button> list=new ArrayList<Button>();
        list.add(btMenu);list.add(btHome);
        list.add(btReturn);list.add(btReboot);
        list.add(btMouse);list.add(btKeyboard);
        list.add(btNavi);
        ControlClass controlClass=new ControlClass(MainActivity.this,rmaddr,list,seekBar);
        controlClass.startControl();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.item, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.show();
            Window window = alertDialog.getWindow();
            window.setContentView(R.layout.dialogabout);
            TextView tv_title = (TextView) window.findViewById(R.id.tv_dialog_title);
            tv_title.setText("欢迎使用Revdo");
            TextView tv_message = (TextView) window.findViewById(R.id.tv_dialog_message);
            tv_message.setText("北京七人阵科技有限公司");
            TextView tv_message2 = (TextView) window.findViewById(R.id.tv_dialog_message2);
            tv_message2.setText("网址：http://www.revdo.com/");
            Button buttondialog=(Button)window.findViewById(R.id.buttondialog);
            buttondialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });

            return true;
        }else if (id == R.id.action_upddate) {
            UpdateClass mUpdate=new UpdateClass(this);
            mUpdate.forceUpdateApp();
            return true;
        } else if (id == R.id.action_feedback) {
        Toast.makeText(this,"意见反馈", Toast.LENGTH_SHORT).show();
            return true;
    }else if (id == R.id.action_connect) {
            //Toast.makeText(this,"意见反馈", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(this,QRCreateActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void makeActionOverflowMenuShown() {
        //devices with hardware menu button (e.g. Samsung Note) don't show action overflow menu
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
        }
    }
}
