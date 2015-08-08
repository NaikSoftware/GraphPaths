package ua.naiksoftware.graphpaths;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Vector;

public class CanvasView extends View {

    private TextPaint paint;
    private int textHeight, space;
    private Vector content = new Vector();

    public CanvasView(Context context) {
        super(context);
        init();
    }

    public CanvasView(Context context, AttributeSet attr) {
        super(context, attr);
        init();
    }

    public CanvasView(Context context, AttributeSet attr, int style) {
        super(context, attr, style);
        init();
    }

    private void init() {
        Rect rect = new Rect();
        paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(5f);
        paint.setTextSize(25f);
        paint.setTypeface(Typeface.MONOSPACE);
        paint.setColor(Color.GREEN);
        paint.getTextBounds("888", 0, 3, rect);
        textHeight = rect.height();
        space = rect.width();
    }

    public void addString(String s) {
        content.add(s);
        requestLayout();
    }

    public void addMatrix(Object[][] m, String name) {
        Matrix matrix = new Matrix(name);
        int len = m.length;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            builder.append("  ");
            for (int j = 0; j < len; j++) {
                String s = m[i][j].toString();
                builder.append("  ").append(s).append("  ");
            }
            if (i < (len - 1)) {
                builder.append("\n");
            }
        }
        matrix.setValues(builder.toString());
        content.add(matrix);
        requestLayout();
    }

    public void reset() {
        content.removeAllElements();
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int size = content.size();
        Object obj;
        Matrix m;
        int h = space;
        for (int i = 0; i < size; i++) {
            obj = content.get(i);
            if (obj instanceof String) {
                canvas.drawText((String) obj, space, h + textHeight / 2, paint);
                h += (textHeight * 2);
            } else if (obj instanceof Matrix) {
                m = (Matrix) obj;
                canvas.drawText(m.name, space, h + m.h / 2 + space, paint);
                canvas.save();
                canvas.translate(space + m.nameW + 5, h + space);
                m.matrixLayout.draw(canvas);
                canvas.restore();
                canvas.drawLine(space + m.nameW, h + space, space + m.nameW, h + m.h + space, paint);
                canvas.drawLine(space + m.nameW + m.w + 7, h + space, space + m.nameW + m.w + 7, h + m.h + space, paint);
                h += (m.h + space * 2);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = content.size();
        Object obj;
        Rect rect = new Rect();
        Matrix m;
        int w = 0, h = space;
        for (int i = 0; i < size; i++) {
            obj = content.get(i);
            if (obj instanceof String) {
                paint.getTextBounds((String) obj, 0, ((String) obj).length(), rect);
                h += (textHeight * 2);
                w = Math.max(w, rect.width());
            } else if (obj instanceof Matrix) {
                m = (Matrix) obj;
                h += (m.h + space * 2);
                w = Math.max(w, m.nameW + m.w + 10);
                //Toast.makeText(getContext(), "max(" + w + ", " + (m.w) + ")", Toast.LENGTH_SHORT).show();
            }
        }
        h += space;
        w += (space * 2);
        //Toast.makeText(getContext(), "onMeasure: w=" + w + " h=" + h, Toast.LENGTH_SHORT).show();
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
    }

    private class Matrix {

        private String values;
        String name;
        int w, h, nameW;
        StaticLayout matrixLayout;

        Matrix(String str) {
            name = str.concat(" = ");
            StaticLayout sl = new StaticLayout(name, paint, (int) Math.ceil(Layout.getDesiredWidth(name, paint)), Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
            nameW = sl.getWidth();
            //Toast.makeText(getContext(), "nameW=" + nameW, Toast.LENGTH_SHORT).show();
        }

        void setValues(String str) {
            values = str;
            matrixLayout = new StaticLayout(values, paint, (int) Math.ceil(Layout.getDesiredWidth(values, paint)), Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
            w = matrixLayout.getWidth();
            h = matrixLayout.getHeight();
            //Toast.makeText(getContext(), "w=" + w + " h=" + h, Toast.LENGTH_SHORT).show();
        }
    }
}
