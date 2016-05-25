package com.example.administrator.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.administrator.coolweather.R;
import com.example.administrator.coolweather.model.City;
import com.example.administrator.coolweather.model.CoolWeatherDB;
import com.example.administrator.coolweather.model.County;
import com.example.administrator.coolweather.model.Province;
import com.example.administrator.coolweather.model.XMLDOMService;
import com.example.administrator.coolweather.util.HttpCallbackListener;
import com.example.administrator.coolweather.util.HttpUtil;
import com.example.administrator.coolweather.util.Utility;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ChooseAreaActivity extends Activity {

    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY=2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private CoolWeatherDB coolWeatherDB;
    private List<String> dataList=new ArrayList<>();
    /*
    省列表
     */
    private List<Province> provinceList;
    /*
    市列表
     */
    private List<City> cityList;
    /*
    县列表
     */
    private List<County> countyList;
    /*
    选中的省份
     */
    private Province selectedProvince;
    /*
    选中的城市
     */
    private City selectedCity;
    /*
    当前选中的级别
     */
    private int currentLevel;
    private Handler myHandler = new Handler() {

        public void handleMessage(Message msg) {
            closeProgressDialog();
            if(msg.what==1){
                queryCities();
            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView=(ListView)findViewById(R.id.list_view);
        titleText=(TextView)findViewById(R.id.title_text);
        arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(arrayAdapter);
        coolWeatherDB=CoolWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel==LEVEL_PROVINCE){
                    selectedProvince=provinceList.get(position);
                    queryCities();

                }else if(currentLevel==LEVEL_CITY){
                    selectedCity=cityList.get(position);
                    Log.e("wdd",selectedCity.getCityCode()+"---"+selectedCity.getCityName());
                    queryCounties();
                }
            }
        });
        Utility.handleProvincesResponse(coolWeatherDB);
        queryProvinces();
    }

    /*
    查询全国所有的省，从数据库查询
     */
    private void queryProvinces(){
        provinceList=coolWeatherDB.loadProvinces();
        if(provinceList.size()>0){
            dataList.clear();
            for (Province province:provinceList){
                dataList.add(province.getProvinceName());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel=LEVEL_PROVINCE;
        }else{
          Toast.makeText(ChooseAreaActivity.this,"can not load province data",Toast.LENGTH_SHORT).show();

        }
    }
    /*
    查询选中省内的所有城市，优先从数据库查询，如果没有查询到再去assets中查询
     */
    private void queryCities(){
        Log.e("wdd","queryCity");
       cityList=coolWeatherDB.loadCities(String.valueOf(selectedProvince.getId()));
        if(cityList.size()>0){
            dataList.clear();
           for (City city:cityList){
               dataList.add(city.getCityName());
           }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel=LEVEL_CITY;
        }else{
            dataList.clear();
            cityList.clear();
            queryFromAssets(selectedProvince.getProvineCode());
        }
    }
    /*
    查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCounties(){
        countyList=coolWeatherDB.loadCounty(Integer.parseInt(selectedCity.getCityCode().substring(3,7)));
        Log.e("wdd","city id ==="+Integer.parseInt(selectedCity.getCityCode().substring(3,7)));
        if(countyList.size()>0){
            dataList.clear();
            for(County county:countyList){
                dataList.add(county.getCountyName());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel=LEVEL_COUNTY;
        }else{
            queryFromAssets(selectedCity.getCityCode());
        }
    }
    /*
    根据传入的代号和类型从Assets中解析出市县级数据
     */
    private void queryFromAssets(final String code) {
        Log.e("wdd","queryFromAssets"+code);
        showProgressDialog();
        new Thread() {
            @Override
            public void run() {
                Message message = new Message();
                XMLDOMService xmlDomService=new XMLDOMService();
                InputStream is=null;
                try {
                    //获取读取文件的输入流对象
                    is = getAssets().open("last.xml");
                    //采用dom解析
                    if(is!=null) {
                        Log.e("wdd","获取输入流");
                        cityList.clear();
                        cityList= xmlDomService.parseXML(is,code,coolWeatherDB);
                        int i=1;
                        for(City c:cityList){
                            if(Integer.parseInt(c.getCityCode().substring(5,7))==i) {
                                i++;
                                coolWeatherDB.saveCity(c);
                                County county=new County();
                                county.setCountyName(c.getCityName());
                                county.setCountyCode(c.getCityCode());
                                county.setCityId(Integer.parseInt(c.getCityCode().substring(3,7)));
                                coolWeatherDB.saveCounty(county);
                                Log.e("wdd",c.getCityCode().substring(5,6)+"---"+ c.getCityCode() + "----" + c.getCityName() + "----" + c.getProvinceId());
                            }else{
                                County county=new County();
                                county.setCountyName(c.getCityName());
                                county.setCountyCode(c.getCityCode());
                                county.setCityId(Integer.parseInt(c.getCityCode().substring(3,7)));

                                coolWeatherDB.saveCounty(county);
                            }
                        }
                        message.what=1;
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
                Log.e("wdd","handler send msg");
                myHandler.sendMessage(message);
            }
        }.start();
    }
    /*
    显示进度对话框
     */
    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("正在加载。。。");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    /*
    关闭进度对话框
     */
    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }
    /*
    捕获back按键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出
     */
    public void onBackPressed(){
        if(currentLevel==LEVEL_COUNTY){
            queryCities();
        }else if(currentLevel==LEVEL_CITY){
            queryProvinces();
        }else{
            finish();
        }
    }

}
