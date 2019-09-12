package cn.cassia.sugar.land.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.cassia.sugar.land.R;
import cn.cassia.sugar.land.base.BaseDialogFragment;
import cn.cassia.sugar.land.model.Level0Item;
import cn.cassia.sugar.land.model.Level1Item;
import cn.cassia.sugar.land.model.Plant;
import cn.cassia.sugar.land.ui.measure.adapter.ExpandableItemAdapter;

/**
 * Created by qingjie on 2018-09-18.0018.
 */
public class UpdateDialog extends BaseDialogFragment {
    private static onClickListener listener;

    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.tv_update_message)
    TextView tvUpdateMessage;
    @BindView(R.id.btn_cancel)
    TextView btnCancel;

    public static UpdateDialog newInstance(String versionName, String upDateDetail, boolean isForcedUpdate, onClickListener l) {
        UpdateDialog fragment = new UpdateDialog();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isForcedUpdate", isForcedUpdate);
        bundle.putString("upDateDetail", upDateDetail);
        bundle.putString("versionName", versionName);
        fragment.setArguments(bundle);
        listener = l;
        return fragment;
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.dialog_update;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.AlertDialogStyle);
    }

    @Override
    protected void init() {
        setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//注意此处
        boolean isForcedUpdate = getArguments().getBoolean("isForcedUpdate");
        String versionName = getArguments().getString("versionName");
        String upDateDetail = getArguments().getString("upDateDetail");
        btnCancel.setVisibility(isForcedUpdate ? View.GONE : View.VISIBLE);
        tvVersion.setText(versionName + "版本闪亮登场，请下载体验。");
        tvUpdateMessage.setText("更新内容：" + upDateDetail);
    }

    @OnClick({R.id.btn_confirm, R.id.btn_cancel})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm:
                listener.onConfirm(getDialog());
                dismiss();
                break;
            case R.id.btn_cancel:
                listener.onCancel(getDialog());
                dismiss();
                break;
            default:
                listener.onCancel(getDialog());
                dismiss();
        }
    }

    public interface onClickListener {
        void onConfirm(Dialog dialog);

        void onCancel(Dialog dialog);
    }
}