package cn.cassia.sugar.land.model;

import java.io.Serializable;

public class DeviceBean implements Serializable {
    public String name;
    public String address;
    public boolean active;

    public DeviceBean(String name,String address,boolean active){
        this.name = name;
        this.address = address;
        this.active = active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
