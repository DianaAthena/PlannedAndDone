package com.pnd.future_bosses.plannedanddone;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringDef;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

import static android.support.v4.content.ContextCompat.startActivity;

public class SimpleCalendar extends LinearLayout {

    Context cont;
    private static final String CUSTOM_GREY = "#a0a0a0";
    private static final String[] ENG_MONTH_NAMES = {"January", "February", "March", "April",
            "May", "June", "July", "August",
            "September", "October", "November", "December"};

    private TextView currentDate;
    private TextView currentMonth;
    private Button selectedDayButton;
    private Button[] days;
    LinearLayout weekOneLayout;
    LinearLayout weekTwoLayout;
    LinearLayout weekThreeLayout;
    LinearLayout weekFourLayout;
    LinearLayout weekFiveLayout;
    LinearLayout weekSixLayout;
    private LinearLayout[] weeks;

    private int currentDateDay, chosenDateDay, currentDateMonth,
            chosenDateMonth, currentDateYear, chosenDateYear,
            pickedDateDay, pickedDateMonth, pickedDateYear;
    int userMonth, userYear;
    private DayClickListener mListener;
    private Drawable userDrawable;

    private Calendar calendar;
    LinearLayout.LayoutParams defaultButtonParams;
    private LinearLayout.LayoutParams userButtonParams;

    public SimpleCalendar(Context context) {
        super(context);
        init(context);
    }

    public SimpleCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SimpleCalendar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SimpleCalendar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        cont = context;
        DisplayMetrics metrics = getResources().getDisplayMetrics();

        View view = LayoutInflater.from(context).inflate(R.layout.simple_calendar, this, true);

