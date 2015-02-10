package com.bestar.student;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bestar.student.Data.DBHelper;
import com.bestar.student.Util.JsonData;
import com.bestar.student.Data.MyApplication;
import com.bestar.student.Data.RequestServerFromHttp;


public class MainActivity extends Activity implements View.OnClickListener{
    Button mInBtn,mOutBtn,mSubmitBtn;
    DBHelper dbHelper = null;
    RequestServerFromHttp mServer;
    EditText mSchoolIdEt;
    ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_main);
        initView();
        initData();
    }

    private void showProgressDialog(){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("正在加载数据，请稍候...");
        mProgressDialog.setTitle("提示：");
        mProgressDialog.show();
    }

    private void dismissProgressDialog(){
        mProgressDialog.dismiss();
        mProgressDialog = null;
    }

    private void initData(){
        mServer = new RequestServerFromHttp();
        dbHelper = DBHelper.getInstance(this);

    }
    Runnable inSchoolRunnable = new Runnable() {
        @Override
        public void run() {
            String schoolId = mSchoolIdEt.getText().toString().trim();
            MyApplication.getInstance().setSchoolId(schoolId);
            String msg = mServer.getStudentData(schoolId, "0");
            if (new JsonData().isSuccessGetInfo(msg, "Code")){
                new JsonData().jsonSchoolData(msg,dbHelper.open());
                handler.sendEmptyMessage(1);
            }else{
                handler.sendEmptyMessage(-1);
            }

//            String sql = "select * from "+ PersonBean.tbName;
//            List<Map<String, Object>> listPerson = dbHelper.selectRow(sql, null);
//            Log.d("bestar", listPerson.size()+"");
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Toast.makeText(MainActivity.this, "获取数据成功！", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "获取数据失败！", Toast.LENGTH_SHORT).show();
            }
            dismissProgressDialog();
            super.handleMessage(msg);
        }
    };
    private void initView(){
        mSchoolIdEt = (EditText) findViewById(R.id.schoolIdEt);
        mInBtn = (Button) findViewById(R.id.ruyuanBtn);
        mOutBtn = (Button) findViewById(R.id.chuyuanBtn);
        mSubmitBtn = (Button) findViewById(R.id.submitBtn);
        mInBtn.setOnClickListener(this);
        mSubmitBtn.setOnClickListener(this);
        mOutBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view == mInBtn){
            Intent intent = new Intent(this,InSchoolActivity.class);
            startActivity(intent);
        }else if(view == mOutBtn){
            Intent intent = new Intent(this,OutSchoolActivity.class);
            startActivity(intent);
        }else if(view == mSubmitBtn){
            showProgressDialog();
            new Thread(inSchoolRunnable).start();
        }
    }
}
