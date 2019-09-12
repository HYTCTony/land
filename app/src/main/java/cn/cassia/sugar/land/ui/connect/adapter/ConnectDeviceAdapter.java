package cn.cassia.sugar.land.ui.connect.adapter;

import android.support.v7.widget.AppCompatImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import cn.cassia.sugar.land.R;
import cn.cassia.sugar.land.model.DeviceBean;

public class ConnectDeviceAdapter extends BaseQuickAdapter<DeviceBean, BaseViewHolder> {

    public ConnectDeviceAdapter() {
        super(R.layout.item_connect_device);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceBean item) {
        AppCompatImageView iv = helper.getView(R.id.iv);
        if (item.active)
            iv.setBackgroundDrawable(mContext.getResources().getDrawable(R.mipmap.btn_ok));
        else iv.setBackgroundDrawable(null);
        helper.setText(R.id.tv_name, item.name != null ? item.name : "");
        helper.setText(R.id.tv_address, item.address != null ? item.address : "");

    }

}