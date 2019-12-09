package com.example.projetboitel;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Random;

public class FightActivity extends AppCompatActivity {

    private TextView name;
    private TextView playerPower;
    private TextView playerLife;
    private TextView monsterPower;
    private TextView monsterName;

    private ImageView imageMonster;

    private Button attackButton;
    private Button escapeButton;

    private String resultFight;

    private boolean isLifeFound = false;
    private boolean isPowerFound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight);

        Intent intent = getIntent();

        //Récupération des différentes données nécessaires
        final String roomId = intent.getStringExtra("roomIndex");
        String imageID = "monstre"+roomId; //Permet de savoir quel monstre afficher
        int idNameMonster = getResources().getIdentifier(imageID, "string", getPackageName());
        int idImageMonster = getResources().getIdentifier(imageID , "drawable", getPackageName());

        String foundPotion = intent.getStringExtra("Potion");

        attackButton = (Button)findViewById(R.id.attackButton);
        escapeButton = (Button)findViewById(R.id.escapeButton);
        name = (TextView)findViewById(R.id.nameLocation);
        playerPower = (TextView)findViewById(R.id.playerPower);
        playerLife = (TextView)findViewById(R.id.lifePoint);
        monsterPower = (TextView)findViewById(R.id.monsterPower);
        monsterName = (TextView)findViewById(R.id.monsterName);
        imageMonster = (ImageView)findViewById(R.id.monsterImage) ;

        name.setText(intent.getStringExtra("Name"));
        monsterPower.setText(intent.getStringExtra("MonsterPower"));
        playerLife.setText(intent.getStringExtra("Life"));
        playerPower.setText(intent.getStringExtra("Power"));
        imageMonster.setImageResource(idImageMonster);
        monsterName.setText(getString(idNameMonster));

        int newLife = Integer.parseInt(playerLife.getText().toString());
        int newPower = Integer.parseInt(playerPower.getText().toString());


        if(foundPotion.matches("power")){
            int powerAdded = genererInt(5,10) * Integer.parseInt(intent.getStringExtra("Level"));
            newPower += powerAdded;
            playerPower.setText(Integer.toString(newPower));

            Toast toast = Toast.makeText(this, "Vous avez trouvé une potion de force ! \n+"+ powerAdded +" de Puissance", Toast.LENGTH_LONG);
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            if( v != null) v.setGravity(Gravity.CENTER);
            toast.show();

            isPowerFound = true;
        }

        if(foundPotion.matches("life")){
            int lifeAdded = genererInt(1,3) + Integer.parseInt(intent.getStringExtra("Level")) -1;
            newLife += lifeAdded;
            playerLife.setText(Integer.toString(newLife));

            Toast toast = Toast.makeText(this, "Vous avez trouvé une potion de vie ! \n+" + lifeAdded + " Point de Vie", Toast.LENGTH_LONG);
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            if( v != null) v.setGravity(Gravity.CENTER);
            toast.show();

            isLifeFound = true;
        }


        attackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double randPlayer = Math.random();
                double randMonster = Math.random();

                double result = Integer.parseInt(playerPower.getText().toString()) * randPlayer - Integer.parseInt(monsterPower.getText().toString()) * randMonster;

                if (result < 0) { //Si défaite
                    resultFight = "lose";

                    Intent returnIntent = new Intent(FightActivity.this, MainActivity.class);

                    if(isLifeFound){
                        returnIntent.putExtra("lifeFound", "oui");
                    } else {
                        returnIntent.putExtra("lifeFound", "non");
                    }

                    if(isPowerFound){
                        returnIntent.putExtra("powerFound", "oui");
                    } else {
                        returnIntent.putExtra("powerFound", "non");
                    }

                    returnIntent.putExtra("result", resultFight);
                    returnIntent.putExtra("idRoom", roomId);
                    returnIntent.putExtra("newPower", playerPower.getText().toString());
                    returnIntent.putExtra("newLife", playerLife.getText().toString());
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } else { //sinon victoire
                    resultFight = "victory";
                    Intent returnIntent = new Intent(FightActivity.this, MainActivity.class);

                    if(isLifeFound){
                        returnIntent.putExtra("lifeFound", "oui");
                    } else {
                        returnIntent.putExtra("lifeFound", "non");
                    }

                    if(isPowerFound){
                        returnIntent.putExtra("powerFound", "oui");
                    } else {
                        returnIntent.putExtra("powerFound", "non");
                    }

                    returnIntent.putExtra("result", resultFight);
                    returnIntent.putExtra("idRoom", roomId);
                    returnIntent.putExtra("newPower", playerPower.getText().toString());
                    returnIntent.putExtra("newLife", playerLife.getText().toString());
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });

        escapeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultFight = "flee";
                Intent returnIntent = new Intent(FightActivity.this, MainActivity.class);

                if(isLifeFound){
                    returnIntent.putExtra("lifeFound", "oui");
                } else {
                    returnIntent.putExtra("lifeFound", "non");
                }

                if(isPowerFound){
                    returnIntent.putExtra("powerFound", "oui");
                } else {
                    returnIntent.putExtra("powerFound", "non");
                }

                returnIntent.putExtra("result", resultFight);
                returnIntent.putExtra("newPower", playerPower.getText().toString());
                returnIntent.putExtra("newLife", playerLife.getText().toString());
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed(){
        resultFight = "flee";
        Intent returnIntent = new Intent(FightActivity.this, MainActivity.class);

        if(isLifeFound){
            returnIntent.putExtra("lifeFound", "oui");
        } else {
            returnIntent.putExtra("lifeFound", "non");
        }

        if(isPowerFound){
            returnIntent.putExtra("powerFound", "oui");
        } else {
            returnIntent.putExtra("powerFound", "non");
        }

        returnIntent.putExtra("result", resultFight);
        returnIntent.putExtra("newPower", playerPower.getText().toString());
        returnIntent.putExtra("newLife", playerLife.getText().toString());
        setResult(Activity.RESULT_CANCELED, returnIntent);
        super.onBackPressed();
    }

    public int genererInt(int min, int max){
        Random random = new Random();
        int nb;
        nb = min + random.nextInt(max+1 - min);
        return nb;
    }
}
