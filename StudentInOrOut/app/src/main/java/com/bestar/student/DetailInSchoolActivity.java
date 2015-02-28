package com.bestar.student;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bestar.student.Data.DBHelper;
import com.bestar.student.Data.FamilyBean;
import com.bestar.student.Data.InSchoolBean;
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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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
    TextView mStudentIdEt;
    Timer mTimer = null;
    RequestServerFromHttp mServer;
    int checkedCount = 0;
    int lastCount = 0;
    DisplayImageOptions options;
    String schoolId ;
    InSchoolBean bean =null;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_in_school_detail);
        initView();
        initData();
        requestData();
        mStudentIdEt.setFocusable(true);
        mStudentIdEt.requestFocus();
    }
    private void initData(){
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(DetailInSchoolActivity.this));
        mUserId = getIntent().getStringExtra("userId");
        schoolId = MyApplication.getInstance().getSchoolId();
        dbHelper = DBHelper.getInstance(this);
        mServer = new RequestServerFromHttp();
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
        if (player!=null){
            player.release();
        }
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
    private void initView(){
        mStudentIdEt = (TextView) findViewById(R.id.numberTv);
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
                    setViewFocus();
                    String sql = "select * from "+ PersonBean.tbName+" where UserCode = '"+mUserId +"'";
                    personBeanList = dbHelper.selectRow(sql, null);
                    if(personBeanList!=null && personBeanList.size()>0){
                        mUserId = personBeanList.get(0).get("id").toString();
                        new Thread(inSchoolRunnable).start();
                    }else{
                        sql = "select * from "+ FamilyBean.tbName+" where IDCard = '"+mUserId +"'";
                        personBeanList = dbHelper.selectRow(sql, null);
                        if(personBeanList!=null && personBeanList.size()>0){
                            mUserId = personBeanList.get(0).get("schoolpersonnelid").toString();
                            new Thread(inSchoolRunnable).start();
                        }else {
                            Toast.makeText(DetailInSchoolActivity.this, "查无此人,请重新刷卡！", Toast.LENGTH_LONG).show();
                        }
                    }
                }else if (str!=null && str.length() == 11 ){
                    mUserId = mStudentIdEt.getText().toString().trim();
                    String sql = "select * from "+ PersonBean.tbName+" where UserSerialNum = '"+mUserId +"'";
                    setViewFocus();
                    personBeanList = dbHelper.selectRow(sql, null);
                    if(personBeanList!=null && personBeanList.size()>0){
                        mUserId = personBeanList.get(0).get("id").toString();
                        new Thread(inSchoolRunnable).start();
                    }else {
                        Toast.makeText(DetailInSchoolActivity.this, "查无此人,请重新刷卡！", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
    Runnable inSchoolRunnable = new Runnable() {
        @Override
        public void run() {
            if (CommUtils.isOpenNetwork(DetailInSchoolActivity.this)) {
                String msg = mServer.InSchool(schoolId, mUserId);
                if (msg.equals("404")) {
                    handler.sendEmptyMessage(-2);
                } else {
                    bean = new JsonData().jsonInSchool(msg);
                    if (bean != null && bean.getResult() != null && (bean.getResult().equals("1") || bean.getInfo().contains("已入园"))) {
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
    private void setViewFocus(){
        mStudentIdEt.setText("");
        mStudentIdEt.setFocusable(true);
        mStudentIdEt.requestFocus();
    }


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
                Toast.makeText(DetailInSchoolActivity.this,msg.obj!=null?msg.obj.toString():"入园失败！",Toast.LENGTH_SHORT).show();
            }else if(msg.what == -2){
                Toast.makeText(DetailInSchoolActivity.this,"入园失败！",Toast.LENGTH_SHORT).show();
            }else if(msg.what == 8){
                Toast.makeText(DetailInSchoolActivity.this, "网络连接失败，请检查网络连接！", Toast.LENGTH_LONG).show();
            }
            mStudentIdEt.setText("");
            mStudentIdEt.setFocusable(true);
            mStudentIdEt.requestFocus();
        }
    };

    MediaPlayer player =null;
    private void player(){
        try {
            if (player !=null && !player.isPlaying()){
                player.start();
            }else{
                player = MediaPlayer.create(this,R.raw.goin);
                player.start();
            }
//            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mediaPlayer) {
//                    player.release();
//                }
//            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
