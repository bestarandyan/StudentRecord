package com.bestar.student;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;

import com.bestar.student.Data.JsonData;
import com.bestar.student.Data.RequestServerFromHttp;

/**
 * Created by bestar on 2015/2/10.
 */
public class InSchoolActivity extends Activity implements View.OnClickListener {
    EditText mStudentIdEt;
    Button mSubmitBtn;
    RequestServerFromHttp mServer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_in_school);
        initView();
        mServer = new RequestServerFromHttp();
    }
    private void initView(){
        mStudentIdEt = (EditText) findViewById(R.id.studentIdEt);
        mSubmitBtn = (Button) findViewById(R.id.submitBtn);
        mSubmitBtn.setOnClickListener(this);
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
//            String msg = mServer.InSchool("54","4764");
            String msg = mServer.getStudentData("54", "0");
            Log.d("bestar",msg);
        }
    };
}
