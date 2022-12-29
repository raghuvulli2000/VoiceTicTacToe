package com.tictactoe.VoiceTacToe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.RequiresApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alan.alansdk.AlanCallback;
import com.alan.alansdk.AlanConfig;
import com.alan.alansdk.ScriptMethodCallback;
import com.alan.alansdk.button.AlanButton;
import com.alan.alansdk.events.EventCommand;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class SinglePlayer extends AppCompatActivity implements View.OnClickListener {

    private TextView PlayerOneScore, playerTwoScore, PlayerStatus, Playing;
    private Button[] Buttons = new Button[9];
    private Button ResetGame, home;
    private int PlayerOneScoreCount, PlayerTwoScoreCount, RoundCount;
    boolean ActivePlayer;
    AlanButton alanButton;
    String randomNum, pointer;
    //player 1=>0
    //player 2=> 1
    //empty => 2

    int[] gameState = {2, 2, 2, 2, 2, 2, 2, 2, 2};
    int[][] winningPositions = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, //rows
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, //columns
            {0, 4, 8}, {2, 4, 6} //cross
    };

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player);

        PlayerOneScore = findViewById(R.id.SplayerOneScore);
        playerTwoScore = findViewById(R.id.BotScore);
        PlayerStatus = findViewById(R.id.SplayerStatus);

        ResetGame = findViewById(R.id.SresetGame);
        home = findViewById(R.id.Shome);
        Playing = findViewById(R.id.Splaying);
        Playing.setText("Player Turn: Player (X)");
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gohome();
            }
        });


        alanButton = findViewById(R.id.Salan_button);
        AlanConfig alanConfig = AlanConfig.builder()
                .setProjectId("43c4f760647d35a5f4c16ae003b2b73b2e956eca572e1d8b807a3e2338fdd0dc/stage")
                .build();
        alanButton.initWithConfig(alanConfig);

        alanButton.registerCallback(new AlanCallback() {
            @Override
            public void onCommand(EventCommand eventcommand) {
                super.onCommand(eventcommand);
                try {
                    Log.i("TAG", eventcommand.getData().getJSONObject("data").get("command").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String commandStr = null;
                try {
                    commandStr = eventcommand.getData().getJSONObject("data").get("command").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                switch (commandStr) {
                    case "Home":
                        gohome();
                        break;
                    case "one":
                            Commands(0, 0);
                        break;
                    case "two":
                        Commands(1, 1);

                        break;
                    case "three":
                        Commands(2, 2);
                        break;
                    case "four":
                        Commands(3, 3);

                        break;
                    case "five":
                        Commands(4, 4);

                        break;
                    case "six":
                        Commands(5, 5);

                        break;
                    case "seven":
                        Commands(6, 6);

                        break;
                    case "eight":
                        Commands(7, 7);

                        break;
                    case "nine":
                        Commands(8, 8);
                        break;
                }
            }
        });

        for (int i = 0; i < Buttons.length; i++) {
            String buttonID = "Sbtn_" + i;
            int ResourceID = getResources().getIdentifier(buttonID, "id", getOpPackageName());
            Buttons[i] = findViewById(ResourceID);
            Buttons[i].setOnClickListener(this);
        }

        RoundCount = 0;
        PlayerOneScoreCount = 0;
        PlayerTwoScoreCount = 0;
        ActivePlayer = true;
    }

    protected void onStart(){
        super.onStart();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    restart();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                alanButton.activate();
                alanButton.playText("say a number from one through nine to place an X or say go home to go back");
                //Do all thing after 5000ms
            }
        }, 2000);
    }
    @Override
    protected void onStop(){
        super.onStop();
        PlayAgain();
        PlayerOneScoreCount = 0;
        PlayerTwoScoreCount = 0;
        PlayerStatus.setText(" ");
        UpdatePlayerScore();
        try {
            restart();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (!((Button) v).getText().toString().equals("")) {
            return;
        }
        String ButtonID = v.getResources().getResourceEntryName(v.getId());
        int gameStatePointer = Integer.parseInt(ButtonID.substring(ButtonID.length() - 1));

        // Button for Touch
        if (ActivePlayer) {
            Playing.setText("Player Turn: Bot (O)");
            ((Button) v).setText("X");
            gameState[gameStatePointer] = 0;
            Integer p = new Integer(gameStatePointer);
            pointer = p.toString();
            try {
                Player();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RoundCount++;
            mainGame();
        }
        else{
            Toast.makeText(this, "It is the bot's turn, press on the 'BOT' button", Toast.LENGTH_SHORT).show();
            }

        }

        public void gohome () {
            Intent intent = new Intent(this, com.tictactoe.VoiceTacToe.HomeScreen.class);
            startActivity(intent);
        }
        public void Commands ( int x, int arraynum){
            if (Buttons[x].getText().equals("")) {
                if(ActivePlayer) {
                    Playing.setText("Player Turn: Player 2 (0)");
                    Buttons[x].setText("X");
                    gameState[arraynum] = 0;
                }
                RoundCount++;
                mainGame();
            } else {
                alanButton.activate();
                alanButton.playText("This number is already chosen, choose another number");
                Toast.makeText(this, "the given number is already chosen, choose another number", Toast.LENGTH_SHORT).show();

            }
        }

        public void BotGame() throws JSONException {
            if (!ActivePlayer) {
                Playing.setText("Player Turn: Player 1 (X)");
                    Random random = new Random();
                    int i = random.nextInt(9);
                    while (Buttons[i].getText().equals("X") || Buttons[i].getText().equals("O")){
                        i = random.nextInt(9);
                    }
                        Integer x = new Integer(i);
                        Buttons[i].setText("O");
                        gameState[i] = 1;
                        randomNum = x.toString();
                        trigger();
                        RoundCount++;
                        mainGame();
                }
            }
        public void mainGame () {
            if (CheckWinner()) {
                if (ActivePlayer) {
                    PlayerOneScoreCount++;
                    UpdatePlayerScore();
                    Toast.makeText(this, "The PLAYER WON", Toast.LENGTH_SHORT).show();
                    alanButton.activate();
                    alanButton.playText("the player won");
                } else {
                    PlayerTwoScoreCount++;
                    UpdatePlayerScore();
                    Toast.makeText(this, "The BOT WON", Toast.LENGTH_SHORT).show();
                    alanButton.activate();
                    alanButton.playText("the bot won");
                }
                PlayAgain();
            } else if (RoundCount == 9) {
                PlayAgain();
                Toast.makeText(this, "No Winner", Toast.LENGTH_SHORT).show();
                alanButton.activate();
                alanButton.playText("It is a tie");
            } else {
                ActivePlayer = !ActivePlayer;

                if(!ActivePlayer){
                    try {
                        BotGame();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (PlayerOneScoreCount > PlayerTwoScoreCount) {
                PlayerStatus.setText("Player is Winning!");
            } else if (PlayerTwoScoreCount > PlayerOneScoreCount) {
                PlayerStatus.setText("The Bot is Winning!");
            } else {
                PlayerStatus.setText("");
            }

            ResetGame.setOnClickListener(v -> {
                PlayAgain();
                PlayerOneScoreCount = 0;
                PlayerTwoScoreCount = 0;
                PlayerStatus.setText(" ");
                UpdatePlayerScore();
                try {
                    restart();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });

        }

        public void trigger() throws JSONException {
            JSONObject callParameters = new JSONObject("{\"data\": \""+randomNum+"\"}");
            alanButton.callProjectApi("BotGame", callParameters.toString(), new ScriptMethodCallback() {
                @Override
                public void onResponse(String methodName, String body, String error) {
                    // handle error and result here
                }
            });
        }

    public void Player() throws JSONException {
        JSONObject callParameters = new JSONObject("{\"data\": \""+pointer+"\"}");
        alanButton.callProjectApi("Player", callParameters.toString(), new ScriptMethodCallback() {
            @Override
            public void onResponse(String methodName, String body, String error) {
                // handle error and result here
            }
        });
    }

        public boolean CheckWinner () {
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
        public void UpdatePlayerScore () {
            PlayerOneScore.setText(Integer.toString(PlayerOneScoreCount));
            playerTwoScore.setText(Integer.toString(PlayerTwoScoreCount));
        } // updates the player's score
        public void PlayAgain () {
            RoundCount = 0;
            ActivePlayer = true;
            Playing.setText("Player Turn: Player (X)");
            for (int i = 0; i < Buttons.length; i++) {
                gameState[i] = 2;
                Buttons[i].setText("");
            }
        }
        public void restart() throws JSONException{
            JSONObject callParameters = new JSONObject("{\"data\": \"restart\"}");
            alanButton.callProjectApi("Restart", callParameters.toString(), new ScriptMethodCallback() {
                @Override
                public void onResponse(String methodName, String body, String error) {
                    // handle error and result here
                }
            });
        }

}



