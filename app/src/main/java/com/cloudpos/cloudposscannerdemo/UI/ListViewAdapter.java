package com.cloudpos.cloudposscannerdemo.UI;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cloudpos.cloudposscannerdemo.R;

import java.util.Locale;

/**
 * Created by gecx on 16-12-5.
 */

public class ListViewAdapter extends BaseAdapter {

    private final String[] data;
    private Context mContext;

    public ListViewAdapter(Context context) {
        mContext = context;
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        data = new String[]{"open","Synchronous Scan", "Asynchronous Scan", "Not full screen", "Return bitmap", "full screen",
                    "only QR or code128", "custom window","close"};
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.listview_item, null);
            holder = new ViewHolder();
            holder.mTv = (TextView) convertView.findViewById(R.id.tv_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mTv.setText(data[position]);

        return convertView;
    }

    private class ViewHolder {
        private TextView mTv;
    }
}
