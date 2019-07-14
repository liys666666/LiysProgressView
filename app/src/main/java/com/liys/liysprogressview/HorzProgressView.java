package com.liys.liysprogressview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * liys 2019-01-13
 * 水平进度条(不带文字)
 */
public class HorzProgressView extends View{

    private double mMaxNum = 100; //最大值
    private double mCurrentNum = 0; //当前的值

    private int mInLineColor = 0; //内线颜色
    private int mOutLineColor = 0; //外线颜色
    private Drawable mInLineDrawable = null; //内线图片
    private Drawable mOutLineDrawable = null; //外线图片

    private int mOutLineSize; //外线 大小 单位sp

    private int mWidth; //宽
    private int mHeight; //高

    //画笔
    private Paint mInPaint;
    private Paint mOutPaint;
    private Paint mPaint = new Paint(); //绘制图片

    public HorzProgressView(Context context) {
        this(context, null);
    }

    public HorzProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HorzProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        // 获取自定义属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HorzProgressView);

        mCurrentNum = typedArray.getInteger(R.styleable.HorzProgressView_liys_progress_line_progress, 0);
        mMaxNum = typedArray.getInteger(R.styleable.HorzProgressView_liys_progress_line_max, 100);

        //颜色
        mInLineColor = typedArray.getColor(R.styleable.HorzProgressView_liys_progress_line_inColor, 0);
        mOutLineColor = typedArray.getColor(R.styleable.HorzProgressView_liys_progress_line_outColor, 0);

        //图片
        mInLineDrawable = typedArray.getDrawable(R.styleable.HorzProgressView_liys_progress_line_inDrawable);
        mOutLineDrawable = typedArray.getDrawable(R.styleable.HorzProgressView_liys_progress_line_outDrawable);

        //大小
        mOutLineSize = typedArray.getDimensionPixelSize(R.styleable.HorzProgressView_liys_progress_line_outSize, 0);

        typedArray.recycle();

        setInPaint();
        setOutPaint();
    }

    /**
     * 内线画笔
     */
    private void setInPaint() {
        mInPaint = new Paint();
        mInPaint.setAntiAlias(true);
        mInPaint.setColor(mInLineColor);
        mInPaint.setStrokeWidth(mHeight); //大小
        mInPaint.setStrokeCap(Paint.Cap.ROUND); // 结束位置圆角
    }

    /**
     * 外线画笔
     */
    private void setOutPaint() {
        mOutPaint = new Paint();
        mOutPaint.setAntiAlias(true);
        mOutPaint.setColor(mOutLineColor);
        mOutPaint.setStrokeWidth(mOutLineSize); //大小
        mOutPaint.setStrokeCap(Paint.Cap.ROUND); // 结束位置圆角
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //1. 获取宽
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) { //具体值
            mWidth = MeasureSpec.getSize(widthMeasureSpec);
        }
        //2.获取高
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) { //具体值
            mHeight = MeasureSpec.getSize(heightMeasureSpec);
        }
        if(mOutLineSize == 0){
            mOutLineSize = mHeight;
        }
        //2. 确定宽高
        setMeasuredDimension(mWidth, mHeight);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //内层
        if(mInLineColor != 0){
            drawInLine(canvas, 0, mWidth, mHeight, mInPaint); //画内线
        }
        if(mInLineDrawable != null){
            Bitmap bitmap = ((BitmapDrawable) mInLineDrawable).getBitmap();
            canvas.drawBitmap(bitmap, null, new Rect(0,0, mWidth, mHeight), mPaint);
        }

        //外层
        int left = (mHeight-mOutLineSize)/2;
        int width = (int)((mWidth-left)*(mCurrentNum/mMaxNum));
        if(mOutLineColor != 0){
            drawOutLine(canvas, left, width, mOutLineSize, mOutPaint); //画外线
        }
        if(mOutLineDrawable != null){
            Bitmap bitmap = ((BitmapDrawable) mOutLineDrawable).getBitmap();
            canvas.drawBitmap(bitmap, null, new Rect(left,(mHeight-mOutLineSize)/2, width, mOutLineSize), mPaint);
        }
    }

    public void drawInLine(Canvas canvas, int left, int width, int height, Paint paint){
        RectF rectF = new RectF(left, mHeight-height, width, mHeight); // 设置个新的长方形
        canvas.drawRoundRect(rectF, height/2, height/2, paint); //第二个参数是x半径，第三个参数是y半径
    }

    /**
     * 进度前进方向为圆角
     */
    public void drawOutLine(Canvas canvas, int left, int width, int height, Paint paint){
        int top = (mHeight-height)/2;
        if((width-left) >= height){ //绘制圆角方式
//            RectF rectF = new RectF(left, mHeight-height, width, mHeight); // 设置个新的长方形
            RectF rectF = new RectF(left, top, width, mHeight-top); // 设置个新的长方形
            canvas.drawRoundRect(rectF, height/2, height/2, paint); //第二个参数是x半径，第三个参数是y半径
        }
        //绘制前面圆
        RectF rectF = new RectF(left, top, width, mHeight-top);
        canvas.clipRect(rectF);
        int r = height/2;
        canvas.drawCircle(left+r, top+r, r, paint);
    }

    public void setMax(double max){
        this.mMaxNum = max;
        invalidate();
    }

    public void setCurrentNum(double currentNum) {
        this.mCurrentNum = currentNum;
        if(mCurrentNum > mMaxNum){
            mCurrentNum = mMaxNum;
        }
        invalidate();
    }
}
