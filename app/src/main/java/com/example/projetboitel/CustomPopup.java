package com.example.projetboitel;

import android.app.Activity;
import android.app.Dialog;
import android.widget.Button;
import android.widget.EditText;

public class CustomPopup extends Dialog {

    private EditText customLife;
    private EditText customPower;
    private EditText customMaxPower;
    private Button ok;

    public CustomPopup(Activity activity){
        super(activity, R.style.Theme_AppCompat_DayNight_Dialog);
        setContentView(R.layout.my_custom_popup);
        this.ok = findViewById(R.id.okButton);
        this.customLife = findViewById(R.id.customLife);
        this.customPower = findViewById(R.id.customPower);
        this.customMaxPower = findViewById(R.id.customMaxPower);
    }

    public Button getOk (){ return ok;}

    public String getLifeValue(){
        return customLife.getText().toString();
    }
    public String getPowerValue(){
        return customPower.getText().toString();
    }
    public String getMaxPowerValue(){
        return customMaxPower.getText().toString();
    }

    public void build(){
        show();
    }
}
