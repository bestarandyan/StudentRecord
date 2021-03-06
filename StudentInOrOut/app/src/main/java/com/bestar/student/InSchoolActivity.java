package com.bestar.student;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bestar.student.Data.DBHelper;
import com.bestar.student.Data.FamilyBean;
import com.bestar.student.Data.InSchoolBean;
import com.bestar.student.Data.MyApplication;
import com.bestar.student.Data.PersonBean;
import com.bestar.student.Data.RequestServerFromHttp;
import com.bestar.student.Util.CommUtils;
import com.bestar.student.Util.GetTimeNumberUtil;
import com.bestar.student.Util.JsonData;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bestar on 2015/2/10.
 */
public class InSchoolActivity extends Activity implements View.OnClickListener {
    TextView mStudentIdEt,mCurrentIdEt;
    Button mSubmitBtn,mCancelBtn;
    DBHelper dbHelper = null;
    List<Map<String, Object>> personBeanList;
    ImageButton mNumberBtn0,mNumberBtn1,mNumberBtn2,mNumberBtn3,mNumberBtn4,mNumberBtn5,mNumberBtn6,mNumberBtn7,mNumberBtn8,mNumberBtn9;
    TextView mYearNum1,mYearNum2,mYearNum3,mYearNum4,mMonthNum1,mMonthNum2,mDayNum1,mDayNum2,mHourNum1,mHourNum2,mMinuteNum1,mMinuteNum2,mWeekTv;
    RequestServerFromHttp mServer;
    String schoolId ;
    InSchoolBean bean =null;
    String mUserId = "";
    boolean isInput = false;//判断是否手动输入过
    Timer mTimer = new Timer();
    private void cancleTimeTask(){
        if (mTimer!=null){
            mTimer.cancel();
            mTimer = null;
            if (localTimeTask!=null){
                localTimeTask.cancel();
                localTimeTask = null;
            }
        }
    }
    TimerTask localTimeTask = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_in_school);
        initView();
        mServer = new RequestServerFromHttp();
        initData();
    }
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

    private void initData(){
        schoolId = MyApplication.getInstance().getSchoolId();
        setTime();
    }

    @Override
    public void onClick(View view) {
        if(view == mSubmitBtn){
            mUserId = mStudentIdEt.getText().toString();
            String sql = "select * from "+ PersonBean.tbName+" where ID = '"+mUserId+"'";
            personBeanList = dbHelper.selectRow(sql, null);
            if (personBeanList!=null && personBeanList.size()>0){
                new Thread(inSchoolRunnable).start();
            }else{
                sql = "select * from "+ PersonBean.tbName+" where UserCode = '"+mUserId +"'";
                personBeanList = dbHelper.selectRow(sql, null);
                if(personBeanList!=null && personBeanList.size()>0){
                    mUserId = personBeanList.get(0).get("id").toString();
                    new Thread(inSchoolRunnable).start();
                }else{
                    sql = "select * from "+ PersonBean.tbName+" where UserSerialNum = '"+mUserId +"'";
                    personBeanList = dbHelper.selectRow(sql, null);
                    if(personBeanList!=null && personBeanList.size()>0){
                        mUserId = personBeanList.get(0).get("id").toString();
                        new Thread(inSchoolRunnable).start();
                    }else{
                        sql = "select * from "+ FamilyBean.tbName+" where IDCard = '"+mUserId +"'";
                        personBeanList = dbHelper.selectRow(sql, null);
                        if(personBeanList!=null && personBeanList.size()>0){
                            mUserId = personBeanList.get(0).get("schoolpersonnelid").toString();
                            new Thread(inSchoolRunnable).start();
                        }else{
                            sql = "select * from "+ FamilyBean.tbName+" where ContactTel = '"+mUserId +"'";
                            personBeanList = dbHelper.selectRow(sql, null);
                            if(personBeanList!=null && personBeanList.size()>0){
                                mUserId = personBeanList.get(0).get("schoolpersonnelid").toString();
                                new Thread(inSchoolRunnable).start();
                            }else {
                                Toast.makeText(InSchoolActivity.this, "查无此人,请重新输入！", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }

            }
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
        }else if(view == mCancelBtn){
            deleteValue();
        }
    }

    private void addValue(String value){
        isInput =  true;
        mStudentIdEt.setText(mStudentIdEt.getText().toString().trim()+value);
    }

    private void deleteValue(){
        String value = mStudentIdEt.getText().toString().trim();
        if (value!=null && value.length()>0){
            isInput = true;
            mStudentIdEt.setText(value.substring(0,value.length()-1));
        }else{
            isInput = false;
        }

    }
    MediaPlayer player =null;
    private void player(){
        try {
            if (player !=null && !player.isPlaying()){
                player.start();
            }else{
                player = MediaPlayer.create(this,R.raw.goin);
                player.start();
            }
//            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mediaPlayer) {
//                    player.release();
//                }
//            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mStudentIdEt.setFocusable(true);
        mStudentIdEt.requestFocus();
    }

    Runnable inSchoolRunnable = new Runnable() {
        @Override
        public void run() {
            if (CommUtils.isOpenNetwork(InSchoolActivity.this)) {
                String msg = mServer.InSchool(schoolId, mUserId);
                if (msg.equals("404")) {
                    handler.sendEmptyMessage(-2);
                } else {
                    bean = new JsonData().jsonInSchool(msg);
                    if (bean != null && bean.getResult() != null && (bean.getResult().equals("1") || bean.getInfo().contains("已入园"))) {
                        handler.sendEmptyMessage(1);
                    } else {
                        Message m = new Message();
                        m.what = -1;
                        m.obj = bean.getInfo();
                        handler.sendMessage(m);
                    }
                }
            }else{
                handler.sendEmptyMessage(8);
            }
        }
    };

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1){
                setViewFocus();
                player();
                Intent intent = new Intent(InSchoolActivity.this,DetailInSchoolActivity.class);
                intent.putExtra("userId",mUserId);
                intent.putExtra("time",bean.getEntertime());
                mStudentIdEt.setText("");
                startActivityForResult(intent, 1);
            }else if(msg.what == 3){
                initTime();
            }else if(msg.what == 4){
              String  currentId = mStudentIdEt.getText().toString().trim();
              if (currentId.length() == mUserId.length()){
                  cancleTimeTask();
                  setViewFocus();
              }
            }else if(msg.what == 5){
                isInput = false;
                String  currentId = mStudentIdEt.getText().toString().trim();
                mStudentIdEt.setText(currentId.substring(0,10));

            }else if(msg.what == -1){
                setViewFocus();
                Toast.makeText(InSchoolActivity.this,msg.obj!=null?msg.obj.toString():"入园失败！",Toast.LENGTH_SHORT).show();
            }else if(msg.what == -2){
                setViewFocus();
                Toast.makeText(InSchoolActivity.this,"入园失败！",Toast.LENGTH_SHORT).show();
            }else if(msg.what == 8){
                setViewFocus();
                Toast.makeText(InSchoolActivity.this, "网络连接失败，请检查网络连接！", Toast.LENGTH_LONG).show();
            }
            super.handleMessage(msg);
        }
    };


    private void initView(){
        dbHelper = DBHelper.getInstance(this);
        mStudentIdEt = (TextView) findViewById(R.id.studentIdEt);
//        mCurrentIdEt = (TextView) findViewById(R.id.currentId);
        mSubmitBtn = (Button) findViewById(R.id.submitBtn);
        mCancelBtn = (Button) findViewById(R.id.cancleBtn);
        mSubmitBtn.setOnClickListener(this);
        mCancelBtn.setOnClickListener(this);
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
        mStudentIdEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = mStudentIdEt.getText().toString().trim();
                if (isInput && str.length()<=11){
                    return;
                }
                if (str!=null && str.length() == 10 ){
                    mUserId = mStudentIdEt.getText().toString().trim();
                    String sql = "select * from "+ PersonBean.tbName+" where UserCode = '"+mUserId +"'";
                    personBeanList = dbHelper.selectRow(sql, null);
                    if(personBeanList!=null && personBeanList.size()>0){
                        mUserId = personBeanList.get(0).get("id").toString();
                        setViewFocus();
                        new Thread(inSchoolRunnable).start();
                    }else{
                        sql = "select * from "+ FamilyBean.tbName+" where IDCard = '"+mUserId +"'";
                        personBeanList = dbHelper.selectRow(sql, null);
                        if(personBeanList!=null && personBeanList.size()>0){
                            mUserId = personBeanList.get(0).get("schoolpersonnelid").toString();
                            setViewFocus();
                            new Thread(inSchoolRunnable).start();
                        }else {
                            Toast.makeText(InSchoolActivity.this, "查无此人,请重新输入！", Toast.LENGTH_LONG).show();
                        }
                    }
                }else if (str!=null && str.length() == 11 ){
                    mUserId = mStudentIdEt.getText().toString().trim();
                    String sql = "select * from "+ PersonBean.tbName+" where UserSerialNum = '"+mUserId +"'";
                    personBeanList = dbHelper.selectRow(sql, null);
                    if(personBeanList!=null && personBeanList.size()>0){
                        mUserId = personBeanList.get(0).get("id").toString();
                        new Thread(inSchoolRunnable).start();
                        setViewFocus();
                    }else{
                        sql = "select * from "+ FamilyBean.tbName+" where ContactTel = '"+mUserId +"'";
                        personBeanList = dbHelper.selectRow(sql, null);
                        if(personBeanList!=null && personBeanList.size()>0){
                            mUserId = personBeanList.get(0).get("schoolpersonnelid").toString();
                            new Thread(inSchoolRunnable).start();
                            setViewFocus();
                        }else {
                            Toast.makeText(InSchoolActivity.this, "查无此人,请重新输入！", Toast.LENGTH_LONG).show();
                            startTimeTask(4);
                        }
                    }
                }else if(str!=null && str.length() > 11){
                    mUserId = mStudentIdEt.getText().toString().trim();
                    startTimeTask(5);
                }
            }
        });
    }

    private void startTimeTask(final int h){
        cancleTimeTask();
        mTimer = new Timer();
        localTimeTask = new TimerTask() {
            public void run() {
                handler.sendEmptyMessage(h);
            }
        };
        mTimer.schedule(localTimeTask, 1000L);
    }
    private TextView findview(int id){
        return (TextView) findViewById(id);
    }
    private void setViewFocus(){
        mStudentIdEt.setText("");
        mStudentIdEt.setFocusable(true);
        mStudentIdEt.requestFocus();
        isInput = false;
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
