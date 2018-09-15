package com.liys.liysprogressview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.text.DecimalFormat;


/**
 * 水波进度条
 */
public class WaterWaveProView extends View {

    private double mMaxNum = 10000; //最大值
    private double mCurrentNum = 0; //当前的值
    private double mPercent = 0.0; //百分比

    private String mText = ""; //当前 百分比
    private int mTextSize;  //字体大小
    private int mTextColor;  //字体大小

    private int mInColor = 0; //里面颜色
    private int mWaterColor = 0; //水波颜色

    //控件宽高
    private int mWidth;
    private int mHeight;
    int mDefaultWidthHeight= 100; //默认宽高，单位sp

    private float mStartX = 0; //开始位置
    private int mWaveWidth; //水波长
    private int mWaveHeight; //水波高度
    private Paint mPaint;
    private Paint mTextPaint;
    private Path mPath;

    public WaterWaveProView(Context context) {
        this(context, null);
    }

    public WaterWaveProView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaterWaveProView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);//关闭硬件加速

        //默认值
        int defaultTextSize = 20;  //默认字体大小 单位sp
        String defaultTextColor = "#FFFFFF"; //默认字体颜色
        String defaultInColor = "#69B655"; //默认里面颜色
        String defaultWaterColor = "#0AA328"; //默认水波颜色

        // 获取自定义属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WaterWaveProView);
        mText = typedArray.getString(R.styleable.WaterWaveProView_liys_progress_water_text);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.WaterWaveProView_liys_progress_water_textSize, sp2px(defaultTextSize));
        mTextColor = typedArray.getColor(R.styleable.WaterWaveProView_liys_progress_water_textColor, Color.parseColor(defaultTextColor));
        mWaterColor = typedArray.getColor(R.styleable.WaterWaveProView_liys_progress_water_waterColor, Color.parseColor(defaultInColor));
        mInColor = typedArray.getColor(R.styleable.WaterWaveProView_liys_progress_water_inColor, Color.parseColor(defaultWaterColor));
        typedArray.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mWaterColor);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);

        mPath = new Path();

        if(mText == null){
            mText = "0.00%";
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int defaultWaterHeight = 5; //默认水波高度 单位sp
        //1.取默认宽高
        mWidth = sp2px(mDefaultWidthHeight);
        mHeight = sp2px(mDefaultWidthHeight);
        //2. 获取宽
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) { //具体值
            mWidth = MeasureSpec.getSize(widthMeasureSpec);
        }
        //3.获取高
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) { //具体值
            mHeight = MeasureSpec.getSize(heightMeasureSpec);
        }
        //4. 确定宽高(保持宽高一致)
        mWidth = mHeight = (mWidth > mHeight ? mHeight : mWidth);
        //5. 确定波长和波高
        mWaveWidth = mWidth/4;
        mWaveHeight = sp2px(defaultWaterHeight);
        setMeasuredDimension(mWidth, mHeight);
        start();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //1. 绘制贝塞尔曲线
        drawBessel(mWidth, mStartX, (int)(mHeight*(1-mPercent)), mWaveWidth, mWaveHeight, mPath, mPaint);
        canvas.drawPath(mPath, mPaint);
        //2. 设置模式
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
        //3. 绘制圆形bitmap
        canvas.drawBitmap(createCircleBitmap(mWidth/2, mInColor), null, new Rect(0,0,mWidth,mHeight), mPaint);
        //4. 绘制文字
        drawText(canvas, mText, mWidth, mHeight, mTextPaint);
    }

    /**
     * 绘制贝塞尔曲线
     * @param width 总共需要绘制的长度
     * @param startX 开始X点坐标(-2*startX 到 0 之间) 左右预留一个波长
     * @param startY 开始Y坐标
     * @param waveWidth 波长(半个周期)
     * @param waveHeight 波高
     * @param path
     * @param paint 画笔
     */
    private void drawBessel(float width, float startX, float startY, float waveWidth, float waveHeight, Path path, Paint paint){
        //Android贝塞尔曲线
        // 二阶写法：rQuadTo(float dx1, float dy1, float dx2, float dy2) 相对上一个起点的坐标
        path.reset();
        int currentWidth = 0; //当前已经绘制的宽度
        path.moveTo(startX,startY); //画笔位置
        while (currentWidth <= width + 4*waveWidth && waveWidth>0){
            path.rQuadTo(waveWidth, -waveHeight, 2*waveWidth, 0);
            path.rQuadTo(waveWidth, waveHeight, 2*waveWidth, 0);
            currentWidth += 2*waveWidth;
        }
        //封闭的区域
        mPath.lineTo(getWidth()+4*waveWidth,getHeight());
        mPath.lineTo(0,getHeight());
        path.close();
    }

    private Bitmap createCircleBitmap(int radius, int color){
        Bitmap canvasBmp = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(canvasBmp);
        canvas.drawColor(Color.TRANSPARENT);
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawCircle(radius, radius, radius, paint); //确定位置
        return canvasBmp;
    }

    /**
     * 绘制文字  居中
     * @param canvas
     * @param text 文字内容
     * @param width 绘制区域 宽
     * @param height 绘制区域 高
     * @param paint
     */
    public void drawText(Canvas canvas, String text, int width, int height, Paint paint){
        Rect bounds = new Rect();
        paint.getTextBounds(text,0, mText.length(), bounds);
        Paint.FontMetricsInt metrics = paint.getFontMetricsInt();
        int dy = (metrics.bottom - metrics.top) / 2 - metrics.bottom;
        int baseLine = height / 2 + dy; //基线
        canvas.drawText(text, width/2-bounds.width()/2, baseLine, paint);
    }

    /**
     * 设置当前进度
     * @param currentNum
     */
    public void setCurrentNum(double currentNum) {
        this.mCurrentNum = currentNum;
        setPercent();
    }

    public void setMaxNum(int maxNum){
        this.mMaxNum = maxNum;
        setPercent();
    }

    private void setPercent(){
        if(mCurrentNum > mMaxNum){
            mCurrentNum = mMaxNum;
        }
        mPercent = mCurrentNum/mMaxNum;
        mText = new DecimalFormat("0.00%").format(mPercent);
    }

    private void setStartX(float startX){
        mStartX = startX;
        invalidate();
    }

    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                getResources().getDisplayMetrics());
    }

    private void start(){
        ValueAnimator animator = ValueAnimator.ofFloat(0-4*mWaveWidth, 0);
        animator.setInterpolator(new LinearInterpolator());//匀速插值器 解决卡顿问题
        animator.setDuration(2000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setStartX((float) animation.getAnimatedValue());
            }
        });
        animator.start();
    }

}
