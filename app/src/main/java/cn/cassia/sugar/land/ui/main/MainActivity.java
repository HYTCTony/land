package cn.cassia.sugar.land.ui.main;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BadgeItem;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

import butterknife.BindView;
import cn.cassia.sugar.land.R;
import cn.cassia.sugar.land.base.BaseActivity;
import cn.cassia.sugar.land.ui.mine.MineFragment;
import cn.cassia.sugar.land.ui.home.HomeFragment;
import cn.cassia.sugar.land.ui.map.MapFragment;
import cn.cassia.sugar.land.ui.messsage.MessageFragment;
import cn.cassia.sugar.land.utils.ActivityUtils;

/**
 * Created by qingjie on 2017/7/4.
 */
public class MainActivity extends BaseActivity {

    @BindView(R.id.bottom_navigation_bar)
    BottomNavigationBar bottom_navigation_bar;
    private BadgeItem badge;

    private HomeFragment homeFragment;
    private MapFragment mapFragment;
    private MessageFragment msgFragment;
    private MineFragment mineFragment;

    public static void start(Context context) {
        Intent starter = new Intent(context, MainActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        initBottomNavigationBar();
        setDefaultFragment();
    }

    /**
     * 设置默认的
     */
    private void setDefaultFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        homeFragment = HomeFragment.newInstance();
        transaction.replace(R.id.fl_container, homeFragment);
        transaction.commit();
    }

    private void initBottomNavigationBar() {

        bottom_navigation_bar.setMode(BottomNavigationBar.MODE_FIXED);
        bottom_navigation_bar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_RIPPLE);
        //设置默认颜色
        bottom_navigation_bar
                .setInActiveColor(R.color.text_color)//设置未选中的Item的颜色，包括图片和文字
                .setActiveColor(R.color.background)
                .setBarBackgroundColor(R.color.subject_color);//设置整个控件的背景色
        //设置徽章
        badge = new BadgeItem()
                //                .setBorderWidth(2)//Badge的Border(边界)宽度
                //                .setBorderColor("#FF0000")//Badge的Border颜色
                //                .setBackgroundColor("#9ACD32")//Badge背景颜色
                //                .setGravity(Gravity.RIGHT| Gravity.TOP)//位置，默认右上角
                .setText("0")//显示的文本
        //                .setTextColor("#F0F8FF")//文本颜色
        //                .setAnimationDuration(2000)
        //                .setHideOnSelect(true)//当选中状态时消失，非选中状态显示
        ;
        //添加选项
        bottom_navigation_bar.addItem(new BottomNavigationItem(R.mipmap.ic_home_on, "首页")
                .setInactiveIcon(ContextCompat.getDrawable(this, R.mipmap.ic_home_off)))
                .addItem(new BottomNavigationItem(R.mipmap.ic_map_on, "地图")
                        .setInactiveIcon(ContextCompat.getDrawable(this, R.mipmap.ic_map_off)))
                .addItem(new BottomNavigationItem(R.mipmap.ic_message_on, "消息")
                        .setBadgeItem(badge)
                        .setInactiveIcon(ContextCompat.getDrawable(this, R.mipmap.ic_message_off)))
                .addItem(new BottomNavigationItem(R.mipmap.ic_mine_on, "我的")
                        .setInactiveIcon(ContextCompat.getDrawable(this, R.mipmap.ic_mine_off)))
                .initialise();//初始化BottomNavigationButton,所有设置需在调用该方法前完成
        bottom_navigation_bar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {

            @Override
            public void onTabSelected(final int position) {//未选中 -> 选中
                FragmentManager fm = MainActivity.this.getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                hideFragment(transaction);
                switch (position) {
                    case 0:
                        if (homeFragment == null) {
                            homeFragment = HomeFragment.newInstance();
                            transaction.add(R.id.fl_container, homeFragment);
                        } else {
                            transaction.show(homeFragment);
                        }
                        break;
                    case 1:
                        if (mapFragment == null) {
                            mapFragment = mapFragment.newInstance();
                            transaction.add(R.id.fl_container, mapFragment);
                        } else {
                            transaction.show(mapFragment);
                        }
                        break;
                    case 2:
                        if (msgFragment == null) {
                            msgFragment = msgFragment.newInstance();
                            transaction.add(R.id.fl_container, msgFragment);
                        } else {
                            transaction.show(msgFragment);
                        }
                        break;
                    case 3:
//                        if (mineFragment == null) {
//                            mineFragment = mineFragment.newInstance();
//                            transaction.add(R.id.fl_container, mineFragment);
//                        } else {
//                            transaction.show(mineFragment);
//                        }
                        break;
                    default:
                        break;
                }
                transaction.commit();
            }

            @Override
            public void onTabUnselected(int position) {//选中 -> 未选中

            }

            @Override
            public void onTabReselected(int position) {//选中 -> 选中

            }
        });

    }

    private void hideFragment(FragmentTransaction transaction) {
        if (homeFragment != null)
            transaction.hide(homeFragment);
        if (mapFragment != null)
            transaction.hide(mapFragment);
        if (msgFragment != null)
            transaction.hide(msgFragment);
        if (mineFragment != null)
            transaction.hide(mineFragment);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    // 两次退出
    private boolean isFirstExit = true;

    private void exit() {
        if (isFirstExit) {
            Toast.makeText(this, "再按一次退出!", Toast.LENGTH_SHORT).show();
            isFirstExit = false;
            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                        isFirstExit = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } else {
            ActivityUtils.getInstance().exit();
        }
    }
}
