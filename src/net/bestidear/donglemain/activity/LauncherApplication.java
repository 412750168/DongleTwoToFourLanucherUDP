package net.bestidear.donglemain.activity;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Message;
import android.util.Log;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.bestidear.donglemain.model.ApplicationInfo;

public class LauncherApplication extends Application {    
    
    private static String Tag = "LauncherApplication";
    public static ArrayList<ApplicationInfo> mApplications;
    
    private String[] topApp = {"net.bestidear.jettyinput"};
    public static String[] delApp = {"com.hpplay.happyplay.aw"};
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        loadApplications(true);
        registerIntentReceivers();
    }
    
    public BroadcastReceiver packageReceiver = new BroadcastReceiver() {  
        @Override  
        public void onReceive(Context context, Intent intent) {  
            if(intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED))
            {
                //处理
                Log.v("test", "received ACTION_LOCALE_CHANGED");
                loadApplications(false);
                if(allAppActivity.getHandler()!=null){
                    Message.obtain(allAppActivity.getHandler(), allAppActivity.RESET).sendToTarget();
                    
                }
            }        
            if(intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED) ||
                    intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED) ||
                    intent.getAction().equals(Intent.ACTION_PACKAGE_CHANGED)){
                Log.d(Tag, "loadApplications()");
                loadApplications(false);
                if(allAppActivity.getHandler()!=null){
                    Message.obtain(allAppActivity.getHandler(), allAppActivity.RESET).sendToTarget();
                    
                }
            }
        }
    };
    
    public BroadcastReceiver LocaleReceiver = new BroadcastReceiver() {  
        @Override  
        public void onReceive(Context context, Intent intent) {  
            if(intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED))
            {
                //处理
                Log.v("test", "received ACTION_LOCALE_CHANGED");
                loadApplications(false);
                if(allAppActivity.getHandler()!=null){
                    Message.obtain(allAppActivity.getHandler(), allAppActivity.RESET).sendToTarget();
                    
                }
            }        
        }
    };
    private void registerIntentReceivers() {
        
        IntentFilter filter = new IntentFilter();
        
        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        registerReceiver(packageReceiver, filter);
        
        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);
        registerReceiver(LocaleReceiver, filter);
        
        
    }
    
    private void unregisterIntentReceivers(){
        unregisterReceiver(packageReceiver);
        unregisterReceiver(LocaleReceiver);
    }
    
    private void appInfoTolistop(ApplicationInfo appInfo,int index){
        for(;index > 0;index--){
            mApplications.set(index, mApplications.get(index-1));
        }
        mApplications.set(0, appInfo);
    }
    
    private void appTotop(){
        int i = 0;
        for(ApplicationInfo appInfo:mApplications){
//            Log.d(Tag, appInfo.intent.getComponent().getPackageName());
            String packageName = appInfo.intent.getComponent().getPackageName();
            for(String temp:topApp){
                if(packageName.equals(temp)){
                    appInfoTolistop(appInfo , i);
                    break;
                }
            }
            i++;
        }
    }
    
    private void appRemove(){
        int i = 0;
        ArrayList<String> delList = new ArrayList<String>();
        for(ApplicationInfo appInfo:mApplications){
            
            String packageName = appInfo.intent.getComponent().getPackageName();
            for(String temp:delApp){
//                Log.d(Tag, appInfo.intent.getComponent().getPackageName());
//                Log.d(Tag, temp);
                if(packageName.equals(temp)){
                    Log.d(Tag, "delList.add(i+"+i);
                    delList.add(i+"");
//                    break;
                }
            }
            i++;
        }
        Log.d(Tag, "delList.size():"+delList.size());
        for(int k=delList.size()-1;k>=0;k--){
            Log.d(Tag, "remove:"+delList.get(k));
            mApplications.remove(Integer.parseInt(delList.get(k)));
        }
    }
    
    /**
     * Loads the list of installed applications in mApplications.
     */
    private void loadApplications(boolean isLaunching) {
        if (isLaunching && mApplications != null) {
            return;
        }
        
        PackageManager manager = getPackageManager();
        
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        
        final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));
        
        if (apps != null) {
            final int count = apps.size();
            
            if (mApplications == null) {
                mApplications = new ArrayList<ApplicationInfo>(count);
            }
            mApplications.clear();
            
            for (int i = 0; i < count; i++) {
                ApplicationInfo application = new ApplicationInfo();
                ResolveInfo info = apps.get(i);
                application.packagename = info.activityInfo.applicationInfo.packageName;
                application.title = (String) info.loadLabel(manager);
                application.setActivity(new ComponentName(
                        info.activityInfo.applicationInfo.packageName,
                        info.activityInfo.name),
                        Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                application.icon = info.activityInfo.loadIcon(manager);
                
                mApplications.add(application);
            }
        }
        appRemove();
        appTotop();
    }
}
