package ua.naiksoftware.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

public class HorizontalNumsLine extends View {

    private static final String tag = "HorizontalNumsLine";
    private int strHeight, shift, interval, nums, lWidth, lHeight;
	private TextPaint paint;
    
    public HorizontalNumsLine(Context context) {
        super(context);
        init();
    }

    public HorizontalNumsLine(Context context, AttributeSet attr) {
        super(context, attr);
        init();
    }

    public HorizontalNumsLine(Context context, AttributeSet attr, int style) {
        super(context, attr, style);
        init();
    }

    private void init() {
        Rect rect = new Rect();
		paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		paint.setStrokeWidth(5);
		paint.setTextSize(30);
		paint.setColor(Color.GRAY);
        paint.getTextBounds("888", 0, 3, rect);
        strHeight = rect.height() * 2;
		shift = (int)(rect.width() * 1.5f);
    }

    @Override
    public void onDraw(Canvas canvas) {
        //canvas.drawColor(Color.DKGRAY);
        for (int j = 0; j < nums; j++) {
            canvas.drawText(String.valueOf(j + 1), shift + j * interval, lHeight * 0.6f, paint);
        }
		canvas.drawLine(0, strHeight, interval * nums + shift, strHeight, paint);
    }

    @Override
    public void onMeasure(int w, int h) {
		//Log.d(tag, "onMeasure");
        //int lineWidth = MeasureSpec.getSize(w);
		setMeasuredDimension(MeasureSpec.makeMeasureSpec(interval * nums + shift, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(strHeight, MeasureSpec.EXACTLY));
    }
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		lHeight = h;
		lWidth = w;
	}
	
	public void setInterval(int interval) {
		this.interval = interval;
		invalidate();
	}
	
	public void setNums(int nums) {
		this.nums = nums;
		invalidate();
		requestLayout();
	}
}
