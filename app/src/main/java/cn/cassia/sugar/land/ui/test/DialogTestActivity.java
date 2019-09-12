package cn.cassia.sugar.land.ui.test;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.cassia.sugar.land.R;
import cn.cassia.sugar.land.dialog.sweetalert.Rotate3dAnimation;
import cn.cassia.sugar.land.dialog.sweetalert.SweetAlertDialog;

/**
 * Created by qingjie on 2018-07-16.0016.
 */
public class DialogTestActivity extends Activity implements View.OnClickListener {

    private Rotate3dAnimation openAnimation;
    private Rotate3dAnimation closeAnimation;
    private int depthZ = 400;
    private int duration = 600;
    private int centerX;
    private int centerY;

    private boolean isOpen = false;

    private RelativeLayout mContentRl;
    private ImageView mLogoIv;
    private TextView mDescTv;
    private Button mOpenBtn;

    public static void start(Context context) {
        Intent starter = new Intent(context, DialogTestActivity.class);
        context.startActivity(starter);
    }

    private int i = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_activity);
        findViewById(R.id.basic_test).setOnClickListener(this);
        findViewById(R.id.under_text_test).setOnClickListener(this);
        findViewById(R.id.error_text_test).setOnClickListener(this);
        findViewById(R.id.success_text_test).setOnClickListener(this);
        findViewById(R.id.warning_confirm_test).setOnClickListener(this);
        findViewById(R.id.warning_cancel_test).setOnClickListener(this);
        findViewById(R.id.custom_img_test).setOnClickListener(this);
        findViewById(R.id.progress_dialog).setOnClickListener(this);

        mOpenBtn = findViewById(R.id.btn_open);
        mOpenBtn.setOnClickListener(this);
        mContentRl = findViewById(R.id.rl_content);
        mLogoIv = findViewById(R.id.iv_logo);
        mDescTv = findViewById(R.id.tv_desc);

    }

    private void initOpenAnim() {
        openAnimation = new Rotate3dAnimation(Rotate3dAnimation.ROLL_BY_Y, 0, 90, centerX, centerY);
        openAnimation.setDuration(duration);
        openAnimation.setFillAfter(true);
        openAnimation.setInterpolator(new AccelerateInterpolator());
        openAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mLogoIv.setVisibility(View.GONE);
                mDescTv.setVisibility(View.VISIBLE);
                Rotate3dAnimation rotateAnimation = new Rotate3dAnimation(Rotate3dAnimation.ROLL_BY_Y, 270, 360, centerX, centerY);
                rotateAnimation.setDuration(duration);
                rotateAnimation.setFillAfter(true);
                rotateAnimation.setInterpolator(new DecelerateInterpolator());
                mContentRl.startAnimation(rotateAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void initCloseAnim() {
        closeAnimation = new Rotate3dAnimation(Rotate3dAnimation.ROLL_BY_Y, 360, 270, centerX, centerY);
        closeAnimation.setDuration(duration);
        closeAnimation.setFillAfter(true);
        closeAnimation.setInterpolator(new AccelerateInterpolator());
        closeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mLogoIv.setVisibility(View.VISIBLE);
                mDescTv.setVisibility(View.GONE);
                Rotate3dAnimation rotateAnimation = new Rotate3dAnimation(Rotate3dAnimation.ROLL_BY_Y, 90, 0, centerX, centerY);
                rotateAnimation.setDuration(duration);
                rotateAnimation.setFillAfter(true);
                rotateAnimation.setInterpolator(new DecelerateInterpolator());
                mContentRl.startAnimation(rotateAnimation);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_open:
                centerX = mContentRl.getWidth() / 2;
                centerY = mContentRl.getHeight() / 2;
                if (openAnimation == null) {
                    initOpenAnim();
                    initCloseAnim();
                }
                if (openAnimation.hasStarted() && !openAnimation.hasEnded()) {
                    return;
                }
                if (closeAnimation.hasStarted() && !closeAnimation.hasEnded()) {
                    return;
                }
                if (isOpen) {
                    mContentRl.startAnimation(closeAnimation);
                } else {
                    mContentRl.startAnimation(openAnimation);
                }
                isOpen = !isOpen;
                mOpenBtn.setText(isOpen ? "关闭" : "打开");
                break;
            case R.id.basic_test:
                SweetAlertDialog sd = new SweetAlertDialog(this);
                sd.setCancelable(true);
                sd.setCanceledOnTouchOutside(true);
                sd.show();
                break;
            case R.id.under_text_test:
                new SweetAlertDialog(this)
                        .setContentText("提示，取消保存数据?")
                        .show();
                break;
            case R.id.error_text_test:
                new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("哎呀...")
                        .setContentText("出错啦!")
                        .show();
                break;
            case R.id.success_text_test:
                new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("成功!")
                        .setContentText("点击继续!")
                        .show();
                break;
            case R.id.warning_confirm_test:
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("确定吗?")
                        .setContentText("你要删除这个数据吗!")
                        .setConfirmText("是，删除!")
                        .setConfirmClickListener(sDialog -> {
                            sDialog.setTitleText("Deleted!")
                                    .setContentText("Your imaginary file has been deleted!")
                                    .setConfirmText("确定")
                                    .setConfirmClickListener(null)
                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        })
                        .show();
                break;
            case R.id.warning_cancel_test:
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("确定吗?")
                        .setContentText("确定要删除这个文件!")
                        .setCancelText("否，取消!")
                        .setConfirmText("是，删除!")
                        .showCancelButton(true)
                        .setCancelClickListener(sDialog -> sDialog.setTitleText("取消!")
                                .setContentText("您取消的该删除操作")
                                .setConfirmText("确定")
                                .showCancelButton(false)
                                .setCancelClickListener(null)
                                .setConfirmClickListener(null)
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE))
                        .setConfirmClickListener(sDialog -> sDialog.setTitleText("成功!")
                                .setContentText("你成功删除该文件!")
                                .setConfirmText("确定")
                                .showCancelButton(false)
                                .setCancelClickListener(null)
                                .setConfirmClickListener(null)
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE))
                        .show();
                break;
            case R.id.custom_img_test:
                new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setTitleText("哈哈哈!")
                        .setContentText("图片弹窗")
                        .setCustomImage(R.mipmap.btn_ok)
                        .show();
                break;
            case R.id.progress_dialog:
                final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                        .setTitleText("加载中...");
                pDialog.show();
                pDialog.setCancelable(false);
                new CountDownTimer(1000 * 3, 1000) {

                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        i = -1;
                        pDialog.setTitleText("获取成功!")
                                .setConfirmText("确定")
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    }
                }.start();
                break;
        }
    }
}
