package tk.smashr.smashit;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class AdvancedSmashing extends AppCompatActivity {
    public List<Boolean> rightAnswer = new ArrayList<>();
    public List<Integer> ranks = new ArrayList<>();
    public List<Integer> answers = new ArrayList<>();
    public List<Boolean> loggedIn = new ArrayList<>();

    RequestQueue queue;
    WebView kahootConsole;
    boolean queuedAnswer = false;
    boolean answerPossible = false;
    boolean launched = false;
    int gamePin;
    int currentId = -1;
    int selectedAnswer = -1;
    List<KahootHandle> kahootSmashers = new ArrayList<>();
    List<OkHttpClient> client = new ArrayList<>();

    List<KahootChallenge> challenges = new ArrayList<>();

    //UI components
    Button redBtn;
    Button blueBtn;
    Button yellowBtn;
    Button greenBtn;

    TextView redText;
    TextView blueText;
    TextView yellowText;
    TextView greenText;

    CheckBox isRandom;

    TextView joined;
    TextView answered;

    Handler interval = new Handler();
    Runnable updateStatsRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_smashing);

        //Get UI
        redBtn = (Button) findViewById(R.id.redButton);
        blueBtn = (Button) findViewById(R.id.blueButton);
        yellowBtn = (Button) findViewById(R.id.yellowButton);
        greenBtn = (Button) findViewById(R.id.greenButton);
        redText = (TextView) findViewById(R.id.redText);
        yellowText = (TextView) findViewById(R.id.yellowText);
        blueText = (TextView) findViewById(R.id.blueText);
        greenText = (TextView) findViewById(R.id.greenText);
        isRandom = (CheckBox) findViewById(R.id.isRandom);
        joined = (TextView) findViewById(R.id.joined);
        answered = (TextView) findViewById(R.id.answered);

        redBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClicked(0);
            }
        });
        blueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClicked(1);
            }
        });
        yellowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClicked(2);
            }
        });
        greenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClicked(3);
            }
        });

        isRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (queuedAnswer) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < kahootSmashers.size(); i++) {
                                kahootSmashers.get(i).AnswerQuestion(4);
                            }
                        }
                    });
                }
            }
        });

        UpdateStats();

        updateStatsRun = new Runnable() {
            @Override
            public void run() {
                UpdateStats();
                UpdateSelectedAnswer();
                interval.postDelayed(updateStatsRun, 30);
            }
        };

        Bundle b = getIntent().getExtras();
        if (b != null) {
            gamePin = b.getInt("gamePin");
        }
        SmashingLogic.readAndInterpretSettings(getApplicationContext());

        queue = Volley.newRequestQueue(this);
        kahootConsole = new WebView(this);
        kahootConsole.getSettings().setJavaScriptEnabled(true);
        kahootConsole.getSettings().setDefaultTextEncodingName("utf-8");
        kahootConsole.setVisibility(View.GONE);
        kahootConsole.loadUrl("https://kahoot.it");
        kahootConsole.getSettings().setBlockNetworkImage(true);
        kahootConsole.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {

                if (!launched) {
                    //startService(service);
                    //if (SmashingLogic.smashingMode == 0)
                    //{
                    for (int i = 0; i < SmashingLogic.numberOfKahoots; i++) {
                        challenges.add(new KahootChallenge(gamePin, kahootConsole, queue, AdvancedSmashing.this));
                    }
                    //}
                    //else
                    //{
                    //    challenges.add(new KahootChallenge(gamePin, kahootConsole, queue, AdvancedSmashing.this));
                    //}
                }
                launched = true;
            }
        });
        interval.postDelayed(updateStatsRun, 30);
    }

    @Override
    protected void onDestroy() {
        for (int i = 0; i < kahootSmashers.size(); i++) {
            kahootSmashers.get(i).disconnect();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        for (int i = 0; i < kahootSmashers.size(); i++) {
            kahootSmashers.get(i).disconnect();
        }

        Intent startSmash = new Intent(AdvancedSmashing.this, GamePin.class);
        startActivity(startSmash);
        finish();
    }

    public void buttonClicked(int index) {
        if (!answerPossible) {
            return;
        }
        isRandom.setChecked(false);
        if (selectedAnswer == index) {
            selectedAnswer = -1;
        } else {
            selectedAnswer = index;
        }
        UpdateSelectedAnswer();
        if (queuedAnswer) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < kahootSmashers.size(); i++) {
                        kahootSmashers.get(i).AnswerQuestion(4);
                    }
                }
            });
        }
    }

    private void UpdateSelectedAnswer() {
        if (answerPossible && !isRandom.isChecked()) {
            redBtn.setBackgroundResource(selectedAnswer == 0 ? R.drawable.red_answer_pressed : R.drawable.red_answer);
            blueBtn.setBackgroundResource(selectedAnswer == 1 ? R.drawable.blue_answer_pressed : R.drawable.blue_answer);
            yellowBtn.setBackgroundResource(selectedAnswer == 2 ? R.drawable.yellow_answer_pressed : R.drawable.yellow_answer);
            greenBtn.setBackgroundResource(selectedAnswer == 3 ? R.drawable.green_answer_pressed : R.drawable.green_answer);
        } else {
            redBtn.setBackgroundResource(R.drawable.red_answer_pressed);
            blueBtn.setBackgroundResource(R.drawable.blue_answer_pressed);
            yellowBtn.setBackgroundResource(R.drawable.yellow_answer_pressed);
            greenBtn.setBackgroundResource(R.drawable.green_answer_pressed);
        }
    }

    public void addToken(String token) {
        currentId++;
        if (currentId % 5 == 0) {
            client.add(new OkHttpClient());
        }
        rightAnswer.add(false);
        ranks.add(100);
        answers.add(-1);
        loggedIn.add(false);
        kahootSmashers.add(new KahootHandle(gamePin, currentId, client.get(client.size() - 1), this, token));
    }

    public String GetName(int index) {
        return SmashingLogic.generateName(index);
    }

    public void LoggedIn(int id, boolean loggedInB) {
        loggedIn.set(id, loggedInB);
    }

    public Integer GetHighestRank() {
        int highest = 0;
        for (int i = 1; i < ranks.size(); i++) {
            if (ranks.get(i) < ranks.get(highest)) {
                highest = i;
            }
        }
        return ranks.get(highest);
    }

    public Integer GetResponse(int choices, int index) {
        if (isRandom.isChecked()) {
            int choice = (int) (Math.random() * choices);
            answers.set(index, choice);
            return choice;
        } else if (selectedAnswer != -1) {
            answers.set(index, selectedAnswer);
            return selectedAnswer;
        }
        answers.set(index, -1);
        queuedAnswer = true;
        return -1;
    }

    public void addQuestionResult(int index, boolean correct, int rank) {
        rightAnswer.set(index, correct);
        ranks.set(index, rank);
        queuedAnswer = false;
        answerPossible = false;
        selectedAnswer = -1;
    }

    private void UpdateStats() {
        if (loggedIn.size() == 0) {
            redText.setText("0/0");
            greenText.setText("0/0");
            blueText.setText("0/0");
            yellowText.setText("0/0");
            joined.setText(getString(R.string.joined) + " N/A");
            answered.setText(getString(R.string.answered) + " N/A");
            return;
        }
        int loggedInCount = 0;
        for (int i = 0; i < loggedIn.size(); i++) {
            if (loggedIn.get(i)) {
                loggedInCount++;
            }
        }
        int red = 0;
        int blue = 0;
        int yellow = 0;
        int green = 0;
        for (int i = 0; i < answers.size(); i++) {
            switch (answers.get(i)) {
                case 0:
                    red++;
                    break;
                case 1:
                    blue++;
                    break;
                case 2:
                    yellow++;
                    break;
                case 3:
                    green++;
            }
        }
        joined.setText(getString(R.string.joined) + " " + loggedInCount + "/" + loggedIn.size());
        answered.setText(getString(R.string.answered) + " " + (red + blue + yellow + green) + "/" + loggedIn.size());
        redText.setText(red + "/" + loggedIn.size());
        blueText.setText(blue + "/" + loggedIn.size());
        yellowText.setText(yellow + "/" + loggedIn.size());
        greenText.setText(green + "/" + loggedIn.size());
    }

    public void makeAnswerPossible() {
        answerPossible = true;
    }
}
