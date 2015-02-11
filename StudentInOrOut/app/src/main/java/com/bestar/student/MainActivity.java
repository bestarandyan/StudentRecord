package com.bestar.student;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bestar.student.Data.DBHelper;
import com.bestar.student.Util.JsonData;
import com.bestar.student.Data.MyApplication;
import com.bestar.student.Data.RequestServerFromHttp;


public class MainActivity extends Activity implements View.OnClickListener{
    Button mInBtn,mOutBtn;
    DBHelper dbHelper = null;
    RequestServerFromHttp mServer;
    EditText mSchoolIdEt;
    ProgressDialog mProgressDialog;
    Dialog dlg =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_main);
        initView();
        initData();
        handler.sendEmptyMessageDelayed(2,500);
    }
    private void showInputDialog() {
       dlg = new AlertDialog.Builder(this).create();
        dlg.setCancelable(false);
        dlg.show();
        Window window = dlg.getWindow();
        // *** 主要就是在这里实现这种效果的.
        // 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
        window.setContentView(R.layout.dialog_main_school_id);
        // 为确认按钮添加事件,执行退出应用操作
        mSchoolIdEt = (EditText) window.findViewById(R.id.schoolEt);
        Button dialogBtn = (Button) window.findViewById(R.id.dialog_btn);
        dialogBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mSchoolIdEt.getText().toString().trim().length() > 0){
                    dlg.dismiss();
                    showProgressDialog();
                    new Thread(inSchoolRunnable).start();
                }
            }
        });
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        InputMethodManager imm = (InputMethodManager)
                getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(mSchoolIdEt, 0); //显示软键盘
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); //显示软键盘
    }

    public void showMoreShareDialog(){
        dlg = new Dialog(this, R.style.dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_main_school_id, null);
        mSchoolIdEt = (EditText) view.findViewById(R.id.schoolEt);
        Button dialogBtn = (Button) view.findViewById(R.id.dialog_btn);
        dialogBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mSchoolIdEt.getText().toString().trim().length() > 0){
                    dlg.dismiss();
                    showProgressDialog();
                    new Thread(inSchoolRunnable).start();
                }
            }
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(MyApplication.getInstance().getScreenW()*0.7f), LinearLayout.LayoutParams.WRAP_CONTENT);
        dlg.addContentView(view, params);
        dlg.setCanceledOnTouchOutside(false);
        dlg.show();
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
        setScreenInfo();

    }

    private void setScreenInfo(){
        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        MyApplication.getInstance().setScreenW(width);
        MyApplication.getInstance().setScreenH(height);
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
                dismissProgressDialog();
            }else if(msg.what == 2){
                showMoreShareDialog();
            }else  if(msg.what == -1){
                Toast.makeText(MainActivity.this, "获取数据失败！", Toast.LENGTH_SHORT).show();
                dismissProgressDialog();
            }

            super.handleMessage(msg);
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

            Intent intent = new Intent(this,InSchoolActivity.class);
            startActivity(intent);
        }else if(view == mOutBtn){
            Intent intent = new Intent(this,OutSchoolActivity.class);
            startActivity(intent);
        }
    }
}
