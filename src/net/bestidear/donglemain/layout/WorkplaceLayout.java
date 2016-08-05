package net.bestidear.donglemain.layout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import net.bestidear.bestidearlauncher.Launcher;
import net.bestidear.donglelauncher.R;
import net.bestidear.donglemain.model.ApplicationInfo;
import net.bestidear.donglemain.model.CellInfo;
import net.bestidear.donglemain.model.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorkplaceLayout extends RelativeLayout {
    private final String Tag = "workplaceLayout";
    
    private Context context;
    
    private CellInfo cellinfo;
    
    private int cellWidth = (int) getResources().getDimension(R.dimen.cellwidth);
    private int cellHeight = (int) getResources().getDimension(R.dimen.cellheight);
    private int cellSpace = (int) getResources().getDimension(R.dimen.cellspace);
    private int cell_OFFSIZE = (int) getResources().getDimension(R.dimen.celloffsize);
    private int worklayouttoLeft = (int) getResources().getDimension(R.dimen.worklayouttoLeft);
    private int worklayouttoTop = (int) getResources().getDimension(R.dimen.worklayouttoTop);
    
    private ArrayList<ApplicationInfo> Applications;
    private ApplicationInfo applicationInfo;
    
    public WorkplaceLayout(Context context) {
        this(context , null);
    }

    public WorkplaceLayout(Context context, AttributeSet attrs) {
        this(context, attrs , 0);
    }

    public WorkplaceLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }
    
    
    public void makeAllAppCelllayout(ArrayList<ApplicationInfo> mApplications){
        Log.d(Tag, "mApplications:"+mApplications.size());
        this.Applications = mApplications;
        int ColNum = (Applications.size())/2+(Applications.size())%2;
        int X,Y,_newcellWidth,_newcellHeight;
        _newcellWidth = cellWidth;
        _newcellHeight = cellHeight;
        for(int i=0;i<Applications.size();i++){
            applicationInfo = Applications.get(i);
            if(i<ColNum){
                X = i;
                Y = 0;
            }else{
                X = i - ColNum;
                Y = 1;
            }
            LayoutParams layoutParams = new LayoutParams(_newcellWidth, _newcellHeight);
            layoutParams.setMargins(worklayouttoLeft+X*cellWidth+cellSpace*X, worklayouttoTop+Y*cellHeight+cellSpace*Y, 0, 0);
            
            AllappcellLayout allappcelllayout = new AllappcellLayout(context);
            allappcelllayout.FillInfo(X,Y,false , i,this,applicationInfo,_newcellWidth,_newcellHeight);
            //if(applicationInfo.packagename.equals("com.hpplay.happyplay.aw"))
            //	continue;
            this.addView(allappcelllayout, i , layoutParams);
        }
    }
  
}
