package com.bestar.student;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bestar.student.Data.DBHelper;
import com.bestar.student.Data.JsonData;
import com.bestar.student.Data.PersonBean;
import com.bestar.student.Data.RequestServerFromHttp;

import java.util.List;
import java.util.Map;


public class MainActivity extends Activity implements View.OnClickListener{
    Button mInBtn,mOutBtn;
    DBHelper dbHelper = null;
    RequestServerFromHttp mServer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_main);
        initView();
        initData();
    }

    private void initData(){
        mServer = new RequestServerFromHttp();
        dbHelper = DBHelper.getInstance(this);

    }
    Runnable inSchoolRunnable = new Runnable() {
        @Override
        public void run() {
            String msg = mServer.getStudentData("54", "0");
            new JsonData().jsonSchoolData(msg,dbHelper.open());
            String sql = "select * from "+ PersonBean.tbName;
            List<Map<String, Object>> listPerson = dbHelper.selectRow(sql, null);
            Log.d("bestar", listPerson.size()+"");
        }
    };
    private void initView(){
        mInBtn = (Button) findViewById(R.id.ruyuanBtn);
        mOutBtn = (Button) findViewById(R.id.chuyuanBtn);
        mInBtn.setOnClickListener(this);
        mOutBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view == mInBtn){
            new Thread(inSchoolRunnable).start();
//            Intent intent = new Intent(this,InSchoolActivity.class);
//            startActivity(intent);
        }else if(view == mOutBtn){
            Intent intent = new Intent(this,OutSchoolActivity.class);
            startActivity(intent);
        }
    }
}
