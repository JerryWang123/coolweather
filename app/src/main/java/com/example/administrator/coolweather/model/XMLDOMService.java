package com.example.administrator.coolweather.model;

import android.util.Log;

import com.example.administrator.coolweather.activity.ChooseAreaActivity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2016/5/24.
 */
public class XMLDOMService {
    public List<City> parseXML(InputStream is,String code,CoolWeatherDB coolWeatherDB) {
        Log.i("wdd","XMLDOMService");
        List<City> list = new ArrayList<City>();
// 创建DOM工厂对象
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
// DocumentBuilder对象
            DocumentBuilder builder = factory.newDocumentBuilder();
// 获取文档对象///
            Document document = builder.parse(is);
// 获取文档对象的root
            Element root = document.getDocumentElement();
// 获取persons根节点中所有的person节点对象
            NodeList cityNodes = root.getElementsByTagName("item");
            list.clear();
// 遍历所有的person节点
            for (int i =0; i <cityNodes.getLength(); i++) {
                City city=new City();
// 根据item(index)获取该索引对应的节点对象
                Element cityNode = (Element) cityNodes.item(i); // 具体的city节点
// 获取该节点下面的所有字节点
                NodeList cityChildNodes = cityNode.getChildNodes();
// 遍历city的字节点
                for (int index =0; index < cityChildNodes.getLength(); index++) {
// 获取子节点
                    Node node = cityChildNodes.item(index);
// 判断node节点是否是元素节点
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
//把节点转换成元素节点
                        Element element = (Element) node;
//判断元素节点是否是name元素节点
                        if("cityid".equals(element.getNodeName())){
                            String id=element.getFirstChild().getNodeValue();

                            if(id.matches("101"+code+"[0-9]{4}")){
                            //    Log.e("wdd","get cityId===province code"+code);
                                city.setCityCode(id);

                            }else{
                                city=null;
                                continue;
                            }
                        }else if ("citynm".equals(element.getNodeName())) {
                            city.setCityName(element.getFirstChild().getNodeValue());
                            city.setProvinceId(code);
                        }

                    }
                }
// 把city对象加入到集合中
              if(city!=null){
                  list.add(city);
              }

            }
//关闭输入流
            is.close();
        } catch (Exception e) {
            Log.i("wdd","eaaaaaaaaaaaaaaaaaaaaa");
// TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.e("wdd","lenght=="+list.size());
        Collections.sort(list, new SortByCode());
        return list;
    }
    class SortByCode implements Comparator {
        public int compare(Object o1, Object o2) {
            City s1 = (City) o1;
            City s2 = (City) o2;
            return s1.getCityCode().compareTo(s2.getCityCode());
        }
    }
}
