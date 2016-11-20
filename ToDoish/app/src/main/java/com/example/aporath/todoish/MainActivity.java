package com.example.aporath.todoish;


import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import android.location.Location;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
    //googleMap
//        OnMyLocationButtonClickListener,
//        OnMapReadyCallback,
//                ActivityCompat.OnRequestPermissionsResultCallback
{

    static MainActivity m_activity;

    AlarmReceiver m_receiver;
    Settings      m_appSettings;

    //todo could be removed?
//    LayoutInflater m_inflater;
//    private AlertDialog m_taskProgressDialog;
    private AlertDialog m_alertDialog;

    Button   m_btnRefreshTable;
    Button   m_btnClearTable;
    Button   m_btnAddTask;
    Button   m_btnResetRepeating;
    CheckBox m_cbShowCompleted;
    CheckBox m_cbShowRepeating;
    CheckBox m_cbShowOnce;
    //todo alarm code - remove if unused --START
//    private IntentFilter m_brFilterTimeTick;
//    private BroadcastReceiver m_brResetRepeating;
    //todo alarm code - remove if unused --END

    //onClicks
//    View.OnClickListener m_btnLoadTableOnClick;
//    View.OnClickListener m_btnClearTableOnClick;
//    View.OnClickListener m_btnAddTask;


    //todo alarmManager code - remove when done --START
//    private PendingIntent m_pendingIntent;
    //todo alarmManager code - remove when done --END

    public GoogleApiClient m_GoogleApiClient;

    static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION   = 133;
    static final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 134;

    public static MainActivity getM_activity(){ return m_activity;};

    //Calender
    public Calendar m_cal;

    //TaskTable
    private TaskTable m_taskTable;


    void clearTaskDB() {
        TaskDB.getInstance().clearTaskDB();
        m_taskTable.clear(); //todo remove from this location, its only for debug purposes
    }

    public void addTaskActivity() {
        // Perform action on click
        Intent intent = new Intent(this, AddTaskActivity.class);
        startActivity(intent);
    }

    public void settingsActivity() {
        // Perform action on click
//        Intent intent = new Intent(this, NewTaskActivity.class);
//        startActivity(intent);
    }

    void mainInitialize() {

        m_activity = this;

        //init widgets
        m_btnRefreshTable = (Button) findViewById(R.id.btnRefreshTable);
        m_btnClearTable = (Button) findViewById(R.id.btnClearTable);
        m_btnAddTask = (Button) findViewById(R.id.btnAddNewTask);
        m_btnResetRepeating = (Button) findViewById(R.id.btnResetRepeating);
        m_cbShowCompleted = (CheckBox) findViewById(R.id.cbShowCompleted);
        m_cbShowRepeating = (CheckBox) findViewById(R.id.cbShowRepeating);
        m_cbShowOnce = (CheckBox) findViewById(R.id.cbShowOnce);

        m_appSettings = new Settings(getApplicationContext());

        m_cbShowCompleted.setChecked(m_appSettings.get_main_activity_TaskListView_ShowCompleted());
        m_cbShowRepeating.setChecked(m_appSettings.get_main_activity_TaskListView_ShowRepeating());
        m_cbShowOnce.setChecked(m_appSettings.get_main_activity_TaskListView_ShowOnce());

        m_cbShowCompleted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                m_appSettings.set_main_activity_TaskListView_ShowCompleted(isChecked);
            }
        });
        m_cbShowRepeating.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                m_appSettings.set_main_activity_TaskListView_ShowRepeating(isChecked);
            }
        });
        m_cbShowOnce.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                m_appSettings.set_main_activity_TaskListView_ShowOnce(isChecked);
            }
        });

        //init Calender
        m_cal = Calendar.getInstance();
        //init TaskTable
        TaskDB.getInstance().initHelper(getApplicationContext());
        m_taskTable = new TaskTable((ListView) findViewById(R.id.lvMainTasks));


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Clear Task");
        alertDialogBuilder
                .setMessage("Are You Sure?")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, clear the TaskDB
                        clearTaskDB();
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        m_alertDialog = alertDialogBuilder.create();

    }

    public boolean getShowSettingsCompleted(){ return m_cbShowCompleted.isChecked();}
    public boolean getShowSettingsRepeating(){ return m_cbShowRepeating.isChecked();}
    public boolean getShowSettingsOnce()     { return m_cbShowOnce.isChecked();}

    void refreshTable() { m_taskTable.refreshTable(this); }

    void testButtonPressed() {
//        registerReceiver(m_brResetRepeating, m_brFilterTimeTick);
        Log.d("MainActivity ", "testButtonPressed");
//        tasksTableResetRepeating("Weekly");
//        Intent intent = new Intent(this, TasksMapsActivity.class);
//        startActivity(intent);
    }

    void startAlarmReceiver() {
        m_receiver = new AlarmReceiver();
        m_receiver.setAlarm(this);
    }

    void tasksTableResetRepeating(String rate) { m_taskTable.resetRepeating(rate); }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                }
                return;
            }
            case MY_PERMISSIONS_ACCESS_FINE_LOCATION: {
                return;
            }
                // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_ACTION_BAR);

        setContentView(R.layout.activity_main);

        mainInitialize();
//        loadTable();
        startAlarmReceiver();

        //start location services
// Create an instance of GoogleAPIClient.
        if (m_GoogleApiClient == null) {
            m_GoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
//            req permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
        }

        m_btnRefreshTable.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(android.view.View v) {
                // Perform action on click
                refreshTable();
            }
        });

        m_btnClearTable.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(android.view.View v) {
                // Perform action on click
                m_alertDialog.show();
            }
        });

        m_btnAddTask.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click
                    addTaskActivity();
            }
        });

        m_btnResetRepeating.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click
                    testButtonPressed();
            }
        });

        final ImageButton buttonSettings = (ImageButton) findViewById(R.id.btnMainSettings);
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Log.d("Settings","Setting Activity DDDDDDDDDDDDDDDDD");
                settingsActivity();
            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();
        // put your code here...
        refreshTable();
        //start location todo
        m_GoogleApiClient.connect();
    }

    public void onStart(){
        m_GoogleApiClient.connect();
        super.onStart();
        // put your code here...
        //stop location todo
    }

    public void onStop(){
        m_GoogleApiClient.connect();
        super.onStop();
        // put your code here...
        //stop location todo
    }

    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(m_brResetRepeating);
        //stop location todo
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "NO LOCATION PERMISSION", Toast.LENGTH_LONG).show();
        }
        else {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    m_GoogleApiClient);
            Toast.makeText(this, "LOCATION: Latitude - " + String.valueOf(mLastLocation.getLatitude())
                            + "  Longitude - " + String.valueOf(mLastLocation.getLongitude()),
                    Toast.LENGTH_LONG).show();
        }
        //todo
    }

    @Override
    public void onConnectionSuspended(int i) {
        //todo
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //todo
    }
}
//textbox that can add rows
//save rows to CSV