package com.pnd.future_bosses.plannedanddone;

import android.annotation.TargetApi;
import android.app.Activity;

import android.app.Dialog;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.provider.ContactsContract;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContentResolverCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.ResourceCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.w3c.dom.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public DBAdapter db;
    List<Integer> taskID;
    final public int notificationID = 13;
    AlarmManager am;

    public String  SORTBY;
    public String  WHERE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // notifikacije :
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Intent intent1 = new Intent(MainActivity.this, NotificationBuilder.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0,intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        int curHr = now.get(Calendar.HOUR_OF_DAY);
        int curMin = now.get(Calendar.MINUTE);
        if ((curHr > 9)||(curHr == 9 && curMin > 0))
        {
                calendar.add(Calendar.DATE, 1);
        }


        am = (AlarmManager) MainActivity.this.getSystemService(MainActivity.this.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        //********************************************
        //DOHVATI ZADATKE:
        //********************************************
        Bundle extras = getIntent().getExtras();

        if (extras != null)
        {
            WHERE = extras.getString("WHERE");
            SORTBY = extras.getString("SORT");
        }
        else{
            WHERE = " 1 = 1 ";
            SORTBY = DataBase.TASK_TIME + " ASC";
        }

        printTasks(WHERE, SORTBY);

        Uri table = Uri.parse( "content://hr.math.provider.contprov/category");
        Cursor a = getContentResolver().query(table, new String[]{DataBase.CATEGORY_ID}, "1=1", null, null);
        ContentValues values1 = new ContentValues();
        ContentValues values2 = new ContentValues();
        if(!a.moveToFirst()) {
            values1.put("name", "Work");
            getContentResolver().insert(table, values1);
            values2.put("name", "Home");
            getContentResolver().insert(table, values2);
        }

    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_notifications) {

            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public void deleteAllDone(MenuItem item) {

        new AlertDialog.Builder(this)
                .setTitle("Delete tasks")
                .setMessage("Do you really want to delete all completed tasks?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Uri table = Uri.parse("content://hr.math.provider.contprov/task");
                        String where = DataBase.TASK_DONE + "=1";

                        int count = getContentResolver().delete(table, where, null);

                        if (count > 0)
                            Toast.makeText(MainActivity.this, R.string.completed_tasks_deleted_success, Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(MainActivity.this, R.string.completed_tasks_deleted_not_success, Toast.LENGTH_SHORT).show();
                        printTasks(WHERE, SORTBY);
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();

    }

    //uredi kategorije
    public void updateCategoryClick(MenuItem item) {
        Intent i = new Intent(MainActivity.this, EditCategories.class);
        startActivity(i);
    }

    public void printTasks(String where, String sortBy) {
        //*********************
        //DOHVATI SVE ZADATKE
        //*********************
        Cursor c;
        taskID = new ArrayList<Integer>();
        Uri table = Uri.parse("content://hr.math.provider.contprov/task");
        if (android.os.Build.VERSION.SDK_INT < 11) {
            c = getContentResolver().query(table, new String[]{DataBase.TASK_ID, DataBase.TASK_NAME, DataBase.TASK_TIME, DataBase.TASK_DEADLINE, DataBase.TASK_PRIORITY, DataBase.TASK_CATEGORY, DataBase.TASK_DONE }, where, null, sortBy);

        } else {
            CursorLoader cursorLoader = new CursorLoader(this, table, null, null, null, null);
            c = getContentResolver().query(table, new String[]{DataBase.TASK_ID, DataBase.TASK_NAME, DataBase.TASK_TIME, DataBase.TASK_DEADLINE, DataBase.TASK_PRIORITY, DataBase.TASK_CATEGORY, DataBase.TASK_DONE }, where, null, sortBy);
        }

        //*********************
        //PRIKAZI ZADATKE U MAIN_AC
        //AKTIVNI & ZAVRSENI
        //*********************
        LinearLayout plannedTasks = (LinearLayout) findViewById(R.id.plannedTasksLayout);
        plannedTasks.removeAllViews();
        LinearLayout doneTasks = (LinearLayout) findViewById(R.id.doneTasksLayout);
        doneTasks.removeAllViews();

        if (c.moveToFirst()) {
            do {
                //DisplayTask(c);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View taskLayout = inflater.inflate(R.layout.listview_item, null);
                taskLayout.setTag(c.getInt(c.getColumnIndex(DataBase.TASK_ID)));

                CheckBox check = (CheckBox) taskLayout.findViewById((R.id.checkBox));
                check.setTag(c.getInt(c.getColumnIndex(DataBase.TASK_ID)));


                ImageButton editButton = (ImageButton) taskLayout.findViewById(R.id.editButton);
                editButton.setTag(c.getInt(c.getColumnIndex(DataBase.TASK_ID)));

                TextView taskName = (TextView) taskLayout.findViewById(R.id.taskName);
                taskName.setText(c.getString(c.getColumnIndex(DataBase.TASK_NAME)));

                TextView taskTime = (TextView) taskLayout.findViewById(R.id.taskDate);
                String planned_ = c.getString(c.getColumnIndex(DataBase.TASK_TIME));
                String planned = "-";
                if(!planned_.equals(""))
                     planned = planned_.substring(6,8) + "/" + planned_.substring(4,6) + "/" + planned_.substring(0,4) + ", " + planned_.substring(8,10) + ":" + planned_.substring(10,12);
                taskTime.setText(taskTime.getText() + planned);

                TextView taskDeadline = (TextView) taskLayout.findViewById(R.id.taskDeadline);
                String deadline_ = c.getString(c.getColumnIndex(DataBase.TASK_DEADLINE));
                String deadline = "-";
                if(!deadline_.equals(""))
                    deadline = deadline_.substring(6,8) + "/" + deadline_.substring(4,6) + "/" + deadline_.substring(0,4) + ", " + deadline_.substring(8,10) + ":" + deadline_.substring(10,12);
                taskDeadline.setText(taskDeadline.getText() + deadline);

                ImageView priorityImg = (ImageView) taskLayout.findViewById((R.id.priorityImg));
                switch (c.getInt(4)) {
                    case 1:
                        priorityImg.setImageResource(R.drawable.crveni);
                        break;
                    case 2:
                        priorityImg.setImageResource(R.drawable.zuti);
                        break;
                    case 3:
                        priorityImg.setImageResource(R.drawable.zeleni);
                        break;
                    default:
                        priorityImg.setImageResource(R.drawable.sivi);
                        break;
                }

                switch (c.getInt(c.getColumnIndex(DataBase.TASK_DONE))) {
                    case 1:
                        editButton.setImageResource(R.drawable.edit_icon1);
                        editButton.setEnabled(false);
                        check.setChecked(true);
                        doneTasks.addView(taskLayout);
                        break;
                    case 0:
                        plannedTasks.addView(taskLayout);
                        break;
                    default:
                        break;
                }


                // Event handler za long click na task : brisanje zadatka
                taskLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        final int id = (int) ((LinearLayout) v).getTag();
                        new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.myDialog))
                                .setTitle("Delete task")
                                .setMessage("Do you really want to delete this task?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        Uri table = Uri.parse("content://hr.math.provider.contprov/task");
                                        String where = DataBase.TASK_ID + "=" + id;
                                        getContentResolver().delete(table, where, null);
                                        printTasks(WHERE, SORTBY);
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null).show();

                        return true;
                    }

                });

            } while (c.moveToNext());
        }

        if(plannedTasks.getChildCount() == 0) {
            TextView taskName1 = new TextView(this); //) noPlannedTasks.findViewById(R.id.taskName);
            taskName1.setText("Get yourself organized and start planning your tasks!");
            taskName1.setTextSize(18);
            taskName1.setTextColor(Color.rgb(90,90,90));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,20,0,20);
            taskName1.setLayoutParams(params);
            taskName1.setGravity(Gravity.CENTER);
            plannedTasks.addView(taskName1);
        }
        if(doneTasks.getChildCount() == 0) {
            TextView taskName2 = new TextView(this); //) noDoneTasks.findViewById(R.id.taskName);
            taskName2.setTextSize(18);
            taskName2.setTextColor(Color.rgb(90,90,90));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,20,0,20);
            taskName2.setLayoutParams(params);
            taskName2.setText("This is where all your done tasks will be displayed.");
            taskName2.setGravity(Gravity.CENTER);

            doneTasks.addView(taskName2);

        }
    }

    public boolean insertTask(String ime, String time, String deadline, int priority, int category, int done) {
        //dodati validaciju podataka?
        ContentValues values = new ContentValues();
        values.put("name", ime);
        values.put("time", time);
        values.put("deadline", deadline);
        values.put("priority", priority);
        values.put("category", category);
        values.put("done", done);

        Uri uri = getContentResolver().insert(
                Uri.parse("content://hr.math.provider.contprov/task"), values);

        return true;
    }


    public void editTask(View view) {
        int id = (int)((ImageButton)view).getTag();
        Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
        intent.putExtra("FLAG", id);
        startActivity(intent);
    }

    public void checkedTask(View view) {
        int doneID = (int) ((CheckBox) view).getTag();
        boolean checked = ((CheckBox) view).isChecked();
        ContentValues values = new ContentValues();
        Uri table = Uri.parse("content://hr.math.provider.contprov/task");
        String where = DataBase.TASK_ID + "=" + doneID;
        Cursor c = getContentResolver().query(table,
                new String[]{DataBase.TASK_ID, DataBase.TASK_NAME, DataBase.TASK_TIME, DataBase.TASK_DEADLINE, DataBase.TASK_PRIORITY, DataBase.TASK_CATEGORY}, where, null, null);
        if (c.moveToFirst()) {

            values.put("name", c.getString(1));
            values.put("time", c.getString(2));
            values.put("deadline", c.getString(3));
            values.put("priority", c.getInt(4));
            values.put("category", c.getInt(5));

            if (checked) {
                values.put("done", 1);
                getContentResolver().update(table, values, where, null);
            } else {
                values.put("done", 0);
                getContentResolver().update(table, values, where, null);
            }
        }
        printTasks(WHERE, SORTBY);
    }

    public void pomodoro(MenuItem item) {
        Intent intent = new Intent(MainActivity.this, Pomodoro.class);
        startActivity(intent);
    }


    public void restartActivity() {
        Intent i = getIntent();
        this.overridePendingTransition(0, 0);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        this.finish();
        //restart the activity without animation
        this.overridePendingTransition(0, 0);
        this.startActivity(i);
    }



    public void filterAndSort(View view){
        Intent intent = new Intent(MainActivity.this, FilterAndSortActivity.class);

        startActivity(intent);
    }

    public void manageNotifications(MenuItem item) {
        if (item.isChecked()){
            item.setChecked(false);
            Intent intent1 = new Intent(MainActivity.this, NotificationBuilder.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0,intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            am.cancel(pendingIntent);
        }
        else {
            item.setChecked(true);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 8);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            Calendar now = Calendar.getInstance();
            now.setTimeInMillis(System.currentTimeMillis());
            int curHr = now.get(Calendar.HOUR_OF_DAY);
            int curMin = now.get(Calendar.MINUTE);
            if ((curHr > 9)||(curHr == 9 && curMin > 0))
            {
                calendar.add(Calendar.DATE, 1);
            }

            Intent intent1 = new Intent(MainActivity.this, NotificationBuilder.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0,intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }

    }

    @TargetApi(19)
    public void exportPDF(MenuItem item) {
        PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);
        printManager.print("print_any_view_job_name", new ViewPrintAdapter(this,
                findViewById(R.id.listOfTasks)), null);
    }

    public void openCalendar(MenuItem item) {
        Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
        startActivity(intent);
    }
}



