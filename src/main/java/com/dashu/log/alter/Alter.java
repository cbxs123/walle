package com.dashu.log.alter;

import com.dashu.log.classification.dao.ErrorLogTypeRepository;
import com.dashu.log.classification.dao.FilterErrorTypeRepository;
import com.dashu.log.entity.ErrorLogType;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @Description
 * @Author: xuyouchang
 * @Date 2018/8/30 下午7:24
 **/
@Component
public class Alter {

    private static final Logger logger = LoggerFactory.getLogger(Alter.class);
    @Autowired
    private AlterFilter alterFilter;

    /**
     * 发出告警
     * @param alterInfoList
     */
    public void alterAction(List<ErrorLogType> alterInfoList){

        if (alterInfoList.size()!=0) {
            //告警
            for (ErrorLogType alterInfo : alterInfoList) {

                if (alterFilter.alterFilter(alterInfo)==false) {
                    continue;
                }
                String businessName = alterInfo.getBusinessName();
                String logLevel = alterInfo.getLogLevel();
                String hostname = alterInfo.getHostName();
//                String message = alterInfo.getMessage();
                String keywords=alterInfo.getKeywords();
                Integer errroTypeId=alterInfo.getId();
                Date curTime = new Date(System.currentTimeMillis());
                String altername=curTime.toString();
                String info="\n"+"日志等级："+logLevel+"\n"+"业务名称："+businessName+"\n"+"主机名："+hostname+"\n"+"错误信息："+keywords+"\n";
                String altermessage=AlterInfoConstruction(altername,info,errroTypeId);

                String url="http://10.1.3.124:9093/api/v1/alerts";
                HttpPost httpPost = new HttpPost(url);
                CloseableHttpClient client = HttpClients.createDefault();
                StringEntity entity = new StringEntity(altermessage,"utf-8");//解决中文乱码问题
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                httpPost.setEntity(entity);
                try {
                    client.execute(httpPost);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 构造告警信息
     * @param altername
     * @param info
     * @return
     */
    public static String AlterInfoConstruction(String altername,String info,Integer errorTypeId){
        JSONArray jsonArray=new JSONArray();


        JSONObject labelsObject=new JSONObject();
        labelsObject.put("altername",altername);
        labelsObject.put("dev","错误ID:");
        labelsObject.put( "instance",errorTypeId.toString());

        JSONObject labels=new JSONObject();
        labels.put("labels",labelsObject);

        JSONObject annotationsObject=new JSONObject();
        annotationsObject.put("info",info);
//        annotationsObject.put("summary","本条告警到此结束。");
        labels.put("annotations",annotationsObject);

        jsonArray.put(labels);
        return jsonArray.toString();

    }

}