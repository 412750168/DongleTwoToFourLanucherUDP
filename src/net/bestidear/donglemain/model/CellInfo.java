package net.bestidear.donglemain.model;

import android.graphics.drawable.Drawable;

public class CellInfo {
    private String cellName;
    private String packageName;
    private String className;
    private int type;//0:app 1:url 2:allapp
    private String background;
    private int OrderX;
    private int OrderY;
    private int spanX;
    private int spanY;
    private boolean isInstall;
    private boolean isNotDisplay;
    private int Left;
    private int Top;
    private Drawable appicon;

    public CellInfo(){
        
    }
    
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public CellInfo(String cellName , 
                    String packageName ,
                    String className ,
                    int OrderX ,
                    int OrderY ,
                    int spanX,
                    int spanY,
                    int type,
                    String background,
                    String focusbackground,
                    Drawable appicon){
        setCellInfo(cellName,packageName,className,OrderX,OrderY,spanX,spanY,type,background,focusbackground,appicon);
    }
    
    public void setCellInfo(String cellName ,
                            String packageName ,
                            String className ,
                            int OrderX ,
                            int OrderY ,
                            int spanX,
                            int spanY,
                            int type,
                            String background,
                            String focusbackground,
                            Drawable appicon){
        this.setCellName(cellName);
        this.setPackageName(packageName);
        this.setClassName(className);
        this.setOrderX(OrderX);
        this.setOrderY(OrderY);
        this.setSpanX(spanX);
        this.setSpanY(spanY);
        this.setType(type);
        this.setBackground(background);
        this.setAppicon(appicon);
    }
    public int getSpanY() {
        return spanY;
    }

    public void setSpanY(int spanY) {
        this.spanY = spanY;
    }

    public int getSpanX() {
        return spanX;
    }

    public void setSpanX(int spanX) {
        this.spanX = spanX;
    }

    public int getOrderX() {
        return OrderX;
    }

    public void setOrderX(int OrderX) {
        this.OrderX = OrderX;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCellName() {
        return cellName;
    }

    public void setCellName(String cellName) {
        this.cellName = cellName;
    }

    public boolean isInstall() {
        return isInstall;
    }

    public void setInstall(boolean isInstall) {
        this.isInstall = isInstall;
    }

    public int getLeft() {
        return Left;
    }

    public void setLeft(int left) {
        Left = left;
    }

    public int getOrderY() {
        return OrderY;
    }

    public void setOrderY(int orderY) {
        OrderY = orderY;
    }

    public int getTop() {
        return Top;
    }

    public void setTop(int top) {
        Top = top;
    }

    public Drawable getAppicon() {
        return appicon;
    }

    public void setAppicon(Drawable appicon) {
        this.appicon = appicon;
    }

    public boolean isNotDisplay() {
        return isNotDisplay;
    }

    public void SetNotDisplay(boolean isNotDisplay) {
        this.isNotDisplay = isNotDisplay;
    }
}
