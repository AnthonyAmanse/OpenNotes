package com.example.ghiea.opennotes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import static android.R.attr.editable;

public class CreateNote extends AppCompatActivity {

    EditText editText;
    int indexOfArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        Intent intent = getIntent();
        String text = intent.getStringExtra("text");
        indexOfArray = intent.getIntExtra("number", 0);
        final SharedPreferences sharedPreferences = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        editText = (EditText) findViewById(R.id.editText);
        editText.setText(text);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    MainActivity.myNotes.set(indexOfArray, charSequence.toString());
                } catch (IndexOutOfBoundsException e) {
                    MainActivity.myNotes.add(charSequence.toString());
                }

                MainActivity.arrayAdapter.notifyDataSetChanged();
                try {
                    sharedPreferences.edit().putString("myNotes", ObjectSerializer.serialize(MainActivity.myNotes)).apply();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
