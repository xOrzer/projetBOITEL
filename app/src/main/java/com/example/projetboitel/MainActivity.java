package com.example.projetboitel;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    //ListView des meilleurs scores
    ListView lv;
    ArrayList<String> listItems = new ArrayList<>();


    //Variables nécessaire au bon fonctionnement de l'activité principale
    private int FIGHT = 1; //Valeur d'envoi dans l'intent
    private int minPower = 1; //Valeur puissance minimale des monstres
    private int maxPower = 100; //Valeur puissance maximale des monstres
    private int initialLife = 10; //Points de vie de départ
    private int initialPower = 100; //Puissance de départ
    private int monstersLeft = 16; //Nombre de monstres encore vivant
    private int powerPotion, lifePotion; //Index des potions
    private int level; //Niveau du joueur
    private boolean isLost = false; //Boolean déterminant si la partie est perdue ou non
    private String name = ""; //Nom du joueur

    //Déclaration des éléments nécessaires pour communiquer avec le XML
    private TextView displayLevel;
    private TextView displayPlayerName;
    private TextView displayPower;
    private TextView displayLife;
    private TextView displayResult;
    private TextView nbRoomLeft;
    private TextView displayGameStatus;

    //Déclaration des Popup
    private CustomPopup customPopup; //Popup pour customiser une partie
    private NamePopup namePopup; //Popup au lancement pour récupérer le pseudo du joueur

    //Déclaration des boutons
    private ImageButton buttonSelected; //Permet la gestion des mise à jour des boutons
    private ImageButton button01;
    private ImageButton button02;
    private ImageButton button03;
    private ImageButton button04;
    private ImageButton button05;
    private ImageButton button06;
    private ImageButton button07;
    private ImageButton button08;
    private ImageButton button09;
    private ImageButton button10;
    private ImageButton button11;
    private ImageButton button12;
    private ImageButton button13;
    private ImageButton button14;
    private ImageButton button15;
    private ImageButton button16;

    //Tableau avec les puissances des adversaires
    Vector enemyPower = new Vector();
    Vector <Integer> bestScores = new Vector <> (10);


    //Ce qu'il va se passer à la création de l'activité
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = (ListView) findViewById(R.id.maListe);

        //initialisation du niveau à 1
        level = 1;

        //Connexion des éléments du XML avec le java
        displayLevel = findViewById(R.id.level);
        displayPlayerName = findViewById(R.id.name);
        displayLife = findViewById(R.id.life);
        displayPower = findViewById(R.id.power);
        nbRoomLeft = findViewById(R.id.roomLeft);
        displayResult = findViewById(R.id.resultGame);
        displayGameStatus = findViewById(R.id.gameStatus);

        askName();
        if(name == ""){
            name = "Invité";
            displayPlayerName.setText(name);
        }
        initEnemyPower(minPower, maxPower);
        initPotions();
        displayGameStatus.setText("C'est parti ! Prêt à tout casser ?");
        displayLevel.setText("Etage " + Integer.toString(level));

        button01 = findViewById(R.id.button01);
        button02 = findViewById(R.id.button02);
        button03 = findViewById(R.id.button03);
        button04 = findViewById(R.id.button04);
        button05 = findViewById(R.id.button05);
        button06 = findViewById(R.id.button06);
        button07 = findViewById(R.id.button07);
        button08 = findViewById(R.id.button08);
        button09 = findViewById(R.id.button09);
        button10 = findViewById(R.id.button10);
        button11 = findViewById(R.id.button11);
        button12 = findViewById(R.id.button12);
        button13 = findViewById(R.id.button13);
        button14 = findViewById(R.id.button14);
        button15 = findViewById(R.id.button15);
        button16 = findViewById(R.id.button16);

        button01.setTag("unknown");
        button02.setTag("unknown");
        button03.setTag("unknown");
        button04.setTag("unknown");
        button05.setTag("unknown");
        button06.setTag("unknown");
        button07.setTag("unknown");
        button08.setTag("unknown");
        button09.setTag("unknown");
        button10.setTag("unknown");
        button11.setTag("unknown");
        button12.setTag("unknown");
        button13.setTag("unknown");
        button14.setTag("unknown");
        button15.setTag("unknown");
        button16.setTag("unknown");

        /*try {
            System.out.println("YOOOOOO 2");
            getFromIntern();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }


    public void onClick(View v){
        buttonSelected = findViewById(v.getId());

        if(buttonSelected.getTag() == "beaten" || isLost || checkVictory()){
            if(isLost){
                Toast.makeText(getApplicationContext(), "Partie terminée ! On s'en refait une ?", Toast.LENGTH_LONG).show();
            }
            else if(checkVictory()){
                Toast.makeText(getApplicationContext(), "T'as déjà tout cassé ! Ça te suffit pas !?", Toast.LENGTH_LONG).show();
            }else if(buttonSelected.getTag() == "beaten"){
                Toast.makeText(getApplicationContext(), "Tu l'as déjà battu, laisse le se reposer !", Toast.LENGTH_LONG).show();
            }
        }else{
            String id = getResources().getResourceEntryName(v.getId());
            String roomID = id.substring(id.length() - 2);
            int indexRoom = Integer.parseInt(id.substring(id.length() - 2));

            //Intent qui contiendra les informations à envoyer
            Intent intent = new Intent(MainActivity.this, FightActivity.class);
            intent.putExtra("Life", displayLife.getText().toString());
            intent.putExtra("Power", displayPower.getText().toString());
            intent.putExtra("Name", name);
            intent.putExtra("MonsterPower", enemyPower.get(indexRoom - 1).toString());
            intent.putExtra("roomIndex", roomID);

            if(indexRoom == powerPotion){
                intent.putExtra("Potion", "power");
            } else if(indexRoom == lifePotion){
                intent.putExtra("Potion", "life");
            } else {
                intent.putExtra("Potion", "nop");
            }

            startActivityForResult(intent, FIGHT);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        displayGameStatus.setText("Résultat combat");

        if(buttonSelected.getTag()!="seen") {
            int roomLeft = Integer.parseInt(nbRoomLeft.getText().toString());
            roomLeft--;
            nbRoomLeft.setText(Integer.toString(roomLeft));
        }

        //Si fuite
        if (resultCode == Activity.RESULT_CANCELED) {

            if(data.getStringExtra("lifeFound").matches("oui")){
                lifePotion = -1;
                displayLife.setText(data.getStringExtra("newLife"));
            }

            if(data.getStringExtra("powerFound").matches("oui")){
                powerPotion = -1;
                displayPower.setText(data.getStringExtra("newPower"));
            }

            displayPower.setText(data.getStringExtra("newPower"));

            int newLife = Integer.parseInt(displayLife.getText().toString());
            newLife--;

            displayLife.setText(Integer.toString(newLife));

            displayResult.setText(getString(R.string.flee));

            buttonSelected.setTag("seen");
            buttonSelected.setImageResource(R.drawable.rakdos);

            checkLose();
        }

        //Si victoire ou défaite
        if (resultCode == Activity.RESULT_OK) {

            String resultFight = data.getStringExtra("result");

            if(data.getStringExtra("lifeFound").matches("oui")){
                lifePotion = -1;
                displayLife.setText(data.getStringExtra("newLife"));
            }

            if(data.getStringExtra("powerFound").matches("oui")){
                powerPotion = -1;
                displayPower.setText(data.getStringExtra("newPower"));
            }

            //Victoire
            if(resultFight.matches("victory")){
                int newPower = Integer.parseInt(displayPower.getText().toString());

                //màj bouton, puissance & nombre de Salles

                buttonSelected.setTag("beaten");
                buttonSelected.setImageResource(R.drawable.cross);
                newPower += 10;

                displayPower.setText(Integer.toString(newPower));
                displayResult.setText(getString(R.string.beaten));
                monstersLeft--;

                checkVictory();
            }

            //Defaite
            if(resultFight.matches("lose")){
                if(data.getStringExtra("lifeFound").matches("oui")){
                    lifePotion = -1;
                    displayLife.setText(data.getStringExtra("newLife"));
                }

                if(data.getStringExtra("powerFound").matches("oui")){
                    powerPotion = -1;
                    displayPower.setText(data.getStringExtra("newPower"));
                }


                int newLife = Integer.parseInt(displayLife.getText().toString());
                newLife -= 3;

                displayLife.setText(Integer.toString(newLife));
                displayResult.setText(getString(R.string.lose));

                buttonSelected.setTag("seen");
                buttonSelected.setImageResource(R.drawable.rakdos);

                checkLose();
            }
        }
    }


    public int genererInt(int min, int max){
        Random random = new Random();
        int nb;
        nb = min + random.nextInt(max+1 - min);
        return nb;
    }

    private void initEnemyPower(int minPower, int maxPower){
        int powerMonster;
        for(int i=0; i<16; i++){
            powerMonster = genererInt(minPower, maxPower);
            System.out.println(powerMonster);
            enemyPower.add(powerMonster);
        }
    }

    private void initPotions(){
        powerPotion = genererInt(0,15);
        do{
            lifePotion = genererInt(0,15);
        }while(powerPotion == lifePotion);
    }

    private boolean checkVictory(){
        if(monstersLeft == 0){
            displayResult.setText(getString(R.string.beaten));
            final AlertDialog.Builder finishPopup = new AlertDialog.Builder(this);
            finishPopup.setTitle("Bien joué !");
            finishPopup.setMessage("Tu as battu tous les monstres !\nPrêt pour passer au niveau suivant ?\nSi non, tu devras tout recommencer !");
            finishPopup.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    nextLevel();
                }
            });

            finishPopup.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            AlertDialog dialog = finishPopup.show();

            TextView messageView = (TextView)dialog.findViewById(android.R.id.message);
            messageView.setGravity(Gravity.CENTER);

            TextView titleView = (TextView)dialog.findViewById(this.getResources().getIdentifier("alertTitle", "id", "android"));
            if (titleView != null) {
                titleView.setGravity(Gravity.CENTER);
            }


            return true;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean checkLose(){
        int currentLife = Integer.parseInt(displayLife.getText().toString());
        if(currentLife <= 0){
            displayResult.setText("Dommage tu as perdu !");
            displayGameStatus.setText("Défaite !");
            isLost = true;

            sauvegarde();

            return true;
        }

        return false;
    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    //Reinitialisation des boutons
    private void resetButtons(){
        int idReset = R.drawable.magic;

        button01.setImageResource(idReset);
        button02.setImageResource(idReset);
        button03.setImageResource(idReset);
        button04.setImageResource(idReset);
        button05.setImageResource(idReset);
        button06.setImageResource(idReset);
        button07.setImageResource(idReset);
        button08.setImageResource(idReset);
        button09.setImageResource(idReset);
        button10.setImageResource(idReset);
        button11.setImageResource(idReset);
        button12.setImageResource(idReset);
        button13.setImageResource(idReset);
        button14.setImageResource(idReset);
        button15.setImageResource(idReset);
        button16.setImageResource(idReset);

        button01.setTag("unknown");
        button02.setTag("unknown");
        button03.setTag("unknown");
        button04.setTag("unknown");
        button05.setTag("unknown");
        button06.setTag("unknown");
        button07.setTag("unknown");
        button08.setTag("unknown");
        button09.setTag("unknown");
        button10.setTag("unknown");
        button11.setTag("unknown");
        button12.setTag("unknown");
        button13.setTag("unknown");
        button14.setTag("unknown");
        button15.setTag("unknown");
        button16.setTag("unknown");
    }

    public void clickPlay(View v){
        EditText temp = namePopup.findViewById(R.id.playerName);
        if(namePopup.getName().trim().matches("")) {
            Toast.makeText(getApplicationContext(), "Veuillez saisir votre pseudo.", Toast.LENGTH_SHORT).show();
            temp.setText("");
        } else {
            displayPlayerName.setText(namePopup.getName());
            name = namePopup.getName();
            namePopup.dismiss();
        }
    }

    public void okPopup(View v) {
        askName();

        if(customPopup.getPowerValue().trim().matches("") || customPopup.getLifeValue().trim().matches("") || customPopup.getMaxPowerValue().trim().matches("")){
            Toast toast = Toast.makeText(this, "Tous les champs ne sont pas valides !", Toast.LENGTH_LONG);
            TextView tv = (TextView) toast.getView().findViewById(android.R.id.message);
            if( tv != null) tv.setGravity(Gravity.CENTER);
            toast.show();
        }else if(Integer.parseInt(customPopup.getLifeValue())<10 || Integer.parseInt(customPopup.getMaxPowerValue())<100 || Integer.parseInt(customPopup.getPowerValue())<50){
            Toast toast = Toast.makeText(this, "Tous les champs ne sont pas valides !\nPoints de vie minimum : 10\nPuissance minimale : 50\n Puissance monstre minimale : 100", Toast.LENGTH_LONG);
            TextView tv = (TextView) toast.getView().findViewById(android.R.id.message);
            if( tv != null) tv.setGravity(Gravity.CENTER);
            toast.show();
        }else{
            isLost = false;

            level = 1;
            initialLife = Integer.parseInt(customPopup.getLifeValue());
            initialPower = Integer.parseInt(customPopup.getPowerValue());

            displayLife.setText(customPopup.getLifeValue());
            displayPower.setText(customPopup.getPowerValue());

            monstersLeft = 16;
            nbRoomLeft.setText(R.string.nb_room_left);

            maxPower = Integer.parseInt(customPopup.getMaxPowerValue());
            enemyPower.clear();
            initEnemyPower(minPower, maxPower);
            initPotions();

            displayGameStatus.setText("Bienvenue ! Prêt à tout casser ?");
            displayResult.setText("Bonne chance !");

            resetButtons();

            customPopup.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.custom){
            customPopup = new CustomPopup(this);
            customPopup.build();
        }

        if (id == R.id.restart) {
            restartGame();
            return true;
        }

        if(id == R.id.bestScores){
            Intent intent = new Intent(MainActivity.this, ListActivity.class);
            startActivity(intent);

            return true;
        }

        if (id == R.id.stop) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void nextLevel(){
        level++;
        minPower += 50;
        maxPower += 75;
        initialLife += 5;
        initialPower += 50;

        displayPower.setText(Integer.toString(initialPower));
        displayLife.setText(Integer.toString(initialLife));
        displayLevel.setText("Etage " + Integer.toString(level));
        displayGameStatus.setText("En route pour le niveau suivant !");
        displayResult.setText("Bonne chance !");

        nbRoomLeft.setText("16");
        monstersLeft = 16;
        enemyPower.clear();
        initEnemyPower(minPower, maxPower);
        initPotions();

        resetButtons();
    }

    private void restartGame(){

        askName();

        isLost = false;
        level = 1;
        minPower = 1;
        maxPower = 100;
        initialLife = 10;
        initialPower = 100;

        displayPower.setText("100");

        displayLevel.setText("Etage " + Integer.toString(level));
        displayLife.setText(Integer.toString(initialLife));
        nbRoomLeft.setText(R.string.nb_room_left);
        displayResult.setText("");

        monstersLeft = 16;

        enemyPower.clear();
        initEnemyPower(minPower, maxPower);

        initPotions();

        displayGameStatus.setText("Bienvenue ! Prêt à tout casser ?");
        resetButtons();
    }

    private void askName(){
        namePopup = new NamePopup(this);
        namePopup.build();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void sauvegarde(){
        Toast.makeText(MainActivity.this, "Sauvegarde du score en cours...", Toast.LENGTH_SHORT).show();

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/YYYY");
        Date now = new Date();
        String date = formatter.format(now);

        String score = displayPlayerName.getText().toString() + " - Etage : " + level + " - Puissance : " + displayPower.getText().toString()+ " - " + date;

        int insert = 0;
        int index = 0;
        int nb = 0;
        boolean done = false;
        String stage = "";
        String power = "";
        String currentStage = "";
        String currentPower = "";
        String currentScore = "";


        while (index != score.length() -1) {
            if(score.substring(index, index+1).matches(":")){
                int j = index+2;
                nb++;
                while(!score.substring(j,j+1).matches(" ")) {
                    if (!score.substring(j, j + 1).matches(" ")) {
                        if (nb == 1) {
                            stage += score.substring(j, j + 1);

                        } else if (nb == 2) {
                            power += score.substring(j, j + 1);
                        }
                    }
                    j++;
                }

            }
            index++;
        }

        if(listItems.isEmpty()){
            listItems.add(score);
        } else {
            for (int i = 0; i < listItems.size(); i++) {
                nb=0;
                index = 0;
                currentPower = "";
                currentStage = "";

                currentScore = listItems.get(i); //Récupération élément Listview

                while (index != currentScore.length() - 1) {
                    if((index+1) > currentScore.length()){
                        break;
                    } else if (currentScore.substring(index, index + 1).matches(":")) {
                        int j = index + 2;
                        nb++;
                        while (!currentScore.substring(j, j + 1).matches(" ")) {
                            if (!currentScore.substring(j, j + 1).matches(" ")) {
                                if (nb == 1) { //Premier ':'
                                    currentStage += currentScore.substring(j, j + 1); //Récupération niveau
                                } else if (nb == 2) { //Deuxième ':'
                                    currentPower += currentScore.substring(j, j + 1); //Récupération puissance
                                }
                            }
                            j++;
                        }
                    }
                    index++;
                }

                if (((Integer.parseInt(stage) > Integer.parseInt(currentStage)) || (Integer.parseInt(stage) == Integer.parseInt(currentStage) && Integer.parseInt(power) > Integer.parseInt(currentPower))) && !done ) {

                    insert = i;
                    done = true;
                }
            }

            if(done) {
                listItems.add(insert, score);
            } else {
                listItems.add(score);
            }
        }

        try {
            FileOutputStream outputStream= openFileOutput("sauvegarde", MODE_PRIVATE);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

            for (int i = 0; i < listItems.size(); i++) {
                myOutWriter.append(listItems.get(i)+"\n");
            }

            myOutWriter.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        lv.setAdapter(adapter);

    }

}

