package com.example.martin.coachingreminder;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class MobileMainActivity extends AppCompatActivity {
    public static final String EXTRA_VOICE_REPLY = "extra_voice_reply";
    private static long back_pressed;

    //sd
    SharedPreferences q1;
    SharedPreferences q2 ;
    SharedPreferences q3 ;
    SharedPreferences q4 ;
    SharedPreferences q5;
    SharedPreferences q6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_main);
        q1 = getSharedPreferences("Q1", MODE_PRIVATE);
        q2 = getSharedPreferences("Q2", MODE_PRIVATE);
        q3 = getSharedPreferences("Q3", MODE_PRIVATE);
        q4 = getSharedPreferences("Q4", MODE_PRIVATE);
        q5 = getSharedPreferences("Q5", MODE_PRIVATE);
        q6 = getSharedPreferences("Q6", MODE_PRIVATE);

        //buttons beschroften
        Button mButtonResults = (Button) findViewById(R.id.button);
        final Button mButtonSearch = (Button) findViewById(R.id.button6);

        final EditText SearchText = (EditText) findViewById(R.id.textView1);
        final ListView lv = (ListView) findViewById(R.id.listView);


        mButtonResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ResultsActivity.class);
                startActivity(intent);
            }
        });


        mButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String x =SearchText.getText().toString();
                populatelistview(x);
            }
        });

        lv.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String vtitle = ((TextView) view.findViewById(R.id.textTitle)).getText().toString();
                final String vstart = ((TextView) view.findViewById(R.id.textDate)).getText().toString();

                String question = "How do u feel now after the meeting?";
                String iteration = "1";

                SimpleDateFormat sdfr = new SimpleDateFormat("HH:mm dd.MMM yyyy");
                Date date = null;

                try {
                    date = sdfr.parse(vstart);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long timestamp = date.getTime();

                //   time = event_Start.getTime()+ 1000*24*60*60*1000;
                Long notitime = new GregorianCalendar().getTimeInMillis();
                //TODO auf 1h runterstellen
                notitime = timestamp+ 3*60*60*1000;


                Intent intentAlarm = new Intent(getApplicationContext(), AlarmReciever.class);
                intentAlarm.putExtra("Titel", vtitle);
                intentAlarm.putExtra("Date", vstart);
                intentAlarm.putExtra("realDate", notitime);
                intentAlarm.putExtra("Iteration", iteration);
                intentAlarm.putExtra("Question", question);

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, notitime, PendingIntent.getBroadcast(getApplicationContext(), 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
                Toast.makeText(getApplicationContext(), "Notification for the " + vstart + " has been scheduled", Toast.LENGTH_LONG).show();
                View a = findViewById(R.id.button2);
                //a.setEnabled(true);
                clearSD();
                lv.setAdapter(null);
            }
        });
    }

    private void clearSD() {
        String clear="";
        q1.edit().putString("Q1", clear).apply();
        q2.edit().putString("Q2", clear).apply();
        q3.edit().putString("Q3", clear).apply();
        q4.edit().putString("Q4", clear).apply();
        q5.edit().putString("Q5", clear).apply();
        q6.edit().putString("Q6", clear).apply();
    }

    private void populatelistview(String x) {

        final Cursor cur;
        ContentResolver cr = getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;

        final ListView lv = (ListView) findViewById(R.id.listView);
        //  String selection ;
        String selection = "(" + CalendarContract.Events.TITLE + " = ?)";
        String[] selectionArgs = new String[]{x};
        try {
            // Submit the query and get a Cursor object back.
            cur = cr.query(uri, new String[]{CalendarContract.Events.CALENDAR_ID, CalendarContract.Events._ID, CalendarContract.Events.TITLE, CalendarContract.Events.DESCRIPTION, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND, CalendarContract.Events.EVENT_LOCATION}, selection, selectionArgs, null);

            lv.setAdapter(new MyAdapter(getApplicationContext(), cur));
            int y =lv.getCount();

            if (y == 0){
                Toast.makeText(getApplicationContext(), "No events found", Toast.LENGTH_SHORT).show();
            }

        } catch (SecurityException e) {
            Log.d("CHECK", "Permisson for Calendar not given?");
        }
    }

    public void onBackPressed() {

    }
}
