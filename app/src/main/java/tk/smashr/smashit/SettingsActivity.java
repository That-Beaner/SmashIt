package tk.smashr.smashit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    Spinner namingMethod;
    Spinner smashingMethod;
    TextView exampleName;
    EditText baseName;
    TextView title5;
    EditText number;
    RelativeLayout numberInput;

    @Override
    public void onBackPressed() {
        Intent startSmash = new Intent(SettingsActivity.this,GamePin.class);
        startActivity(startSmash);
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        numberInput = (RelativeLayout)findViewById(R.id.numberLayout);

        namingMethod = (Spinner) findViewById(R.id.namingMethod);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,R.array.NamingMethods,android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        namingMethod.setAdapter(adapter2);


        smashingMethod = (Spinner) findViewById(R.id.smashingMode);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,R.array.SmashingMethods,android.R.layout.simple_spinner_dropdown_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        smashingMethod.setAdapter(adapter3);


        namingMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                SmashingLogic.namingMethod = (int)id;
                baseNameVisible(SmashingLogic.namingMethod==0);
                updateExample();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                SmashingLogic.namingMethod = 0;
                baseNameVisible(true);
                namingMethod.setSelection(0);
                updateExample();
            }
        });

        smashingMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                SmashingLogic.smashingMode = (int)id;
                updateNumber();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                SmashingLogic.smashingMode = 0;
                smashingMethod.setSelection(0);
                updateNumber();
            }
        });

        Button saveBtn = (Button) findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(number.getText().length()==0)
                {
                    number.setText("50");
                }
                SmashingLogic.numberOfKahoots = Integer.parseInt(number.getText().toString());
                SmashingLogic.saveToFile(getApplicationContext());
                onBackPressed();
            }
        });

        Button newExample = (Button)findViewById(R.id.newExample);
        newExample.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                updateExample();
            }
        });

        exampleName = (TextView)findViewById(R.id.exampleName);
        SmashingLogic.readAndInterpretSettings(getApplicationContext());

        title5 = (TextView)findViewById(R.id.textView5);
        baseName = (EditText) findViewById(R.id.namingStart);
        baseName.setText(SmashingLogic.baseName);
        baseNameVisible(SmashingLogic.namingMethod!=0);
        baseName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                SmashingLogic.baseName = s.toString();
                updateExample();
            }
        });

        number = (EditText) findViewById(R.id.number);
        number.setText(SmashingLogic.numberOfKahoots+"");
        number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //if(!canSmash&&!number.getText().toString().isEmpty() && Integer.parseInt(number.getText().toString())>35) {
                //    tooMany.show();
                //}
                //if(!number.getText().toString().isEmpty() && Integer.parseInt(number.getText().toString())>200)
                //{
                //    number.setText("200");
                //    maxReached.show();
                //}
                limitNumberInput();
            }
        });
        namingMethod.setSelection(SmashingLogic.namingMethod);
        smashingMethod.setSelection(SmashingLogic.smashingMode);
        updateExample();
    }

    private void updateExample()
    {
        exampleName.setText(getString(R.string.example)+" "+SmashingLogic.generateName((int)(Math.random()*100)));
    }

    private void updateNumber()
    {
        //if (SmashingLogic.smashingMode==1)
        //{
        //    numberInput.setVisibility(View.GONE);
        //}
        //else
        //{
        //    numberInput.setVisibility(View.VISIBLE);
        //}
        limitNumberInput();
    }

    private void limitNumberInput()
    {
        if(number.getText().toString().isEmpty())
        {
            return;
        }
        switch (SmashingLogic.smashingMode)
        {
            //CHANGE THIS TO INCREASE SMASHER NUMBER
            case 0:
                if(Integer.parseInt(number.getText().toString())>100)
                {
                    number.setText("100");
                    //Warning, I limited it to 100 kahoots for a reason
                }
                break;
            default:
                if(Integer.parseInt(number.getText().toString())>50)
                {
                    number.setText("50");
                }
        }
    }

    private void baseNameVisible(Boolean hide)
    {
        title5.setVisibility(hide?View.GONE:View.VISIBLE);
        baseName.setVisibility(hide?View.GONE:View.VISIBLE);
    }
}