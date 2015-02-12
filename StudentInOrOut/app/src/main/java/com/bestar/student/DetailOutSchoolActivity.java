package com.bestar.student;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.bestar.student.Data.PersonBean;
import com.bestar.student.Data.RequestServerFromHttp;
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

/**
 * Created by bestar on 2015/2/10.
 */
public class DetailOutSchoolActivity extends Activity{
    ImageView mStudentHeadImg;
    TextView mNameTv,mXueHaoTv,mClassNameTv,mInSchoolTimeTv,mTiWenTv;
    LinearLayout mFamilyLayout;
    String mUserId;
    Button mOverBtn;
    DBHelper dbHelper = null;
    List<Map<String, Object>> personBeanList;
    List<Map<String, Object>> familyBeanList;
    private Bitmap mBitmap;
    String headImgUrl = "";
    DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_out_school_detail);
        initView();
        initData();
    }
    private void initData(){
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(DetailOutSchoolActivity.this));
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
            handler.sendEmptyMessageDelayed(1,3000);
        }
    }

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


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
           if(msg.what == 1){
               finish();
           }
        }
    };
}
