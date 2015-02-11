package com.bestar.student;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bestar.student.Data.DBHelper;
import com.bestar.student.Data.FamilyBean;
import com.bestar.student.Data.PersonBean;
import com.bestar.student.Data.RequestServerFromHttp;


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by bestar on 2015/2/10.
 */
public class DetailOutSchoolActivity extends Activity{
    ImageView mStudentHeadImg;
    TextView mNameTv,mXueHaoTv,mClassNameTv,mInSchoolTimeTv,mTiWenTv;
    LinearLayout mFamilyLayout;
    String mUserId;
    DBHelper dbHelper = null;
    List<Map<String, Object>> personBeanList;
    List<Map<String, Object>> familyBeanList;
    private Bitmap mBitmap;
    String headImgUrl = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_out_school_detail);
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
        mInSchoolTimeTv.setText("2015,2,10");
        mTiWenTv.setText("37.8C");
        headImgUrl = RequestServerFromHttp.IMGURL+map.get("portraitpath").toString();
        selectFamilyData();
        new Thread(connectNet).start();
    }

    private  void selectFamilyData(){
        String sql = "select * from "+ FamilyBean.tbName+" where SchoolPersonnelID = "+mUserId;
        familyBeanList = dbHelper.selectRow(sql,null);
        if (familyBeanList!= null && familyBeanList.size() > 0){
            for(int i=0;i<familyBeanList.size();i++){
                Map<String, Object> map = familyBeanList.get(i);
                Object path = map.get("portraitpath");
                Object title = map.get("contacttitle");
                mFamilyLayout.addView(getFamilyLayout(path==null?"":path.toString(),title == null?"":title.toString()));
            }
        }
    }

    private LinearLayout getFamilyLayout(String headImgUrl,String name){
        LinearLayout view = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_family,null);
        ImageView head = (ImageView) view.findViewById(R.id.imgView);
        TextView nameTv = (TextView) view.findViewById(R.id.nameTv);
        nameTv.setText(name);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = 20;
        view.setLayoutParams(params);
        return view;
    }


    private void initView(){
        mStudentHeadImg = (ImageView) findViewById(R.id.studentHeadImg);
        mFamilyLayout = (LinearLayout) findViewById(R.id.familyLayout);
        mNameTv = (TextView) findViewById(R.id.UserNameTv);
        mXueHaoTv = (TextView) findViewById(R.id.xuehaoTv);
        mClassNameTv = (TextView) findViewById(R.id.ClassName);
        mInSchoolTimeTv = (TextView) findViewById(R.id.inSchoolTime);
        mTiWenTv = (TextView) findViewById(R.id.tiwenTv);
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
                Toast.makeText(DetailOutSchoolActivity.this, "无法链接网络！", Toast.LENGTH_SHORT).show();
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
