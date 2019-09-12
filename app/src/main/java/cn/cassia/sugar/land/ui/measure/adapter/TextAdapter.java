package cn.cassia.sugar.land.ui.measure.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import cn.cassia.sugar.land.R;

/**
 * Created by qingjie on 2018-10-08.0008.
 */
public class TextAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public TextAdapter() {
        super(R.layout.item_pop);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.text_content, item);
    }
}
