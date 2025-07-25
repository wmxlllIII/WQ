package com.memory.wq.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.memory.wq.R;

public class MenuAdapter extends BaseAdapter {
    private Context context;
    private String[] options;

    public MenuAdapter(Context context, String[] options) {
        this.context = context;
        this.options = options;
    }

    @Override
    public int getCount() {
        return options.length;
    }

    @Override
    public Object getItem(int position) {
        return options[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_menu_layout, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String option = options[position];
        viewHolder.tv_option.setText(option);
        return convertView;
    }

    private static class ViewHolder {
        private TextView tv_option;

        public ViewHolder(View view) {
            tv_option = (TextView) view.findViewById(R.id.tv_option);
        }
    }
}
