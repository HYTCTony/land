package cn.cassia.sugar.land.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import cn.cassia.sugar.land.ui.measure.adapter.ExpandableItemAdapter;

/**
 * Created by qingjie on 2018-06-28.0028.
 */
public class Level1Item implements MultiItemEntity {
    public String title;
    public String subTitle;

    public Level1Item(String title, String subTitle) {
        this.subTitle = subTitle;
        this.title = title;
    }

    @Override
    public int getItemType() {
        return ExpandableItemAdapter.TYPE_LEVEL_1;
    }

}
