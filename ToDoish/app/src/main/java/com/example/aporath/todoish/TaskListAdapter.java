package com.example.aporath.todoish;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

/**
 * Created by porata1 on 7/20/2016.
 */
public class TaskListAdapter extends ArrayAdapter<Task> {

    public TaskListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public TaskListAdapter(Context context, int resource, List<Task> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        Task task = getItem(position);

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            if (task.m_isRepeating)
                v = vi.inflate(R.layout.task_repeating_listview_layout, null);
            else
                v = vi.inflate(R.layout.task_once_listview_layout, null);
        }


        if (task != null) {
            if (task.m_isRepeating)
            {
                TextView tt1 = (TextView) v.findViewById(R.id.tvTaskRepeatingListviewLayoutTaskName);
                if (tt1 != null) {
                    tt1.setText(task.m_taskName);
                    tt1.setTextColor(Color.BLUE);
                }

                TextView tvProgress = (TextView) v.findViewById(R.id.tvTaskRepeatingListviewLayoutProgress);
                if (tvProgress != null) {
                    tvProgress.setText(task.m_progress + "/" + task.m_goal + "," + task.m_repeat);
                }

                ProgressBar pb = (ProgressBar) v.findViewById(R.id.pbTaskRepeatingListviewLayoutProgressBar);
                if (pb != null) {
                    pb.setMax(task.m_goal);
                    pb.setProgress(task.m_progress);
                }
            }
            else{
                TextView tt1 = (TextView) v.findViewById(R.id.tvTaskOnceListviewLayoutTaskName);
                TextView tt2 = (TextView) v.findViewById(R.id.tvTaskOnceListviewLayoutTaskDeadline);

                if (tt1 != null) {
                    tt1.setTextColor(Color.BLACK);
                    tt1.setText(task.m_taskName);
                }

                if (task.m_progress == 1) {
                    if (tt1 != null)
                        tt1.setTextColor(Color.GRAY);
                    if (tt2 != null) {
                        tt2.setText("Done ;)");
                        tt2.setTextColor(Color.GRAY);
                    }
                }
                else if (tt2 != null) {
                    String cs = task.parseDeadlineDelta();
                    String[] s = cs.split(" ");

                    tt2.setTextColor(Color.RED);

                    if (cs.equals("passed"))
                        tt2.setText("Passed DL");
                    else {
                        if (cs.toString().endsWith("Days"))
                        {
    //                        if (Integer.parseInt(s[0]) > 10)
                            if (Integer.parseInt(s[0]) > 3)
                                tt2.setTextColor(Color.GREEN);
    //                        else if (Integer.parseInt(s[0]) > 3)
                            else
                                tt2.setTextColor(MainActivity.getM_activity().getResources().getColor(R.color.Fuchsia));
                        }

                        tt2.setText("Due " + Task.m_formatter.format(task.m_deadline) + " (" + cs + ")");
                    }
                }
            }
        }
        //set the on-touch background coloring of the item
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundColor(Color.CYAN);
                }
                else{
                        v.setBackgroundColor(Color.WHITE);
                }
                return false;
            }
        });
        v.setOnClickListener(task.m_tRowOnClickListener);
        return v;
    }

}