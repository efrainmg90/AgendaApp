package com.example.efrainmg90.agendaapp.tasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.efrainmg90.agendaapp.activities.AppointmentListActivity;

/**
 * Created by Efrainmg90 on 30/03/2017.
 */

public class LoadingTask extends AsyncTask<Integer,Integer,Void> {
    Context context;
    ProgressBar progressBar;
    TextView textView;


    public LoadingTask(Context context,ProgressBar progressBar, TextView textView) {
        this.context = context;
        this.progressBar = progressBar;
        this.textView = textView;
    }

    @Override
    protected Void doInBackground(Integer... integers) {
        for (int count= 1;count<=integers[0];count++){
            try {
                Thread.sleep(60);
                publishProgress(count);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        progressBar.setProgress(values[0]);
        textView.setText("Cargando... "+values[0]+"%");
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Intent intent = new Intent(context, AppointmentListActivity.class);
        intent.putExtra("flag",false);
        context.startActivity(intent);
        ((Activity)context).finish();

    }
}
