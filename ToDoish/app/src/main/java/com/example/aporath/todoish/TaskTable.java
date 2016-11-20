package com.example.aporath.todoish;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by porata1 on 9/25/2016.
 */
public     class TaskTable {
    HashMap<String, Task> m_taskMap;

    private final ListView m_lvLayout;

    TaskTable (ListView listView) {
        m_taskMap = new HashMap<String, Task>();
        m_lvLayout = listView;
    }

    //reset the progress of repeating tasks following a time event
    public void resetRepeating(String rate) {
        loadTaskDBIntoTaskMap();
        for(Map.Entry<String, Task> entry : m_taskMap.entrySet())
            entry.getValue().resetProgress(rate);
    };
    public void clear() { m_taskMap.clear(); }
    public void refreshTable(Context context)  {
        loadTaskDBIntoTaskMap();
        taskMapIntoListView(context);
    }

    //filter out unwanted tasks from view
    void applyTaskFilters(List<Task> list) {
        if (!MainActivity.getM_activity().getShowSettingsCompleted()){
            for (int i = list.size() - 1; i >= 0; i--) {
                if (list.get(i).m_progress == list.get(i).m_goal)
                    list.remove(i);
            }
        }

        if (!MainActivity.getM_activity().getShowSettingsRepeating()){
            for (int i = list.size() - 1; i >= 0; i--) {
                if (list.get(i).m_isRepeating)
                    list.remove(i);
            }
        }

        if (!MainActivity.getM_activity().getShowSettingsOnce()){
            for (int i = list.size() - 1; i >= 0; i--) {
                if (!list.get(i).m_isRepeating)
                    list.remove(i);
            }
        }
    };

    private void loadTaskDBIntoTaskMap() {
        m_taskMap.clear();

        //for loop on taskDB and load all tasks.
        Cursor cur = TaskDB.getInstance().getTaskCursor();

        if (cur.isAfterLast())
        {
            Log.d(getClass().getSimpleName(), "Empty Cursor in loadTaskDBIntoTaskMap\n");
            return;
        }

        do
        {
            String taskName = cur.getString(cur.getColumnIndex(TaskDB.TaskTableContract.TaskTableEntry.COLUMN_NAME_TASK_NAME));
            m_taskMap.put(taskName, new Task(cur));
        }
        while(cur.moveToNext());
    }

    private void taskMapIntoListView(Context context) {
        List<Task> list = new ArrayList<Task>(m_taskMap.values());

        applyTaskFilters(list);

        TaskListAdapter adapter = new TaskListAdapter(
                context,
                R.layout.task_once_listview_layout,
                list);
        m_lvLayout.setAdapter(adapter);
    }
}//class TaskTable

