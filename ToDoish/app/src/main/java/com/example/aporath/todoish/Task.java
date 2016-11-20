package com.example.aporath.todoish;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by aporath on 5/20/16.
 */

public class Task {

    //static members
    private static long s_secondsInMilli = 1000;
    private static long s_minutesInMilli = s_secondsInMilli * 60;
    private static long s_hoursInMilli = s_minutesInMilli * 60;
    private static long s_daysInMilli = s_hoursInMilli * 24;

    public static DateFormat m_formatter = new SimpleDateFormat("dd/MM/yyyy");
    public View.OnClickListener m_tRowOnClickListener;
    public enum GoalUnits {
        Minutes,
        Hours,
        Sessions,
    };
    public enum Priority {
        Low,
        Medium,
        High;

        public int getColor() {
            if (this == High)
                return Color.RED;
            if (this == Medium)
                return Color.BLACK;
            if (this == Low)
                return Color.YELLOW;

            return Color.BLUE;
        }
    };

    private void parseDeadline(String date){
        try{
            m_deadline = m_formatter.parse(date);
        }
        catch (java.text.ParseException e)
        {
            Log.d("Task", "error parsing the date for m_deadline. date=" + date);
            try{
                m_deadline = m_formatter.parse("23/9/2008");
            }
            catch (java.text.ParseException e2) {

            }
        }
    }

    private void advanceDeadline(int i){
        m_deadline = new Date(m_deadline.getTime() + i*s_daysInMilli);
        refreshInTaskDB();
    }

