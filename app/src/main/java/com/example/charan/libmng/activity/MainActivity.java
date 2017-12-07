package com.example.charan.libmng.activity;


import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.charan.libmng.R;
import com.example.charan.libmng.adapters.LibAdp;
import com.example.charan.libmng.modal.LibData;
import com.example.charan.libmng.modal.LibData;
import com.example.charan.libmng.sqlite.SqliteHelper;
import com.example.charan.libmng.sqlite.SearchActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    FloatingActionButton addTask;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<LibData> tdd = new ArrayList<>();
    SqliteHelper mysqlite;
    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_s);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        addTask = (FloatingActionButton) findViewById(R.id.imageButton);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        adapter = new LibAdp(tdd, getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.accent), getResources().getColor(R.color.divider));
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                updateCardView();
            }
        });
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.custom_dailog);
                dialog.show();
                Button save = (Button) dialog.findViewById(R.id.btn_save);
                Button cancel = (Button) dialog.findViewById(R.id.btn_cancel);
                CheckBox cb = (CheckBox) dialog.findViewById(R.id.checkbox);
                TextView tvstatus = (TextView) dialog.findViewById(R.id.status);
                cb.setVisibility(View.GONE);
                tvstatus.setVisibility(View.GONE);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText bookText = (EditText) dialog.findViewById(R.id.input_task_desc);
                        EditText bookNotes = (EditText) dialog.findViewById(R.id.input_task_notes);
                        if (bookText.getText().length() >= 2) {
                            RadioGroup proritySelection = (RadioGroup) dialog.findViewById(R.id.toDoRG);
                            String RadioSelection = new String();
                            if (proritySelection.getCheckedRadioButtonId() != -1) {
                                int id = proritySelection.getCheckedRadioButtonId();
                                View radiobutton = proritySelection.findViewById(id);
                                int radioId = proritySelection.indexOfChild(radiobutton);
                                RadioButton btn = (RadioButton) proritySelection.getChildAt(radioId);
                                RadioSelection = (String) btn.getText();
                            }
                            Spinner getTime = (Spinner) dialog.findViewById(R.id.spinner);
                            EditText timeInNumb = (EditText) dialog.findViewById(R.id.input_task_time);
                            if(getTime.getSelectedItem().toString().matches("Days") && !(timeInNumb.getText().toString().matches(""))) {
                                int longtime = Integer.parseInt(timeInNumb.getText().toString());
                                long miliTime = longtime * 24 * 60 * 60 * 1000 ;
                                scheduleNotification(miliTime,bookText.getText().toString(),RadioSelection);
                            } else if (getTime.getSelectedItem().toString().matches("Minutes") && !(timeInNumb.getText().toString().matches(""))) {
                                int longtime = Integer.parseInt(timeInNumb.getText().toString());
                                long miliTime = longtime * 60 * 1000 ;
                                scheduleNotification(miliTime,bookText.getText().toString(),RadioSelection);
                            } else if (getTime.getSelectedItem().toString().matches("Hours") && !(timeInNumb.getText().toString().matches(""))) {
                                int longtime = Integer.parseInt(timeInNumb.getText().toString());
                                long miliTime = longtime * 60 * 60 * 1000 ;
                                scheduleNotification(miliTime,bookText.getText().toString(),RadioSelection);
                            }
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("BookDetails", bookText.getText().toString());
                            contentValues.put("ToReadPrority", RadioSelection);
                            contentValues.put("ToReadStatus", "Incomplete");
                            contentValues.put("BookNotes", bookNotes.getText().toString());
                            mysqlite = new SqliteHelper(getApplicationContext());
                            Boolean b = mysqlite.insertInto(contentValues);
                            if (b) {
                                dialog.hide();
                                updateCardView();
                            } else {
                                Toast.makeText(getApplicationContext(), "Some thing went wrong", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "Please enter BookDatails", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

            }
        });
    }
    public void scheduleNotification(long time, String TaskTitle, String TaskPrority) {
        Calendar Calendar_Object = Calendar.getInstance();
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        final int _id = (int) System.currentTimeMillis();
        Intent myIntent = new Intent(MainActivity.this, AlarmRecever.class);
        myIntent.putExtra("TaskTitle", TaskTitle);
        myIntent.putExtra("TaskPrority",TaskPrority);
        myIntent.putExtra("id",_id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,
                _id, myIntent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.set(AlarmManager.RTC, Calendar_Object.getTimeInMillis() + time,
                pendingIntent);

    }
    public void updateCardView() {
        swipeRefreshLayout.setRefreshing(true);
        mysqlite = new SqliteHelper(getApplicationContext());
        Cursor result = mysqlite.selectAllData();
        if (result.getCount() == 0) {
            tdd.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(), "No Tasks", Toast.LENGTH_SHORT).show();
        } else {
            tdd.clear();
            adapter.notifyDataSetChanged();
            while (result.moveToNext()) {
               LibData tddObj = new LibData();
                tddObj.setBookID(result.getInt(0));
                tddObj.setBookDetails(result.getString(1));
                tddObj.setToReadPrority(result.getString(2));
                tddObj.setToReadStatus(result.getString(3));
                tddObj.setBookNotes(result.getString(4));
                tdd.add(tddObj);
            }
            adapter.notifyDataSetChanged();
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        updateCardView();
    }
    public void search(View v)
    {
        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
        startActivity(intent);
    }
}
