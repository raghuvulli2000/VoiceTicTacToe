package com.tictactoe.VoiceTacToe;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alan.alansdk.AlanCallback;
import com.alan.alansdk.AlanConfig;
import com.alan.alansdk.ScriptMethodCallback;
import com.alan.alansdk.button.AlanButton;
import com.alan.alansdk.events.EventCommand;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView playerOneScore, PlayerTwoScore,playerStatus,playing;
    private Button [] buttons= new Button[9];
    private Button resetGame, home;
    private int playerOneScoreCount, playerTwoScoreCount, roundCount;
    boolean activePlayer;
    ImageView guide;
    Drawable draw;
    AlanButton alanButton;
    String playerOne, playerTwo;

    //player 1=>0
    //player 2=> 1
    //empty => 2

    int[] gameState = {2,2,2,2,2,2,2,2,2};
    int[][] winningPositions = {
            {0,1,2}, {3,4,5}, {6,7,8}, //rows
            {0,3,6}, {1,4,7}, {2,5,8}, //columns
            {0,4,8}, {2,4,6} //cross
    };

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        guide = findViewById(R.id.guide);
        draw = getResources().getDrawable(R.drawable.guide);
        guide.setImageDrawable(draw);
        playerOneScore = findViewById(R.id.playerOneScore);
        PlayerTwoScore = findViewById(R.id.playerTwoScore);
        playerStatus = findViewById(R.id.playerStatus);

        resetGame = findViewById(R.id.resetGame);
       home = findViewById(R.id.home);
        playing= findViewById(R.id.playing);
        playing.setText("Player Turn: Player 1 (X)");

        home.setOnClickListener(new View.OnClickListener() {
            @Override
        public void onClick(View v) {
                goHome();
        }
    });
        //Alan AI embed
        alanButton = findViewById(R.id.alan_button);
        AlanConfig alanConfig = AlanConfig.builder()
                .setProjectId("cb4d53aef615867af4c16ae003b2b73b2e956eca572e1d8b807a3e2338fdd0dc/stage")
                .build();
        alanButton.initWithConfig(alanConfig);
        // embed end
        //game with voice commands
        alanButton.registerCallback(new AlanCallback() {
            @Override
            public void onCommand(EventCommand eventcommand) {
                super.onCommand(eventcommand);
                try{
                    Log.i("TAG", eventcommand.getData().getJSONObject("data").get("command").toString());
                } catch (JSONException e){
                    e.printStackTrace();
                }
                String commandStr = null;
                try{
                    commandStr = eventcommand.getData().getJSONObject("data").get("command").toString();
                }catch(JSONException e){
                    e.printStackTrace();
                }
                switch (commandStr) {
                    case "one":
                        AlanCommands(0, 0);

                        break;
                    case "two":
                        AlanCommands(1, 1);

                        break;
                    case "three":
                        AlanCommands(2, 2);
                        break;
                    case "four":
                        AlanCommands(3, 3);

                        break;
                    case "five":
                        AlanCommands(4, 4);

                        break;
                    case "six":
                        AlanCommands(5, 5);

                        break;
                    case "seven":
                        AlanCommands(6, 6);

                        break;
                    case "eight":
                        AlanCommands(7, 7);

                        break;
                    case "nine":
                        AlanCommands(8, 8);
                        break;
                    case "Home":
                        goHome();
                        break;
                }

                if(commandStr.equals("restart")){
                    playAgain();
                    playerOneScoreCount=0;
                    playerTwoScoreCount=0;
                    playerStatus.setText(" ");
                    updatePlayerScore();
                }

            }

        });

        for (int i = 0; i < buttons.length; i++) {
            String ButtonID = "btn_" + i;
            int resourceID = getResources().getIdentifier(ButtonID, "id", getOpPackageName());
            buttons[i] = findViewById(resourceID);
            buttons[i].setOnClickListener(this);
        }
        roundCount = 0;
        playerOneScoreCount = 0;
        playerTwoScoreCount = 0;
        activePlayer = true;
    }// game play with voice commands

    protected void onStart(){
        super.onStart();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    reset();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                alanButton.activate();
                alanButton.playText("say a number from one through nine to place an X or an O or say go home to go back");
                //Do all thing after 5000ms
            }
        }, 2000);
    }

    @Override
    protected void onStop(){
        super.onStop();
        playAgain();
        playerOneScoreCount = 0;
        playerTwoScoreCount = 0;
        playerStatus.setText(" ");
        updatePlayerScore();
        try {
            reset();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if(!((Button)v).getText().toString().equals("")){
            return;
        }
        String ButtonID = v.getResources().getResourceEntryName(v.getId());
        int gameStatePointer = Integer.parseInt(ButtonID.substring(ButtonID.length()-1));

        // Button for Touch
        if(activePlayer){
            playing.setText("Player Turn: Player 2 (O)");
            ((Button)v).setText("X");
            gameState[gameStatePointer]= 0;
            Integer one = new Integer(gameStatePointer);
            playerOne = one.toString();
            try {
                playerOne();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            playing.setText("Player Turn: Player 1 (X)");
            ((Button)v).setText("O");
            gameState[gameStatePointer]= 1;
            Integer two = new Integer(gameStatePointer);
            playerTwo = two.toString();
            try {
                playerTwo();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        roundCount++;
        maingame();
        } // game play when you touch screen and play


    public boolean checkWinner() {
        boolean winnerResult = false;
        for (int[] winningPositions : winningPositions) {
            if (gameState[winningPositions[0]] == gameState[winningPositions[1]] &&
                    gameState[winningPositions[1]] == gameState[winningPositions[2]] &&
                    gameState[winningPositions[0]] != 2) {
                winnerResult = true;
                break;
            }
        }
        return winnerResult;
    } // checks the winner
    public void updatePlayerScore(){
        playerOneScore.setText(Integer.toString(playerOneScoreCount));
        PlayerTwoScore.setText(Integer.toString(playerTwoScoreCount));
    } // updates the player's score
    public void playAgain(){
        roundCount=0;
        activePlayer=true;
        playing.setText("Player Turn: Player 1 (X)");
        for(int i=0; i< buttons.length; i++){
            gameState[i]=2;
            buttons[i].setText("");
        }
    } // restarts the game

   public void AlanCommands(int x, int arraynum) {

       if (buttons[x].getText().equals("")) {
           if (activePlayer) {
               playing.setText("Player Turn: Player 2 (O)");
               buttons[x].setText("X");
               gameState[arraynum] = 0;
           } else {
               playing.setText("Player Turn: Player 1 (X)");
               buttons[x].setText("O");
               gameState[arraynum] = 1;
           }
           roundCount++;
           maingame();
       }
       else{
           Toast.makeText(this, "the given number is already chosen, choose another number", Toast.LENGTH_SHORT).show();
           
       }
   }  // tells you what to do when you get a command from ALan

   public void maingame(){
       if(checkWinner()) {
           if (activePlayer) {
               playerOneScoreCount++;
               updatePlayerScore();
               Toast.makeText(this, "Player One WON", Toast.LENGTH_SHORT).show();
               alanButton.activate();
               alanButton.playText("player one won");
               playAgain();

           } else {
               playerTwoScoreCount++;
               updatePlayerScore();
               Toast.makeText(this, "Player Two WON", Toast.LENGTH_SHORT).show();
               alanButton.activate();
               alanButton.playText("player two won");
               playAgain();

           }
       }
       else if(roundCount==9){
           playAgain();
           Toast.makeText(this, "No Winner", Toast.LENGTH_SHORT).show();
           alanButton.activate();
           alanButton.playText("It is a tie");
       }
       else{
           activePlayer = !activePlayer;
       }
       if(playerOneScoreCount>playerTwoScoreCount){
           playerStatus.setText("Player One is Winning!");
       }
       else if(playerTwoScoreCount>playerOneScoreCount){
           playerStatus.setText("Player Two is Winning!");
       }
       else{
           playerStatus.setText("");
       }

       resetGame.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               playAgain();
               playerOneScoreCount=0;
               playerTwoScoreCount=0;
               playerStatus.setText(" ");
               updatePlayerScore();
               try {
                   reset();
               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }
       });

   } // checks the winner and flips the active player

    public static void wait(int ms){
        try{
            Thread.sleep(ms);
        }
        catch(InterruptedException ex){
            Thread.currentThread().interrupt();
        }
    }
    public void goHome(){
        Intent intent = new Intent(this, com.tictactoe.VoiceTacToe.HomeScreen.class);
        startActivity(intent);
    }
    public void reset() throws JSONException{
        JSONObject callParameters = new JSONObject("{\"data\": \"restart\"}");
        alanButton.callProjectApi("Restart", callParameters.toString(), new ScriptMethodCallback() {
            @Override
            public void onResponse(String methodName, String body, String error) {
                // handle error and result here
            }
        });
    }

    public void playerOne() throws JSONException {
        JSONObject callParameters = new JSONObject("{\"data\": \""+playerOne+"\"}");
        alanButton.callProjectApi("playerOne", callParameters.toString(), new ScriptMethodCallback() {
            @Override
            public void onResponse(String methodName, String body, String error) {
                // handle error and result here
            }
        });
    }

    public void playerTwo() throws JSONException {
        JSONObject callParameters = new JSONObject("{\"data\": \""+playerTwo+"\"}");
        alanButton.callProjectApi("playerTwo", callParameters.toString(), new ScriptMethodCallback() {
            @Override
            public void onResponse(String methodName, String body, String error) {
                // handle error and result here
            }
        });
    }

    }
