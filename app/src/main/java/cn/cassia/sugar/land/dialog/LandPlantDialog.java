package cn.cassia.sugar.land.dialog;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.cassia.sugar.land.R;
import cn.cassia.sugar.land.base.BaseDialogFragment;
import cn.cassia.sugar.land.model.Plant;
import cn.cassia.sugar.land.ui.measure.adapter.ExpandableItemAdapter;
import cn.cassia.sugar.land.model.Level0Item;
import cn.cassia.sugar.land.model.Level1Item;

/**
 * Created by qingjie on 2018-08-24.0024.
 */
public class LandPlantDialog extends BaseDialogFragment {
    @BindView(R.id.rv)
    RecyclerView rv;
    ExpandableItemAdapter myAdapter;
    ArrayList<MultiItemEntity> list;
    private static List<Plant> plant;

    public static LandPlantDialog newInstance(List<Plant> data) {
        LandPlantDialog fragment = new LandPlantDialog();
        plant = data;
        return fragment;
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.dialog_land_plant;
    }

    @Override
    protected void init() {
        setCancelable(true);
        getDialog().setCanceledOnTouchOutside(true);

        list = generateData(plant);
        myAdapter = new ExpandableItemAdapter(list);
        myAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        rv.setAdapter(myAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(manager);
    }

    @OnClick(R.id.btn_cancel)
    void onClick() {
        dismiss();
    }

    private ArrayList<MultiItemEntity> generateData(List<Plant> plant) {
        ArrayList<MultiItemEntity> res = new ArrayList<>();
        for (int i = 0; i < plant.size(); i++) {
            Level0Item lv0 = new Level0Item(plant.get(i).getGrinding_season_id()[1] + " 年榨季");
            Level1Item lv1 = new Level1Item("编号：", plant.get(i).getCode());
            lv0.addSubItem(lv1);
            Level1Item lv2 = new Level1Item("农作物：", plant.get(i).getCrop_id()[1]);
            lv0.addSubItem(lv2);
            if (!TextUtils.isEmpty(plant.get(i).getPlanting_time())) {
                Level1Item lv3 = new Level1Item("植期：", plant.get(i).getPlanting_time());
                lv0.addSubItem(lv3);
            }
            res.add(lv0);
        }
        return res;
    }
}
