package cn.cassia.sugar.land.model;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

import cn.cassia.sugar.land.ui.measure.adapter.ExpandableItemAdapter;

/**
 * Created by qingjie on 2018-06-28.0028.
 */
public class Level0Item extends AbstractExpandableItem<Level1Item> implements MultiItemEntity {
    public String title;
    public List<Level1Item> landInfomation;

    public Level0Item() {
        mSubItems = landInfomation;
    }

    public Level0Item(String title) {
        this.title = title;
    }

    @Override
    public int getItemType() {
        return ExpandableItemAdapter.TYPE_LEVEL_0;
    }

    @Override
    public int getLevel() {
        return ExpandableItemAdapter.TYPE_LEVEL_0;
    }
}
