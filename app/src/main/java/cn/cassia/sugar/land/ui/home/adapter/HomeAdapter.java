package cn.cassia.sugar.land.ui.home.adapter;

import android.support.v7.widget.AppCompatImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import cn.cassia.sugar.land.R;
import cn.cassia.sugar.land.model.HomeBean;

/**
 * Created by qingjie on 2018-05-25.0025.
 */
public class HomeAdapter extends BaseQuickAdapter<HomeBean, BaseViewHolder> {
    public HomeAdapter() {
        super(R.layout.item_home);
    }

    @Override
    protected void convert(BaseViewHolder helper, HomeBean item) {
        AppCompatImageView iv = helper.getView(R.id.iv);
        iv.setBackgroundDrawable(mContext.getResources().getDrawable(item.imageResourse));

        helper.setText(R.id.tv_title, item.title);
    }
}
