package cn.cassia.sugar.land.ui.measure.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.cassia.sugar.land.R;


/**
 * Created by qingjie on 2018-10-11.0011.
 */
public class DialogAdapter extends BaseAdapter {

    private Context mContext;
    private List<String[]> list = new ArrayList<>();

    public DialogAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setDatatoAdapter(List<String[]> data) {
        this.list = data == null ? new ArrayList<>() : data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String[] getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflatert = LayoutInflater.from(mContext);
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = layoutInflatert.inflate(R.layout.item_dialog, parent, false);
            holder = new ViewHolder();
            holder.tv = convertView.findViewById(R.id.text_content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv.setText(list.get(position)[1]);
        return convertView;
    }

    static class ViewHolder {
        AppCompatTextView tv;
    }
}
