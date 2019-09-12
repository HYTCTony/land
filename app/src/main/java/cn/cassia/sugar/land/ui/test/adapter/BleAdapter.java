package cn.cassia.sugar.land.ui.test.adapter;

import android.support.v7.widget.AppCompatImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.inuker.bluetooth.library.search.SearchResult;

import cn.cassia.sugar.land.R;

/**
 * Created by qingjie on 2018-07-13.0013.
 */
public class BleAdapter extends BaseQuickAdapter<SearchResult, BaseViewHolder> {
    public BleAdapter() {
        super(R.layout.item_connect_device);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchResult item) {
        AppCompatImageView iv = helper.getView(R.id.iv);
        iv.setBackgroundDrawable(mContext.getResources().getDrawable(R.mipmap.btn_ok));
        helper.setText(R.id.tv_name, item.getName());
        helper.setText(R.id.tv_address, item.getAddress());
    }
}
