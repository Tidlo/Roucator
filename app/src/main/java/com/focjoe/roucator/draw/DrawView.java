package com.focjoe.roucator.draw;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

import com.focjoe.roucator.R;

public class DrawView extends View {

    public static Canvas mCanvas;
    public Paint mPaint = new Paint();
    public Bitmap circle3B;
    private Bitmap circle1B;
    private Bitmap circle2B;
    private Bitmap circle4B;
    private Bitmap originalB;
    private Bitmap destinationB;
    private int mCount;
    private int xPos = 0;
    private int yPos = 0;
    private int xOffset = 0;
    private int yOffset = 0;
    private int xOriginal = 1280;
    private int yOriginal;
    private int xDestination = 1280;
    private int yDestination;
    private int radius = 0;

    //constructor
    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mCanvas = new Canvas();
        this.setDrawingCacheEnabled(true);

        Resources res1 = this.getResources();
        originalB = BitmapFactory.decodeResource(res1, R.drawable.round_location_on_white_48);
        originalB = small(originalB);
        destinationB = BitmapFactory.decodeResource(res1, R.drawable.round_wifi_tethering_white_48);
        destinationB = small(destinationB);
    }

    private static Bitmap small(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(0.25f, 0.25f); //长和宽放大缩小的比例
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public Bitmap drawCircleInBitmap(int width, int height, int radius, int color) {
        Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(color);
        c.drawCircle(xPos, yPos, radius, p);

        return bm;
    }

    public Bitmap drawLineInBitmap(Bitmap bm, int startX, int startY, int stopX, int stopY) {
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStrokeWidth(5);
        p.setColor(Color.WHITE);
        c.drawLine(startX, startY, stopX, stopY, p);

        return bm;
    }

    public Bitmap drawBitmapOnBitmap(Bitmap src, Bitmap dst, int x, int y) {
        Canvas c = new Canvas(dst);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        c.drawBitmap(src, x, y, p);
        return dst;
    }

    public void drawCircle(int x, int y, double distance, int count) {
        mCount = count;
        xOffset = x - xPos;
        yOffset = y - yPos;
        xPos = x;
        yPos = y;
        radius = (int) distance * 20;
    }

    public Bitmap getBitmap() {
        return this.getDrawingCache();
    }


    public void setxOriginal(int x) {
        xOriginal = x - 12;
    }

    public void setxDestination(int x) {
        this.xDestination = x - 12;
    }

    public void setyDestination(int y) {
        this.yDestination = y - 12;
    }

    public void setyOriginal(int y) {
        yOriginal = y - 12;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setFilterBitmap(false);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setShader(null);
        mPaint.setColor(0xFF66AAFF);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DARKEN));
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        switch (mCount) {
            case 1:
                circle1B = drawCircleInBitmap(1080, 1080, radius, 0xFF4285F4);
                canvas.drawBitmap(circle1B, 0, 0, mPaint);
                break;
            case 2:
                canvas.drawBitmap(circle1B, 0, 0, mPaint);
                circle2B = drawCircleInBitmap(1080, 1080, radius, 0xFF34A853);
                circle2B = drawLineInBitmap(circle2B, xPos - xOffset, yPos - yOffset, xPos, yPos);
                canvas.drawBitmap(circle2B, 0, 0, mPaint);
                break;
            case 3:
                canvas.drawBitmap(circle1B, 0, 0, mPaint);
                canvas.drawBitmap(circle2B, 0, 0, mPaint);
                circle3B = drawCircleInBitmap(1080, 1080, radius, 0xFFFBBC05);
                circle3B = drawLineInBitmap(circle3B, xPos - xOffset, yPos - yOffset, xPos, yPos);
                canvas.drawBitmap(circle3B, 0, 0, mPaint);
                mPaint.setXfermode(null);
                break;
            default:
                circle4B = drawCircleInBitmap(1080, 1080, radius, 0xFFEA4335);
                drawBitmapOnBitmap(originalB, circle4B, xOriginal, yOriginal);
                drawBitmapOnBitmap(destinationB, circle4B, xDestination, yDestination);
                canvas.drawBitmap(circle4B, 0, 0, mPaint);
                break;
        }

    }
}
