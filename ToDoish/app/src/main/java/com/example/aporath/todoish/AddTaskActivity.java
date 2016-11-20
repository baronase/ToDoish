package com.example.aporath.todoish;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Calendar;


public class AddTaskActivity extends AppCompatActivity {

    final private String starting_priority = "Medium";
    final private String starting_goal_units = "Sessions";

    EditText m_newTaskName;
    EditText m_etGoal;
    RadioGroup m_rgTaskType;
    RadioGroup m_radioRepeatGroup;
    TextView m_tvRepeat;
    TextView m_tvAddTaskDeadline;
    Button   m_btnSetDate;
    EditText m_newTaskInfo;
    LinearLayout m_extendedOptionsLayout;
    View m_vExtendedOnceOptions;
    View m_vExtendedRepeatingOptions;

    private void prioOnceChosen(){
        //remove the "Goal" field
        // Add Deadline date field
        m_tvAddTaskDeadline.setEnabled(true);
        m_btnSetDate.setEnabled(true);
        m_etGoal.setEnabled(false);
//        m_spnrGoalUnits.getSelectedView().setEnabled(false);
        m_spnrGoalUnits.setEnabled(false);
        for (int i = 0; i < m_radioRepeatGroup.getChildCount(); i++) {
            m_radioRepeatGroup.getChildAt(i).setEnabled(false);
        }

        m_extendedOptionsLayout.removeView(m_vExtendedRepeatingOptions);
        m_extendedOptionsLayout.addView(m_vExtendedOnceOptions);
    }

    private void prioOnceUnChosen() {
        //Undo prioOnceChosen
        m_tvAddTaskDeadline.setEnabled(false);
        m_btnSetDate.setEnabled(false);
        m_etGoal.setEnabled(true);
//        m_spnrGoalUnits.getSelectedView().setEnabled(true);
        m_spnrGoalUnits.setEnabled(true);
        for (int i = 0; i < m_radioRepeatGroup.getChildCount(); i++) {
            m_radioRepeatGroup.getChildAt(i).setEnabled(true);
        }
        m_extendedOptionsLayout.removeView(m_vExtendedOnceOptions);
        m_extendedOptionsLayout.addView(m_vExtendedRepeatingOptions);
    }

    private void addTaskToTable() {
        //Serialize all the choices and make a task
        int selectedIdType = m_rgTaskType.getCheckedRadioButtonId();
        RadioButton rbTaskType = (RadioButton) findViewById(selectedIdType);

        int selectedId = m_radioRepeatGroup.getCheckedRadioButtonId();
        RadioButton radioRepeatButton = (RadioButton) m_vExtendedRepeatingOptions.findViewById(selectedId);

        Task task = new Task(m_newTaskName.getText().toString(),
                            Integer.parseInt(m_etGoal.getText().toString()),
                            0,
                            m_goalUnitsSelected,
                            !rbTaskType.getText().equals("Once"),
                            radioRepeatButton.getText().toString(),
                            m_prioritySelected,
                            m_tvAddTaskDeadline.getText().toString(),
                            //todo parse real values below:
                            0,
                            "b",
                            m_newTaskInfo.getText().toString());

        //Insert that task to the taskDB
        //todo print to dbg "adding task " + task.toString();
        Log.d("AddTask","adding task " + task.toString());
        TaskDB.getInstance().insertTask(task);
    }

