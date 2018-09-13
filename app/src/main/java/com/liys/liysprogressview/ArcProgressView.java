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
 * 圆弧进度条
 * @author liys  401654546@qq.com
 * @version 1.0  2018/09/12
 */
public class ArcProgressView extends View {

    private double mMaxNum = 10000; //最大值
    private double mCurrentNum = 0; //当前的值
    private String mText = "0%"; //当前 百分比

    private int mTextSize;  //字体大小

    private int mTextColor = 0; //字体颜色
    private int mInColor = 0; //内圈颜色
    private int mOutColor = 0; //外圈颜色

    private int mInCircleSize; //外圈大小 单位sp
    private int mOutCircleSize; //内圈大小 单位sp

    private int mStartAngle; //开始角度
    private int mDrawAngle; //需要绘制的角度
    private int mCurrentAngle = 0; //当前角度

    private int mWidth; //宽
    private int mHeight; //高
    int defaultWidth = 100; //默认宽高，单位sp

    //画笔
    private Paint mTextPaint;
    private Paint mInPaint;
    private Paint mOutPaint;

    public ArcProgressView(Context context) {
        this(context, null, 0);

    }

    public ArcProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //默认值
        int defaultTextSize = 20;  //默认字体大小
        int defaultCircleSize = 10; //默认圆弧大小 单位sp
        int defaultStartAngle = -90; //默认开始角度
        int defaultDrawAngle = 360; //默认绘制角度
        String defaultTextColor = "#ABC4DF"; //默认字体颜色
        String defaultInColor = "#EDEDED"; //默认内颜色
        String defaultOutColor = "#CCBD00"; //默认外颜色

        // 获取自定义属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ArcProgressView);

        mText = typedArray.getString(R.styleable.ArcProgressView_liys_progress_arc_text);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.ArcProgressView_liys_progress_arc_textSize, sp2px(defaultTextSize));

        mStartAngle = typedArray.getInt(R.styleable.ArcProgressView_liys_progress_arc_startAngle, defaultStartAngle);
        mDrawAngle = typedArray.getInt(R.styleable.ArcProgressView_liys_progress_arc_drawAngle, defaultDrawAngle);

        mTextColor = typedArray.getColor(R.styleable.ArcProgressView_liys_progress_arc_textColor, Color.parseColor(defaultTextColor));
        mInColor = typedArray.getColor(R.styleable.ArcProgressView_liys_progress_arc_inCircleColor, Color.parseColor(defaultInColor));
        mOutColor = typedArray.getColor(R.styleable.ArcProgressView_liys_progress_arc_outCircleColor, Color.parseColor(defaultOutColor));

        mInCircleSize = typedArray.getDimensionPixelSize(R.styleable.ArcProgressView_liys_progress_arc_inCircleSize, sp2px(defaultCircleSize));
        mOutCircleSize = typedArray.getDimensionPixelSize(R.styleable.ArcProgressView_liys_progress_arc_outCircleSize, sp2px(defaultCircleSize));
        typedArray.recycle();

        //设置画笔
        mTextPaint = new Paint();
        mInPaint = new Paint();
        mOutPaint = new Paint();

        setTextPaint();
        setInPaint();
        setOutPaint();
    }

    /**
     * 文字画笔
     */
    private void setTextPaint() {
        mTextPaint.setColor(mTextColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mTextSize);
    }

    /**
     * 内圆弧画笔
     */
    private void setInPaint() {
        mInPaint.setColor(mInColor);
        mInPaint.setAntiAlias(true);
        mInPaint.setStrokeWidth(mInCircleSize); //大小
        mInPaint.setStrokeCap(Paint.Cap.ROUND); // 结束位置圆角
        mInPaint.setStyle(Paint.Style.STROKE); //空心样式
    }

    /**
     * 外圆弧画笔
     */
    private void setOutPaint() {
        mOutPaint.setColor(mOutColor);
        mOutPaint.setAntiAlias(true);
        mOutPaint.setStrokeWidth(mOutCircleSize); //大小
        mOutPaint.setStrokeCap(Paint.Cap.ROUND); // 结束位置圆角
        mOutPaint.setStyle(Paint.Style.STROKE); //空心样式
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //取默认值
        mWidth = sp2px(defaultWidth);
        mHeight = sp2px(defaultWidth);
        //1. 获取宽
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) { //具体值
            mWidth = MeasureSpec.getSize(widthMeasureSpec);
        }
        //2.获取高
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) { //具体值
            mHeight = MeasureSpec.getSize(heightMeasureSpec);
        }
        //3. 确定宽高(保持宽高一致)
        mWidth = mHeight = (mWidth > mHeight ? mHeight : mWidth);
        setMeasuredDimension(mWidth, mHeight);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawText(canvas);
        drawInCircle(canvas);
        drawOutCircle(canvas);
    }

    //内圆弧
    private void drawInCircle(Canvas canvas) {
        int r = mInCircleSize / 2; //圆弧的一半
        RectF rectF = new RectF(r, r, mWidth - r, mHeight - r);
        canvas.drawArc(rectF, mStartAngle, mDrawAngle, false, mInPaint);
    }

    //内圆弧
    private void drawOutCircle(Canvas canvas) {
        int r = mOutCircleSize / 2; //圆弧的一半
        RectF rectF = new RectF(r, r, mWidth - r, mHeight - r);
        if (mCurrentAngle > mDrawAngle) {
            mCurrentAngle = mDrawAngle;
        }
        canvas.drawArc(rectF, mStartAngle, mCurrentAngle, false, mOutPaint);
    }

    private void drawText(Canvas canvas) {
        //1. 获取绘制字体区域
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(mText, 0, mText.length(), bounds);
        //2.获取准线
        Paint.FontMetricsInt metrics = mTextPaint.getFontMetricsInt();
        int dy = (metrics.bottom - metrics.top) / 2 - metrics.bottom;
        int baseLine = mHeight / 2 + dy;
        //3.绘制文字
        canvas.drawText(mText, mWidth / 2 - bounds.width() / 2, baseLine, mTextPaint);
    }

    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                getResources().getDisplayMetrics());
    }

    public void setMaxNum(double maxNum) {
        this.mMaxNum = maxNum;
    }

    public void setCurrentNum(double currentNum) {
        this.mCurrentNum = currentNum;
        //计算角度 mCurrentStep/mMaxStep = mCurrentAngle/mDrawAngle;
        mCurrentAngle = (int)(currentNum * mDrawAngle / mMaxNum);
        mText =  new DecimalFormat("0.00%").format(mCurrentNum/mMaxNum);
        invalidate();
    }
}
