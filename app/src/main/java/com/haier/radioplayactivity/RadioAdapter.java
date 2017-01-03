package com.haier.radioplayactivity;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Harry.Kong on 2016/12/30.
 */


public class RadioAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    List<String> mData;
    private int clickTemp;

    public RadioAdapter(Context context, List<String> mData) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = mData;
    }

    //标识选择的Item
    public void setSeclection(int position) {
        clickTemp = position;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mData.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.activity_radio_adapter, null);
            holder.title = (TextView) convertView.findViewById(R.id.tv_radio_name0);
            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }
// 点击改变选中listItem的背景色
        if (clickTemp == position) {
            holder.title.setTextColor(Color.parseColor("#4eb7ff"));
        } else {
            holder.title.setTextColor(Color.parseColor("#d5d5d5"));
        }
        holder.title.setText(mData.get(position));

        final ViewHolder finalHolder = holder;


        return convertView;
    }

    class ViewHolder {
        public TextView title;
    }


}
