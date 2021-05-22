package com.templatevilla.colorbookspaint.ui;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.templatevilla.colorbookspaint.textview.Vector2D;
import com.templatevilla.colorbookspaint.textview.ViewGestureDetector;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DrawingArea extends View implements View.OnTouchListener {
    private Canvas mCanvas;
    private Path mPath;
    private Paint mPaint;
    private boolean isZoom, isZoomableStart;
    boolean isScaled = false;
    int n = 2;
    private boolean mIsTextPinchZoomable;
    private ImageView imageView;
    private ArrayList<Path> paths = new ArrayList<>();
    private ArrayList<Path> undonePaths = new ArrayList<>();
    private ArrayList<Paint> paints = new ArrayList<>();
    private ArrayList<Paint> undoPaints = new ArrayList<>();
    private ViewGestureDetector mScaleGestureDetector;
    int color = Color.RED, smooth = 0, stroke = 10, eraseStroke = 10;
    boolean isErase = false;
    boolean isPaint = false;
    private boolean isMagicBrush;
    int count;
    private List<Vector2> mPositions = new ArrayList<>(100);
    private List<Bitmap> mBitmaps = new ArrayList<>();
    private Bitmap mBitmapBrush;
    DrawTouch drawTouch;


    public DrawingArea(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private static final class Vector2 {
        Vector2(float x, float y) {
            this.x = x;
            this.y = y;
        }

        final float x;
        final float y;
    }


    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }


    private Bitmap getBitmapFromAsset(String strName) {
        AssetManager assetManager = getContext().getAssets();
        InputStream istr = null;
        try {
            istr = assetManager.open(strName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(istr);
    }

    public void setDrawingListener(DrawTouch drawTouch) {
        this.drawTouch = drawTouch;

    }

    public interface DrawTouch {
        void onDrawTouch();
    }


    public void init() {


//        mBitmapBrush = getBitmapFromAsset("magicbrush/pattern_1.png");

        Log.e("mBitmapBrush", "" + mBitmapBrush);

//        mBitmapBrush = getResizedBitmap(mBitmapBrush, 50, 50);


        mIsTextPinchZoomable = true;
        mScaleGestureDetector = new ViewGestureDetector(new ScaleGestureListener());

        setFocusable(true);
        setFocusableInTouchMode(true);
        setOnTouchListener(this);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(stroke);
        mCanvas = new Canvas();
        mPath = new Path();

        if (smooth > 15) {
            mPaint.setMaskFilter(new BlurMaskFilter(smooth, BlurMaskFilter.Blur.NORMAL));
        } else {
            mPaint.setMaskFilter(null);
        }
        paths.add(mPath);
        paints.add(mPaint);

    }

    public DrawingArea(Context context) {
        super(context);

    }

    public void setMagicPattern(String magicBrush) {
        Log.e("magicBrush", "" + magicBrush);
        mBitmapBrush = getBitmapFromAsset("magicbrush/" + magicBrush);
        mBitmapBrush = getResizedBitmap(mBitmapBrush, 50, 50);
    }


    public void isMagicBrush(boolean isMagicBrush) {
        this.isMagicBrush = isMagicBrush;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (paths.size() == paints.size()) {
            for (int i = 0; i < paths.size(); i++) {
                canvas.drawPath(paths.get(i), paints.get(i));
            }
        }

        Paint rectanglePaint = new Paint();
        rectanglePaint.setARGB(255, 0, 0, 0);
        rectanglePaint.setStrokeWidth(2);
        rectanglePaint.setColor(Color.BLUE);
        rectanglePaint.setStyle(Paint.Style.STROKE);


        if (mPositions.size() > 0) {

            for (int i = 0; i < mPositions.size(); i++) {
                Vector2 pos = mPositions.get(i);
                canvas.drawBitmap(mBitmaps.get(i), pos.x, pos.y, rectanglePaint);
            }


        }


    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
        setPaint();
    }


    public void setEnableView(boolean isPaint) {
        this.isPaint = isPaint;

    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
        setPaint();
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        mPath = new Path();
        mPaint = new Paint();

        setPaint();
        mCanvas.drawPath(mPath, mPaint);
        paths.add(mPath);
        paints.add(mPaint);
    }


    public void setPaint() {

        mPaint.setAntiAlias(true);
        mPaint.setDither(true);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        if (isErase) {
            mPaint.setStrokeWidth(eraseStroke);
            mPaint.setColor(Color.WHITE);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
            mPaint.setMaskFilter(null);

        } else {
            mPaint.setStrokeWidth(stroke);
            mPaint.setColor(color);
            setLayerType(LAYER_TYPE_SOFTWARE, null);
            if (smooth > 15) {
                mPaint.setMaskFilter(new BlurMaskFilter(smooth, BlurMaskFilter.Blur.NORMAL));
            } else {
                mPaint.setMaskFilter(null);
            }

        }


    }

    public void clearMagicBrush() {
        mPositions.clear();
        mBitmaps.clear();
        invalidate();
    }


    public boolean getMagicBrush() {
        return isMagicBrush;
    }


    public void undoOperation() {
        if (paths.size() > 0) {
            undonePaths.add(paths.remove(paths.size() - 1));
            undoPaints.add(paints.remove(paints.size() - 1));
            invalidate();
        }
    }

    public void redoOperation() {
        if (undonePaths.size() > 0) {
            paths.add(undonePaths.remove(undonePaths.size() - 1));
            paints.add(undoPaints.remove(undoPaints.size() - 1));
            invalidate();
        }
        //toast the user
    }

    public int getRedoSize() {
        return undonePaths.size();
    }

    public int getUndoSize() {
        return paths.size();
    }


    public void setDrawingColor(int color) {
        this.color = color;
        //toast the user
    }

    public void setDrawingStroke(int stroke) {
        this.stroke = stroke;
        //toast the user
    }

    public boolean getZoomEnabled() {
        return isZoom;
    }


    public void clearDrawing() {
        paths.clear();
        paints.clear();
        undonePaths.clear();
        undoPaints.clear();
        invalidate();
    }


    public void enableEraser(boolean isErase) {
        this.isErase = isErase;
    }

    public void setDrawingSmooth(int smooth) {
        this.smooth = smooth;
        //toast the user
    }

    public void setEraserStroke(int eraseStroke) {
        this.eraseStroke = eraseStroke;
        //toast the user
    }


    public void setZoomEnabled(boolean Zoom, ImageView imageView) {
        isZoom = Zoom;
        this.imageView = imageView;
        invalidate();
    }

    public void setZoomEnabledStart(boolean Zoom) {
        isZoomableStart = Zoom;
        invalidate();
    }


    @Override
    public boolean onTouch(View arg0, MotionEvent event) {

        if (drawTouch != null) {
            drawTouch.onDrawTouch();
        }
        if (!isZoom) {
            if (isPaint) {
                if (isMagicBrush) {
                    final float x = event.getX();
                    final float y = event.getY();

                    int maskedAction = event.getActionMasked();

                    switch (maskedAction) {
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_POINTER_DOWN: {
                            break;
                        }
                        case MotionEvent.ACTION_MOVE: { // a pointer was moved
                            count++;
                            if (count == 8) {
                                count = 0;
                                Log.e("count", "" + count);
                                mPositions.add(new Vector2(x, y));
                                mBitmaps.add(mBitmapBrush);
                            }
                            break;
                        }
                        case MotionEvent.ACTION_UP:
                            mPositions.add(new Vector2(x, y));
                            mBitmaps.add(mBitmapBrush);
                            count = 0;
                        case MotionEvent.ACTION_POINTER_UP:
                        case MotionEvent.ACTION_CANCEL: {
                            count = 0;
                            break;
                        }
                    }
                    invalidate();
                    return true;
                } else {
                    float x = event.getX();
                    float y = event.getY();

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            touch_start(x, y);
                            invalidate();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            touch_move(x, y);
                            invalidate();
                            break;
                        case MotionEvent.ACTION_UP:
                            touch_up();
                            invalidate();
                            break;
                    }
                }
                return true;
            } else {
                return false;
            }
        } else {

            if (isZoomableStart) {
                if (imageView != null) {
                    mScaleGestureDetector.onTouchEvent(imageView, event);
                    mScaleGestureDetector.onTouchEvent(this, event);
                }
            }
            return true;
        }
    }



    private class ScaleGestureListener extends ViewGestureDetector.SimpleOnScaleGestureListener {

        private float mPivotX;
        private float mPivotY;
        private Vector2D mPrevSpanVector = new Vector2D();

        @Override
        public void onTouchStart() {
            super.onTouchStart();
        }

        @Override
        public boolean onScaleBegin(View view, ViewGestureDetector detector) {
            Log.e("onScale", "onScaleEnd");
            isScaled = true;
            mPivotX = detector.getFocusX();
            mPivotY = detector.getFocusY();
            mPrevSpanVector.set(detector.getCurrentSpanVector());
            return mIsTextPinchZoomable;
        }

        @Override
        public boolean onScale(View view, ViewGestureDetector detector) {
            Log.e("onScaleTrue", "onScaleEnd");
            isScaled = true;
            TransformInfo info = new TransformInfo();
            info.deltaScale = detector.getScaleFactor();
            info.deltaAngle = 0.0f;
            info.deltaX = detector.getFocusX() - mPivotX;
            info.deltaY = detector.getFocusY() - mPivotY;
            info.pivotX = mPivotX;
            info.pivotY = mPivotY;
            info.minimumScale = 0.5f;
            info.maximumScale = 10.0f;
            move(view, info);
            return !mIsTextPinchZoomable;
        }


        @Override
        public void onScaleEnd(View view, ViewGestureDetector detector) {
            Log.e("onScaleEnd", "onScaleEnd");
            super.onScaleEnd(view, detector);
            isScaled = false;
            n = 2;

        }
    }

    private static void move(View view, TransformInfo info) {
        computeRenderOffset(view, info.pivotX, info.pivotY);
        adjustTranslation(view, info.deltaX, info.deltaY);

        float scale = view.getScaleX() * info.deltaScale;
        scale = Math.max(info.minimumScale, Math.min(info.maximumScale, scale));
        view.setScaleX(scale);
        view.setScaleY(scale);

        float rotation = adjustAngle(view.getRotation() + info.deltaAngle);
        view.setRotation(rotation);
    }

    private static float adjustAngle(float degrees) {
        if (degrees > 180.0f) {
            degrees -= 360.0f;
        } else if (degrees < -180.0f) {
            degrees += 360.0f;
        }

        return degrees;
    }

    private static void adjustTranslation(View view, float deltaX, float deltaY) {
        float[] deltaVector = {deltaX, deltaY};
        view.getMatrix().mapVectors(deltaVector);
        view.setTranslationX(view.getTranslationX() + deltaVector[0]);
        view.setTranslationY(view.getTranslationY() + deltaVector[1]);
    }

    private static void computeRenderOffset(View view, float pivotX, float pivotY) {
        if (view.getPivotX() == pivotX && view.getPivotY() == pivotY) {
            return;
        }

        float[] prevPoint = {0.0f, 0.0f};
        view.getMatrix().mapPoints(prevPoint);

        view.setPivotX(pivotX);
        view.setPivotY(pivotY);

        float[] currPoint = {0.0f, 0.0f};
        view.getMatrix().mapPoints(currPoint);

        float offsetX = currPoint[0] - prevPoint[0];
        float offsetY = currPoint[1] - prevPoint[1];

        view.setTranslationX(view.getTranslationX() - offsetX);
        view.setTranslationY(view.getTranslationY() - offsetY);
    }


    private class TransformInfo {
        float deltaX;
        float deltaY;
        float deltaScale;
        float deltaAngle;
        float pivotX;
        float pivotY;
        float minimumScale;
        float maximumScale;
    }

}
