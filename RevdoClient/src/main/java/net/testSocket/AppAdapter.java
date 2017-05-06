package net.testSocket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by administrator on 6/6/16.
 */
public class AppAdapter extends BaseAdapter{
    Context context;
    ArrayList<AppInfo> dataList = new ArrayList<AppInfo>();
    public AppAdapter(Context context, ArrayList<AppInfo> inputDataList) {
        this.context = context;   // 传递MainActivity的context
        dataList.clear();
        for (int i = 0; i < inputDataList.size(); i++) {
            dataList.add(inputDataList.get(i));
        }
    }
    @Override
    public int getCount() {
        return dataList.size();
    }
    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final AppInfo appUnit = dataList.get(position);
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //LayoutInflater vi=LayoutInflater.from(context);//或者可以这样
            convertView = vi.inflate(R.layout.app_item, null);
            //convertView.setClickable(true);  //如果加了这个会无法click view
        }
        TextView appNameText = (TextView) convertView
                .findViewById(R.id.ItemText);
        TextView packageNameText = (TextView) convertView
                .findViewById(R.id.ItemTitle);
        ImageView appIcon = (ImageView) convertView.findViewById(R.id.ItemImage);
        if (appNameText != null)
            appNameText.setText(appUnit.appName);
        if (packageNameText != null)
            packageNameText.setText(appUnit.packageName);
        if (appIcon != null)
            appIcon.setImageDrawable(appUnit.appIcon);
        return convertView;
    }
}
