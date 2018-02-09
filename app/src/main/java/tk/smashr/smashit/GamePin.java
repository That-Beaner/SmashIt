package tk.smashr.smashit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


public class GamePin extends AppCompatActivity {
    EditText gamePin;
    RequestQueue queue;
    AlertDialog pinWrong;
    AlertDialog oldSmashing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AlertDialog.Builder badPin = new AlertDialog.Builder(this);
        badPin.setPositiveButton(getString(R.string.contin), null).setMessage(getString(R.string.badPinBody)).setTitle(getString(R.string.badPin));
        pinWrong = badPin.create();

        AlertDialog.Builder oldSmashBuilder = new AlertDialog.Builder(this);
        oldSmashBuilder.setPositiveButton(getString(R.string.contin), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent startSmash = new Intent(GamePin.this, Smashing.class);
                Bundle b = new Bundle();
                b.putInt("gamePin", Integer.parseInt(gamePin.getText().toString()));
                startSmash.putExtras(b);
                startActivity(startSmash);
                finish();
            }
        }).setNegativeButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent startSmash = new Intent(GamePin.this, SettingsActivity.class);
                startActivity(startSmash);
                finish();
            }
        }).setTitle(getString(R.string.oldSmash)).setMessage(getString(R.string.oldSmashInfo));
        oldSmashing = oldSmashBuilder.create();

        SmashingLogic.readAndInterpretSettings(getApplicationContext());

        queue = Volley.newRequestQueue(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_pin);

        gamePin = (EditText) findViewById(R.id.gamePin);

        Button enterBtn = (Button) findViewById(R.id.enter);
        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!gamePin.getText().toString().isEmpty()) {
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://kahoot.it/reserve/session/" + gamePin.getText().toString(),
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    switch (SmashingLogic.smashingMode) {
                                        case 1:
                                        case 2:
                                            //Warning of the retro
                                            oldSmashing.show();
                                            break;
                                        case 0:
                                        default:
                                            Intent startSmash = new Intent(GamePin.this, AdvancedSmashing.class);
                                            Bundle b = new Bundle();
                                            b.putInt("gamePin", Integer.parseInt(gamePin.getText().toString()));
                                            startSmash.putExtras(b);
                                            startActivity(startSmash);
                                            finish();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Alert
                            Log.println(Log.ERROR, "Pin", "Bad pin");
                            pinWrong.show();
                        }
                    });
                    queue.add(stringRequest);
                }
            }
        });

        Button settingsButton = (Button) findViewById(R.id.settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startSmash = new Intent(GamePin.this, SettingsActivity.class);
                startActivity(startSmash);
                finish();
            }
        });

    }
}
