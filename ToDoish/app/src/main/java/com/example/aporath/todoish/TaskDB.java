package com.example.aporath.todoish;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import junit.framework.Assert;

import static java.security.AccessController.getContext;


/**
 * Created by aporath on 5/20/16.
 */
public class TaskDB {

//    private static SQLiteDatabase mDatabase;
    private static TaskTableDbHelper taskTablehelper;

    //Singleton methods
    private static TaskDB ourInstance = new TaskDB();

    public static TaskDB getInstance() {
        return ourInstance;
    }

    //Contract
    public final class TaskTableContract {
        // To prevent someone from accidentally instantiating the contract class,
        // give it an empty constructor.
        public TaskTableContract() {
        }

        /* Inner class that defines the table contents */
        public abstract class TaskTableEntry implements BaseColumns {
            public static final String TABLE_NAME = "TaskTable";
            public static final String COLUMN_NAME_TASK_NAME = "TaskName";
            public static final String COLUMN_NAME_GOAL = "Goal";
            public static final String COLUMN_NAME_PROGRESS = "Progress";
            public static final String COLUMN_NAME_GOALUNITS = "GoalUnits";
            public static final String COLUMN_NAME_IS_REPEATING = "IsRepeating";
            public static final String COLUMN_NAME_REPEAT = "Repeat";
            public static final String COLUMN_NAME_PRIORITY = "Priority";
            public static final String COLUMN_NAME_DATE = "Date";
            public static final String COLUMN_NAME_TAGS = "Tags";
            public static final String COLUMN_NAME_LOCATION = "Location";
            public static final String COLUMN_NAME_TASK_INFO = "TaskInfo";
        }
    }

    //basic action helpers
    private static final String TEXT_TYPE = " text";
    private static final String NUMERATOR_TYPE = " text";
    private static final String COMMA_SEP = ",";
    private static final String STAR_ALL = "*";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TaskTableContract.TaskTableEntry.TABLE_NAME + " (" +
                    TaskTableContract.TaskTableEntry.COLUMN_NAME_TASK_NAME + TEXT_TYPE + COMMA_SEP +
                    TaskTableContract.TaskTableEntry.COLUMN_NAME_GOAL + NUMERATOR_TYPE + COMMA_SEP +
                    TaskTableContract.TaskTableEntry.COLUMN_NAME_PROGRESS + NUMERATOR_TYPE + COMMA_SEP +
                    TaskTableContract.TaskTableEntry.COLUMN_NAME_GOALUNITS + TEXT_TYPE + COMMA_SEP +
                    TaskTableContract.TaskTableEntry.COLUMN_NAME_IS_REPEATING + TEXT_TYPE + COMMA_SEP +
                    TaskTableContract.TaskTableEntry.COLUMN_NAME_REPEAT + TEXT_TYPE + COMMA_SEP +
                    TaskTableContract.TaskTableEntry.COLUMN_NAME_PRIORITY + TEXT_TYPE + COMMA_SEP +
                    TaskTableContract.TaskTableEntry.COLUMN_NAME_DATE + TEXT_TYPE + COMMA_SEP +
                    TaskTableContract.TaskTableEntry.COLUMN_NAME_TAGS + TEXT_TYPE + COMMA_SEP +
                    TaskTableContract.TaskTableEntry.COLUMN_NAME_LOCATION + TEXT_TYPE + COMMA_SEP +
                    TaskTableContract.TaskTableEntry.COLUMN_NAME_TASK_INFO + TEXT_TYPE +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TaskTableContract.TaskTableEntry.TABLE_NAME;


    //SQLite Helper
    public class TaskTableDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 2;
        public static final String DATABASE_NAME = "ToDoish_TaskTable.db";

        public TaskTableDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate (SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over

            Log.d("TaskDb ", "onUpgrade- oldVersion:" + oldVersion + " newVersion: " + newVersion);

//            Delete and re create
//            db.execSQL(SQL_DELETE_ENTRIES);
//            onCreate(db);
//
            //check that the old version is not already new version so there won't be a double update
//            int i = 10 / (DATABASE_VERSION - oldVersion);
            //just to use i for compilation..
//            if (i == 11)
//                return;
//
//             If you need to add a column
//            if (newVersion > oldVersion) {
//                db.execSQL("ALTER TABLE " + TaskTableContract.TaskTableEntry.TABLE_NAME + " ADD COLUMN " +
//                        TaskTableContract.TaskTableEntry.COLUMN_NAME_TAGS + NUMERATOR_TYPE + " DEFAULT '0'");
//                db.execSQL("ALTER TABLE " + TaskTableContract.TaskTableEntry.TABLE_NAME + " ADD COLUMN " +
//                        TaskTableContract.TaskTableEntry.COLUMN_NAME_LOCATION + TEXT_TYPE + " DEFAULT 'a'");
//                db.execSQL("ALTER TABLE " + TaskTableContract.TaskTableEntry.TABLE_NAME + " ADD COLUMN " +
//                        TaskTableContract.TaskTableEntry.COLUMN_NAME_TASK_INFO + TEXT_TYPE + " DEFAULT 'a'");
//            }
        }

