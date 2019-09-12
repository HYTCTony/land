package cn.cassia.sugar.land.ui.home;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.cassia.sugar.land.R;
import cn.cassia.sugar.land.dialog.LoadingDialog;
import cn.cassia.sugar.land.mvp.BaseModule;
import cn.cassia.sugar.land.mvp.BaseMvpFragment;
import cn.cassia.sugar.land.ui.home.adapter.HomeAdapter;
import cn.cassia.sugar.land.model.HomeBean;
import cn.cassia.sugar.land.ui.measure.LandManagementActivity;
import cn.cassia.sugar.land.ui.test.DialogTestActivity;
import cn.cassia.sugar.land.ui.test.TestActivity;
import cn.cassia.sugar.land.utils.SharedPreferencesUtils;

/**
 * Created by qingjie on 2018-05-25.0025.
 */
public class HomeFragment extends BaseMvpFragment<HomeContract.IHomeView, HomePresenter> implements HomeContract.IHomeView {

    @BindView(R.id.rv)
    RecyclerView rv;
    HomeAdapter adapter;
    List<HomeBean> datas = new ArrayList<>();
    int[] img = {R.mipmap.land_survey, R.mipmap.land_survey1, R.mipmap.land_survey2};
    String[] title = {"地块测量", "测试功能1", "测试功能2"};
    @BindView(R.id.iv1)
    ImageView iv1;
    @BindView(R.id.iv2)
    ImageView iv2;
    @BindView(R.id.iv3)
    ImageView iv3;

    public static HomeFragment newInstance() {
        Bundle args = new Bundle();
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected HomePresenter initPresenter() {
        return new HomePresenter();
    }

    @Override
    protected BaseModule initModule() {
        return new HomeModule();
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void init() {
        adapter = new HomeAdapter();
        rv.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        rv.setAdapter(adapter);

        for (int i = 0; i < img.length; i++) {
            HomeBean bean = new HomeBean(img[i], title[i]);
            datas.add(bean);
        }
        adapter.setNewData(datas);

        adapter.setOnItemClickListener((adapter, view, position) -> {
            switch (position) {
                case 0:
                    LandManagementActivity.start(getActivity());
                    break;
                case 1:
                    DialogTestActivity.start(getActivity());
                    break;
                case 2:
                    TestActivity.start(getActivity());
                    break;
                case 3:
                    //BluetoothTestActivity.start(getActivity());
                    break;
                case 4:
//                    Animation a = null;
//                    a.start();
                    break;
                default:
            }
        });
//        String url = "https://img3.duitang.com/uploads/item/201605/24/20160524132459_h4LRn.jpeg";
//        GlideManager.glideLoader(getActivity(), url, iv1, GlideManager.IMG_TYPE_CIRCULAR);
//        GlideManager.glideLoader(getActivity(), url, iv2, GlideManager.IMG_TYPE_NOMAL);
//        GlideManager.glideLoader(getActivity(), url, iv3, GlideManager.IMG_TYPE_ROUNDED_RECTANGLE);
    }

    @Override
    protected boolean hasEvent() {
        return false;
    }

    @Override
    public int getUid() {
        return SharedPreferencesUtils.getUserId(getActivity());
    }

    @Override
    public String getPwd() {
        return SharedPreferencesUtils.getP(getActivity());
    }

    @Override
    public void showProgress() {
        LoadingDialog.show(getActivity());
    }

    @Override
    public void hideProgress() {
        LoadingDialog.dismiss(getActivity());
    }

    @Override
    public void onFailure(int code, String err) {
        showToast(err);
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResult(String s) {

    }
}
