package com.pnd.future_bosses.plannedanddone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class Calendar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);


        Log.e("Calendar", "usam sam u klasu");

        SimpleCalendar simpleCalendar = (SimpleCalendar) findViewById(R.id.square_day);

        simpleCalendar.setUserCurrentMonthYear(2, 2017);

        simpleCalendar.setCallBack(new SimpleCalendar.DayClickListener() {
            @Override
            public void onDayClick(View view) {

            }
        });

    }
}
