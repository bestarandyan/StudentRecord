package com.bestar.student;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bestar.student.Util.GetTimeNumberUtil;
import com.bestar.student.Util.JsonData;
import com.bestar.student.Data.MyApplication;
import com.bestar.student.Data.OutSchoolBean;
import com.bestar.student.Data.RequestServerFromHttp;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bestar on 2015/2/10.
 */
public class OutSchoolActivity extends Activity implements View.OnClickListener {
    ImageButton mNumberBtn0,mNumberBtn1,mNumberBtn2,mNumberBtn3,mNumberBtn4,mNumberBtn5,mNumberBtn6,mNumberBtn7,mNumberBtn8,mNumberBtn9;
    TextView mYearNum1,mYearNum2,mYearNum3,mYearNum4,mMonthNum1,mMonthNum2,mDayNum1,mDayNum2,mHourNum1,mHourNum2,mMinuteNum1,mMinuteNum2,mWeekTv;
    TextView mStudentIdEt;
    Button mSubmitBtn;
    RequestServerFromHttp mServer;
    String schoolId ;
    String mUserId = "";
    OutSchoolBean bean =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_out_school);
        initView();
        initData();
    }

    private void initData(){
        mServer = new RequestServerFromHttp();
        schoolId = MyApplication.getInstance().getSchoolId();
        setTime();
    }
    @Override
    public void onClick(View view) {
        if(view == mSubmitBtn){
            new Thread(inSchoolRunnable).start();
        }else if (view == mNumberBtn0){
            addValue("0");
        }else if (view == mNumberBtn1){
            addValue("1");
        }else if (view == mNumberBtn2){
            addValue("2");
        }else if (view == mNumberBtn3){
            addValue("3");
        }else if (view == mNumberBtn4){
            addValue("4");
        }else if (view == mNumberBtn5){
            addValue("5");
        }else if (view == mNumberBtn6){
            addValue("6");
        }else if (view == mNumberBtn7){
            addValue("7");
        }else if (view == mNumberBtn8){
            addValue("8");
        }else if (view == mNumberBtn9){
            addValue("9");
        }
    }

    private void addValue(String value){
        mStudentIdEt.setText(mStudentIdEt.getText().toString().trim()+value);
    }

    Runnable inSchoolRunnable = new Runnable() {
        @Override
        public void run() {
            mUserId = mStudentIdEt.getText().toString();
            String msg = mServer.OutSchool(schoolId,mUserId);
            bean = new JsonData().jsonOutSchool(msg);
            if (bean !=null && bean.getResult()!=null && bean.getResult().equals("1")){
                handler.sendEmptyMessage(1);
            }else{
                handler.sendEmptyMessage(-1);
            }
            Log.d("bestar",msg);
        }
    };

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1){
                Toast.makeText(OutSchoolActivity.this, "出园成功！", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OutSchoolActivity.this,DetailOutSchoolActivity.class);
                intent.putExtra("userId",mUserId);
                intent.putExtra("time",bean.getLeavetime());
                startActivity(intent);
            }else if(msg.what == 3){
                initTime();
            }else{
                Toast.makeText(OutSchoolActivity.this,"出园失败！",Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };
    public int[] mainBigNumber = {R.drawable.ru_x_zero,R.drawable.ru_x_one,R.drawable.ru_x_two,R.drawable.ru_x_three,R.drawable.ru_x_four,R.drawable.ru_x_five,R.drawable.ru_x_six,R.drawable.ru_x_seven,R.drawable.ru_x_eight,R.drawable.ru_x_nine};
    public int[] mainletterNumber = {R.drawable.xx_zero,R.drawable.xx_one,R.drawable.xx_two,R.drawable.xx_three,R.drawable.xx_four,R.drawable.xx_five,R.drawable.xx_six,R.drawable.xx_seven,R.drawable.xx_eight,R.drawable.xx_nine};
    public int[] weekNumber = {R.drawable.x_monday,R.drawable.x_tuesday,R.drawable.x_wednesday,R.drawable.x_thursday,R.drawable.x_friday,R.drawable.x_saturday,R.drawable.x_sunday};
    private void initTime(){
        GetTimeNumberUtil.getInstance().setmTime();
        mYearNum1.setBackgroundResource(mainletterNumber[GetTimeNumberUtil.getInstance().getNianNum1()]);
        mYearNum2.setBackgroundResource(mainletterNumber[GetTimeNumberUtil.getInstance().getNianNum2()]);
        mYearNum3.setBackgroundResource(mainletterNumber[GetTimeNumberUtil.getInstance().getNianNum3()]);
        mYearNum4.setBackgroundResource(mainletterNumber[GetTimeNumberUtil.getInstance().getNianNum4()]);
        mMonthNum1.setBackgroundResource(mainletterNumber[GetTimeNumberUtil.getInstance().getYueNum1()]);
        mMonthNum2.setBackgroundResource(mainletterNumber[GetTimeNumberUtil.getInstance().getYueNum2()]);
        mDayNum1.setBackgroundResource(mainletterNumber[GetTimeNumberUtil.getInstance().getRiNum1()]);
        mDayNum2.setBackgroundResource(mainletterNumber[GetTimeNumberUtil.getInstance().getRiNum2()]);
        mHourNum1.setBackgroundResource(mainBigNumber[GetTimeNumberUtil.getInstance().getHouseNum1()]);
        mHourNum2.setBackgroundResource(mainBigNumber[GetTimeNumberUtil.getInstance().getHouseNum2()]);
        mMinuteNum1.setBackgroundResource(mainBigNumber[GetTimeNumberUtil.getInstance().getMinuteNum1()]);
        mMinuteNum2.setBackgroundResource(mainBigNumber[GetTimeNumberUtil.getInstance().getMinuteNum2()]);
        int week = GetTimeNumberUtil.getInstance().getWeekNum();
        mWeekTv.setBackgroundResource(weekNumber[week-1]);
    }

    private void initView(){
        mStudentIdEt = (TextView) findViewById(R.id.studentIdEt);
        mSubmitBtn = (Button) findViewById(R.id.submitBtn);
        mSubmitBtn.setOnClickListener(this);
        mNumberBtn0 = (ImageButton) findViewById(R.id.numberBtn0);
        mNumberBtn1 = (ImageButton) findViewById(R.id.numberBtn1);
        mNumberBtn2 = (ImageButton) findViewById(R.id.numberBtn2);
        mNumberBtn3 = (ImageButton) findViewById(R.id.numberBtn3);
        mNumberBtn4 = (ImageButton) findViewById(R.id.numberBtn4);
        mNumberBtn5 = (ImageButton) findViewById(R.id.numberBtn5);
        mNumberBtn6 = (ImageButton) findViewById(R.id.numberBtn6);
        mNumberBtn7 = (ImageButton) findViewById(R.id.numberBtn7);
        mNumberBtn8 = (ImageButton) findViewById(R.id.numberBtn8);
        mNumberBtn9 = (ImageButton) findViewById(R.id.numberBtn9);
        mNumberBtn0.setOnClickListener(this);
        mNumberBtn1.setOnClickListener(this);
        mNumberBtn2.setOnClickListener(this);
        mNumberBtn3.setOnClickListener(this);
        mNumberBtn4.setOnClickListener(this);
        mNumberBtn5.setOnClickListener(this);
        mNumberBtn6.setOnClickListener(this);
        mNumberBtn7.setOnClickListener(this);
        mNumberBtn8.setOnClickListener(this);
        mNumberBtn9.setOnClickListener(this);
        mYearNum1 = findview(R.id.nian1tv);
        mYearNum2 = findview(R.id.nian2tv);
        mYearNum3 = findview(R.id.nian3tv);
        mYearNum4 = findview(R.id.nian4tv);
        mMonthNum1 = findview(R.id.yue1tv);
        mMonthNum2 = findview(R.id.yue2tv);
        mDayNum1 = findview(R.id.day1tv);
        mDayNum2 = findview(R.id.day2tv);
        mHourNum1 = findview(R.id.time1Tv);
        mHourNum2 = findview(R.id.time2Tv);
        mMinuteNum1 = findview(R.id.time3Tv);
        mMinuteNum2 = findview(R.id.time4Tv);
        mWeekTv = findview(R.id.weekTv);
    }
    private TextView findview(int id){
        return (TextView) findViewById(id);
    }

    private void setTime() {
        TimerTask localTimeTask = new TimerTask() {
            public void run() {
                handler.sendEmptyMessage(3);
            }
        };
        new Timer().schedule(localTimeTask, 0L, 1000L);
    }
}
