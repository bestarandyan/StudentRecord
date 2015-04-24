package com.bestar.student;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bestar.student.Data.DBHelper;
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


public class MainActivity extends Activity implements View.OnClickListener{
    Button mInBtn,mOutBtn;
    ImageButton mRefreshBtn;
    DBHelper dbHelper = null;
    RequestServerFromHttp mServer;
    EditText mSchoolIdEt;
    ProgressDialog mProgressDialog;
    Dialog dlg =null;
    TextView mYearNum1,mYearNum2,mYearNum3,mYearNum4,mMonthNum1,mMonthNum2,mDayNum1,mDayNum2,mHourNum1,mHourNum2,mMinuteNum1,mMinuteNum2,mWeekTv;
    String mSchoolId = "";
    Button mChangeBtn;
    int changeBtnClickSum = 0;//修改学校id的按钮的点击次数   只有在点击数等于3的时候才能弹出框框
    Long lastClickTime = 0L;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_main);
        initView();
        initData();

    }

    public void showMoreShareDialog(){
        dlg = new Dialog(this, R.style.dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_main_school_id, null);
        mSchoolIdEt = (EditText) view.findViewById(R.id.schoolEt);
        Button dialogBtn = (Button) view.findViewById(R.id.dialog_btn);
        dialogBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mSchoolIdEt.getText().toString().trim().length() > 0){
                    mSchoolId = mSchoolIdEt.getText().toString().trim();
                    MyApplication.getInstance().setSchoolId(mSchoolId);
                    saveData("schoolId", mSchoolId);
                    dlg.dismiss();
                    showProgressDialog();
                    new Thread(getDataRunnable).start();
                }
            }
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(MyApplication.getInstance().getScreenW()*0.5f), LinearLayout.LayoutParams.WRAP_CONTENT);
        dlg.addContentView(view, params);
        dlg.setCanceledOnTouchOutside(false);
        dlg.show();
    }
    private void showProgressDialog(){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("正在加载数据，请稍候...");
        mProgressDialog.setTitle("提示：");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void dismissProgressDialog(){
        if (mProgressDialog!=null){
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    private void initData(){
        mServer = new RequestServerFromHttp();
        dbHelper = DBHelper.getInstance(this);
        setScreenInfo();
        setTime();
        handler.sendEmptyMessageDelayed(2, 500);
    }

    private boolean hasData(){
        String sql = "select * from "+ PersonBean.tbName;
        List<Map<String,Object>> list = dbHelper.selectRow(sql,null);
        if (list!=null && list.size()>0){
            return true;
        }else{
            return false;
        }
    }

    private void setScreenInfo(){
        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        MyApplication.getInstance().setScreenW(width);
        MyApplication.getInstance().setScreenH(height);
    }
    Runnable getDataRunnable = new Runnable() {
        @Override
        public void run() {
            if (CommUtils.isOpenNetwork(MainActivity.this)){
                String msg = mServer.getStudentData(mSchoolId, "0");
                if (new JsonData().isSuccessGetInfo(msg, "Code")){
                    new JsonData().jsonSchoolData(msg,dbHelper.open());
                    handler.sendEmptyMessage(1);
                }else{
                    handler.sendEmptyMessage(-1);
                }
            }else{
                handler.sendEmptyMessage(8);
            }
        }
    };

    private void saveData(String tag,String value){
        SharedPreferences settings = MainActivity.this.getSharedPreferences(tag, 0);
        SharedPreferences.Editor localEditor = settings.edit();
        localEditor.putString(tag,value);
        localEditor.commit();
    }
    boolean isGetSuccess = false;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                isGetSuccess = true;
                saveData("getDataDate",GetTimeNumberUtil.getInstance().getDateNumber());
                Toast.makeText(MainActivity.this, "获取数据成功！", Toast.LENGTH_SHORT).show();
                dismissProgressDialog();
            }else if(msg.what == 2){
                String schoolId_old = getSharedPreferences("schoolId", 0).getString("schoolId","");
                String lastGetDataDate = getSharedPreferences("getDataDate", 0).getString("getDataDate","");
                if (lastGetDataDate.equals(GetTimeNumberUtil.getInstance().getDateNumber()) && hasData() && schoolId_old!=null && schoolId_old.length()>0){
                    isGetSuccess = true;
                    mSchoolId = schoolId_old;
                    MyApplication.getInstance().setSchoolId(mSchoolId);
                    saveData("schoolId", mSchoolId);
                    return;
                }
                getData();
            }else  if(msg.what == -1){
                Toast.makeText(MainActivity.this, "获取数据失败！请检查网络连接", Toast.LENGTH_SHORT).show();
                dismissProgressDialog();
            }else if(msg.what == 3){
                initTime();
            }else if(msg.what == 8){
                Toast.makeText(MainActivity.this, "网络连接失败，请检查网络连接！", Toast.LENGTH_LONG).show();
            }

            super.handleMessage(msg);
        }
    };
    private void initView(){
        mInBtn = (Button) findViewById(R.id.ruyuanBtn);
        mOutBtn = (Button) findViewById(R.id.chuyuanBtn);
        mRefreshBtn = (ImageButton) findViewById(R.id.refreshBtn);
        mChangeBtn = (Button) findViewById(R.id.changeIdBtn);
        mChangeBtn.setOnClickListener(this);
        mInBtn.setOnClickListener(this);
        mOutBtn.setOnClickListener(this);
        mRefreshBtn.setOnClickListener(this);
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
    public int[] mainBigNumber = {R.drawable.zero,R.drawable.one,R.drawable.two,R.drawable.three,R.drawable.four,R.drawable.five,R.drawable.six,R.drawable.seven,R.drawable.eight,R.drawable.nine};
    public int[] mainletterNumber = {R.drawable.x_zero,R.drawable.x_one,R.drawable.x_two,R.drawable.x_three,R.drawable.x_four,R.drawable.x_five,R.drawable.x_six,R.drawable.x_seven,R.drawable.x_eight,R.drawable.x_nine};
    public int[] weekNumber = {R.drawable.monday,R.drawable.tuesday,R.drawable.wednesday,R.drawable.thursday,R.drawable.friday,R.drawable.saturday,R.drawable.sunday};
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

    @Override
    public void onClick(View view) {
        if (CommUtils.isOpenNetwork(MainActivity.this)){
            if (view == mInBtn){
                if (isGetSuccess){
                    Intent intent = new Intent(this,InSchoolActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(MainActivity.this, "请获取学生信息！", Toast.LENGTH_SHORT).show();
                }
            }else if(view == mOutBtn){
                if (isGetSuccess) {
                    Intent intent = new Intent(this, OutSchoolActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(MainActivity.this, "请获取学生信息！", Toast.LENGTH_SHORT).show();
                }
            }else if(view == mRefreshBtn){
                getData();
            }else if(view == mChangeBtn){
                changeBtnClickSum++;
                long currentTime = System.currentTimeMillis();
                long cha = currentTime - lastClickTime;
                if (cha >2000){
                    changeBtnClickSum = 1;
                }
                lastClickTime = System.currentTimeMillis();
                if (changeBtnClickSum == 3){
                    changeBtnClickSum = 0;
                    showMoreShareDialog();
                }
            }
        }else{
            handler.sendEmptyMessage(8);
        }

    }

    private void getData(){
        String schoolId_old = getSharedPreferences("schoolId", 0).getString("schoolId","");
        if (schoolId_old!=null && schoolId_old.length()>0){
            mSchoolId = schoolId_old;
            MyApplication.getInstance().setSchoolId(mSchoolId);
            saveData("schoolId", mSchoolId);
            showProgressDialog();
            new Thread(getDataRunnable).start();
        }else{
            showMoreShareDialog();
        }
    }
}
