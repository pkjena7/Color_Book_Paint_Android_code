package com.templatevilla.colorbookspaint.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;


public class DialogTextViewView extends TextView {

    private Integer strokeColor;
    private Paint.Join strokeJoin;
    private float strokeMiter;


    public DialogTextViewView(Context context) {
        super(context);
        init(null);
    }

    public DialogTextViewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public DialogTextViewView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public void init(AttributeSet attrs) {



    }

    public void setStroke(float width, int color, Paint.Join join, float miter) {
        strokeColor = color;
        strokeJoin = join;
        strokeMiter = miter;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int restoreColor = this.getCurrentTextColor();
        if (strokeColor != null) {
            TextPaint paint = this.getPaint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(strokeJoin);
            paint.setStrokeMiter(strokeMiter);
            this.setTextColor(strokeColor);
//            paint.setStrokeWidth(strokeWidth);
            paint.setStrokeWidth(0.3f);
            super.onDraw(canvas);
            paint.setStyle(Paint.Style.FILL);
            this.setTextColor(restoreColor);
        }
    }
}
