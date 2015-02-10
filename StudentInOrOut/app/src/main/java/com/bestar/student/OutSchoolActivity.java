package com.bestar.student;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bestar.student.Data.InSchoolBean;
import com.bestar.student.Data.JsonData;
import com.bestar.student.Data.MyApplication;
import com.bestar.student.Data.OutSchoolBean;
import com.bestar.student.Data.RequestServerFromHttp;

/**
 * Created by bestar on 2015/2/10.
 */
public class OutSchoolActivity extends Activity implements View.OnClickListener {
    EditText mStudentIdEt;
    Button mSubmitBtn;
    RequestServerFromHttp mServer;
    String schoolId ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_out_school);
        initView();
        initData();
    }
    private void initView(){
        mStudentIdEt = (EditText) findViewById(R.id.studentIdEt);
        mSubmitBtn = (Button) findViewById(R.id.submitBtn);
        mSubmitBtn.setOnClickListener(this);
    }
    private void initData(){
        mServer = new RequestServerFromHttp();
        schoolId = MyApplication.getInstance().getSchoolId();
    }
    @Override
    public void onClick(View view) {
        if(view == mSubmitBtn){
            new Thread(inSchoolRunnable).start();
        }
    }

    Runnable inSchoolRunnable = new Runnable() {
        @Override
        public void run() {
            String userId = mStudentIdEt.getText().toString();
            String msg = mServer.OutSchool(schoolId,userId);
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
            }else{
                Toast.makeText(OutSchoolActivity.this,"出园失败！",Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };
}
