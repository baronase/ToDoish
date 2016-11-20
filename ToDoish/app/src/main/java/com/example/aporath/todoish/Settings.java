package com.example.aporath.todoish;

/**
 * Created by porata1 on 9/27/2016.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import static android.content.Context.MODE_PRIVATE;


public class Settings {
    //consts
    final String MY_PREFS_NAME = "MyPrefsName";

/*Steps to add a new setting to the app:
1. add the setting field to the "Configuration" class
2. add the setting's init & load function in the init/load functions
3. add a getter
4. add a setter for when the setting changes
5. The setter should invoke a commit to the settings file
*/

    //classes
    private class Configuration {
        // main activity TaskListView settings
        boolean main_activity_TaskListView_ShowCompleted;
        boolean main_activity_TaskListView_ShowRepeating;
        boolean main_activity_TaskListView_ShowOnce;

        //C'tor
        Configuration(){
            main_activity_TaskListView_ShowCompleted = true;
            main_activity_TaskListView_ShowRepeating = true;
            main_activity_TaskListView_ShowOnce = true;
        }
    }

    SharedPreferences m_prefs;
    Configuration     m_configuration;

    Settings(Context context) {
        m_prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        initSettings();

        //main task view preferences
        //theme
    }


    void initSettings() {
        m_configuration = new Configuration();
        loadSettings();
    }

    void loadSettings() {
        m_configuration.main_activity_TaskListView_ShowCompleted = m_prefs.getBoolean("main_activity_TaskListView_ShowCompleted", true);
        m_configuration.main_activity_TaskListView_ShowRepeating = m_prefs.getBoolean("main_activity_TaskListView_ShowRepeating", true);
        m_configuration.main_activity_TaskListView_ShowOnce = m_prefs.getBoolean("main_activity_TaskListView_ShowOnce", true);
    }

    public void storeBoolean (String field, boolean flag) {
        Editor editor = m_prefs.edit();
        editor.putBoolean(field, flag);
        editor.commit();
    }

    //getters
    public boolean get_main_activity_TaskListView_ShowCompleted(){
        return m_configuration.main_activity_TaskListView_ShowCompleted;
    };
    public boolean get_main_activity_TaskListView_ShowRepeating(){
        return m_configuration.main_activity_TaskListView_ShowRepeating;
    };
    public boolean get_main_activity_TaskListView_ShowOnce(){
        return m_configuration.main_activity_TaskListView_ShowOnce;
    };

    //setters
    public void set_main_activity_TaskListView_ShowCompleted(boolean flag){
        m_configuration.main_activity_TaskListView_ShowCompleted = flag;
        storeBoolean("main_activity_TaskListView_ShowCompleted", flag);
    }
    public void set_main_activity_TaskListView_ShowRepeating(boolean flag){
        m_configuration.main_activity_TaskListView_ShowRepeating = flag;
        storeBoolean("main_activity_TaskListView_ShowRepeating", flag);
    }
    public void set_main_activity_TaskListView_ShowOnce(boolean flag){
        m_configuration.main_activity_TaskListView_ShowOnce = flag;
        storeBoolean("main_activity_TaskListView_ShowOnce", flag);
    }


//        public void storeSettings () {
//            Editor editor = m_prefs.edit();
////          editor.putString("name", "Elena");
////          editor.putInt("idName", 12);
//            editor.commit();
//        }

}