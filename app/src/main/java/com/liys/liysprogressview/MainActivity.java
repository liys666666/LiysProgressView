package com.liys.liysprogressview;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    LineTextProgressView mLineProView;
    HorzTextProgressView mHorzView;
    ArcProgressView mArcProView;
    EditText editText;
    WaterWaveProView mWaterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        findViewById(R.id.btn_start).setOnClickListener(this);

        mArcProView = findViewById(R.id.arc_view);
        mLineProView = findViewById(R.id.line_view);
        mHorzView = findViewById(R.id.horz_view);
        mWaterView = findViewById(R.id.water_view);
    }

    public void start(int value){
        ValueAnimator anim = ValueAnimator.ofInt(0, value);
        anim.setDuration(1500);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mArcProView.setCurrentNum((int)animation.getAnimatedValue());
                mLineProView.setCurrentNum((int)animation.getAnimatedValue());
                mHorzView.setCurrentNum((int)animation.getAnimatedValue());
            }
        });
        anim.start();
    }


    public void start2(int value){
        ValueAnimator anim = ValueAnimator.ofInt(0, value);
        anim.setDuration(5000);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mWaterView.setCurrentNum((int)animation.getAnimatedValue());
            }
        });
        anim.start();
    }



    @Override
    public void onClick(View v) {
        int value = Integer.parseInt(editText.getText().toString());
        start(value);
        start2(value);
    }
}
