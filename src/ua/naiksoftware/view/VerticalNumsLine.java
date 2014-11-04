package ua.naiksoftware.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.text.TextPaint;
import android.widget.Toast;

public class VerticalNumsLine extends View {

    private static final String tag = "VerticalNumsLine";
	private int shift, width, lHeight, lWidth, nums, interval;
	private TextPaint paint;

    public VerticalNumsLine(Context context) {
        super(context);
		init();
    }

    public VerticalNumsLine(Context context, AttributeSet attr) {
        super(context, attr);
		init();
    }

    public VerticalNumsLine(Context context, AttributeSet attr, int style) {
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
		width = rect.width();
		shift = width / 3;
	}

    @Override
    public void onDraw(Canvas canvas) {
        //canvas.drawColor(Color.DKGRAY);
        for (int j = 0; j < nums; j++) {
            canvas.drawText(String.valueOf(j + 1), shift, j * interval + interval / 2, paint);
        }
		canvas.drawLine(width, 0, width, nums * interval, paint);
    }

    @Override
    public void onMeasure(int w, int h) {
        //Log.d(tag, "onMeasure");
		//int lineHeight = MeasureSpec.getSize(h);
		setMeasuredDimension(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(interval * nums, MeasureSpec.EXACTLY));
		//Toast.makeText(getContext(), "vert h=" + lineHeight, Toast.LENGTH_SHORT).show();
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
	}
}
