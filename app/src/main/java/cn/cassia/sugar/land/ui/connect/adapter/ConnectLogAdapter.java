package cn.cassia.sugar.land.ui.connect.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import cn.cassia.sugar.land.R;

public class ConnectLogAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public ConnectLogAdapter() {
        super(R.layout.item_connect_log);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv, item != null ? item : "");
    }
}
