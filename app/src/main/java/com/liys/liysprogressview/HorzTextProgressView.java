package com.liys.liysprogressview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.text.DecimalFormat;

/**
 * 水平进度条(带文字)
 * @author liys  401654546@qq.com
 * @version 1.0  2018/09/12
 */
public class HorzTextProgressView extends View{

    private double mMaxNum = 10000; //最大值
    private double mCurrentNum = 0; //当前的值
    private String mText = "0%"; //当前 百分比

    private int mTextSize;  //字体大小

    private int mTextColor = 0; //字体颜色
    private int mInLineColor = 0; //内线颜色
    private int mOutLineColor = 0; //外线颜色

    private int mInLineSize; //外线 大小 单位sp
    private int mOutLineSize; //内线 大小 单位sp

    private int mWidth; //宽
    private int mHeight; //高
    private int mDefaultWidth = 300; //默认宽，单位sp
    private int mDefaultHeight = 20; //默认高，单位sp

    int boxWidth = 30; //文字框 宽 单位sp

    //画笔
    private Paint mTextPaint;
    private Paint mInPaint;
    private Paint mOutPaint;
    private Paint mBoxPaint;

    public HorzTextProgressView(Context context) {
        this(context, null);
    }

    public HorzTextProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HorzTextProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //默认值
        int defaultTextSize = 10;  //默认字体大小 单位sp
        String defaultTextColor = "#FFFFFF"; //默认字体颜色
        String defaultInColor = "#EDEDED"; //默认内颜色
        String defaultOutColor = "#CCBD00"; //默认外颜色
        int defaultLineSize = 10; //默认线的大小 单位sp

        
        // 获取自定义属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HorzTextProgressView);

        mText = typedArray.getString(R.styleable.HorzTextProgressView_liys_progress_horz_text);
        mTextColor = typedArray.getColor(R.styleable.HorzTextProgressView_liys_progress_horz_textColor, Color.parseColor(defaultTextColor));
        mInLineColor = typedArray.getColor(R.styleable.HorzTextProgressView_liys_progress_horz_inLineColor, Color.parseColor(defaultInColor));
        mOutLineColor = typedArray.getColor(R.styleable.HorzTextProgressView_liys_progress_horz_outLineColor, Color.parseColor(defaultOutColor));

        mTextSize = typedArray.getDimensionPixelSize(R.styleable.HorzTextProgressView_liys_progress_horz_textSize, sp2px(defaultTextSize));
        mInLineSize = typedArray.getDimensionPixelSize(R.styleable.HorzTextProgressView_liys_progress_horz_inLineSize, sp2px(defaultLineSize));
        mOutLineSize = typedArray.getDimensionPixelSize(R.styleable.HorzTextProgressView_liys_progress_horz_outLineSize, sp2px(defaultLineSize));
        typedArray.recycle();

        setTextPaint();
        setInPaint();
        setOutPaint();
        setBoxPaint();
        if(mText == null){
            mText = "0%";
        }
        boxWidth = sp2px(boxWidth);
    }

    /**
     * 方框画笔
     */
    private void setBoxPaint() {
        mBoxPaint = new Paint();
        mBoxPaint.setAntiAlias(true);
        mBoxPaint.setColor(mOutLineColor);
    }
    /**
     * 文字画笔
     */
    private void setTextPaint() {
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
    }

    /**
     * 内线画笔
     */
    private void setInPaint() {
        mInPaint = new Paint();
        mInPaint.setAntiAlias(true);
        mInPaint.setColor(mInLineColor);
        mInPaint.setStrokeWidth(mInLineSize); //大小
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
        //取默认值
        mWidth = sp2px(mDefaultWidth);
        mHeight = sp2px(mDefaultHeight);
        //1. 获取宽
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) { //具体值
            mWidth = MeasureSpec.getSize(widthMeasureSpec);
        }
        //2.获取高
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) { //具体值
            mHeight = MeasureSpec.getSize(heightMeasureSpec);
        }
        //2. 确定宽高
        setMeasuredDimension(mWidth, mHeight);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //1. 获取当前进度
        int outWidth = (int)(mCurrentNum/mMaxNum * mWidth); //计算当前进度距离
        if(outWidth >= mWidth - boxWidth){
            outWidth = (mWidth - boxWidth);
        }

        //2. 画进度条
        int inTop = (mHeight - mInLineSize)/2;
        int outTop = (mHeight - mOutLineSize)/2;
        drawHLine(canvas, 0, inTop, mWidth, mHeight - inTop, mInPaint); //画内线
        drawHLine(canvas, 0, outTop, outWidth+sp2px(2), mHeight - outTop, mOutPaint); //画外线

        //3. 画文字框
        drawBox(canvas, outWidth, boxWidth, mHeight);

        //4. 画文字
        Paint.FontMetricsInt metrics = mTextPaint.getFontMetricsInt();
        int dy = (metrics.bottom - metrics.top) / 2 - metrics.bottom;
        int baseLine = mHeight/2 + dy; //基线

        //文字变化的时候 为了保证文字居中 所以需要知道文字区域大小
        Rect bound = new Rect();
        mTextPaint.getTextBounds(mText, 0, mText.length(), bound); //获取文字区域大小
        canvas.drawText(mText, outWidth + (boxWidth/2 - bound.width()/2), baseLine, mTextPaint);
    }

    /**
     * @param canvas
     * @param left 左边距离
     * @param width 矩形 宽
     * @param height 矩形 高
     */
    public void drawBox(Canvas canvas, int left, int width, int height){
        RectF rectF = new RectF(left, 0, width + left, height); // 设置个新的长方形
        canvas.drawRoundRect(rectF, height/2, height/2, mBoxPaint); //第二个参数是x半径，第三个参数是y半径
    }

    /**
     * 水平进度条(前进方向平的) 通用
     * @param canvas
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @param paint
     */
    public void drawHLine(Canvas canvas, int left, int top, int right, int bottom, Paint paint){
        int height = bottom - top; //高度
        int r = height/2; //半径
        int cFirstX = left + r; //第一个分割点x坐标
        int cSecondX = mWidth - left - r; //第二个分割点x坐标
        int cy = top + r; //圆心y坐标

        //1. 绘制第一个圆
        canvas.save();
        canvas.clipRect(new RectF(left, top, right, bottom));
        canvas.drawCircle(left+r, cy, r, paint);
        canvas.restore();

        //2. 绘制中间矩形
        if(right >= cFirstX){
            canvas.save();
            int currentRight = right;
            if(right > cSecondX){
                currentRight = cSecondX;
            }
            canvas.drawRect(new RectF(left+r, top, currentRight, bottom), paint);
            canvas.restore();
        }

        //3. 绘制最后的圆
        if(right >= cSecondX){
            canvas.save();
            canvas.clipRect(new RectF(cSecondX, top, right, bottom));
            canvas.drawCircle(cSecondX, cy, r, paint);
            canvas.restore();
        }
    }
    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                getResources().getDisplayMetrics());
    }

    public void setCurrentNum(double currentNum) {
        this.mCurrentNum = currentNum;
        if(mCurrentNum > mMaxNum){
            mCurrentNum = mMaxNum;
        }
        mText = new DecimalFormat("0%").format(mCurrentNum/mMaxNum);
        invalidate();
    }
}