    //Function to get Delta Time, as string, between current time & Task deadline
    public String parseDeadlineDelta(){
        Date taskDate = m_deadline;

        Date currentDate = new Date(System.currentTimeMillis());

        long different = taskDate.getTime() + s_daysInMilli - currentDate.getTime();

//        System.out.println("currentDate : " + currentDate);
//        System.out.println("taskDate : " + taskDate);
//        System.out.println("different : " + different);

        long elapsedDays = different / s_daysInMilli;
        different = different % s_daysInMilli;

        long elapsedHours = (different + s_hoursInMilli) / s_hoursInMilli;
        different = different % s_hoursInMilli;

        long elapsedMinutes = (different + s_minutesInMilli)/ s_minutesInMilli;
        different = different % s_minutesInMilli;

        long elapsedSeconds = different / s_secondsInMilli;

//        System.out.printf(
//                "%d days, %d hours, %d minutes, %d seconds%n",
//                elapsedDays,
//                elapsedHours, elapsedMinutes, elapsedSeconds);

        if (elapsedDays > 0)
            return elapsedDays + " Days";
        if (elapsedHours > 0)
            return elapsedHours + " Hours";
        if (elapsedMinutes > 0)
            return elapsedMinutes + " Min";

        return "passed";

    }
    //init the row-layout onClick listener used in mainActivity
    private void initRowOnclick(){
        m_tRowOnClickListener = new View.OnClickListener()
        {
            public void onClick(View v) {
                LayoutInflater inflater = MainActivity.getM_activity().getLayoutInflater();
                AlertDialog.Builder taskProgressDialogBuilder = new AlertDialog.Builder(v.getContext());
                taskProgressDialogBuilder.setCancelable(true);

                if (m_isRepeating) {

                    final View dialogView = inflater.inflate(R.layout.task_progress_dialog, null);
                    final EditText etProg = (EditText) dialogView.findViewById(R.id.etTaskProgressDialogProgress);
                    final EditText tvTaskInfo = (EditText) dialogView.findViewById(R.id.tvTaskProgressDialogTaskInfo);

                    taskProgressDialogBuilder.setView(dialogView)
                            // Add action buttons
                            .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    m_progress += Integer.parseInt(etProg.getText().toString());
                                    m_taskInfo = tvTaskInfo.getText().toString();
                                    refreshInTaskDB();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });

                    final AlertDialog taskProgressDialog = taskProgressDialogBuilder.create();
                    Button btnEdit = (Button) dialogView.findViewById(R.id.btnTaskProgressDialogEdit);
                    btnEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            editTask(view.getContext());
                            //todo need to close the dialog here also
                        }
                    });

                    Button btnDelete = (Button) dialogView.findViewById(R.id.btnTaskEditProgressDialogDelete);
                    btnDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TaskDB.getInstance().deleteTask(m_taskName);
                            taskProgressDialog.cancel();
                        }
                    });

                    TextView tvTaskName = (TextView) dialogView.findViewById(R.id.tvTaskProgress);
                    tvTaskName.setText(m_taskName);

                    Button btnPlusProgress = (Button) dialogView.findViewById(R.id.btnTaskProgressDialogPlusProgress);
                    btnPlusProgress.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            etProg.setText("" + (Integer.parseInt(etProg.getText().toString()) + 1));
                        }
                    });

                    Button btnPlusTenProgress = (Button) dialogView.findViewById(R.id.btnTaskProgressDialogPlusTenProgress);
                    btnPlusTenProgress.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            etProg.setText("" + (Integer.parseInt(etProg.getText().toString()) + 10));
                        }
                    });

                    Button btnResetProgress = (Button) dialogView.findViewById(R.id.btnTaskProgressDialogResetProgress);
                    btnResetProgress.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            m_progress = 0;
                            refreshInTaskDB();
                            taskProgressDialog.cancel();
                        }
                    });

                    //todo set task info
                    tvTaskInfo.setText(m_taskInfo);

                    // Perform action on click
                    taskProgressDialog.show();
                }
                else {
                    final View dialogView = inflater.inflate(R.layout.task_once_dialog, null);
                    final EditText tvTaskInfo = (EditText) dialogView.findViewById(R.id.tvTaskOnceDialogTaskInfo );

                    taskProgressDialogBuilder.setView(dialogView)
                            // Add action buttons
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    m_taskInfo = tvTaskInfo.getText().toString();
                                    refreshInTaskDB();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });

                    final AlertDialog taskProgressDialog = taskProgressDialogBuilder.create();

                    Button btnSetDone = (Button) dialogView.findViewById(R.id.btnTaskOnceDialogSetDone);
                    btnSetDone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setDone();
                            taskProgressDialog.cancel();
                        }
                    });

                    Button btnPushDL1 = (Button) dialogView.findViewById(R.id.btnTaskOnceDialogPushDL1);
                    btnPushDL1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            advanceDeadline(1);
                            taskProgressDialog.cancel();
                        }
                    });

                    Button btnEdit = (Button) dialogView.findViewById(R.id.btnTaskOnceDialogEdit);
                    btnEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            editTask(view.getContext());
                            //todo need to close the dialog here also
                        }
                    });

                    Button btnDelete = (Button) dialogView.findViewById(R.id.btnTaskOnceDialogDelete);
                    btnDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TaskDB.getInstance().deleteTask(m_taskName);
                            taskProgressDialog.cancel();
                        }
                    });

                    TextView tvTaskName = (TextView) dialogView.findViewById(R.id.tvTaskOnceDialogTitle);
                    tvTaskName.setText(m_taskName);

                    tvTaskInfo.setText(m_taskInfo);

                    // Perform action on click
                    taskProgressDialog.show();
                }
            }
        };
    }
    //Constructors
    Task(Cursor c) {
        m_taskName  = c.getString(c.getColumnIndex(TaskDB.TaskTableContract.TaskTableEntry.COLUMN_NAME_TASK_NAME));
        m_goal      = c.getInt(c.getColumnIndex(TaskDB.TaskTableContract.TaskTableEntry.COLUMN_NAME_GOAL));
        m_progress  = c.getInt(c.getColumnIndex(TaskDB.TaskTableContract.TaskTableEntry.COLUMN_NAME_PROGRESS));
        m_goalUnits = GoalUnits.valueOf(c.getString((c.getColumnIndex(TaskDB.TaskTableContract.TaskTableEntry.COLUMN_NAME_GOALUNITS))));
        m_isRepeating = c.getString(c.getColumnIndex(TaskDB.TaskTableContract.TaskTableEntry.COLUMN_NAME_IS_REPEATING)).equals("YES");
        m_repeat    = c.getString(c.getColumnIndex(TaskDB.TaskTableContract.TaskTableEntry.COLUMN_NAME_REPEAT));
        m_priority  = Priority.valueOf(c.getString((c.getColumnIndex(TaskDB.TaskTableContract.TaskTableEntry.COLUMN_NAME_PRIORITY))));
        parseDeadline(c.getString((c.getColumnIndex(TaskDB.TaskTableContract.TaskTableEntry.COLUMN_NAME_DATE))));
        m_tags      = c.getInt(c.getColumnIndex(TaskDB.TaskTableContract.TaskTableEntry.COLUMN_NAME_TAGS));
        m_location  = c.getString(c.getColumnIndex(TaskDB.TaskTableContract.TaskTableEntry.COLUMN_NAME_LOCATION));
        m_taskInfo  = c.getString(c.getColumnIndex(TaskDB.TaskTableContract.TaskTableEntry.COLUMN_NAME_TASK_INFO));
        initRowOnclick();
    }

    Task(String taskName, int goal, int progress, GoalUnits units, boolean isRepeating, String repeat, Priority priority, String date, int tags, String location,String taskInfo) {
        m_taskName = taskName;
        m_goal     = goal;
        m_progress = progress;
        m_goalUnits = units;
        m_isRepeating = isRepeating;
        m_repeat   = repeat;
        m_priority = priority;
        parseDeadline(date);
        m_tags = tags;
        m_location = location;
        m_taskInfo = taskInfo;
        initRowOnclick();
    }

    public String toString() {
        return String.format("%s: %s, %s: %d, %s: %s, %s: %s\n",
                "Task Name: ", m_taskName,
                "Goal: ", m_goal,
                "Repeat: ", m_repeat,
                "Priority: ", m_priority.toString());
    }

    public void editTask(Context context) {
        // Perform action on click
        TaskEditActivity.setTask(this);
        Intent intent = new Intent(context, TaskEditActivity.class);
        context.startActivity(intent);
    }
    public void setDone() {
        // Repeating Task shouldn't get here
        assert (!m_isRepeating);
        if(m_progress != 1){
            m_progress = 1;
            refreshInTaskDB();
        }
    }

    //reset progress according ot the type
    public void resetProgress(String repeat) {
        if (!m_isRepeating)
            return;

        if (m_repeat.equals(repeat)) {
            m_progress = 0;
            refreshInTaskDB();
        }
    }
    public void refreshInTaskDB() { TaskDB.getInstance().insertTask(this); }

