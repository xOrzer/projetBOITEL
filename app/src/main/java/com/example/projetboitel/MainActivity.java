package com.example.projetboitel;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
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

    /* ListView des meilleurs scores */
    ArrayList<String> listItems = new ArrayList<>();

    /* Variables nécessaire au bon fonctionnement de l'activité principale */
    private int FIGHT = 1; //Valeur d'envoi dans l'intent
    private int minPower = 1; //Valeur puissance minimale des monstres
    private int maxPower = 150; //Valeur puissance maximale des monstres

    private int initialLife = 10; //Points de vie de départ
    private int initialPower = 100; //Puissance de départ
    private int monstersLeft = 16; //Nombre de monstres encore vivant
    private int powerPotion, lifePotion; //Index des potions
    private int level; //Niveau du joueur
    private boolean isLost = false; //Boolean déterminant si la partie est perdue ou non
    private boolean isWon = false; //Boolean déterminant si la partie est gagnée ou non
    private boolean isCustomGame = false;
    private String name = ""; //Nom du joueur

    /* Déclaration des éléments nécessaires pour communiquer avec le XML */
    private TextView displayLevel;
    private TextView displayPlayerName;
    private TextView displayPower;
    private TextView displayLife;
    private TextView displayResult;
    private TextView nbRoomLeft;
    private TextView displayGameStatus;

    /* Déclaration des Popup */
    private CustomPopup customPopup; //Popup pour customiser une partie
    private NamePopup namePopup; //Popup au lancement pour récupérer le pseudo du joueur

    /* Déclaration des boutons */
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

    /* Tableau avec les puissances des adversaires */
    Vector enemyPower = new Vector();

    private MediaPlayer hurt;
    private MediaPlayer sword;
    private MediaPlayer lose;
    private MediaPlayer closeDoor;
    private MediaPlayer affraid;
    private MediaPlayer openDoor;
    private MediaPlayer win;

    /* Ce qu'il va se passer à la création de l'activité */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        level = 1; //initialisation du niveau à 1

        /* Liaison des éléments du XML avec le java + initialisation des boutons*/
        displayLevel = findViewById(R.id.level);
        displayPlayerName = findViewById(R.id.name);
        displayLife = findViewById(R.id.life);
        displayPower = findViewById(R.id.power);
        nbRoomLeft = findViewById(R.id.roomLeft);
        displayResult = findViewById(R.id.resultGame);
        displayGameStatus = findViewById(R.id.gameStatus);

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

        this.hurt = MediaPlayer.create(getApplicationContext(), R.raw.hurt);
        this.openDoor = MediaPlayer.create(getApplicationContext(), R.raw.open_door);
        this.closeDoor = MediaPlayer.create(getApplicationContext(), R.raw.close_door);
        this.affraid = MediaPlayer.create(getApplicationContext(), R.raw.ohoh);
        this.sword = MediaPlayer.create(getApplicationContext(), R.raw.hit);
        this.lose = MediaPlayer.create(getApplicationContext(), R.raw.lose_sound);
        this.win = MediaPlayer.create(getApplicationContext(), R.raw.win);

        /* Demande du nom du joueur via popup */
        askName();

        /* S'il ne saisit pas de nom (si le joueur clique à côté de la popup) */
        if(name.matches("")){
            name = "Invité"; //Il s'appelle Invité par défaut
            displayPlayerName.setText(name);
        }

        /* Initialisation de la partie */
        displayGameStatus.setText(R.string.letsgo);
        displayResult.setText(R.string.goodluck);
        displayLevel.setText("Etage " + level);
        initEnemyPower(minPower, maxPower);
        initPotions();

        /* Récupération des meilleurs scores */
        try {
            getFromIntern();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void onClick(View v){
        /* Sauvegarde du bouton sur lequel on vient de cliquer */
        buttonSelected = findViewById(v.getId());

        /* Différents cas qui implique qu'aucune action ne doit être faite si on clique sur le bouton */
        /* Cas 1 : Le monstre a déjà été battu */
        /* Cas 2 : La partie est terminée */
        if(buttonSelected.getTag() == "beaten" || isLost){
            if(isLost){
                Toast.makeText(getApplicationContext(), "Partie terminée ! On s'en refait une ?", Toast.LENGTH_LONG).show();

                /* On affiche une popup afin de faire passer au niveau suivant */
                final AlertDialog.Builder finishPopup = new AlertDialog.Builder(this);
                finishPopup.setTitle("Aïe aïe aïe, c'est perdu !");
                finishPopup.setMessage("On s'en refait une ?");
                finishPopup.setPositiveButton("Oui", new DialogInterface.OnClickListener() { //Bouton permmettant de passer au niveau suivant
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        restartGame();
                    }
                });

                finishPopup.setNegativeButton("Non", new DialogInterface.OnClickListener() { //Bouton qui arrêtera la partie
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                AlertDialog dialog = finishPopup.show(); //Affichage de la popup

            }else if(buttonSelected.getTag() == "beaten" && isWon){
                /* On affiche une popup afin de faire passer au niveau suivant */
                final AlertDialog.Builder finishPopup = new AlertDialog.Builder(this);
                finishPopup.setTitle("Bien joué !");
                finishPopup.setMessage("Tu as battu tous les monstres !\nPrêt pour passer au niveau suivant ?\nSi non, tu devras tout recommencer !");
                finishPopup.setPositiveButton("Oui", new DialogInterface.OnClickListener() { //Bouton permmettant de passer au niveau suivant
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        nextLevel();
                    }
                });

                finishPopup.setNegativeButton("Non", new DialogInterface.OnClickListener() { //Bouton qui arrêtera la partie
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                AlertDialog dialog = finishPopup.show(); //Affichage de la popup

                /* Esthétique de la popup pour afficher le message au milieu */
                TextView messageView = (TextView)dialog.findViewById(android.R.id.message);
                messageView.setGravity(Gravity.CENTER);
            }else if(buttonSelected.getTag() == "beaten" && !isWon){
                Toast.makeText(getApplicationContext(), "Tu l'as déjà battu, laisse le se reposer !", Toast.LENGTH_LONG).show();
            }
        }else{
            openDoor.start();
            String id = getResources().getResourceEntryName(v.getId());
            String roomID = id.substring(id.length() - 2);
            int indexRoom = Integer.parseInt(roomID);

            /* Intent qui contiendra les informations nécessaires à envoyer à l'activité de combat */
            Intent intent = new Intent(MainActivity.this, FightActivity.class);
            intent.putExtra("Life", displayLife.getText().toString());
            intent.putExtra("Power", displayPower.getText().toString());
            intent.putExtra("Name", name);
            intent.putExtra("Level", Integer.toString(level));
            intent.putExtra("MonsterPower", enemyPower.get(indexRoom - 1).toString());
            intent.putExtra("roomIndex", roomID);

            if(indexRoom == powerPotion){ //Si le joueur a cliqué sur la salle où se trouve la potion de puissance
                intent.putExtra("Potion", "power");
            } else if(indexRoom == lifePotion){ //Si le joueur a cliqué sur la salle où se trouve la potion de vie
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

        /* Si la salle n'avait pas encore été visitée */
        if(buttonSelected.getTag()!="seen") {
            /* màj pièces non explorées */
            int roomLeft = Integer.parseInt(nbRoomLeft.getText().toString());
            roomLeft--;
            nbRoomLeft.setText(Integer.toString(roomLeft));
        }

        //Si la potion de vie a été trouvée
        if(data.getStringExtra("lifeFound").matches("oui")){
            lifePotion = -1; //Modification index afin de ne plus pouvoir retomber dessus
            displayLife.setText(data.getStringExtra("newLife"));
        }

        //Si la potion de force a été trouvée
        if(data.getStringExtra("powerFound").matches("oui")){
            powerPotion = -1; //Modification index afin de ne plus pouvoir retomber dessus
            displayPower.setText(data.getStringExtra("newPower"));
        }

        //Si fuite
        if (resultCode == Activity.RESULT_CANCELED) {
            //Son de fuite
            affraid.start();

            //Fuite implique la perte de 1 point de vie
            int newLife = Integer.parseInt(displayLife.getText().toString());
            newLife--;
            displayLife.setText(Integer.toString(newLife));

            displayResult.setText(getString(R.string.flee)); //Affichage d'un message en référence à la fuite

            //Le monstre a été vu donc on modifie l'image
            buttonSelected.setTag("seen");
            buttonSelected.setImageResource(R.drawable.rakdos);

            checkLose();
        }

        //Si victoire ou défaite
        if (resultCode == Activity.RESULT_OK) {

            /* Récupération résultat combat */
            String resultFight = data.getStringExtra("result");

            /* Si le combat se résulte par une victoire */
            if(resultFight.matches("victory")){
                sword.start();
                int newPower = Integer.parseInt(displayPower.getText().toString());

                /*màj tag bouton indiquant que le monstre a été battu
                 *màj de la puissance du joueur
                 *màj du nombre de monstres encore en vie
                 */
                buttonSelected.setTag("beaten");
                buttonSelected.setImageResource(R.drawable.cross);
                newPower += 10 + (2 * (level-1)); //légère modification de la formule car sinon trop difficile

                displayPower.setText(Integer.toString(newPower));
                displayResult.setText(getString(R.string.beaten));
                monstersLeft--;

                checkVictory(); //On vérifie si le joueur a battu tous les monstres de l'étage
            }

            /* Si le combat se résulte par une défaite */
            if(resultFight.matches("lose")){
                hurt.start();
                /* Perte de 3 points de vie */
                int newLife = Integer.parseInt(displayLife.getText().toString());
                newLife -= 3;

                displayLife.setText(Integer.toString(newLife));
                displayResult.setText(getString(R.string.lose));

                /* Le monstre a été "vu" et non "battu" */
                buttonSelected.setTag("seen");
                buttonSelected.setImageResource(R.drawable.rakdos);

                checkLose(); //On vérifie si le joueur a perdu la partie
            }
        }
    }



    /* Fonction permettant si le joueur a terminé l'étage */
    private boolean checkVictory(){
        /* S'il n'y a plus de mostres en vie */
        if(monstersLeft == 0){
            win.start();
            isWon = true;
            displayResult.setText(getString(R.string.beaten));

            /* On affiche une popup afin de faire passer au niveau suivant */
            final AlertDialog.Builder finishPopup = new AlertDialog.Builder(this);
            finishPopup.setTitle("Bien joué !");
            finishPopup.setMessage("Tu as battu tous les monstres !\nPrêt pour passer au niveau suivant ?\nSi non, tu devras tout recommencer !");
            finishPopup.setPositiveButton("Oui", new DialogInterface.OnClickListener() { //Bouton permmettant de passer au niveau suivant
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    nextLevel();
                }
            });

            finishPopup.setNegativeButton("Non", new DialogInterface.OnClickListener() { //Bouton qui arrêtera la partie
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            AlertDialog dialog = finishPopup.show(); //Affichage de la popup

            /* Esthétique de la popup pour afficher le message au milieu */
            TextView messageView = (TextView)dialog.findViewById(android.R.id.message);
            messageView.setGravity(Gravity.CENTER);

            return true;
        }
        return false;
    }

    /* Fonction permettant de savoir si le joueur a perdu */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean checkLose(){
        int currentLife = Integer.parseInt(displayLife.getText().toString());
        if(currentLife <= 0){
            lose.start();
            displayResult.setText(R.string.gameLost);
            displayGameStatus.setText(R.string.defeat);
            isLost = true;

            /* On sauvegarde seulement s'il s'agit d'une partie non personnalisée */
            if(!isCustomGame)
                sauvegarde();

            /* On affiche une popup afin de faire passer au niveau suivant */
            final AlertDialog.Builder finishPopup = new AlertDialog.Builder(this);
            finishPopup.setTitle("Aïe aïe aïe, c'est perdu !");
            finishPopup.setMessage("On s'en refait une ?");
            finishPopup.setPositiveButton("Oui", new DialogInterface.OnClickListener() { //Bouton permmettant de passer au niveau suivant
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    restartGame();
                }
            });

            finishPopup.setNegativeButton("Non", new DialogInterface.OnClickListener() { //Bouton qui arrêtera la partie
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            AlertDialog dialog = finishPopup.show(); //Affichage de la popup

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


    /* Actions réalisées lors de la validation de la saisie du nom */
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

    /* Actions réalisées lors de la validation de la partie custom */
    public void okPopup(View v) {
        askName();

        /* Gestion des différents cas invalides */
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
        }else{ //Si tout est ok alors on paramètre la partie !
            isLost = false; //Au cas où la partie précédente était perdue
            isWon = false; //Au cas où la partie précédente était gagnée
            isCustomGame = true; //Permet de savoir si la game est Custom ou non

            level = 1; //On redémarre au niveau 1
            initialLife = Integer.parseInt(customPopup.getLifeValue());
            initialPower = Integer.parseInt(customPopup.getPowerValue());

            /* On modifie les différents affichages */
            displayLife.setText(customPopup.getLifeValue());
            displayPower.setText(customPopup.getPowerValue());

            monstersLeft = 16; //Réinitialisation du nombre de monstres  en vie
            nbRoomLeft.setText(R.string.nb_room_left);

            //Initialisation de la puissance des monstres
            maxPower = Integer.parseInt(customPopup.getMaxPowerValue());
            enemyPower.clear();
            initEnemyPower(minPower, maxPower);

            initPotions(); //Initialisation des potions

            displayGameStatus.setText(R.string.letsgo);
            displayResult.setText(R.string.goodluck);

            resetButtons();

            customPopup.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Différents choix du menu

        if(id == R.id.custom){ //Pour une partie personnalisée..
            customPopup = new CustomPopup(this);
            customPopup.build();
        }

        if (id == R.id.restart) { //Recommencer la partie à 0 (on relance une partie non paramétrée
            restartGame();
            return true;
        }

        if(id == R.id.bestScores){ //Afficher les 10 meilleurs scores
            Intent intent = new Intent(MainActivity.this, ListActivity.class);
            startActivity(intent);

            return true;
        }

        if (id == R.id.stop) { //Arrêter l'application
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /* Fonction appelée lorsque le joueur passe au niveau suivant */
    private void nextLevel(){
        level++; //Incrémentation du niveau
        isWon = false; //Réinitialisation de la victoire

        minPower += 80; //On augmente la puissance min et max des monstres
        maxPower += 100; //afin d'augmenter la difficulté
        initialLife += 3; //On lui donne un peu plus de vie
        initialPower += 60; //Et plus de puissance initiale

        /* Mise à jours des données à l'écran */
        displayPower.setText(Integer.toString(initialPower));
        displayLife.setText(Integer.toString(initialLife));
        displayLevel.setText("Etage " + level);
        displayGameStatus.setText(R.string.letsgolevelup);
        displayResult.setText(R.string.goodluck);

        nbRoomLeft.setText(R.string.nb_room_left);
        monstersLeft = 16; //Réinitialisation du nombre de monstre en vie

        /* On réinitialise la puissance des monstres ainsi que les potions */
        enemyPower.clear();
        initEnemyPower(minPower, maxPower);
        initPotions();

        /* Reset des boutons */
        resetButtons();
    }

    /* Réinitialisation intégrale de la partie par défaut (non customisée) */
    private void reInitAll(){
        isLost = false;
        isWon = false;
        isCustomGame = false;
        level = 1;
        minPower = 1;
        maxPower = 150;
        initialLife = 10;
        initialPower = 100;

        displayPower.setText("100");

        displayLevel.setText("Etage " + level);
        displayLife.setText(Integer.toString(initialLife));
        nbRoomLeft.setText(R.string.nb_room_left);

        monstersLeft = 16;

        enemyPower.clear();
        initEnemyPower(minPower, maxPower);

        initPotions();

        displayGameStatus.setText(R.string.letsgo);
        displayResult.setText(R.string.goodluck);
        resetButtons();
    }

    /* Réinitialisation de la partie */
    private void restartGame(){
        askName();
        reInitAll();
    }

    /* Fonction qui affiche la popup afin de récupérer le nom du joueur */
    private void askName(){
        namePopup = new NamePopup(this);
        namePopup.build();
    }


    private Vector getStageandPower(String s){
        Vector stageAndPower = new Vector();

        int index = 0; //Index servant à parcourir le score
        int nb = 0; //Utile afin de se repérer durant le parcours de notre score (Représente le nombre de ':' rencontré(s))
        String stage = ""; //Etage - Niveau atteint lors de la dernière partie
        String power = ""; //Puissance atteinte lors de la dernière partie

        while (index != s.length() -1) {
            //Si le caractère actuel est un ':'
            if(s.substring(index, index+1).matches(":")){
                int j = index+2; //Saut de l'espace présent dans la string
                nb++;

                /* Parcours afin de récupérer soit l'étage, soit la puissance du score */
                while(!s.substring(j,j+1).matches(" ")) {
                    if (nb == 1) { //Premier ':'
                        stage += s.substring(j, j + 1);
                    } else if (nb == 2) { //Deuxième ':'
                        power += s.substring(j, j + 1);
                    }
                    j++;
                }

            }
            index++; // Passage au caractère suivant
        }

        stageAndPower.add(stage);
        stageAndPower.add(power);

        System.out.println("YOOOOOO " + stageAndPower.get(0) + " EHHHHE " + stageAndPower.get(1));

        return stageAndPower;
    }

    /* Fonction permettant la sauvegarde du score de la partie qui vient de se terminer */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void sauvegarde(){
        Toast.makeText(MainActivity.this, "Sauvegarde du score en cours...", Toast.LENGTH_SHORT).show();

        /* Récupération de la date actuelle sous la forme DD/MM/YYYY */
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/YYYY");
        Date now = new Date();
        String date = formatter.format(now);

        /* Construction du score */
        String score = displayPlayerName.getText().toString() + " - Etage : " + level + " - Puissance : " + displayPower.getText().toString()+ " - " + date;

        int insert = 0; //index où le score sera inséré dans le listItems
        int index = 0; //Index servant à parcourir le score
        int nb = 0; //Utile afin de se repérer durant le parcours de notre score (Représente le nombre de ':' rencontré(s))

        boolean done = false; //Boolean permettant d'indiquer si l'index a été trouvé

        String stage = ""; //Etage - Niveau atteint lors de la dernière partie
        String power = ""; //Puissance atteinte lors de la dernière partie

        String currentScore = ""; //Score actuel que l'on traite dans le listItems
        String currentStage = ""; //String qui récupère la valeur de l'étage du score actuel
        String currentPower = ""; //String qui récupère la valeur de la puissance du score actuel

        /* Parcours du dernier score et récupération de l'étage et la puissance atteint(es) */
        while (index != score.length() -1) {
            //Si le caractère actuel est un ':'
            if(score.substring(index, index+1).matches(":")){
                int j = index+2;
                nb++;

                /* Parcours afin de récupérer soit l'étage, soit la puissance du score */
                while(!score.substring(j, j+1).matches(" ")) {
                    if (!score.substring(j, j+1).matches(" ")) {
                        if (nb == 1) {
                            stage += score.substring(j, j+1);

                        } else if (nb == 2) {
                            power += score.substring(j, j+1);
                        }
                    }
                    j++;
                }
            }
            index++; // Passage au caractère suivant
        }

        /* Si la liste est vide alors on ajoute */
        if(listItems.isEmpty()){
            listItems.add(score);
        } else { //Si elle n'est pas vide, on parcourt la liste afin de trouver où insérer le dernier score
            for (int i = 0; i < listItems.size(); i++) {
                nb=0;
                index = 0;
                currentPower = "";
                currentStage = "";

                currentScore = listItems.get(i); //Récupération du score à traiter

                /* Parcours du score à traiter et  récupération du niveau et de la puissance de ce score */
                while (index != currentScore.length() - 1) {
                    if((index+1) > currentScore.length()){ //Si on est en fin de chaîne, on arrête
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

                /* Condition qui permet de bien récupérer l'index où positionner le dernier score */
                if (((Integer.parseInt(stage) > Integer.parseInt(currentStage)) || (Integer.parseInt(stage) == Integer.parseInt(currentStage) && Integer.parseInt(power) > Integer.parseInt(currentPower))) && !done ) {
                    insert = i;
                    done = true;
                }
            }

            if(done) { //Si un index a été trouvé on ajoute le score à cet index
                listItems.add(insert, score);
                /* On s'assure de n'avoir que les 10 meilleurs scores */
                if(listItems.size() > 10){
                    for (int i = 10; i<=listItems.size(); i++){
                        listItems.remove(i);
                    }
                }

            } else { //Sinon on l'ajoute à la fin
                listItems.add(score);
                /* On s'assure de n'avoir que les 10 meilleurs scores */
                if(listItems.size() > 10){
                    for (int i = 10; i<=listItems.size(); i++){
                        listItems.remove(i);
                    }
                }
            }
        }

        /* On réécrit les scores dans le fichier de sauvegarde */
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


    /* Initialisation de la puissance des ennemis entre minPower et maxPower */
    private void initEnemyPower(int minPower, int maxPower){
        int powerMonster;
        for(int i=0; i<16; i++){
            powerMonster = genererInt(minPower, maxPower);
            System.out.println(powerMonster);
            enemyPower.add(powerMonster);
        }
    }

    /* Initialisation des indexs des potions */
    private void initPotions(){
        powerPotion = genererInt(0,15);
        do{
            lifePotion = genererInt(0,15);
        }while(powerPotion == lifePotion); /* Pour ne pas mettre les potions au même monstre */
    }

    /* Reinitialisation des boutons */
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

    /* Génération d'un entier aléatoire entre min et max COMPRIS */
    public int genererInt(int min, int max){
        Random random = new Random();
        int nb;
        nb = min + random.nextInt(max+1 - min);
        return nb;
    }

    /* Fonction permettant de récupérer les scores enregistrés */
    private void getFromIntern() throws IOException {

        String value = ""; //String qui contiendra le score

        FileInputStream inputStream = openFileInput("sauvegarde"); //Ouverture du fichier avec les scores enregistrés
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"utf8"), 8192);
        int content;

        /* Boucle remplissant notre chaine "value" */
        while ((content=br.read())!=-1){

            if (content==10){ //Si on rencontre un saut de ligne alors la chaîne est finie
                listItems.add(value); //On ajoute alors notre chaîne qui n'est autre qu'un score
                value = ""; //Réinitialisation de value pour la prochaine itération
                continue; //Itération suivante
            }

            value += (char) content; //Contruction de notre chaîne

        }
    }
}

