package net.bestidear.donglemain.model;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class ImageUtil {   
    
    //放大缩小图片   
    public static Bitmap zoomBitmap(Bitmap bitmap,int w,int h){   
        int width = bitmap.getWidth();   
        int height = bitmap.getHeight();   
        Matrix matrix = new Matrix();   
        float scaleWidht = ((float)w / width);   
        float scaleHeight = ((float)h / height);   
        matrix.postScale(scaleWidht, scaleHeight);   
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);   
        return newbmp;   
    }   

        
     //获得圆角图片的方�?  
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap,float roundPx){   
           
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap   
                .getHeight(), Config.ARGB_8888);   
        Canvas canvas = new Canvas(output);   
    
        final int color = 0xff424242;   
        final Paint paint = new Paint();   
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());   
        final RectF rectF = new RectF(rect);   
    
        paint.setAntiAlias(true);   
        canvas.drawARGB(0, 0, 0, 0);   
        paint.setColor(color);   
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);   
    
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));   
        canvas.drawBitmap(bitmap, rect, rect, paint);   
    
        return output;   
    }   
    //获得带�?影的图片方法   
    public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap){   
        final int reflectionGap = 4;   
        int width = bitmap.getWidth();   
        int height = bitmap.getHeight();   
           
        Matrix matrix = new Matrix();   
        matrix.preScale(1, -1);   
           
        Bitmap reflectionImage = Bitmap.createBitmap(bitmap,    
                0, height/2, width, height/2, matrix, false);   
           
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height/2), Config.ARGB_8888);   
           
        Canvas canvas = new Canvas(bitmapWithReflection);   
        canvas.drawBitmap(bitmap, 0, 0, null);   
        Paint deafalutPaint = new Paint();   
        canvas.drawRect(0, height,width,height + reflectionGap,   
                deafalutPaint);   
           
        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);   
           
        Paint paint = new Paint();   
        LinearGradient shader = new LinearGradient(0,   
                bitmap.getHeight(), 0, bitmapWithReflection.getHeight()   
                + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);   
        paint.setShader(shader);   
        // Set the Transfer mode to be porter duff and destination in   
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));   
        // Draw a rectangle using the paint with our linear gradient   
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()   
                + reflectionGap, paint);   
    
        return bitmapWithReflection;   
    }   
    
    public static Drawable drawLayoutDropShadow(Resources res,Drawable drawable){
        
        BlurMaskFilter blurFilter = new BlurMaskFilter(30, BlurMaskFilter.Blur.NORMAL);  
        Paint shadowPaint = new Paint();  
        shadowPaint.setMaskFilter(blurFilter);  
        int[] offsetXY =new int[2];  
        Bitmap originalBitmap = drawableToBitmap(drawable);  
        Bitmap shadowBitmap = originalBitmap.extractAlpha(shadowPaint,offsetXY);  
        Bitmap shadowImage32 = shadowBitmap.copy(Bitmap.Config.ARGB_8888, true);  
        if(android.os.Build.VERSION.SDK_INT>=19&&!shadowImage32.isPremultiplied()){
            shadowImage32.setPremultiplied(true);
        }
        
        Canvas c = new Canvas(shadowImage32);  
        c.drawBitmap(originalBitmap, -offsetXY[0], -offsetXY[1], null);  
        Drawable d =new BitmapDrawable(res,shadowImage32);  
        return d;
      }
      
      public static Bitmap drawableToBitmap(Drawable drawable) {
          Bitmap bitmap = Bitmap
          .createBitmap(
          drawable.getIntrinsicWidth(),
          drawable.getIntrinsicHeight(),
          drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
          : Bitmap.Config.RGB_565);
          Canvas canvas = new Canvas(bitmap);
          canvas.setBitmap(bitmap);
          drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
          drawable.draw(canvas);
          return bitmap;
      }
       
      
}  
