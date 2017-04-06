package com.example.efrainmg90.agendaapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.efrainmg90.agendaapp.R;
import com.example.efrainmg90.agendaapp.tasks.LoadingTask;

public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textView = (TextView) findViewById(R.id.tvLoading);
        progressBar.setMax(100);
        progressBar.setProgress(0);

        new LoadingTask(this,progressBar,textView).execute(100);

    }
}
