package com.bestar.student.Data;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
public class RequestServerFromHttp {
	public static final String SERVER_ADDRESS = "http://pingban.iiyey.com/default.aspx";//外网服务器总接口地址
	public static final String APPKEY = "e071ee980e80e9b5";//
	public static final String IMGURL = "http://servercomponents.iiyey.com/";//


 	public String InSchool(String schoolId,String persionId){
 		String msgString = "";
 		List<NameValuePair> params = new ArrayList<NameValuePair>();
 		params.add(new BasicNameValuePair("method", "Door.SchoolIn"));
 		params.add(new BasicNameValuePair("appKey", APPKEY));
 		params.add(new BasicNameValuePair("SchoolID", schoolId));
 		params.add(new BasicNameValuePair("Personid", persionId));
 		params.add(new BasicNameValuePair("Remarks","0"));
 		msgString = getData(SERVER_ADDRESS, params);
 		return msgString;
 	}

	public String OutSchool(String schoolId,String persionId){
 		String msgString = "";
 		List<NameValuePair> params = new ArrayList<NameValuePair>();
 		params.add(new BasicNameValuePair("method", "Door.SchoolIn"));
 		params.add(new BasicNameValuePair("appKey", APPKEY));
 		params.add(new BasicNameValuePair("SchoolID", schoolId));
 		params.add(new BasicNameValuePair("Personid", persionId));
 		params.add(new BasicNameValuePair("Remarks","1"));
 		msgString = getData(SERVER_ADDRESS, params);
 		return msgString;
 	}

	public String getStudentData(String schoolId,String userCode){
 		String msgString = "";
 		List<NameValuePair> params = new ArrayList<NameValuePair>();
 		params.add(new BasicNameValuePair("method", "Door.SchoolPersonnel_List"));
 		params.add(new BasicNameValuePair("appKey", APPKEY));
 		params.add(new BasicNameValuePair("SchoolID", schoolId));
 		params.add(new BasicNameValuePair("UserCode", userCode));
 		msgString = getData(SERVER_ADDRESS, params);
 		return msgString;
 	}


	
	/**
     * 访问服务器的总函数
     * @author 刘星星
     * @param urlString 接口地址
     * @param list 参数集合
     * @return 服务器返回值
     */
    public static  String getData(String urlString,List<NameValuePair> list){
    	String msgString = "";
    	HttpPost httpPost = new HttpPost(urlString);
    	try {
			HttpEntity httpEntity = new UrlEncodedFormEntity(list,HTTP.UTF_8);
			httpPost.setEntity(httpEntity);
			HttpClient httpClient = new DefaultHttpClient();
			//设置超时时间
			httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			
			msgString = EntityUtils.toString(httpResponse.getEntity());
			System.out.println(msgString);
			if(msgString.length()>0 && msgString.substring(0, 1).equals("<")){
				msgString = "404";
			}
    	} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			msgString = "404";
			e.printStackTrace();
		}
    	return msgString;
    }
    

}