        ImageButton prev = (ImageButton)view.findViewById(R.id.previousMonth);
        ImageButton next = (ImageButton)view.findViewById(R.id.nextMonth);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPrevoiusClick(v);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextClick(v);
            }
        });

        calendar = Calendar.getInstance();

        weekOneLayout = (LinearLayout) view.findViewById(R.id.calendar_week_1);
        weekTwoLayout = (LinearLayout) view.findViewById(R.id.calendar_week_2);
        weekThreeLayout = (LinearLayout) view.findViewById(R.id.calendar_week_3);
        weekFourLayout = (LinearLayout) view.findViewById(R.id.calendar_week_4);
        weekFiveLayout = (LinearLayout) view.findViewById(R.id.calendar_week_5);
        weekSixLayout = (LinearLayout) view.findViewById(R.id.calendar_week_6);
        currentDate = (TextView) view.findViewById(R.id.current_date);
        currentMonth = (TextView) view.findViewById(R.id.current_month);

        currentDateDay = chosenDateDay = calendar.get(Calendar.DAY_OF_MONTH);

        if (userMonth != 0 && userYear != 0) {
            currentDateMonth = chosenDateMonth = userMonth;
            currentDateYear = chosenDateYear = userYear;
        } else {
            currentDateMonth = chosenDateMonth = calendar.get(Calendar.MONTH);
            currentDateYear = chosenDateYear = calendar.get(Calendar.YEAR);
        }

        currentDate.setText("" + currentDateDay);
        currentMonth.setText(ENG_MONTH_NAMES[currentDateMonth]);

        initializeDaysWeeks();
        if (userButtonParams != null) {
            defaultButtonParams = userButtonParams;
        } else {
            defaultButtonParams = getdaysLayoutParams();
        }
        addDaysinCalendar(defaultButtonParams, context, metrics);

        initCalendarWithDate(chosenDateYear, chosenDateMonth, chosenDateDay, context);

    }

    private void onNextClick(View v) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();

        calendar = Calendar.getInstance();
        View view = LayoutInflater.from(cont).inflate(R.layout.simple_calendar, this, true);

        weekOneLayout = (LinearLayout) view.findViewById(R.id.calendar_week_1);
        weekTwoLayout = (LinearLayout) view.findViewById(R.id.calendar_week_2);
        weekThreeLayout = (LinearLayout) view.findViewById(R.id.calendar_week_3);
        weekFourLayout = (LinearLayout) view.findViewById(R.id.calendar_week_4);
        weekFiveLayout = (LinearLayout) view.findViewById(R.id.calendar_week_5);
        weekSixLayout = (LinearLayout) view.findViewById(R.id.calendar_week_6);

        weekOneLayout.removeAllViews();
        weekTwoLayout.removeAllViews();
        weekThreeLayout.removeAllViews();
        weekFourLayout.removeAllViews();
        weekFiveLayout.removeAllViews();
        weekSixLayout.removeAllViews();

        initializeDaysWeeks();
        addDaysinCalendar(defaultButtonParams, cont, metrics);

        int godina;
        int mjesec;

        if (chosenDateMonth == 11) {
            mjesec = 0;
            godina = chosenDateYear + 1;
        }
        else{
            mjesec = chosenDateMonth +1;
            godina = chosenDateYear;
        }

        currentDate.setText("" + currentDateDay);
        currentMonth.setText(ENG_MONTH_NAMES[mjesec]);
        initCalendarWithDate(godina, mjesec, 1, cont);

    }

    private void onPrevoiusClick(View v) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();

        calendar = Calendar.getInstance();
        View view = LayoutInflater.from(cont).inflate(R.layout.simple_calendar, this, true);

        weekOneLayout = (LinearLayout) view.findViewById(R.id.calendar_week_1);
        weekTwoLayout = (LinearLayout) view.findViewById(R.id.calendar_week_2);
        weekThreeLayout = (LinearLayout) view.findViewById(R.id.calendar_week_3);
        weekFourLayout = (LinearLayout) view.findViewById(R.id.calendar_week_4);
        weekFiveLayout = (LinearLayout) view.findViewById(R.id.calendar_week_5);
        weekSixLayout = (LinearLayout) view.findViewById(R.id.calendar_week_6);

        weekOneLayout.removeAllViews();
        weekTwoLayout.removeAllViews();
        weekThreeLayout.removeAllViews();
        weekFourLayout.removeAllViews();
        weekFiveLayout.removeAllViews();
        weekSixLayout.removeAllViews();

        initializeDaysWeeks();
        addDaysinCalendar(defaultButtonParams, cont, metrics);

        int godina;
        int mjesec;

        if (chosenDateMonth == 0) {
            mjesec = 11;
            godina = chosenDateYear - 1;
        }
        else{
            mjesec = chosenDateMonth - 1;
            godina = chosenDateYear;
        }

        currentDate.setText("" + currentDateDay);
        currentMonth.setText(ENG_MONTH_NAMES[mjesec]);
        initCalendarWithDate(godina, mjesec, 1, cont);
    }

    private void initializeDaysWeeks() {
        weeks = new LinearLayout[6];
        days = new Button[6 * 7];

        weeks[0] = weekOneLayout;
        weeks[1] = weekTwoLayout;
        weeks[2] = weekThreeLayout;
        weeks[3] = weekFourLayout;
        weeks[4] = weekFiveLayout;
        weeks[5] = weekSixLayout;
    }

    private void initCalendarWithDate(int year, int month, int day, Context context) {
        if (calendar == null)
            calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        int daysInCurrentMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        chosenDateYear = year;
        chosenDateMonth = month;
        chosenDateDay = day;

        calendar.set(year, month, 1);
        int firstDayOfCurrentMonth = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.set(year, month, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

        int dayNumber = 1;
        int daysLeftInFirstWeek = 0;
        int indexOfDayAfterLastDayOfMonth = 0;

        if (firstDayOfCurrentMonth != 1) {
            daysLeftInFirstWeek = firstDayOfCurrentMonth;
            indexOfDayAfterLastDayOfMonth = daysLeftInFirstWeek + daysInCurrentMonth;
            for (int i = firstDayOfCurrentMonth; i < firstDayOfCurrentMonth + daysInCurrentMonth; ++i) {
                if (currentDateMonth == chosenDateMonth && currentDateYear == chosenDateYear && dayNumber == currentDateDay) {
                    days[i].setTypeface(null, Typeface.BOLD);
                    days[i].setTextColor(Color.BLACK);
                }

                // oznacavamo datume na koje imamo planove
                days[i].setTextColor(Color.BLACK);
                days[i].setBackgroundColor(Color.TRANSPARENT);
                String filter = "" + String.valueOf(year);
                if ((month+1) < 10)
                    filter += "0" + String.valueOf(month+1);
                else
                    filter += String.valueOf(month+1);
                if ( dayNumber < 10)
                    filter += "0" + String.valueOf(dayNumber);
                else
                    filter += String.valueOf(dayNumber);

                //ima li u bazi zadataka.. dohvati dan
                Uri table = Uri.parse("content://hr.math.provider.contprov/task");
                Cursor c = context.getContentResolver().query(table,
                        new String[]{DataBase.TASK_TIME,DataBase.TASK_NAME,DataBase.TASK_PRIORITY},
                        DataBase.TASK_TIME + " LIKE '" + filter +"%'", null, "time DESC");


                if(c.moveToFirst()) {
                    int prioritet = 4;
                    do{
                        if (((c.getInt(2) < prioritet) && (c.getInt(2) != 0))|| (c.getInt(2) == 1 ))
                            prioritet = c.getInt(2);
                    }while(c.moveToNext() && (prioritet != 1));

                    switch (prioritet){
                        case 1:
                            days[i].setBackgroundColor(Color.rgb(255, 102, 102));
                            break;
                        case 2:
                            days[i].setBackgroundColor(Color.rgb(255, 224, 102));
                            break;
                        case 3:
                            days[i].setBackgroundColor(Color.parseColor("#7ac442"));
                            break;
                        default:
                            days[i].setBackgroundColor(Color.rgb(166, 166, 166));
                            break;
                    }
                }


                int[] dateArr = new int[3];
                dateArr[0] = dayNumber;
                dateArr[1] = chosenDateMonth;
                dateArr[2] = chosenDateYear;
                days[i].setTag(dateArr);
                days[i].setText(String.valueOf(dayNumber));

                days[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onDayClick(v);
                    }
                });
                ++dayNumber;
            }
        } else {
            daysLeftInFirstWeek = 8;
            indexOfDayAfterLastDayOfMonth = daysLeftInFirstWeek + daysInCurrentMonth;
            for (int i = 8; i < 8 + daysInCurrentMonth; ++i) {
                days[i].setTextColor(Color.BLACK);
                days[i].setBackgroundColor(Color.TRANSPARENT);

                int[] dateArr = new int[3];
                dateArr[0] = dayNumber;
                dateArr[1] = chosenDateMonth;
                dateArr[2] = chosenDateYear;
                days[i].setTag(dateArr);
                days[i].setText(String.valueOf(dayNumber));

                days[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onDayClick(v);
                    }
                });
                ++dayNumber;
            }
        }

        if (month > 0)
            calendar.set(year, month - 1, 1);
        else
            calendar.set(year - 1, 11, 1);
        int daysInPreviousMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = daysLeftInFirstWeek - 1; i >= 0; --i) {
            int[] dateArr = new int[3];

            if (chosenDateMonth > 0) {
                if (currentDateMonth == chosenDateMonth - 1
                        && currentDateYear == chosenDateYear
                        && daysInPreviousMonth == currentDateDay) {
                } else {
                    days[i].setBackgroundColor(Color.TRANSPARENT);
                }

                dateArr[0] = daysInPreviousMonth;
                dateArr[1] = chosenDateMonth - 1;
                dateArr[2] = chosenDateYear;
            } else {
                if (currentDateMonth == 11
                        && currentDateYear == chosenDateYear - 1
                        && daysInPreviousMonth == currentDateDay) {
                } else {
                    days[i].setBackgroundColor(Color.TRANSPARENT);
                }

                dateArr[0] = daysInPreviousMonth;
                dateArr[1] = 11;
                dateArr[2] = chosenDateYear - 1;
            }

            days[i].setTag(dateArr);
            days[i].setText(String.valueOf(daysInPreviousMonth--));
            days[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDayClick(v);
                }
            });
        }

        int nextMonthDaysCounter = 1;
        for (int i = indexOfDayAfterLastDayOfMonth; i < days.length; ++i) {
            int[] dateArr = new int[3];

            if (chosenDateMonth < 11) {
                days[i].setBackgroundColor(Color.TRANSPARENT);

                dateArr[0] = nextMonthDaysCounter;
                dateArr[1] = chosenDateMonth + 1;
                dateArr[2] = chosenDateYear;
            } else {
                days[i].setBackgroundColor(Color.TRANSPARENT);

                dateArr[0] = nextMonthDaysCounter;
                dateArr[1] = 0;
                dateArr[2] = chosenDateYear + 1;
            }

            days[i].setTag(dateArr);
            days[i].setTextColor(Color.parseColor(CUSTOM_GREY));
            days[i].setText(String.valueOf(nextMonthDaysCounter++));
            days[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDayClick(v);
                }
            });
        }

        calendar.set(chosenDateYear, chosenDateMonth, chosenDateDay);
    }

    public void onDayClick(View view) {
        mListener.onDayClick(view);

        selectedDayButton = (Button) view;
        if (selectedDayButton.getTag() != null) {
            int[] dateArray = (int[]) selectedDayButton.getTag();
            pickedDateDay = dateArray[0];
            pickedDateMonth = dateArray[1];
            pickedDateYear = dateArray[2];
        }

        LinearLayout popis = (LinearLayout)findViewById(R.id.Zadaci);
        popis.removeAllViews();

        currentDate.setText(""+ pickedDateDay);
        String filter = "" + String.valueOf(pickedDateYear);
        if ((pickedDateMonth+1) < 10)
            filter += "0" + String.valueOf(pickedDateMonth+1);
        else
            filter += String.valueOf(pickedDateMonth+1);
        if ( pickedDateDay < 10)
            filter += "0" + String.valueOf(pickedDateDay);
        else
            filter += String.valueOf(pickedDateDay);

        //ima li u bazi zadataka.. dohvati dan
        Uri table = Uri.parse("content://hr.math.provider.contprov/task");
        Cursor c = cont.getContentResolver().query(table,
                new String[]{DataBase.TASK_TIME,DataBase.TASK_NAME,DataBase.TASK_PRIORITY},
                DataBase.TASK_TIME + " LIKE '" + filter +"%'", null, "time DESC");

        if(c.moveToFirst()){
            TextView caption = new TextView(cont);
            caption.setText("Tasks for " + pickedDateDay + "." +(pickedDateMonth+1) +".:" );
            caption.setTextSize(17);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(40,40,40,10);
            caption.setLayoutParams(params);
            caption.setTextColor(Color.rgb(0,0,0));
            popis.addView(caption);

            do{
                LayoutInflater inflater = (LayoutInflater) cont.getSystemService(cont.LAYOUT_INFLATER_SERVICE);
                View taskLayout = inflater.inflate(R.layout.calendar_task_view, null);
                TextView taskName = (TextView) taskLayout.findViewById(R.id.calTaskName);
                taskName.setText(c.getString(c.getColumnIndex(DataBase.TASK_NAME)));

                ImageView priorImag = (ImageView) taskLayout.findViewById(R.id.calTaskPriority);

                switch (c.getInt(c.getColumnIndex(DataBase.TASK_PRIORITY))){
                    case 1:
                        priorImag.setImageResource(R.drawable.crveni);
                        break;
                    case 2:
                        priorImag.setImageResource(R.drawable.zuti);
                        break;
                    case 3:
                        priorImag.setImageResource(R.drawable.zeleni);
                        break;
                    default:
                        priorImag.setImageResource(R.drawable.sivi);
                        break;
                }
                popis.addView(taskLayout);
            }while(c.moveToNext());
        }
        else{
            TextView caption = new TextView(cont);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(40,40,20,40);
            caption.setLayoutParams(params);
            caption.setText("You don't have tasks for " + pickedDateDay + "." +(pickedDateMonth+1) +"." );
            caption.setTextSize(17);
            popis.addView(caption);
            caption.setTextColor(Color.rgb(0,0,0));

        }

    }

    private void addDaysinCalendar(LayoutParams buttonParams, Context context,
                                   DisplayMetrics metrics) {
        int engDaysArrayCounter = 0;

        for (int weekNumber = 0; weekNumber < 6; ++weekNumber) {
            for (int dayInWeek = 0; dayInWeek < 7; ++dayInWeek) {
                final Button day = new Button(context);
                day.setTextColor(Color.parseColor(CUSTOM_GREY));
                day.setBackgroundColor(Color.TRANSPARENT);
                day.setLayoutParams(buttonParams);
                day.setTextSize((int) metrics.density * 8);
                day.setSingleLine();

                days[engDaysArrayCounter] = day;
                weeks[weekNumber].addView(day);

                ++engDaysArrayCounter;
            }
        }
    }

    private LayoutParams getdaysLayoutParams() {
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        buttonParams.weight = 1;
        return buttonParams;
    }

    public void setUserDaysLayoutParams(LinearLayout.LayoutParams userButtonParams) {
        this.userButtonParams = userButtonParams;
    }

    public void setUserCurrentMonthYear(int userMonth, int userYear) {
        this.userMonth = userMonth;
        this.userYear = userYear;
    }

    public void setDayBackground(Drawable userDrawable) {
        this.userDrawable = userDrawable;
    }

    public interface DayClickListener {
        void onDayClick(View view);
    }

    public void setCallBack(DayClickListener mListener) {
        this.mListener = mListener;
    }
}