package net.testSocket;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

/**
 * Created by administrator on 6/14/16.
 */
public class WifiClass {
   private WifiManager mWifiManager;

    public WifiClass(Context context){
        // 取得WifiManager对象
        mWifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        // 取得WifiInfo对象
    }

    // wifi热点开关
    public boolean setWifiAp(boolean enabled,String ssid,String pwd) {
        if (enabled) { // disable WiFi in any case
            //wifi和热点不能同时打开，所以打开热点的时候需要关闭wifi
            mWifiManager.setWifiEnabled(false);
        }
        //关闭热点
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
            method.setAccessible(true);
            WifiConfiguration config = (WifiConfiguration) method.invoke(mWifiManager);
            Method method2 = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method2.invoke(mWifiManager, config, false);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //配置热点并打打热点
        try {
            //热点的配置类
            WifiConfiguration apConfig = new WifiConfiguration();
            //配置热点的名称(可以在名字后面加点随机数什么的)
            Random rand=new Random();
            int a=rand.nextInt(9999);//产生一个1到9999的随机数
            apConfig.SSID = ssid;
            //配置热点的密码
            apConfig.preSharedKey=pwd;
            //通过反射调用设置热点
            apConfig.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            apConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            apConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            apConfig.allowedKeyManagement
                    .set(WifiConfiguration.KeyMgmt.WPA_PSK);
            apConfig.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            apConfig.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            apConfig.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.CCMP);
            apConfig.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.TKIP);
            Method method = mWifiManager.getClass().getMethod(
                    "setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            //返回热点打开状态
            return (Boolean) method.invoke(mWifiManager, apConfig, enabled);
        } catch (Exception e) {
            return false;
        }
    }
}