    private String[] arraySpinnerPriority;
    private String[] arraySpinnerGoalUnits;
    Spinner m_spnrPrio;
    Spinner m_spnrGoalUnits;
    Task.Priority  m_prioritySelected;
    Task.GoalUnits m_goalUnitsSelected;

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        return new DatePickerDialog(this, datePickerListener,
                MainActivity.m_activity.m_cal.get(Calendar.YEAR),
                MainActivity.m_activity.m_cal.get(Calendar.MONTH),
                MainActivity.m_activity.m_cal.get(Calendar.DAY_OF_MONTH));
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener(){
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            m_tvAddTaskDeadline.setText(selectedDay + "/" + (selectedMonth + 1) + "/"
                    + selectedYear);
        }
    };

    void initWidgets(){
        m_newTaskName = (EditText)findViewById (R.id.addTaskName);
        m_extendedOptionsLayout = (LinearLayout)findViewById (R.id.llAddTaskOptionsLayout);
        m_vExtendedOnceOptions = getLayoutInflater().inflate(R.layout.add_task_once_options_layout, null);
        m_vExtendedRepeatingOptions = getLayoutInflater().inflate(R.layout.add_task_repeating_options_layout, null);
        m_etGoal = (EditText) m_vExtendedRepeatingOptions.findViewById(R.id.etAddTaskGoal);
        m_radioRepeatGroup = (RadioGroup) m_vExtendedRepeatingOptions.findViewById (R.id.rgRepeat);
        m_tvRepeat = (TextView) m_vExtendedRepeatingOptions.findViewById (R.id.tvAddTaskRepeat);
        m_rgTaskType = (RadioGroup) findViewById (R.id.rgTaskType);
        m_tvAddTaskDeadline = (TextView) m_vExtendedOnceOptions.findViewById(R.id.tvAddTaskOnceDeadLine);
        m_btnSetDate = (Button) m_vExtendedOnceOptions.findViewById(R.id.btnAddTaskOnceSetDate);
        m_newTaskInfo = (EditText)findViewById (R.id.addTaskInfo);
    }

    void initialize(){
        initWidgets();

        m_btnSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(0);
            }
        });

        m_rgTaskType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                RadioButton radioRepeatButton = (RadioButton) findViewById(checkedId);
                switch (radioRepeatButton.getText().toString()) {
                    case "Once":
                        prioOnceChosen();
                        break;
                    default:
                        prioOnceUnChosen();
                        break;
                }
            }
        });

        // setting the repeat textView to change according to selection
        m_radioRepeatGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                RadioButton radioRepeatButton = (RadioButton) findViewById(checkedId);
                switch (radioRepeatButton.getText().toString()) {
                    case "Weekly":
                        m_tvRepeat.setText("A Week");
                        break;
                    case "Monthly":
                        m_tvRepeat.setText("A Month");
                        break;
                    default:
                        break;
                }
            }
        });

        //init priority selection
        this.arraySpinnerPriority = new String[] {
                "High", "Medium", "Low"
        };

        m_spnrPrio = (Spinner) findViewById(R.id.spnrAddTaskPriority);
        ArrayAdapter<String> adapterPrio = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinnerPriority);
        m_spnrPrio.setAdapter(adapterPrio);
        m_prioritySelected = Task.Priority.valueOf(starting_priority);
        m_spnrPrio.setSelection(Arrays.asList(arraySpinnerPriority).indexOf(starting_priority));
        m_spnrPrio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long i)
            {
                m_prioritySelected = Task.Priority.valueOf(arraySpinnerPriority[position]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        //todo Need to make choose on this spinner change the variable m_prioritySelected
        Log.d("AddTaskActivity","Need to make choose on this spinner change the variable m_prioritySelected\n");

        //init goal units selection
        this.arraySpinnerGoalUnits = new String[] {
                "Hours", "Minutes", "Sessions"
        };
        m_spnrGoalUnits = (Spinner) m_vExtendedRepeatingOptions.findViewById(R.id.spnrAddTaskGoalUnits);
        ArrayAdapter<String> adapterGoalUnits = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinnerGoalUnits);
        m_spnrGoalUnits.setAdapter(adapterGoalUnits);

        m_goalUnitsSelected = Task.GoalUnits.valueOf(starting_goal_units);
        m_spnrGoalUnits.setSelection(Arrays.asList(arraySpinnerGoalUnits).indexOf(starting_goal_units));
        m_spnrGoalUnits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long i)
            {
                m_goalUnitsSelected = Task.GoalUnits.valueOf(arraySpinnerGoalUnits[position]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
        //todo Need to make choose on this spinner change the variable m_goalUnitsSelected
        Log.d("AddTaskActivity","Need to make choose on this spinner change the variable m_goalUnitsSelected\n");

        prioOnceChosen();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_add_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initialize();

        final Button addTaskButton = (Button) findViewById(R.id.btnAddTaskCreateTask);
        addTaskButton.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(android.view.View v) {
                // Perform action on click
                addTaskToTable();
            }
        });


    }

}
