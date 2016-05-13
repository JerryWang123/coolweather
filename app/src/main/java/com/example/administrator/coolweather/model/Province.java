package com.example.administrator.coolweather.model;

/**
 * Created by Administrator on 2016/5/11.
 */
public class Province {
    private  int id;
    private  String provinceName;
    private  String provineCode;

    public String getProvinceName() {
        return provinceName;
    }

    public String getProvineCode() {
        return provineCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public void setProvineCode(String provineCode) {
        this.provineCode = provineCode;
    }
}
