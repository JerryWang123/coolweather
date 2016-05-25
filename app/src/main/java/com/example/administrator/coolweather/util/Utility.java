package com.example.administrator.coolweather.util;

import android.text.TextUtils;

import com.example.administrator.coolweather.model.City;
import com.example.administrator.coolweather.model.CoolWeatherDB;
import com.example.administrator.coolweather.model.County;
import com.example.administrator.coolweather.model.Province;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;

/**
 * Created by Administrator on 2016/5/13.
 */
public class Utility {

   public static String[] provinceNames=new String[]{"北京","上海","天津","重庆","黑龙江","吉林","辽宁","内蒙古","河北","山西","陕西","山东","新疆","西藏","青海",
        "甘肃","宁夏","河南","江苏","湖北","浙江","安徽","福建","江西","湖南","贵州","四川","广东","云南","广西","海南","香港","澳门","台湾"};
    /*
    解析和处理服务器返回的省级数据
     */
    public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB){
       for(int i=0;i<provinceNames.length;i++){
           Province province=new Province();
           province.setProvinceName(provinceNames[i]);
           province.setProvineCode(String.format("%02d", i+1));
           coolWeatherDB.saveProvince(province);
       }
        return true;
    }
}
