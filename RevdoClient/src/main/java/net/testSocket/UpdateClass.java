package net.testSocket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.widget.Toast;

import com.iflytek.autoupdate.IFlytekUpdate;
import com.iflytek.autoupdate.IFlytekUpdateListener;
import com.iflytek.autoupdate.UpdateConstants;
import com.iflytek.autoupdate.UpdateErrorCode;
import com.iflytek.autoupdate.UpdateInfo;
import com.iflytek.autoupdate.UpdateType;

/**
 * Created by administrator on 5/15/16.
 */
public class UpdateClass {

    private IFlytekUpdate updManager;
    private Toast mToast;
    Context context;

    public UpdateClass(Context context){
        this.context=context;
    }

    public String showVersion() {
        try {
            PackageManager manager = context.getApplicationContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getApplicationContext().getPackageName(), 0);
           // Toast.makeText(context, "版本号"+ info.versionCode, Toast.LENGTH_SHORT).show();
            String strVer="当前版本号：V"+info.versionCode;
            return strVer;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public  void forceUpdateApp(){  //用于手动更新，不管wifi还是移动网络都行
        //初始化自动更新对象
        updManager = IFlytekUpdate.getInstance(context);
        //开启调试模式,默认不开启
        updManager.setDebugMode(true);
        //开启wifi环境下检测更新,仅对自动更新有效,强制更新则生效
        updManager.setParameter(UpdateConstants.EXTRA_WIFIONLY, "false");  //可以在移动网络下更新
        //设置更新提示类型,默认为通知栏提示
        updManager.setParameter(UpdateConstants.EXTRA_STYLE, UpdateConstants.UPDATE_UI_DIALOG);
        //updManager.setParameter(UpdateConstants.EXTRA_STYLE, UpdateConstants.UPDATE_UI_NITIFICATION);
        // 启动自动更新
        updManager.forceUpdate(context, updateListener);
    }

    public  void autoUpdateApp(){ //用于后台检测更新，可以设置用wifi还是移动网络
        //初始化自动更新对象
        updManager = IFlytekUpdate.getInstance(context);
        //开启调试模式,默认不开启
        updManager.setDebugMode(true);
        //开启wifi环境下检测更新,仅对自动更新有效,强制更新则生效
        updManager.setParameter(UpdateConstants.EXTRA_WIFIONLY, "false");  //可以在移动网络下更新
        //设置更新提示类型,默认为通知栏提示
        updManager.setParameter(UpdateConstants.EXTRA_STYLE, UpdateConstants.UPDATE_UI_DIALOG);
        //updManager.setParameter(UpdateConstants.EXTRA_STYLE, UpdateConstants.UPDATE_UI_NITIFICATION);
        // 启动自动更新
        updManager.autoUpdate(context, null);
    }

    //自动更新回调方法,详情参考demo
    private IFlytekUpdateListener updateListener = new IFlytekUpdateListener() {
        @Override
        public void onResult(int errorcode, UpdateInfo result) {
            if(errorcode == UpdateErrorCode.OK && result!= null) {
                if(result.getUpdateType() == UpdateType.NoNeed) {
                  //  showTip("已经是最新版本！" );
                    Message msg=new Message();
                    msg.obj=showVersion();
                   MainActivity.handler1.sendMessage(msg);
                    return;
                }
                updManager.showUpdateInfo(context, result);
            }
            else
            {
               // showTip("请求更新失败！\n更新错误码：" + errorcode);
                Message msg=new Message();
                msg.obj="请求更新失败！\n更新错误码：" + errorcode;
                MainActivity.handler1.sendMessage(msg);

            }
        }
    };


}
