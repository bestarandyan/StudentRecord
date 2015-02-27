package com.bestar.student;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bestar.student.Data.DBHelper;
import com.bestar.student.Data.FamilyBean;
import com.bestar.student.Data.MyApplication;
import com.bestar.student.Data.OutSchoolBean;
import com.bestar.student.Data.PersonBean;
import com.bestar.student.Data.RequestServerFromHttp;
import com.bestar.student.Util.CommUtils;
import com.bestar.student.Util.JsonData;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bestar on 2015/2/10.
 */
public class DetailOutSchoolActivity extends Activity{
    ImageView mStudentHeadImg;
    TextView mNameTv,mXueHaoTv,mClassNameTv,mInSchoolTimeTv,mTiWenTv;
    LinearLayout mFamilyLayout;
    TextView mStudentIdEt;
    String mUserId;
    Button mOverBtn;
    DBHelper dbHelper = null;
    List<Map<String, Object>> personBeanList;
    List<Map<String, Object>> familyBeanList;
    private Bitmap mBitmap;
    String headImgUrl = "";
    String schoolId ;
    DisplayImageOptions options;
    OutSchoolBean bean =null;
    RequestServerFromHttp mServer;
    int checkedCount = 0;
    int lastCount = 0;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    Timer mTimer = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_out_school_detail);
        initView();
        initData();
        requestData();
        mStudentIdEt.setFocusable(true);
        mStudentIdEt.requestFocus();
    }
    private void initData(){
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(DetailOutSchoolActivity.this));
        mUserId = getIntent().getStringExtra("userId");
        mServer = new RequestServerFromHttp();
        schoolId = MyApplication.getInstance().getSchoolId();
        dbHelper = DBHelper.getInstance(this);
        String time = getIntent().getStringExtra("time");
        if (time!=null){
            mInSchoolTimeTv.setText(changeTimeStr(time));
        }else{
            mInSchoolTimeTv.setText("");
        }
    }
    private void requestData(){
        String sql = "select * from "+ PersonBean.tbName+" where ID = "+mUserId;
        personBeanList = dbHelper.selectRow(sql, null);
        Map<String, Object> map = personBeanList.get(0);
        mNameTv.setText(map.get("petname").toString());
        mXueHaoTv.setText(map.get("userserialnum").toString());
        mClassNameTv.setText(map.get("userdescribe").toString());
        mTiWenTv.setText("");
        String headStr = map.get("portraitpath").toString();
        if (headStr!=null && headStr.startsWith("/") && RequestServerFromHttp.IMGURL.endsWith("/")){
            headImgUrl = RequestServerFromHttp.IMGURL+headStr.substring(1);
        }else{
            headImgUrl = RequestServerFromHttp.IMGURL+headStr;
        }
        try{
            initOption();
            selectFamilyData();
            ImageLoader.getInstance().displayImage(headImgUrl, mStudentHeadImg, options, animateFirstListener);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            mStudentIdEt.setFocusable(true);
            mStudentIdEt.requestFocus();
            lastCount = checkedCount;
            cancleTimeTask();
            mTimer = new Timer();
            localTimeTask = new TimerTask() {
                public void run() {
                    handler.sendEmptyMessage(9);
                }
            };
            mTimer.schedule(localTimeTask, 3000L, 3000L);
        }
    }

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

    @Override
    protected void onPause() {
        cancleTimeTask();
        super.onPause();
    }

    TimerTask localTimeTask = new TimerTask() {
        public void run() {
            handler.sendEmptyMessage(9);
        }
    };



    @Override
    protected void onDestroy() {
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();
        super.onDestroy();
    }

    public void initOption(){

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_empty)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(20))
                .build();
    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                if (loadedImage!=null){
                    imageView.setImageBitmap(loadedImage);
                }
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            ImageView imageView = (ImageView) view;
            imageView.setImageResource(R.drawable.ic_empty);
            super.onLoadingFailed(imageUri, view, failReason);
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            ImageView imageView = (ImageView) view;
            imageView.setImageResource(R.drawable.ic_empty);
            super.onLoadingCancelled(imageUri, view);
        }
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

    private  void selectFamilyData(){
        mFamilyLayout.removeAllViews();
        String sql = "select * from "+ FamilyBean.tbName+" where SchoolPersonnelID = "+mUserId;
        familyBeanList = dbHelper.selectRow(sql,null);
        if (familyBeanList!= null && familyBeanList.size() > 0){
            for(int i=0;i<familyBeanList.size();i++){
                Map<String, Object> map = familyBeanList.get(i);
                Object path = map.get("portraitpath");
                Object title = map.get("contacttitle");
                if (path!=null && !path.equals("")){
                    if (path!=null && path.toString().startsWith("/") && RequestServerFromHttp.IMGURL.endsWith("/")){
                        path = RequestServerFromHttp.IMGURL+path.toString().substring(1);
                    }else{
                        path = RequestServerFromHttp.IMGURL+path.toString();
                    }
                }else{
                    path = "";
                }
                mFamilyLayout.addView(getFamilyLayout(path==null?"":path.toString(),title == null?"":title.toString()));
            }
        }
    }

    private LinearLayout getFamilyLayout(String url,String name){
        LinearLayout view = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_family,null);
        ImageView head = (ImageView) view.findViewById(R.id.imgView);
        TextView nameTv = (TextView) view.findViewById(R.id.nameTv);
        if (name!=null && name.length()>5){
            name = "未知";
        }
        nameTv.setText(name);
        ImageLoader.getInstance().displayImage(url, head, options, animateFirstListener);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = 20;
        view.setLayoutParams(params);
        return view;
    }


    private void initView(){
        mStudentIdEt = (TextView) findViewById(R.id.numberTv);
        mStudentHeadImg = (ImageView) findViewById(R.id.studentHeadImg);
        mFamilyLayout = (LinearLayout) findViewById(R.id.familyLayout);
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
                if (str!=null && str.length() == 10 ){
                    mUserId = mStudentIdEt.getText().toString().trim();
                    mStudentIdEt.setText("");
                    mStudentIdEt.setFocusable(true);
                    mStudentIdEt.requestFocus();
                    String sql = "select * from "+ PersonBean.tbName+" where UserCode = '"+mUserId +"'";
                    personBeanList = dbHelper.selectRow(sql, null);
                    if(personBeanList!=null && personBeanList.size()>0){
                        mUserId = personBeanList.get(0).get("id").toString();
                        new Thread(outSchoolRunnable).start();
                    }
                }else if (str!=null && str.length() == 11 ){
                    mUserId = mStudentIdEt.getText().toString().trim();
                    String sql = "select * from "+ PersonBean.tbName+" where UserSerialNum = '"+mUserId +"'";
                    personBeanList = dbHelper.selectRow(sql, null);
                    if(personBeanList!=null && personBeanList.size()>0){
                        mUserId = personBeanList.get(0).get("id").toString();
                        new Thread(outSchoolRunnable).start();
                    }else {
                        Toast.makeText(DetailOutSchoolActivity.this, "查无此人,请重新输入！", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    Runnable outSchoolRunnable = new Runnable() {
        @Override
        public void run() {
            if (CommUtils.isOpenNetwork(DetailOutSchoolActivity.this)) {
            checkedCount ++;
            String msg = mServer.OutSchool(schoolId,mUserId);
            if (msg.equals("404")){
                handler.sendEmptyMessage(-2);
            }else {
                bean = new JsonData().jsonOutSchool(msg);
                if (bean != null && bean.getResult() != null && bean.getResult().equals("1")) {
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

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
           if(msg.what == 9){
               if (checkedCount == lastCount){
                   finish();
               }
           }else if (msg.what == 1){
               player();
               requestData();
           }else if(msg.what == 3){
           }else if(msg.what == -1){
               Toast.makeText(DetailOutSchoolActivity.this,msg.obj!=null?msg.obj.toString():"出园失败！",Toast.LENGTH_SHORT).show();
           }else if(msg.what == -2){
               Toast.makeText(DetailOutSchoolActivity.this,"入园失败！",Toast.LENGTH_SHORT).show();
           }else if(msg.what == 8){
               Toast.makeText(DetailOutSchoolActivity.this, "网络连接失败，请检查网络连接！", Toast.LENGTH_LONG).show();
           }
            mStudentIdEt.setText("");
            mStudentIdEt.setFocusable(true);
            mStudentIdEt.requestFocus();
        }
    };


    MediaPlayer player =null;
    private void player(){
        try {
            player = MediaPlayer.create(this,R.raw.goout);
            player.start();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    player.release();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
