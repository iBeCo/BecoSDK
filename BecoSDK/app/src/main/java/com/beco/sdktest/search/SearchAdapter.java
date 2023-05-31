package com.beco.sdktest.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.beco.sdk.model.BEPoint;
import com.beco.sdktest.R;

import java.util.List;

public class SearchAdapter extends BaseAdapter {

    private List<BEPoint> originalData = null;
    private LayoutInflater mInflater;

    public SearchAdapter(Context context, List<BEPoint> data) {
        this.originalData = data;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return originalData.size();
    }

    public void setOriginalData(List<BEPoint> originalData) {
        this.originalData = originalData;
    }

    public Object getItem(int position) {
        return originalData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.search_list_item, null);
            holder = new ViewHolder();
            holder.location = convertView.findViewById(R.id.location);
            holder.floor = convertView.findViewById(R.id.floor);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.location.setText(originalData.get(position).getName());
        holder.floor.setText(originalData.get(position).getFloorDescription());
        return convertView;
    }

    static class ViewHolder {
        TextView location;
        TextView floor;
    }

}
