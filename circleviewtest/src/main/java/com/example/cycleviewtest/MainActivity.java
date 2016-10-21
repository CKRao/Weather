package com.example.cycleviewtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private CircleProgress mCircleProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCircleProgress =
                (CircleProgress) findViewById(R.id.circle);
        mCircleProgress.setSweepValue(0);
    }
}