        public void onClear(SQLiteDatabase db) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

    public void initHelper(Context context) {
        taskTablehelper = new TaskTableDbHelper(context);
    }

    //clearTaskDB
    public void clearTaskDB() {
        // Gets the data repository in write mode
        SQLiteDatabase db = taskTablehelper.getWritableDatabase();
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    //insertTask
    public long insertTask(Task task) {
        // Gets the data repository in write mode
        SQLiteDatabase db = taskTablehelper.getWritableDatabase();
        Log.d("TaskDb ", "task deadline- format:" + Task.m_formatter.format(task.m_deadline));
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(TaskTableContract.TaskTableEntry.COLUMN_NAME_TASK_NAME, task.m_taskName);
        values.put(TaskTableContract.TaskTableEntry.COLUMN_NAME_GOAL, task.m_goal);
        values.put(TaskTableContract.TaskTableEntry.COLUMN_NAME_PROGRESS, task.m_progress);
        values.put(TaskTableContract.TaskTableEntry.COLUMN_NAME_GOALUNITS, task.m_goalUnits.toString());
        values.put(TaskTableContract.TaskTableEntry.COLUMN_NAME_IS_REPEATING, task.m_isRepeating ? "YES" :"NO");
        values.put(TaskTableContract.TaskTableEntry.COLUMN_NAME_REPEAT, task.m_repeat);
        values.put(TaskTableContract.TaskTableEntry.COLUMN_NAME_PRIORITY, task.m_priority.toString());
        values.put(TaskTableContract.TaskTableEntry.COLUMN_NAME_DATE, Task.m_formatter.format(task.m_deadline));
        //todo replace with real task values
        values.put(TaskTableContract.TaskTableEntry.COLUMN_NAME_TAGS, task.m_tags);
        values.put(TaskTableContract.TaskTableEntry.COLUMN_NAME_LOCATION, task.m_location);
        values.put(TaskTableContract.TaskTableEntry.COLUMN_NAME_TASK_INFO, task.m_taskInfo);


        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                TaskTableContract.TaskTableEntry.TABLE_NAME,
                null,    //TaskTableContract.TaskTableEntry.COLUMN_NAME_NULLABLE,
                values);

        return newRowId;
    }

    //deleteTask
    public void deleteTask(Task task) {
        // Gets the data repository in write mode
        Log.d("TaskDb ", "deleteTask:" + task.m_taskName);

        // Delete Task by taskName
        SQLiteDatabase db = taskTablehelper.getWritableDatabase();
        db.execSQL("delete from "+ TaskTableContract.TaskTableEntry.TABLE_NAME + " where " +
                TaskTableContract.TaskTableEntry.COLUMN_NAME_TASK_NAME + "='" + task.m_taskName + "'");
//        db.execSQL("delete from "+ TaskTableContract.TaskTableEntry.TABLE_NAME +" where Progress='" + task.m_progress + "'");
    }

    //deleteTask
    public void deleteTask(String taskName) {
        // Gets the data repository in write mode
        Log.d("TaskDb ", "deleteTask:" + taskName);

        // Delete Task by taskName
        SQLiteDatabase db = taskTablehelper.getWritableDatabase();
        db.execSQL("delete from "+ TaskTableContract.TaskTableEntry.TABLE_NAME + " where " +
                TaskTableContract.TaskTableEntry.COLUMN_NAME_TASK_NAME + "='" + taskName + "'");
    }

    public Cursor getTaskCursor() {

        SQLiteDatabase db = taskTablehelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                TaskTableContract.TaskTableEntry.COLUMN_NAME_TASK_NAME,
                TaskTableContract.TaskTableEntry.COLUMN_NAME_GOAL,
                TaskTableContract.TaskTableEntry.COLUMN_NAME_PROGRESS,
                TaskTableContract.TaskTableEntry.COLUMN_NAME_GOALUNITS,
                TaskTableContract.TaskTableEntry.COLUMN_NAME_IS_REPEATING,
                TaskTableContract.TaskTableEntry.COLUMN_NAME_REPEAT,
                TaskTableContract.TaskTableEntry.COLUMN_NAME_PRIORITY,
                TaskTableContract.TaskTableEntry.COLUMN_NAME_DATE,
                TaskTableContract.TaskTableEntry.COLUMN_NAME_TAGS,
                TaskTableContract.TaskTableEntry.COLUMN_NAME_LOCATION,
                TaskTableContract.TaskTableEntry.COLUMN_NAME_TASK_INFO,
        };

        String selection = TaskTableContract.TaskTableEntry.COLUMN_NAME_TASK_NAME;
        String[] selectionArgs = {STAR_ALL};

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                TaskTableContract.TaskTableEntry.COLUMN_NAME_TASK_NAME + " DESC";

        Cursor c = db.query(
                TaskTableContract.TaskTableEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,//selection,                                // The columns for the WHERE clause
                null,//selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        c.moveToFirst();
        return c;
    }

        //save/load?
}
