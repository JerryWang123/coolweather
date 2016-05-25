package com.example.administrator.coolweather.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.administrator.coolweather.activity.ChooseAreaActivity;
import com.example.administrator.coolweather.db.CoolWeatherOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/11.
 */
public class CoolWeatherDB {
    /*
    数据库名称
     */
    public static final String DB_NAME="Cool_Weather";
    /*
    数据库版本
     */
    public static final int VERSION=1;
    private static CoolWeatherDB coolWeatherDB;
    private SQLiteDatabase db;
    /*
    将构造方法私有化
     */
    private CoolWeatherDB(Context context){
        CoolWeatherOpenHelper dbHelper=new CoolWeatherOpenHelper(context,DB_NAME,null,VERSION);
        db=dbHelper.getWritableDatabase();
    }
    /*
    获取CoolWeatherDB的实例
     */
    public synchronized static CoolWeatherDB getInstance(Context context){
        if(coolWeatherDB==null){
            coolWeatherDB=new CoolWeatherDB(context);
        }
        return coolWeatherDB;
    }
    /*
    将province实例存储到数据库
     */
    public void saveProvince(Province province){
        if(province!=null){
            ContentValues values=new ContentValues();
            values.put("province_name",province.getProvinceName());
            values.put("province_code",province.getProvineCode());
            db.insert("Province",null,values);
        }
    }
    /*
    从数据库读取全国所有的省份信息
     */
    public List<Province> loadProvinces(){
        List<Province> list= new ArrayList<>();
        Cursor cursor=db.query("Province",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                Province province=new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvineCode(cursor.getString(cursor.getColumnIndex("province_code")));
                list.add(province);
            }while (cursor.moveToNext());
        }
        return list;
    }
    /*
    将city实例存储到数据库
     */
    public void saveCity(City city){
        if(city!=null){
            Log.e("wdd","city get");
            ContentValues values=new ContentValues();
            values.put("city_name",city.getCityName());
            values.put("city_code",city.getCityCode());
            values.put("province_id",city.getProvinceId());
            db.insert("City",null,values);
        }
    }
    /*
    从数据库读取某省份下所有城市的信息
     */
    public List<City> loadCities(String provinceId){
        List<City> list= new ArrayList<>();
        Cursor cursor=db.query("City",null,"province_id=?",new String[]{provinceId},null,null,null);
        if(cursor.moveToFirst()){
            do{
               City city=new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(provinceId);
                list.add(city);
            }while (cursor.moveToNext());
        }
        return list;
    }
    /*
       将county实例存储到数据库
        */
    public void saveCounty(County county){
        if(county!=null){
            Log.e("wdd","county get");
            ContentValues values=new ContentValues();
            values.put("county_name",county.getCountyName());
            values.put("county_code",county.getCountyCode());
            values.put("city_id",county.getCityId());
            db.insert("County",null,values);
        }
    }
    /*
    从数据库读取某城市下所有县的信息
     */
    public List<County> loadCounty(int cityCode){
        List<County> list=new ArrayList<County>();
        Cursor cursor=db.query("County",null,"city_id=?",new String[]{String.valueOf(cityCode)},null,null,null);
        if(cursor.moveToFirst()){
            do{
                County county=new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCityId(cityCode);
                list.add(county);
            }while (cursor.moveToNext());
        }
        return list;
    }
}
