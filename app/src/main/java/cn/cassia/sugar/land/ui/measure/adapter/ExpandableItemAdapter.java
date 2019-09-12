package cn.cassia.sugar.land.ui.measure.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

import cn.cassia.sugar.land.R;
import cn.cassia.sugar.land.model.Level0Item;
import cn.cassia.sugar.land.model.Level1Item;

/**
 * Created by qingjie on 2018-06-28.0028.
 */
public class ExpandableItemAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
    public static final int TYPE_LEVEL_0 = 0;
    public static final int TYPE_LEVEL_1 = 1;

    public ExpandableItemAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(TYPE_LEVEL_0, R.layout.item_expandable_lv0);
        addItemType(TYPE_LEVEL_1, R.layout.item_expandable_lv1);
    }

    @Override
    protected void convert(BaseViewHolder holder, MultiItemEntity item) {
        switch (holder.getItemViewType()) {
            case TYPE_LEVEL_0:
                Level0Item lv0 = (Level0Item) item;
                holder.setText(R.id.title, lv0.title);
                holder.itemView.setOnClickListener((View v) -> {
                    int pos = holder.getAdapterPosition();
                    if (lv0.isExpanded()) {
                        collapse(pos);
                    } else {
                        expand(pos);
                    }
                });
                break;
            case TYPE_LEVEL_1:
                Level1Item lv1 = (Level1Item) item;
                holder.setText(R.id.title, lv1.title)
                        .setText(R.id.content, lv1.subTitle);
//                holder.itemView.setOnLongClickListener((View v) -> {
//                    int pos = holder.getAdapterPosition();
//                    remove(pos);
//                    return true;
//                });
                break;
        }
    }
}
