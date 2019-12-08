package com.example.projetboitel;

import android.app.Activity;
import android.app.Dialog;
import android.widget.Button;
import android.widget.EditText;

public class NamePopup extends Dialog {

    private EditText playerName;
    private Button play;

    public NamePopup(Activity activity){
        super(activity, R.style.Theme_AppCompat_DayNight_Dialog);
        setContentView(R.layout.name_popup);
        this.play = findViewById(R.id.playButton);
        this.playerName = findViewById(R.id.playerName);
    }

    public Button getPlay (){ return play;}

    public String getName(){
        return playerName.getText().toString();
    }

    public void build(){
        show();
    }
}
