package cn.cassia.sugar.land.ui.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.cassia.sugar.land.R;
import cn.cassia.sugar.land.base.BaseViewActivity;
import cn.cassia.sugar.land.ui.measure.adapter.TextAdapter;
import cn.cassia.sugar.land.utils.CharacterParser;

/**
 * Created by qingjie on 2018-05-29.0029.
 */
public class TestActivity extends BaseViewActivity {

    @BindView(R.id.et_text)
    EditText etText;
    @BindView(R.id.rv)
    RecyclerView rv;
    private CharacterParser characterParser;
    boolean isFilterData = true;
    private TextAdapter adapter;
    private String[] name = {"泳池派对盲僧", "泳池派对吉欧斯", "泳池派对雷欧塞", "泳池派对雷雷塞", "泳池派对格雷福斯", "源计划艾克", "源计划艾希", "源计划易", "源计划劫", "源计划卢锡安", "冰雪节布隆", "冰雪节奥利安娜", "冰雪节崔丝塔娜", "冰雪节诡术妖姬", "冰雪节克格莫", "电玩勇者伊泽瑞尔", "电玩上校库奇", "电玩女神莎娜", "神拳李青", "任性学霸艾克"};
    private ArrayList<String> list = new ArrayList<>();

    public static void start(Context context) {
        Intent starter = new Intent(context, TestActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        toolbar.setTitle("测试");
        super.initToolbar(toolbar);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_test;
    }

    @Override
    protected void initView() {
        characterParser = CharacterParser.getInstance();
        for (int i = 0; i < 20; i++) {
            list.add(name[i]);
        }
        adapter = new TextAdapter();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        etText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isFilterData) {
                    filterData(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    List<String> filterDateList = new ArrayList<>();

    private void filterData(String filterStr) {
        filterDateList = new ArrayList<>();
        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = list;
        } else {
            for (int i = 0; i < list.size(); i++) {
                String character = characterParser.getSelling(list.get(i));
                if (list.get(i).contains(filterStr) || character.contains(filterStr)) {
                    filterDateList.add(list.get(i));
                }
            }
        }
        adapter.setNewData(filterDateList);
    }

}