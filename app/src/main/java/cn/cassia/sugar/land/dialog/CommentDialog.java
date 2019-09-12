package cn.cassia.sugar.land.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import cn.cassia.sugar.land.R;

/**
 * Created by qingjie on 2018-05-30.0030.
 */
public class CommentDialog extends AlertDialog {
    private BackEditText editText;
    private AppCompatTextView send;

    private onClickListener listener;

    public CommentDialog(@NonNull Context context) {
        this(context, R.style.CustomDialog); //设置Style
    }

    protected CommentDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(getContext());
    }

    private void init(final Context context) {
        setCancelable(true);//是否可以取消 (也可以在调用处设置)
        setCanceledOnTouchOutside(true);//是否点击外部消失

        View view = View.inflate(context, R.layout.dialog_comment, null);

        initView(view);
        initListener();

        setContentView(view);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        Window dialog_window = this.getWindow();
        dialog_window.setGravity(Gravity.BOTTOM);//设置显示的位置
        dialog_window.setAttributes(params);//设置显示的大小

    }

    public void setListener(onClickListener listener) {
        this.listener = listener;
    }

    private void initListener() {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focused) {
                if (focused) {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);//使得点击 Dialog 中的EditText 可以弹出键盘
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);//总是显示键盘
                }
            }
        });
        //自定义的 EditText 监听 Back 键的点击( 在软键盘显示的情况下 )
        editText.setCallBack(new BackEditText.PressBackCallBack() {
            @Override
            public void callBack() {
                dismiss();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editText.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(getContext(), "请输入内容...", Toast.LENGTH_SHORT).show();
                    return;
                }
                listener.onButtonClick(content);
                dismiss();
            }
        });
    }

    @Override
    public void dismiss() {
        View view = getCurrentFocus();
        if (view instanceof TextView) {
            InputMethodManager mInputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }
        super.dismiss();
    }

    private void initView(View view) {
        editText = view.findViewById(R.id.edit_text);
        send = view.findViewById(R.id.send);
    }

    public interface onClickListener {
        void onButtonClick(String content);
    }
}