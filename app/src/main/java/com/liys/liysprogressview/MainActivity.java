package com.liys.liysprogressview;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    LineProgressView mLineProView;
    ArcProgressView mArcProView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mArcProView = findViewById(R.id.arc_view);
        mLineProView = findViewById(R.id.line_view);
        findViewById(R.id.btn_start).setOnClickListener(this);
    }

    public void start(){
        ValueAnimator anim = ValueAnimator.ofInt(0, 8156);
        anim.setDuration(1500);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mArcProView.setCurrentNum((int)animation.getAnimatedValue());
            }
        });
        anim.start();
    }

    public void start2(){
        ValueAnimator anim = ValueAnimator.ofInt(0, 8100);
        anim.setDuration(1500);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mLineProView.setCurrentNum((int)animation.getAnimatedValue());
            }
        });
        anim.start();
    }



    @Override
    public void onClick(View v) {
        start();
        start2();
    }
}
