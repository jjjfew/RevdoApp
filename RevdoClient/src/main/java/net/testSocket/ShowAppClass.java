package net.testSocket;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by administrator on 6/9/16.
 */
public class ShowAppClass {
    private Context context;

    private ListView mListView;

    public ShowAppClass(Context context,ListView mListView) {
        this.context = context;
        this.mListView=mListView;
    }

    void startShowApp() {
        final ArrayList<AppInfo> appList = new ArrayList<AppInfo>(); //用来存储获取的应用信息数据
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            AppInfo tmpInfo = new AppInfo();
            tmpInfo.appName = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
            tmpInfo.packageName = packageInfo.packageName;
            tmpInfo.versionName = packageInfo.versionName;
            tmpInfo.versionCode = packageInfo.versionCode;
            tmpInfo.appIcon = packageInfo.applicationInfo.loadIcon(context.getPackageManager());
            //Only display the non-system app info
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                // tmpInfo.print();
                appList.add(tmpInfo);//非系统应用，则添加至appList
            }
        }

        final AppAdapter appAdapter = new AppAdapter(context, appList);
        if (mListView != null) {
            mListView.setAdapter(appAdapter);
        }
    }
}
