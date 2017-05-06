package net.testSocket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.List;

/**
 * Created by administrator on 6/9/16.
 */
public class ControlClass {
    private Context context;
    private String rmaddr;
    private Button btMenu ;
    private Button btHome;
    private Button btReturn ;
    private Button btReboot;
    private Button btMouse ;
    private Button btKeyboard;
    private Button btNavi;
    private SeekBar seekBar;
    private Activity mActivity;
    public static Handler handler1;

    public ControlClass(Context context,String rmaddr,List list,SeekBar seekBar)
    {
        this.context=context;
        this.btMenu=(Button)list.get(0);
        this.btHome=(Button)list.get(1);
        this.btReturn=(Button)list.get(2);
        this.btReboot=(Button)list.get(3);
        this.btMouse=(Button)list.get(4);
        this.btKeyboard=(Button)list.get(5);
        this.btNavi=(Button)list.get(6);
        this.seekBar=seekBar;
        this.rmaddr=rmaddr;
    }

    void startControl() {
        handler1=new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what==111){
                    Toast.makeText(context,msg.obj.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        };

        setButtonListener(btMenu,"menu");
        setButtonListener(btHome,"home");
        setButtonListener(btReturn,"return");
        setButtonListener(btReboot,"reboot");
        setButtonListener(btNavi,"activity_navi");
        btMouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,MouseActivity.class);
                context.startActivity(intent);
            }
        });
        setButtonListener(btKeyboard,"keyboard");

        seekBar.setMax(15);
        seekBar.setProgress(7);
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListenerImp());
    }

    private class OnSeekBarChangeListenerImp implements SeekBar.OnSeekBarChangeListener{
        //触发操作，拖动
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            //mTextView.append("正在拖动,当前值:"+seekBar.getProgress()+"\n");

        }
        //表示进度条刚开始拖动，开始拖动时候触发的操作
        public void onStartTrackingTouch(SeekBar seekBar) {
            //mTextView.append("开始拖动,当前值:"+seekBar.getProgress()+"\n");
        }
        //停止拖动时候
        public void onStopTrackingTouch(SeekBar seekBar) {
            //mTextView.append("停止拖动,当前值:"+seekBar.getProgress()+"\n");
            final int tempVolume=seekBar.getProgress();
            Log.w("xxx","当前值"+tempVolume+"");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SocketClient socketClient=new SocketClient(rmaddr);
                    socketClient.sendToServer("vol:"+tempVolume+"");
                }
            }).start();
        }
    }


    void setButtonListener(Button button,final String str) {
       button.setOnClickListener(new Button.OnClickListener() {
           @Override
           public void onClick(View v) {
               //Toast.makeText(context,"hello",Toast.LENGTH_SHORT).show();
               new Thread(new Runnable() {
                   @Override
                   public void run() {
                       SocketClient socketClient = new SocketClient(rmaddr);
                       socketClient.sendToServer(str);
                   }
               }).start();
           }
       });
   }
}
