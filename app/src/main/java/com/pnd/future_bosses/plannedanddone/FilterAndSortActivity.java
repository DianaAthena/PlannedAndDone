package com.pnd.future_bosses.plannedanddone;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import java.util.Calendar;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

public class FilterAndSortActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_and_sort);

        LinearLayout layoutCategories = (LinearLayout) findViewById(R.id.Categories);
        layoutCategories.removeAllViews();
        Uri table = Uri.parse( "content://hr.math.provider.contprov/category");

        Cursor c;
        if (android.os.Build.VERSION.SDK_INT <11) {
            //---before Honeycomb---
            c = managedQuery(table, null, null, null, null);
        } else {
            //---Honeycomb and later---
            CursorLoader cursorLoader = new CursorLoader(
                    this,
                    table, null, null, null, null);
            c = cursorLoader.loadInBackground();
        }
        if (c.moveToFirst())
        {
            do {
                CheckBox newCategory = new CheckBox(this);
                newCategory.setText(c.getString(1));
                newCategory.setTag(c.getInt(0));
                layoutCategories.addView(newCategory);

            } while (c.moveToNext());
        }
        else{
            TextView newCategory = new TextView(this);
            newCategory.setText("You don't have any categories yet.");
            newCategory.setTag(-1);
            layoutCategories.addView(newCategory);
        }
    }


    public void searchAndSort(View view){

        String where = null;
        String priority = null;
        String deadline = null;
        String plannedTime = null;
        String category = null;

        //PRIORITY
        CheckBox checkBox = (CheckBox)this.findViewById(R.id.priorityHighest);
        if (checkBox.isChecked()) priority =  DataBase.TASK_PRIORITY + "= 1";

        checkBox = (CheckBox)this.findViewById(R.id.priorityMedium);
        if (checkBox.isChecked())
            if( priority == null)  priority =  DataBase.TASK_PRIORITY + "= 2";
                else priority +=  " OR " + DataBase.TASK_PRIORITY + "= 2";

        checkBox = (CheckBox)this.findViewById(R.id.priorityLowest);
        if (checkBox.isChecked())
            if( priority == null)  priority =  DataBase.TASK_PRIORITY + "= 3";
            else priority +=  " OR " + DataBase.TASK_PRIORITY + "= 3";

        Calendar calendar= Calendar.getInstance();
        String datum = null;

        //DEADLINE
        checkBox = (CheckBox)this.findViewById(R.id.deadlineDay);
        if (checkBox.isChecked()) {
            //slaganje datuma..
            datum = null;

            String year = String.valueOf(calendar.get(Calendar.YEAR));
            datum = year;

            int month = calendar.get(Calendar.MONTH)+1;
            if (month < 10) datum += "0" +  String.valueOf(month);
            else datum += String.valueOf(month);

            int day = calendar.get(Calendar.DAY_OF_MONTH);
            if (day < 10) datum += "0" +  String.valueOf(day);
            else  datum += String.valueOf(day);

            deadline =  DataBase.TASK_DEADLINE + " LIKE '" + datum + "%'";
        }

        checkBox = (CheckBox)this.findViewById(R.id.deadlineMonth);
        if (checkBox.isChecked()){
            //slaganje datuma..
            datum = null;

            String year = String.valueOf(calendar.get(Calendar.YEAR));
            datum = year;

            int month = calendar.get(Calendar.MONTH)+1;
            if (month < 10) datum += "0" +  String.valueOf(month);
            else datum += String.valueOf(month);

            if (deadline == null) deadline =  DataBase.TASK_DEADLINE + " LIKE '" + datum + "%'";
                else deadline += " OR " + DataBase.TASK_DEADLINE + " LIKE '" + datum + "%'";
        }

        //PLANNED
        checkBox = (CheckBox)this.findViewById(R.id.plannedDay);
        if (checkBox.isChecked()) {
            //slaganje datuma..
            datum = null;

            String year = String.valueOf(calendar.get(Calendar.YEAR));
            datum = year;

            int month = calendar.get(Calendar.MONTH)+1;
            if (month < 10) datum += "0" +  String.valueOf(month);
            else datum += String.valueOf(month);

            int day = calendar.get(Calendar.DAY_OF_MONTH);
            if (day < 10) datum += "0" +  String.valueOf(day);
            else  datum += String.valueOf(day);

            plannedTime =  DataBase.TASK_TIME + " LIKE '" + datum + "%'";
        }

        checkBox = (CheckBox)this.findViewById(R.id.plannedMonth);
        if (checkBox.isChecked()){
            //slaganje datuma..
            datum = null;

            String year = String.valueOf(calendar.get(Calendar.YEAR));
            datum = year;

            int month = calendar.get(Calendar.MONTH)+1;
            if (month < 10) datum += "0" +  String.valueOf(month);
            else datum += String.valueOf(month);

            if (plannedTime == null) plannedTime =  DataBase.TASK_TIME + " LIKE '" + datum + "%'";
            else plannedTime += " OR " + DataBase.TASK_TIME + " LIKE '" + datum + "%'";
        }

        //CATEGORY
        LinearLayout kategorije = (LinearLayout)findViewById(R.id.Categories);
        int count = kategorije.getChildCount();

        //provjeravam tko mu je prvo dijete
        if ((int)(kategorije.getChildAt(0)).getTag() != -1 ) {
            for(int i = 0; i < count; i++ ){
                CheckBox check = (CheckBox) kategorije.getChildAt(i);
                if(check.isChecked()){
                    if(category == null) category = DataBase.TASK_CATEGORY + " = " + (int)check.getTag();
                    else category += " OR " + DataBase.TASK_CATEGORY + " = " + (int)check.getTag();
                }
            }
        }

        //KREIRANJE UVIJETA ZA UPIT
        if(priority != null) where = "( " + priority +" )";

        if (deadline != null){
            if (where == null) where = "( " + deadline +" )";
            else where += " AND ( " + deadline +" )";
        }

        if (plannedTime != null){
            if (where == null) where = "( " + plannedTime +" )";
            else where += " AND ( " + plannedTime +" )";
        }

        if(category != null){
            if (where == null) where = "( " + category +" )";
            else where += " AND ( " + category +" )";
        }

        //SORT..
        String sortBy = null;
        RadioGroup radioGroup = (RadioGroup) this.findViewById(R.id.sortBy);
        int checkedRadio = radioGroup.getCheckedRadioButtonId();

        switch (checkedRadio){
            case R.id.deadlineUp:
                sortBy = DataBase.TASK_DEADLINE + " ASC";
                break;
            case R.id.deadlineDown:
                sortBy = DataBase.TASK_DEADLINE + " DESC";
                break;
            case R.id.plannedTimeUp:
                sortBy = DataBase.TASK_TIME + " ASC";
                break;
            case R.id.plannedTimeDown:
                sortBy = DataBase.TASK_TIME + " DESC";
                break;
            case R.id.priorityDown:
                sortBy = DataBase.TASK_PRIORITY + " ASC";
                break;
            case R.id.priorityUp:
                sortBy = DataBase.TASK_PRIORITY + " DESC";
                break;
            default:
                sortBy = DataBase.TASK_TIME + " ASC";
        }



        if ( where != null ){
            Log.e("FILTER AND SORT- where", where);
            Log.e("FILTER AND SORT- sortBy", sortBy);

        }

        Intent intent = new Intent(FilterAndSortActivity.this, MainActivity.class);
        intent.putExtra("WHERE", where);
        intent.putExtra("SORT", sortBy);
        startActivity(intent);

    }
}
