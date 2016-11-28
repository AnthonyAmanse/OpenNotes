package com.example.ghiea.opennotes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView notesList;
    static ArrayList<String> myNotes = new ArrayList<>();
    static ArrayAdapter<String> arrayAdapter;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(getApplicationContext(), CreateNote.class);
                intent.putExtra("number", myNotes.size());
                startActivity(intent);

            }
        });

        sharedPreferences = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        if(myNotes.size() == 0) {
            try {
                myNotes = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("myNotes", ObjectSerializer.serialize(new ArrayList<String>())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        notesList = (ListView) findViewById(R.id.notesList);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, myNotes);
        notesList.setAdapter(arrayAdapter);


        arrayAdapter.notifyDataSetChanged();
        notesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = (TextView) view;
                Intent intent = new Intent(getApplicationContext(), CreateNote.class);
                intent.putExtra("text", textView.getText());
                intent.putExtra("number", i);
                startActivity(intent);
            }
        });
        notesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = (TextView) view;
                int substring = 10;
                final int index = i;
                boolean gotTheLowest = false;
                do {
                    try {
                        new AlertDialog.Builder(MainActivity.this)
                                .setIcon(android.R.drawable.ic_delete)
                                .setTitle("Delete this note")
                                .setMessage(textView.getText().toString().substring(0, substring) + "...")
                                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Toast.makeText(MainActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                        myNotes.remove(index);
                                        arrayAdapter.notifyDataSetChanged();
                                        try {
                                            sharedPreferences.edit().putString("myNotes", ObjectSerializer.serialize(myNotes)).apply();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();
                        gotTheLowest = true;
                    } catch (StringIndexOutOfBoundsException e) {
                        gotTheLowest = false;
                        substring--;
                    }
                } while (!gotTheLowest);


                return true;
            }
        });
        try {
            sharedPreferences.edit().putString("myNotes", ObjectSerializer.serialize(myNotes)).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.about) {
            new AlertDialog.Builder(this, R.style.MyDialogTheme)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle("Open Notes v1.0")
                    .setMessage("Open source note taking app\nby Archaic Ants\nanthonyamanse@gmail.com")
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
