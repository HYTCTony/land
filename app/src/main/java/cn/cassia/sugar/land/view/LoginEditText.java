package cn.cassia.sugar.land.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import cn.cassia.sugar.land.R;

/**
 * 带清除按钮的输入框
 * 禁止复制粘贴
 */

public class LoginEditText extends AppCompatEditText implements
        View.OnFocusChangeListener, TextWatcher {
    private Drawable mClearDrawable; // 删除按钮的引用
    private Drawable mDrawable; // 密码显示隐藏按钮的引用
    private boolean hasFoucs; // 控件是否有焦点
    private boolean hasShow = false;//是否显示明文

    boolean canPaste() {
        return false;
    }

    boolean canCut() {
        return false;
    }

    boolean canCopy() {
        return false;
    }

    boolean canSelectAllText() {
        return false;
    }

    boolean canSelectText() {
        return false;
    }

    boolean textCanBeSelected() {
        return false;
    }


    public LoginEditText(Context context) {
        this(context, null);
    }

    public LoginEditText(Context context, AttributeSet attrs) {
        // 这里构造方法也很重要，不加这个很多属性不能再XML里面定义
        this(context, attrs, android.R.attr.editTextStyle);


    }

    public LoginEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLongClickable(false);
        setTextIsSelectable(false);
        setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {

                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                return false;
            }
        });
        init();
    }

    private void init() {
        // 获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
//        mClearDrawable = getCompoundDrawables()[2];

//        if (mClearDrawable == null) {
//            mClearDrawable = getResources().getDrawable(R.drawable.et_clear); // 设置图片
//        }
//        
//        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(),
//                mClearDrawable.getIntrinsicHeight());

        mDrawable = getCompoundDrawables()[2];
        if (mDrawable == null) {
            mDrawable = getResources().getDrawable(R.mipmap.ic_eye); // 设置图片
        }
        mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(),
                mDrawable.getIntrinsicHeight());
        //setPadding(20, 15, 20, 15);
        // 默认设置隐藏图标
        setIconVisible(false);


        // 默认设置隐藏图标
        setClearIconVisible(false);
        // 设置焦点改变的监听
        setOnFocusChangeListener(this);
        // 设置输入框里面内容发生改变的监听
        addTextChangedListener(this);
    }

    /**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件 当我们按下的位置 在 EditText的宽度 -
     * 图标到控件右边的间距 - 图标的宽度 和 EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向就没有考虑
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_UP) {

            if (getCompoundDrawables()[2] != null) {
                boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
                        && (event.getX() < ((getWidth() - getPaddingRight())));
                if (touchable) hasShow = !hasShow;
                if (hasShow) {
                    this.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    this.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        }
        return super.onTouchEvent(event);
    }

    // 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFoucs = hasFocus;
        if (hasFocus) {
//            setClearIconVisible(getText().length() > 0);
            setIconVisible(getText().length() > 0);
        } else {
//            setClearIconVisible(false);
            setIconVisible(false);
        }
    }

    // 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
    protected void setClearIconVisible(boolean visible) {
        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0],
                getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }

    // 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
    protected void setIconVisible(boolean visible) {
        Drawable right = visible ? mDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0],
                getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }

    // 当输入框里面内容发生变化的时候回调的方法
    @Override
    public void onTextChanged(CharSequence s, int start, int count, int after) {
        if (hasFoucs) {
//            setClearIconVisible(s.length() > 0);
            setIconVisible(s.length() > 0);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}