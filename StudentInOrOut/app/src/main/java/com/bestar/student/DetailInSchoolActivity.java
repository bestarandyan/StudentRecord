package com.bestar.student;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bestar.student.Data.DBHelper;
import com.bestar.student.Data.PersonBean;
import com.bestar.student.Data.RequestServerFromHttp;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by bestar on 2015/2/10.
 */
public class DetailInSchoolActivity extends Activity{
    ImageView mStudentHeadImg;
    TextView mNameTv,mXueHaoTv,mClassNameTv,mInSchoolTimeTv,mTiWenTv;
    Button mOverBtn;
    String mUserId;
    DBHelper dbHelper = null;
    List<Map<String, Object>> personBeanList;
    private Bitmap mBitmap;
    String headImgUrl = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_in_school_detail);
        initView();
        initData();
    }
    private void initData(){
        mUserId = getIntent().getStringExtra("userId");
        dbHelper = DBHelper.getInstance(this);
        String sql = "select * from "+ PersonBean.tbName+" where ID = "+mUserId;
        personBeanList = dbHelper.selectRow(sql, null);
        Map<String, Object> map = personBeanList.get(0);
        mNameTv.setText(map.get("petname").toString());
        mXueHaoTv.setText(map.get("userserialnum").toString());
        mClassNameTv.setText(map.get("userdescribe").toString());
        String time = getIntent().getStringExtra("time");
        if (time!=null){
            mInSchoolTimeTv.setText(changeTimeStr(time));
        }else{
            mInSchoolTimeTv.setText("");
        }
        mTiWenTv.setText("37.8C");
        headImgUrl = RequestServerFromHttp.IMGURL+map.get("portraitpath").toString();
        new Thread(connectNet).start();
    }

    private String changeTimeStr(String time){
        String t = "";
        if (time.contains("-")){
            t = time.replace("-",".");
        }

        if(t.split(":").length == 3){
            t = t.substring(0,t.lastIndexOf(":"));
        }
        return t;
    }
    private void initView(){
        mStudentHeadImg = (ImageView) findViewById(R.id.studentHeadImg);
        mNameTv = (TextView) findViewById(R.id.UserNameTv);
        mXueHaoTv = (TextView) findViewById(R.id.xuehaoTv);
        mClassNameTv = (TextView) findViewById(R.id.ClassName);
        mInSchoolTimeTv = (TextView) findViewById(R.id.inSchoolTime);
        mTiWenTv = (TextView) findViewById(R.id.tiwenTv);
        mOverBtn = (Button) findViewById(R.id.overBtn);
        mOverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    /**
     * Get image from newwork
     * @param path The path of image
     * @return InputStream
     * @throws Exception
     */
    public InputStream getImageStream(String path) throws Exception{
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
            return conn.getInputStream();
        }
        return null;
    }

    private Runnable connectNet = new Runnable(){
        @Override
        public void run() {
            try {
                mBitmap = BitmapFactory.decodeStream(getImageStream(headImgUrl));
                connectHanlder.sendEmptyMessage(0);
            } catch (Exception e) {
                Toast.makeText(DetailInSchoolActivity.this,"无法链接网络！", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }

    };

    private Handler connectHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 更新UI，显示图片
            if (mBitmap != null) {
                mStudentHeadImg.setImageBitmap(mBitmap);// display image
            }
        }
    };
}
