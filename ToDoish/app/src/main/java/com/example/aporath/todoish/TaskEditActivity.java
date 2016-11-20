package com.example.aporath.todoish;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TaskEditActivity extends AppCompatActivity {

    static Task m_task;
    EditText m_etProg;
    static int m_progressToAdd = 0;

    static public void setTask(Task task){
        m_task = task;
    };

    void plusProgress(){}

    void addProgressToTask(){
        Log.d("TaskEditActivity", "adding " + m_progressToAdd + "to task "+ m_task.m_taskName);
        m_task.m_progress = m_task.m_progress + m_progressToAdd;
        Log.d("TaskEditActivity", "m_task.m_progress is now " + m_task.m_progress);
        m_progressToAdd = 0;
        updateProgress();
        m_task.refreshInTaskDB();
    }

    private void initialize(){
        TextView tvTaskName = (TextView) findViewById(R.id.tvTaskEditTaskName);
        tvTaskName.setText(m_task.m_taskName);

        m_progressToAdd = 0;
        m_etProg = (EditText) findViewById(R.id.etTaskEditProgress);
        updateProgress();
    }

    private void updateProgress(){
        m_etProg.setText("" + m_progressToAdd);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initialize();

        Button btnPlusProgress = (Button) findViewById(R.id.btnTaskEditPlusProgress);
        btnPlusProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //++ progress value
                m_progressToAdd++;
                updateProgress();
            }
        });

        Button btnAddProgress = (Button) findViewById(R.id.btnTaskEditAddProgress);
        btnAddProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add progress value to task
                addProgressToTask();
            }
        });
    }

}
