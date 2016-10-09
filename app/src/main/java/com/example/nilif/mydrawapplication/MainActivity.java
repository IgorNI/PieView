package com.example.nilif.mydrawapplication;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PieView.OnClickListener{

    private PieView pv;
    private List<BaseMessage> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pv = (PieView) findViewById(R.id.pv);

        pv.setTextColor(Color.parseColor("#000000"));
        mList = new ArrayList<>();

        BaseMessage baseMessage = new BaseMessage();
        baseMessage.percent = 50;
        baseMessage.content = "身高";
        baseMessage.color = Color.parseColor("#ff0000");
        mList.add(baseMessage);

        BaseMessage baseMessage1 = new BaseMessage();
        baseMessage1.percent = 20;
        baseMessage1.content = "家庭情况";
        baseMessage1.color = Color.parseColor("#00dd00");
        mList.add(baseMessage1);

        BaseMessage baseMessage2 = new BaseMessage();
        baseMessage2.percent = 30;
        baseMessage2.content = "工作情况";
        baseMessage2.color = Color.parseColor("#ff9966");
        mList.add(baseMessage2);

        BaseMessage baseMessage3 = new BaseMessage();
        baseMessage3.percent = 20;
        baseMessage3.content = "相貌";
        baseMessage3.color = Color.parseColor("#ee99ee");
        mList.add(baseMessage3);

        pv.setPieData(mList);
    }

    @Override
    public void onClick(View v) {

    }
}
