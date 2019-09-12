package cn.cassia.sugar.land.spiderman;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import cn.cassia.sugar.land.R;
import cn.cassia.sugar.land.base.BaseViewActivity;

/**
 * Created by qingjie on 2018-06-28.0028.
 */
public class CrashActivity extends BaseViewActivity {

    public static final String CRASH_MODEL = "crash_model";
    @BindView(R.id.textMessage)
    AppCompatTextView textMessage;
    @BindView(R.id.tv_packageName)
    AppCompatTextView tvPackageName;
    @BindView(R.id.tv_className)
    AppCompatTextView tvClassName;
    @BindView(R.id.tv_methodName)
    AppCompatTextView tvMethodName;
    @BindView(R.id.tv_lineNumber)
    AppCompatTextView tvLineNumber;
    @BindView(R.id.tv_exceptionType)
    AppCompatTextView tvExceptionType;
    @BindView(R.id.tv_time)
    AppCompatTextView tvTime;
    @BindView(R.id.tv_model)
    AppCompatTextView tvModel;
    @BindView(R.id.tv_brand)
    AppCompatTextView tvBrand;
    @BindView(R.id.tv_version)
    AppCompatTextView tvVersion;
    @BindView(R.id.tv_fullException)
    AppCompatTextView tvFullException;
    @BindView(R.id.root)
    View root;
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private CrashModel model;
    private String json;

    @Override
    protected void initToolbar(Toolbar toolbar) {
        toolbar.setTitle("崩溃信息展示");
        super.initToolbar(toolbar);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_crash;
    }

    @Override
    protected void initView() {
        model = getIntent().getParcelableExtra(CRASH_MODEL);
        if (model == null) {
            return;
        }
        model.getEx().printStackTrace();

        tvPackageName.setText(model.getClassName());
        textMessage.setText(model.getExceptionMsg());
        tvClassName.setText(model.getFileName());
        tvMethodName.setText(model.getMethodName());
        tvLineNumber.setText(String.valueOf(model.getLineNumber()));
        tvExceptionType.setText(model.getExceptionType());
        tvFullException.setText(model.getFullException());
        tvTime.setText(df.format(model.getTime()));

        tvModel.setText(model.getDevice().getModel());
        tvBrand.setText(model.getDevice().getBrand());
        tvVersion.setText(model.getDevice().getVersion());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_crash, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.simpleCopyText:
                json = new Gson().toJson(modelToMap(model));
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (cm != null) {
                    ClipData mClipData = ClipData.newPlainText("crash", json);
                    cm.setPrimaryClip(mClipData);
                    showToast("拷贝成功");
                }
                break;
            case R.id.simpleShareText:
                json = new Gson().toJson(modelToMap(model));
                shareText(json);
                break;
            case R.id.simpleShareImage:
                if (ContextCompat.checkSelfPermission(mContext,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                        ContextCompat.checkSelfPermission(mContext,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE);
                } else {
                    shareImage();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private Map<String, Object> modelToMap(CrashModel model) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("崩溃信息", model.getExceptionMsg());
        map.put("类名", model.getFileName());
        map.put("方法", model.getMethodName());
        map.put("行数", model.getLineNumber());
        map.put("类型", model.getExceptionType());
        map.put("时间", df.format(model.getTime()));
        map.put("设备名称", model.getDevice().getModel());
        map.put("设备厂商", model.getDevice().getBrand());
        map.put("系统版本", model.getDevice().getVersion());
        map.put("全部信息", model.getFullException());
        return map;
    }

    private void shareText(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "崩溃信息：");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, "分享到"));
    }

    private static final int REQUEST_CODE = 110;

    private void requestPermission(String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        //判断请求码，确定当前申请的权限
        if (requestCode == REQUEST_CODE) {
            //判断权限是否申请通过
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //授权成功
                shareImage();
            } else {
                //授权失败
                showToast("请授予SD卡权限才能分享图片");
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public Bitmap getBitmapByView(NestedScrollView view) {
        if (view == null) return null;
        int height = 0;
        for (int i = 0; i < view.getChildCount(); i++) {
            height += view.getChildAt(i).getHeight();
        }
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRGB(255, 255, 255);
        view.draw(canvas);
        return bitmap;
    }

    private File BitmapToFile(Bitmap bitmap) {
        if (bitmap == null) return null;
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .getAbsolutePath();
        File imageFile = new File(path, "spiderMan-" + df.format(model.getTime()) + ".jpg");
        try {
            FileOutputStream out = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            bitmap.recycle();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageFile;
    }

    private void shareImage() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            showToast("未插入sd卡");
            return;
        }
        File file = BitmapToFile(getBitmapByView(findViewById(R.id.scrollView)));
        if (file == null || !file.exists()) {
            showToast("图片文件不存在");
            return;
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri contentUri = FileProvider.getUriForFile(getApplicationContext(),
                    getApplicationContext().getPackageName() + ".spidermanfileprovider", file);
            intent.putExtra(Intent.EXTRA_STREAM, contentUri);
        } else {
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "分享图片"));
    }

    private void showToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
}
