package cn.cassia.sugar.land.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.tencent.smtt.sdk.DownloadListener;

//内部类
public class HttpDownloadListener implements DownloadListener {

    private Context mContext;
    Message msg = new Message();
    Handler handler;
    long length = 0;

    public HttpDownloadListener(Context context, Handler handler) {
        mContext = context;
        this.handler = handler;
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        length = contentLength;
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast t = Toast.makeText(mContext, "需要SD卡。", Toast.LENGTH_LONG);
            t.setGravity(Gravity.CENTER, 0, 0);
            t.show();
            return;
        }
        DownloaderTask task = new DownloaderTask();
        task.execute(url);
    }

    //内部类
    private class DownloaderTask extends AsyncTask<String, Integer, String> {

        public DownloaderTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            String path = params[0];
            try {
                URL url = new URL(path.trim());
                //打开连接
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == urlConnection.HTTP_OK) {
                    //得到输入流
                    InputStream input = urlConnection.getInputStream();
                    writeToSDCard("kxy_land", input);
                    input.close();
                    return "kxy/" + "kxy_land.apk";
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            closeProgressDialog();
            if (result == null) {
                Toast t = Toast.makeText(mContext, "连接错误！请稍后再试！", Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
                return;
            }

            Toast t = Toast.makeText(mContext, "已保存到SD卡。", Toast.LENGTH_LONG);
            t.setGravity(Gravity.CENTER, 0, 0);
            t.show();
            File directory = Environment.getExternalStorageDirectory();
            File file = new File(directory, result);
            Log.i("tag", "Path=" + file.getAbsolutePath());

            Intent intent = getFileIntent(file);

            mContext.startActivity(intent);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            String str = String.valueOf(progress[0]);
            mDialog.setMessage("已下载 : " + progress[0] + "%");
            Logger.d("进度：" + progress[0]);
            super.onProgressUpdate(progress);
        }

        public void writeToSDCard(String fileName, InputStream input) throws IOException, InterruptedException {

            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File directory = Environment.getExternalStorageDirectory();
                File path = new File(directory, "kxy/");
                File file = new File(path, fileName + ".apk");
                if (!path.exists()) path.mkdirs();
                if (file.exists()) {
                    Log.i("tag", "The file has already exists.");
                } else {
                    file.createNewFile();
                }

                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] b = new byte[2048];
                    int j = 0;
                    long completed = 0;
                    int percent = 0;
                    while ((j = input.read(b)) != -1) {
                        fos.write(b, 0, j);
                        completed += j;
                        percent = (int) (completed * 100 / length);
                        publishProgress(percent);
                    }
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.i("tag", "NO SDCard.");
            }
        }
    }

    private ProgressDialog mDialog;

    private void showProgressDialog() {
        if (mDialog == null) {
            mDialog = new ProgressDialog(mContext);
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//设置风格为圆形进度条
            mDialog.setMessage("正在加载 ，请等待...");
            mDialog.setIndeterminate(false);//设置进度条是否为不明确
            mDialog.setCancelable(true);//设置进度条是否可以按退回键取消
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setOnDismissListener(new OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    mDialog = null;
                }
            });
            mDialog.show();

        }
    }

    private void closeProgressDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    public Intent getFileIntent(File file) {
        Uri uri = Uri.fromFile(file);
        String type = getMIMEType(file);
        Log.i("tag", "type=" + type);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uri, type);
        return intent;
    }

    private String getMIMEType(File f) {
        String type = "";
        String fName = f.getName();
        /* 取得扩展名 */
        String end = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();

        /* 依扩展名的类型决定MimeType */
        if (end.equals("pdf")) {
            type = "application/pdf";//
        } else if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") ||
                end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
            type = "audio/*";
        } else if (end.equals("3gp") || end.equals("mp4")) {
            type = "video/*";
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png") ||
                end.equals("jpeg") || end.equals("bmp")) {
            type = "image/*";
        } else if (end.equals("apk")) {
            /* android.permission.INSTALL_PACKAGES */
            type = "application/vnd.android.package-archive";
        }
//      else if(end.equals("pptx")||end.equals("ppt")){
//    	  type = "application/vnd.ms-powerpoint"; 
//      }else if(end.equals("docx")||end.equals("doc")){
//    	  type = "application/vnd.ms-word";
//      }else if(end.equals("xlsx")||end.equals("xls")){
//    	  type = "application/vnd.ms-excel";
//      }
        else {
//    	  /*如果无法直接打开，就跳出软件列表给用户选择 */  
            type = "*/*";
        }
        return type;
    }

}
