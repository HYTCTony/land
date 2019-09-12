package cn.cassia.sugar.land.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.util.Util;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static com.bumptech.glide.request.RequestOptions.overrideOf;

/**
 * @author 作者 :QJ
 * @date 创建时间：2017/7/31 15:17
 */
public class GlideManager {

    public static final int IMG_TYPE_NOMAL = 1;//正常图片
    public static final int IMG_TYPE_CIRCULAR = 2;//圆形
    public static final int IMG_TYPE_ROUNDED_RECTANGLE = 3;//圆角矩形

    private static final int NOMAL = 1;
    private static final int CENTERCROP = 2;
    private static final int FITCENTER = 3;


    /**
     * 加载图标，将其变为圆形或者圆角矩形
     *
     * @param url
     * @param iv
     * @param type 图片类型
     */
    public static void glideLoader(Context context, String url, ImageView iv, int type) {
        if (context == null)
            return;
        if (Util.isOnMainThread()) {
            if (type == IMG_TYPE_NOMAL) {
                Glide.with(context).load(url).into(iv);
            } else if (type == IMG_TYPE_CIRCULAR) {
                Glide.with(context).load(url).apply(bitmapTransform(new CropCircleTransformation())).into(iv);
            } else if (type == IMG_TYPE_ROUNDED_RECTANGLE) {
                Glide.with(context).load(url).apply(bitmapTransform(new RoundedCornersTransformation(10, 10, RoundedCornersTransformation.CornerType.ALL))).into(iv);
            }
        }
    }

    public static void glideLoader(Context context, String url, int width, int height, boolean isCrop, ImageView iv, int type) {
        if (context == null)
            return;
        if (Util.isOnMainThread()) {
            if (type == IMG_TYPE_NOMAL) {
                if (isCrop) {
                    Glide.with(context).load(url).apply(overrideOf(width, height)).apply(bitmapTransform(new FitCenter())).into(iv);
                } else {
                    Glide.with(context).load(url).apply(overrideOf(width, height)).into(iv);
                }
            } else if (type == IMG_TYPE_CIRCULAR) {
                if (isCrop) {
                    Glide.with(context).load(url).apply(overrideOf(width, height)).apply(bitmapTransform(new MultiTransformation<>(new CenterCrop(), new CropCircleTransformation()))).into(iv);
                } else {
                    Glide.with(context).load(url).apply(overrideOf(width, height)).apply(bitmapTransform(new CropCircleTransformation())).into(iv);
                }
            } else if (type == IMG_TYPE_ROUNDED_RECTANGLE) {
                if (isCrop) {
                    Glide.with(context).load(url).apply(overrideOf(width, height)).apply(bitmapTransform(new MultiTransformation<Bitmap>(new FitCenter(), new RoundedCornersTransformation(10, 10, RoundedCornersTransformation.CornerType.ALL)))).into(iv);
                } else {
                    Glide.with(context).load(url).apply(overrideOf(width, height)).apply(bitmapTransform(new RoundedCornersTransformation(10, 10, RoundedCornersTransformation.CornerType.ALL))).into(iv);
                }
            }
        }
    }

    //高斯模糊
    public static void Blur(Context context, String url, ImageView iv) {
        if (context == null)
            return;
        Glide.with(context)
                .load(url)
                .apply(bitmapTransform(new BlurTransformation(14, 3)))
                .into(iv);
    }

    /**
     * 停止加载图片（Glide v4）
     *
     * @param context
     * @param iv
     */
    public static void clear(Context context, ImageView iv) {
        Glide.with(context).clear(iv);
    }
}
