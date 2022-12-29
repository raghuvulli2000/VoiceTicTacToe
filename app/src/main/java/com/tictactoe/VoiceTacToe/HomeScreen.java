package com.tictactoe.VoiceTacToe;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.alan.alansdk.AlanCallback;
import com.alan.alansdk.AlanConfig;
import com.alan.alansdk.ScriptMethodCallback;
import com.alan.alansdk.button.AlanButton;
import com.alan.alansdk.events.EventCommand;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeScreen extends AppCompatActivity {

    private Button singlePlayer, multiPlayer;
    private ImageView homePic;
    Drawable drawable;
    AlanButton alanButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);

        homePic = findViewById(R.id.homePic);
        drawable = getResources().getDrawable(R.drawable.image);
        homePic.setImageDrawable(drawable);
        singlePlayer = findViewById(R.id.SinglePlayer);
        multiPlayer = findViewById(R.id.multiPlayer);
        multiPlayer.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openMultiPlay();
        }
        });

        singlePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSinglePlay();
            }
        });

        alanButton = findViewById(R.id.alan_button);
        AlanConfig alanConfig = AlanConfig.builder()
                .setProjectId("6b8eaa7b8b7f9bedf4c16ae003b2b73b2e956eca572e1d8b807a3e2338fdd0dc/stage")
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
                    case "Single Player":
                        openSinglePlay();
                        break;
                    case "Two Players":
                        openMultiPlay();
                        break;
                }
            }
        });

    }

    protected void onStart(){
        super.onStart();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                alanButton.activate();
                try {
                    playIntro();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Do all thing after 5000ms
            }
        }, 2000);
    }


    public void playIntro() throws JSONException {
        JSONObject callParameters = new JSONObject("{\"data\":\"Started\"}");
        alanButton.callProjectApi("startGame", callParameters.toString(), new ScriptMethodCallback() {
            @Override
            public void onResponse(String methodName, String body, String error) {
                // handle error and result here
            }
        });
    }
    public void openMultiPlay(){
        Intent intent = new Intent(this, com.tictactoe.VoiceTacToe.MainActivity.class);
        startActivity(intent);
    }

    public void openSinglePlay(){
        Intent intent = new Intent(this, com.tictactoe.VoiceTacToe.SinglePlayer.class);
        startActivity(intent);
    }
}

