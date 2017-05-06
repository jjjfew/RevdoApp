package com;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AutoStartReceiver extends BroadcastReceiver {
    public AutoStartReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Log.v("xxx","auto reboot complete");
            //启动自身activity
            Intent intent2=new Intent(context,MainActivity.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent2);
        }

        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
