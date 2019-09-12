package cn.cassia.sugar.land.model;

import java.io.Serializable;

/**
 * Created by qingjie on 2018-05-25.0025.
 */
public class HomeBean implements Serializable {
    public int imageResourse;
    public String title;

    public HomeBean(int imageResourse, String title) {
        this.imageResourse = imageResourse;
        this.title = title;
    }
}
