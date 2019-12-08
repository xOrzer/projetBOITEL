package com.example.projetboitel;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;

public class ListActivity extends AppCompatActivity {

    ListView lv;
    ArrayList<String> listItems = new ArrayList<>();

    Intent intent = getIntent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        lv = (ListView) findViewById(R.id.maListe);

        try {
            getFromIntern();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        lv.setAdapter(adapter);

    }

    private void getFromIntern() throws IOException {

        String value = "";

        FileInputStream inputStream = openFileInput("sauvegarde");
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"utf8"), 8192);
        int content;

        while ((content=br.read())!=-1){

            if (content==10){
                listItems.add(value);
                value = "";
                continue;
            }

            value += (char) content;


        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, listItems);
        lv.setAdapter(adapter);
    }
}
