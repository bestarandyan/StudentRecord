/**
 * 
 */
package com.bestar.student.Util;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.bestar.student.Data.FamilyBean;
import com.bestar.student.Data.InSchoolBean;
import com.bestar.student.Data.OutSchoolBean;
import com.bestar.student.Data.PersonBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author bestar
 * @createDate 2015、2、10
 * 用json
 *
 */
public class JsonData {

	public void jsonSchoolData(String msg,SQLiteDatabase database){
        String data = getJsonObject(msg,"Data");
        String jsonStudent = getJsonObject(data,"pe");
        String jsonFimally = getJsonObject(data,"ct");
        jsonStudents(jsonStudent,database);
        jsonFamily(jsonFimally,database);
	}

    public boolean isSuccessGetInfo(String msg,String tag){
        boolean isSuccess = false;
        String code = getJsonObject(msg,tag);
        if (code != null && code.equals("0")){
            isSuccess = true;
        }else{
            isSuccess = false;
        }
        return isSuccess;
    }

    private void jsonStudents(String msg,SQLiteDatabase database){
        Type listType = new TypeToken<LinkedList<PersonBean>>(){}.getType();
        Gson gson = new Gson();
        LinkedList<PersonBean> list;
        PersonBean bean;
        list = gson.fromJson(msg, listType);
        if(list!=null && list.size()>0){
            for(Iterator<PersonBean> iterator = list.iterator();iterator.hasNext();){
                bean = iterator.next();
                ContentValues contentValues = new ContentValues();
                contentValues.put("ClassID", bean.getClassID());
                contentValues.put("ID", bean.getID());
                contentValues.put("OrgDepID", bean.getOrgDepID());
                contentValues.put("OrgClassID", bean.getOrgClassID());
                contentValues.put("RealName", bean.getRealName());
                contentValues.put("UserID", bean.getUserID());
                contentValues.put("PetName", bean.getPetName());
                contentValues.put("PortraitPath", bean.getPortraitPath());
                contentValues.put("UserCode", bean.getUserCode());
                contentValues.put("UserDescribe", bean.getUserDescribe());
                contentValues.put("UserSerialNum", bean.getUserSerialNum());
                int a = database.update(PersonBean.tbName, contentValues, "ID=?", new String[]{bean.getID()+""});
                if(a == 0){
                    database.insert(PersonBean.tbName, null, contentValues);
                }
            }
        }
    }
    private void jsonFamily(String msg,SQLiteDatabase database){
        Type listType = new TypeToken<LinkedList<FamilyBean>>(){}.getType();
        Gson gson = new Gson();
        LinkedList<FamilyBean> list;
        FamilyBean bean;
        list = gson.fromJson(msg, listType);
        if(list!=null && list.size()>0){
            for(Iterator<FamilyBean> iterator = list.iterator();iterator.hasNext();){
                bean = iterator.next();
                ContentValues contentValues = new ContentValues();
                contentValues.put("ID", bean.getID());
                contentValues.put("SchoolPersonnelID", bean.getSchoolPersonnelID());
                contentValues.put("ContactTitle", bean.getContactTitle());
                contentValues.put("ContactName", bean.getContactName());
                contentValues.put("ContactTel", bean.getContactTel());
                contentValues.put("IDCard", bean.getIDCard());
                contentValues.put("PortraitPath", bean.getPortraitPath());
                int a = database.update(FamilyBean.tbName, contentValues, "ID=?", new String[]{bean.getID()+""});
                if(a == 0){
                    database.insert(FamilyBean.tbName, null, contentValues);
                }
            }
        }
    }

    public String getJsonObject(String msg,String json) {
        String jsonStr = "";
        try {
            JSONObject jsonObject = new JSONObject(msg);
            jsonStr = jsonObject.getString(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonStr;
    }
	/**
	 * 入园
	 * @param msg
	 */
	public InSchoolBean jsonInSchool(String msg) {
        InSchoolBean bean =null;
        try {
            JSONObject jsonObject = new JSONObject(msg);
            bean = new InSchoolBean();
            bean.setResult(jsonObject.getString("result"));
            bean.setInfo(jsonObject.getString("info"));
            bean.setEntertime(jsonObject.getString("entertime"));
            bean.setLeavetime(jsonObject.getString("leavetime"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bean;
    }
	
	/**
	 * 出园
	 * @param msg
	 */
	public OutSchoolBean jsonOutSchool(String msg) {
        OutSchoolBean bean = null;
        try {
            JSONObject jsonObject = new JSONObject(msg);
            bean = new OutSchoolBean();
            bean.setResult(jsonObject.getString("result"));
            bean.setInfo(jsonObject.getString("info"));
            bean.setEntertime(jsonObject.getString("entertime"));
            bean.setLeavetime(jsonObject.getString("leavetime"));
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return bean;
    }


	
	/**
	 * 判断服务器返回值是否为无数据的格式
	 * @param str
	 * @return
	 */
	public boolean isNoData(String str){
		String format = "\\d+\\,20[1,2]\\d-((0?[0-9])|1[0-2])-(([0-2][0-9])|3[0-1])(\\s([0-1][0-9]|2[0-4]):([0-5][0-9])(:([0-5][0-9]))?)?";
		Matcher matcher = Pattern.compile(format).matcher(str);
		while(matcher.find()){
			return true;
		}
		return false;
	}
}