//    public TableRow getAsTableRow(Context context) {
//        TableRow tRow = new TableRow(context);
//        tRow.setGravity(Gravity.BOTTOM|Gravity.LEFT);
//
//        TextView tvTaskName = new TextView(context);
//        tvTaskName.setTextSize(30);
//        tvTaskName.setTextColor(m_priority.getColor());
//        tvTaskName.setText(m_taskName);
//
//        LinearLayout layout = new LinearLayout(context);
//        layout.setOrientation(LinearLayout.VERTICAL);
//
//        ProgressBar pb = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
//        pb.setMax(m_goal);
//        pb.setProgress(m_progress);
//        pb.setRotation(180);
//
//        TextView tvProgress = new TextView(context);
//        if (m_isRepeating) {
//            tvProgress.setText(m_progress + "/" + m_goal + "," + m_repeat);
//            layout.addView(tvProgress);
//            layout.addView(pb);
//        }
//        else {
//            if (m_progress == 1)
//                tvProgress.setText("Done ;)");
//            else
//                tvProgress.setText("Due " + m_formatter.format(m_deadline) + " (" + parseDeadlineDelta(m_deadline) + ")");
//
//            tvProgress.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
//            layout.addView(tvProgress);
//        }
//
//        tRow.addView(tvTaskName);
//        tRow.addView(layout);
//
//        tRow.setOnClickListener(m_tRowOnClickListener);
//        return tRow;
//    }

    //Variables:
    public String    m_taskName;
    public int       m_goal;
    public int       m_progress;
    public GoalUnits m_goalUnits;
    public Priority  m_priority;
    public boolean   m_isRepeating;
    public String    m_repeat;
    public Date      m_deadline;
    public int       m_tags;
    public String    m_location;
    public String    m_taskInfo;
    /*
        Details - text
        Location
        Save History
    */


}


