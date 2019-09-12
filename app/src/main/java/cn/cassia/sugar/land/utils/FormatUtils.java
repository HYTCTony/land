package cn.cassia.sugar.land.utils;

import java.math.BigDecimal;

/**
 * Created by qingjie on 2018-06-12.0012.
 */
public class FormatUtils {
    /**
     * 保留newScale位小数
     *
     * @param d
     * @param newScale
     * @return double
     */
    public static double format(double d, int newScale) {
        double f = d;
        BigDecimal bg = new BigDecimal(f);
        double f1 = bg.setScale(newScale, BigDecimal.ROUND_HALF_UP).doubleValue();
        System.out.println(f1);
        return f1;
    }

}
