package com.pnd.future_bosses.plannedanddone;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class EditCategories extends AppCompatActivity {


    DBAdapter db1;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;
    List<Integer> arrayID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_categories);


        ListView list = (ListView)findViewById(R.id.listView);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                final int position = pos;


                new AlertDialog.Builder(new ContextThemeWrapper(EditCategories.this, R.style.myDialog))
                        .setTitle("Delete category")
                        .setMessage("Do you really want to delete this category?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Uri table = Uri.parse( "content://hr.math.provider.contprov/category");
                                String where = DataBase.CATEGORY_ID + "=" + arrayID.get(position);
                                getContentResolver().delete(table, where, null);
                                listAllCategories();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();

                return true;
            }
        });

        listAllCategories();
    }

    public void addNewCategory(View view) {
        EditText editText = (EditText) findViewById(R.id.editText2);
        String text = editText.getText().toString();

        if(!text.equals("")){

            ContentValues values = new ContentValues();
            values.put("name", text);

            Uri uri = getContentResolver().insert(
                    Uri.parse("content://hr.math.provider.contprov/category"), values);
            editText.setText("");
            listAllCategories();
        }

    }

    public void listAllCategories(){

        Uri table = Uri.parse( "content://hr.math.provider.contprov/category");

        ListView list = (ListView)findViewById(R.id.listView);
        arrayList = new ArrayList<String>();
        arrayID = new ArrayList();
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
                arrayList.add(c.getString(c.getColumnIndex(DataBase.CATEGORY_NAME)));
                arrayID.add(c.getInt(c.getColumnIndex(DataBase.CATEGORY_ID)));

            } while (c.moveToNext());
        }
        else{
            arrayList.add("You don't have any categories yet.");
        }
        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.category_item_layout, arrayList);
        list.setAdapter(adapter);

    }

    public void backToMain(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
