package com.bestar.student.Util;


import android.app.Application;
import android.text.format.Time;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bestar on 2015/2/12.
 */
public class GetTimeNumberUtil{
    public static GetTimeNumberUtil getTimeNumberUtil = null;
    public GetTimeNumberUtil() {
        // TODO Auto-generated constructor stub
    }
    public static GetTimeNumberUtil getInstance(){
        if(getTimeNumberUtil == null){
            getTimeNumberUtil = new GetTimeNumberUtil();
        }
        return getTimeNumberUtil;
    }

    public Time mTime;

    public Time getmTime() {
        return mTime;
    }

    public void setmTime() {
        this.mTime = new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。
        this.mTime.setToNow(); // 取得系统时间。
    }

    public int getNianNum1() {
        return mTime.year/1000;
    }
    public int getNianNum2() {
        return (mTime.year/100)%10;
    }
    public int getNianNum3() {
        return (mTime.year/10)%10;
    }
    public int getNianNum4() {
        return mTime.year%10;
    }

    public String getDateNumber(){
        return mTime.year+""+mTime.month+""+mTime.monthDay;
    }

    public int getYueNum1(){
        if (mTime.month+1<10){
            return 0;
        }else{
            return 1;
        }
    }
    public int getYueNum2(){
        if (mTime.month+1<10){
            return mTime.month+1;
        }else{
            return (mTime.month+1)%10;
        }
    }
    public int getRiNum1(){
        if (mTime.monthDay<10){
            return 0;
        }else{
            return mTime.monthDay/10;
        }
    }
    public int getRiNum2(){
        if (mTime.monthDay<10){
            return mTime.monthDay;
        }else{
            return mTime.monthDay%10;
        }
    }

    public int getHouseNum1(){
        if (mTime.hour<10){
            return 0;
        }else{
            return mTime.hour/10;
        }
    }

    public int getHouseNum2(){
        if (mTime.hour<10){
            return mTime.hour;
        }else{
            return mTime.hour%10;
        }
    }


    public int getMinuteNum1(){
        if (mTime.minute<10){
            return 0;
        }else{
            return mTime.minute/10;
        }
    }

    public int getMinuteNum2(){
        if (mTime.minute<10){
            return mTime.minute;
        }else{
            return mTime.minute%10;
        }
    }

    public int getWeekNum(){
        return mTime.weekDay;
    }


}
