package net.bestidear.donglemain.layout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


import net.bestidear.donglelauncher.R;
import net.bestidear.donglemain.model.ApplicationInfo;
import net.bestidear.donglemain.model.ImageUtil;

import java.util.Random;


public class AllappcellLayout extends FrameLayout {
    private final String Tag = "AllappcellLayout";
    private Context context;
    private FrameLayout framlayout;
    
    private ImageView icon;
    private TextView title;
    private ImageView cellimage;
    
    
    private boolean isFocus;
    
    public AllappcellLayout(Context context) {
        this(context , null);
    }

    public AllappcellLayout(Context context, AttributeSet attrs) {
        this(context, attrs , 0);
    }

    public AllappcellLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        framlayout = (FrameLayout) View.inflate(context, R.layout.celllayoutview, this);
    
        icon = (ImageView) findViewById(R.id.icon);
        title = (TextView) findViewById(R.id.cellname);
        cellimage = (ImageView) findViewById(R.id.cellimage);
        
        this.setFocusable(true);
    }
    
   /* private static int app_normal[][] = {{R.drawable.app_blue_normal,R.drawable.app_green_normal,R.drawable.app_orange_normal}, 
                                         {R.drawable.app_yellow_normal,R.drawable.app_red_normal,R.drawable.app_blue_normal}};
                                         */
    private static int app_normal[][] = {{R.drawable.app_button_normal,R.drawable.app_button_normal,R.drawable.app_button_normal}, 
        {R.drawable.app_button_normal,R.drawable.app_button_normal,R.drawable.app_button_normal}};
    //************************************
  /*
    //应用是列表内的话用一张图片作背景
    public static String usePicApps[]={"com.android.browser","app_adsl","app_advertise",
        "com.gsoft.appinstall","net.bestidear.videoplayer","com.android.email",
        "com.farcore.videoplayer","app_moltenphoto","net.bestidear.photo",
        "tv.pps.tpad","net.bestidear.utoostore","com.airplayme.android.tvbox",
        "com.youku.tv","com.cloud.tv","com.togic.upnp",
        "air.com.fyzb.HD","com.fb.FileBrower","app_flashplayer_setting",
        "net.bestidear.FileBrower","cn.com.wasu.main","com.kandian.vodapp4tv",
        "xlcao.sohutv4","app_miracast","com.pplive.androidpad",
        "net.bestidear.jettyinput","com.tencent.qqlivehd","com.myzaker.ZAKER_HD",
        "com.qiyi.video"};
    
    public static int appPic[] = {R.drawable.app_browser,R.drawable.app_adsl,R.drawable.app_advertise,
        R.drawable.app_appinstaller,R.drawable.app_bestidear,R.drawable.app_email,
        R.drawable.app_media,R.drawable.app_moltenphoto,R.drawable.app_photo,
        R.drawable.app_pps,R.drawable.app_utoostore,R.drawable.app_yinyuetv,
        R.drawable.app_youku,R.drawable.app_cloudtv,R.drawable.app_dlna,
        R.drawable.app_fengyun,R.drawable.app_filemanager,R.drawable.app_flashplayer_setting,
        R.drawable.app_jiasiflie,R.drawable.app_huashu,R.drawable.app_kuaishou,
        R.drawable.app_longlong,R.drawable.app_miracast,R.drawable.app_pptv,
        R.drawable.app_remotecontrol,R.drawable.app_tencentvideo,R.drawable.app_zaker,
        R.drawable.app_qiyi};
  
    public static int isUsePic(String pname){
        int result = -1;
        for(int i= 0;i< usePicApps.length;i++){
            if(pname.equals(usePicApps[i])){
                result = i;
                break;
            }
        }
        return result;
    }
    
  */
    //*********************************
    public void FillInfo(int X,int Y,boolean Focusblock , final int index,final WorkplaceLayout wplo , final ApplicationInfo applicationInfo,int _newcellWidth,int _newcellHeight){
        
        isFocus = Focusblock;
        int colorX = X%app_normal[0].length;
        int colorY = Y;
        int background_id = app_normal[colorY][colorX];
            if(isFocus){
                Drawable drawable = this.getResources().getDrawable(background_id);
                Drawable drawableWithShadow = ImageUtil.drawLayoutDropShadow(context.getResources(),drawable);
                this.setBackgroundDrawable(drawableWithShadow);
//                cellimage.setBackgroundResource(background_id);
//                this.setBackgroundResource(R.drawable.shape);
//                this.setPadding(10, 10, 10, 10);
            }else{
                this.setBackgroundResource(background_id);
            }
         /*   int isUserPic = isUsePic(applicationInfo.packagename);
            if(isUserPic == -1){
                icon.setImageDrawable(applicationInfo.icon);
            }else{
                icon.setImageResource(appPic[isUserPic]);
            }*/
            icon.setImageDrawable(applicationInfo.icon);

            if(applicationInfo.title.length()>15){//应用名超�?5个字符，就用点点代替后面的字�?
                title.setText(applicationInfo.title.substring(0, 13)+"..");
            }else{
                title.setText(applicationInfo.title);
            }
        
        final Intent intent = applicationInfo.intent;
        
        this.setOnClickListener(new OnClickListener() {
        
            @Override
            public void onClick(View v) {
				cellimage.setBackgroundResource(R.drawable.app_button_focus);
                context.startActivity(intent);
            }
        });
        
        this.setOnFocusChangeListener(new OnFocusChangeListener() {
            
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
//                    Log.d(Tag, index+"");
					cellimage.setBackgroundResource(R.drawable.app_button_focus);
              /*      Message msg = Message.obtain(wplo.getHandler(), wplo.AllappFOCUS);
                    msg.arg1 = index;
                    msg.sendToTarget();*/
                }else{
					cellimage.setBackgroundResource(R.drawable.app_button_normal);
               /*     Message msg = Message.obtain(wplo.getHandler(), wplo.AllappNotFOCUS);
                    msg.arg1 = index;
                    msg.sendToTarget();*/
                }
            }
        });
        
        cellimage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cellimage.setBackgroundResource(R.drawable.app_button_focus);
                context.startActivity(intent);
			}
		});
        
        cellimage.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus){
					cellimage.setBackgroundResource(R.drawable.app_button_focus);
				/*	Message msg = Message.obtain(wplo.getHandler(), wplo.AllappFOCUS);
                    msg.arg1 = index;
                    msg.sendToTarget();*/
				}else {
					cellimage.setBackgroundResource(R.drawable.app_button_normal);
				/*	 Message msg = Message.obtain(wplo.getHandler(), wplo.AllappNotFOCUS);
	                 msg.arg1 = index;
	                 msg.sendToTarget();*/
				}

			}
		});
    }
}
