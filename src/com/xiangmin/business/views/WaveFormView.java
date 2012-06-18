
package com.xiangmin.business.views;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class WaveFormView extends View {
    private Paint paint;
    private List<Integer> mScales = new ArrayList<Integer>();
    //private List<Integer> totalScales = new ArrayList<Integer>();
    public WaveFormView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(2f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.SQUARE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        BlurMaskFilter localBlurMaskFilter = new BlurMaskFilter(1f, Blur.NORMAL);
        paint.setMaskFilter(localBlurMaskFilter);
        
    }

    public void setScale(int scale) {
    	if(mScales.size()>getWidth())
    		mScales.remove(0);
    	mScales.add(scale);
    	invalidate();
    }

    public void stop(){
    	mScales = new ArrayList<Integer>();
    	invalidate();
    }

    public void onDraw(Canvas canvas) {
           for (int i = 0; i < mScales.size(); i++) {
               canvas.drawLine(i, 120-((mScales.get(i))*10), i,120, paint);
           }
    }
}
