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

import com.bestar.student.Util.JsonData;
import com.bestar.student.Data.MyApplication;
import com.bestar.student.Data.OutSchoolBean;
import com.bestar.student.Data.RequestServerFromHttp;

/**
 * Created by bestar on 2015/2/10.
 */
public class OutSchoolActivity extends Activity implements View.OnClickListener {
    ImageButton mNumberBtn0,mNumberBtn1,mNumberBtn2,mNumberBtn3,mNumberBtn4,mNumberBtn5,mNumberBtn6,mNumberBtn7,mNumberBtn8,mNumberBtn9;
    TextView mStudentIdEt;
    Button mSubmitBtn;
    RequestServerFromHttp mServer;
    String schoolId ;
    String mUserId = "";
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
            OutSchoolBean bean = new JsonData().jsonOutSchool(msg);
            if (bean.getResult().equals("1")){
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
                startActivity(intent);
            }else{
                Toast.makeText(OutSchoolActivity.this,"出园失败！",Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };


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
    }
}
