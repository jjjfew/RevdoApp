package com.view;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

/**
 * Created by administrator on 6/14/16.
 */
public class WifiClass {
    // 定义WifiManager对象
    private WifiManager mWifiManager;
    // 定义WifiInfo对象
    private WifiInfo mWifiInfo;
    // 扫描出的网络连接列表
    private List<ScanResult> mWifiList;
    // 网络连接列表
    private List<WifiConfiguration> mWifiConfiguration;
    // 定义一个WifiLock
    WifiManager.WifiLock mWifiLock;
    // 构造器
    public WifiClass(Context context) {
        // 取得WifiManager对象
        mWifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        // 取得WifiInfo对象
        mWifiInfo = mWifiManager.getConnectionInfo();
    }
    // 打开WIFI
    public boolean openWifi() {
        boolean temp=true;
        if (!mWifiManager.isWifiEnabled()) {
            temp= mWifiManager.setWifiEnabled(true);
        }
        return temp;
    }
    // 关闭WIFI
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }
    // 锁定WifiLock
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }
    // 解锁WifiLock
    public void releaseWifiLock() {
        // 判断时候锁定
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }
    // 创建一个WifiLock
    public void creatWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("Test");
    }
    // 得到配置好的网络
    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfiguration;
    }
    // 指定配置好的网络进行连接
    public void connectConfiguration(int index) {
        // 索引大于配置好的网络索引返回
        if (index > mWifiConfiguration.size()) {
            return;
        }
        // 连接配置好的指定ID的网络
        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,
                true);
    }
    public void startScan() {
        mWifiManager.startScan();
        // 得到扫描结果
        mWifiList = mWifiManager.getScanResults();
        // 得到配置好的网络连接
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();
    }
    // 得到网络列表
    public List<ScanResult> getWifiList() {
        return mWifiList;
    }
    // 查看扫描结果
    public StringBuilder lookUpScan() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mWifiList.size(); i++) {
            stringBuilder
                    .append("Index_" + new Integer(i + 1).toString() + ":");
            // 将ScanResult信息转换成一个字符串包
            // 其中把包括：BSSID、SSID、capabilities、frequency、level
            stringBuilder.append((mWifiList.get(i)).toString());
            stringBuilder.append("/n");
        }
        return stringBuilder;
    }
    // 得到MAC地址
    public String getMacAddress() {return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();}
    // 得到接入点的BSSID
    public String getBSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }
    //获得的SSID值带引号，如"revod"
    public String getSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getSSID();
    }
    // 得到IP地址
    public int getIPAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }
    // 得到连接的ID
    public int getNetworkId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }
    // 得到WifiInfo的所有信息包
    public String getWifiInfo() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
    }

    // 添加一个网络并连接
    public void addNetwork(WifiConfiguration wcg) {
        int wcgID = mWifiManager.addNetwork(wcg);
        boolean b =  mWifiManager.enableNetwork(wcgID, true);
        System.out.println("a--" + wcgID);
        System.out.println("b--" + b);
        Log.w("xxx","a--"+wcgID);
        Log.w("xxx","b--"+b);
    }
    // 断开指定ID的网络
    public void disconnectWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }
    //然后是一个实际应用方法，只验证过没有密码的情况：
    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type)
    {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        if(Type == 1) //WIFICIPHER_NOPASS
        {
            config.wepKeys[0] = "\"" + "\"";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if(Type == 2) //WIFICIPHER_WEP
        {
            config.hiddenSSID = true;
            config.wepKeys[0]= "\""+Password+"\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if(Type == 3) //WIFICIPHER_WPA
        {
            config.preSharedKey = "\""+Password+"\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    //传入的SSID参数不带引号
    public WifiConfiguration IsExsits(String SSID)
    {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs)
        {
            if (existingConfig.SSID.equals("\""+SSID+"\""))
            {
                return existingConfig;
            }
        }
        return null;
    }

    //删除所有保存的网络设置
    public void remove_all_ssid()
    {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs)
        {
            Log.w("xxx","remove "+existingConfig.SSID);
            mWifiManager.removeNetwork(existingConfig.networkId);
        }
    }

    //提供一个外部接口，传入要连接的无线网
    public boolean connect(String SSID, String Password, int Type)
    {
        if(!this.openWifi())
        {
            return false;
        }
        //开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
        //状态变成WIFI_STATE_ENABLED 的时候才能执行下面的语句
        //WifiManager.WIFI_STATE_ENABLING 2
        //WifiManager.WIFI_STATE_ENABLED 3
        System.out.println("wifiManager.getWifiState()" + mWifiManager.getWifiState());
        while(mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING )
        {
            System.out.println("wifiManager.getWifiState" + mWifiManager.getWifiState());
            try{
                //为了避免程序一直while循环，让它睡个100毫秒在检测……
                Thread.currentThread();
                Thread.sleep(100);
            }
            catch(InterruptedException ie){
            }
        }
        WifiConfiguration wifiConfig = this.CreateWifiInfo(SSID, Password, Type);
        if(wifiConfig == null)
        {
            System.out.println("cannot set wifiConfig" );
            return false;
        }

        remove_all_ssid();

        int netID = mWifiManager.addNetwork(wifiConfig);
        boolean bRet = mWifiManager.enableNetwork(netID, false);
        System.out.println("addNetwork" );
        return bRet;
    }
}
