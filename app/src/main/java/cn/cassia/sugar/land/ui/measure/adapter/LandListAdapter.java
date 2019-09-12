package cn.cassia.sugar.land.ui.measure.adapter;

import android.os.Build;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableStringBuilder;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import cn.cassia.sugar.land.R;
import cn.cassia.sugar.land.model.LandList;
import cn.cassia.sugar.land.utils.SpanUtils;

/**
 * Created by qingjie on 2018-06-05.0005.
 */
public class LandListAdapter extends BaseQuickAdapter<LandList, BaseViewHolder> {
    private int position = -1;

    public LandListAdapter() {
        super(R.layout.item_land_list);
    }

    public void setSelecter(int position) {
        this.position = position;
        notifyDataSetChanged();
    }

    @Override
    protected void convert(BaseViewHolder helper, LandList item) {
        SpannableStringBuilder builder = new SpanUtils(mContext).appendLine("编号：" + item.code).setFontSize(14, true)
                .appendLine("烟区：" + item.getSugarcane_region_id()[1]).setFontSize(14, true)
                .append("烟户：" + item.getFarmer_id()[1]).setFontSize(14, true)
                .create();
        helper.setText(R.id.tv_title, builder)
                .setText(R.id.tv_content, "由" + item.getGeo_create_uid()[1] + "于" + item.getGeo_create_date() + "测量")
                .addOnClickListener(R.id.ll);
        
        AppCompatTextView tv = helper.getView(R.id.tv_statu);
        switch (item.getState()) {
            case "draft":
                tv.setText("草稿");
                tv.setBackgroundResource(R.color.text_color);
                break;
            case "confirm":
                tv.setText("待审核");
                tv.setBackgroundResource(R.color.color_login_text_right);
                break;
            case "accept":
                tv.setText("已审核");
                tv.setBackgroundResource(R.color.success_stroke_color);
                break;
            case "history":
                tv.setText("历史");
                tv.setBackgroundResource(R.color.grey);
                break;
            case "reject":
                tv.setText("已驳回");
                tv.setBackgroundResource(R.color.colorAccent);
                break;
            default:
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (helper.getAdapterPosition() == position) {
                helper.setBackgroundRes(R.id.cardview, R.color.item_select);
            } else {
                helper.setBackgroundRes(R.id.cardview, R.color.cardview_light_background);
            }
        } else {
            if (helper.getAdapterPosition() == position) {
                helper.setBackgroundRes(R.id.rv, R.drawable.bg_cardview_on);
            } else {
                helper.setBackgroundRes(R.id.rv, R.drawable.bg_cardview_off);
            }
        }
    }

}
