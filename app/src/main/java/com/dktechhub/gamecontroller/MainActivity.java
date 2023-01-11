package com.dktechhub.gamecontroller;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnTouchListener {
    private SensorManager sensorManager;
    private Sensor sensor;
    boolean inSensorMode=false,inSideSensorMode=false,inUpSensorMode=false;
    float accLimit=2.0f,breakLimit=2f,sideLimit=5;
    int[] screen ={R.id.settings,R.id.unconnected,R.id.sensor,R.id.manual};
    Client c;
    ImageButton settingsButton,saveButton,btnLeft,btnRight,btnUp,btnDown,enter;
    Button connectButton,space;
    EditText ip,side,acc,brek;
    ToggleButton sideSensor,upSensor;
    Switch mode;
    HashMap<Integer,Integer> keys;

    void setScreen(int scr)
    {
        for (int j : screen) {
            findViewById(j).setVisibility(View.GONE);
        }
        findViewById(scr).setVisibility(View.VISIBLE);

        if(scr==R.id.sensor)
        {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        }else{
            sensorManager.unregisterListener(this);
            c.close();
        }
    }
    void loadItems()
    {
        connectButton=findViewById(R.id.btncon);
        settingsButton=findViewById(R.id.settingsbtn);
        ip=findViewById(R.id.remoteip);
        saveButton=findViewById(R.id.btnSave);
        mode=findViewById(R.id.mode);
        side=findViewById(R.id.sideLimit);
        acc=findViewById(R.id.accLimit);
        brek=findViewById(R.id.breakLimit);
        ///out=findViewById(R.id.sensorOutput);
        btnLeft=findViewById(R.id.btn_left);
        btnRight=findViewById(R.id.btn_right);
        space=findViewById(R.id.btn_space);
        sideSensor=findViewById(R.id.toggleButton_side_sensor);
        upSensor=findViewById(R.id.up_sensor_btn);
        btnUp=findViewById(R.id.btn_up);
        btnDown=findViewById(R.id.btn_down);
        enter=findViewById(R.id.btn_enter);

    }

    @SuppressLint("ClickableViewAccessibility")
    void assignListeners()
    {
        settingsButton.setOnClickListener(view -> setScreen(R.id.settings));
        saveButton.setOnClickListener(view -> setScreen(R.id.unconnected));
        mode.setChecked(inSensorMode);
        mode.setOnCheckedChangeListener((compoundButton, b) -> inSensorMode=b);
        connectButton.setOnClickListener(view -> connect());
        acc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length()>0){
                    try{
                        accLimit=Float.parseFloat(editable.toString());
                    }catch (Exception e)
                    {
                        Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        brek.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length()>0){
                    try{
                        breakLimit=Float.parseFloat(editable.toString());
                    }catch (Exception e)
                    {
                        Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });



        side.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length()>0){
                    try{
                        sideLimit=Float.parseFloat(editable.toString());
                    }catch (Exception e)
                    {
                        Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        
        sideSensor.setChecked(inSideSensorMode);
        sideSensor.setOnCheckedChangeListener((compoundButton, b) -> inSideSensorMode=b);
        upSensor.setChecked(inUpSensorMode);
        upSensor.setOnCheckedChangeListener((compoundButton, b) -> inUpSensorMode=b);
        btnRight.setOnTouchListener(this);
        btnLeft.setOnTouchListener(this);
        space.setOnTouchListener(this);
        enter.setOnTouchListener(this);
        btnDown.setOnTouchListener(this);
        btnUp.setOnTouchListener(this);


    }

    @Override
    public void onBackPressed() {
        if(findViewById(R.id.unconnected).getVisibility()==View.VISIBLE)
        super.onBackPressed();
        else setScreen(R.id.unconnected);
    }

    ProgressDialog progressDialog;

    void connect()
    {
        String mip = ip.getText().toString();
        if(mip.length()==0)
        {
            Toast.makeText(this, "Enter ip first", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Connecting...");
        progressDialog.show();
        c.connect(mip, new Client.OnConnectionStateChangeListener() {
            @Override
            public void onConnectionSuccess() {
                progressDialog.hide();
                setScreen(R.id.sensor);
            }

            @Override
            public void onConnectionBreak() {
                progressDialog.hide();
                setScreen(R.id.unconnected);
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(getActionBar()!=null)
            getActionBar().hide();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        c=new Client();
        loadItems();
        assignListeners();
        setScreen(R.id.unconnected);
        assignKeys();



    }

    void assignKeys()
    {   keys=new HashMap<>();
        keys.put(R.id.btn_left,37);
        keys.put(R.id.btn_right,39);
        keys.put(R.id.btn_space,32);
        keys.put(R.id.btn_down,40);
        keys.put(R.id.btn_up,38);
        keys.put(R.id.btn_enter,10);
    }


    int preSide=0;
    int preAcc=0;
    @Override
    public void onSensorChanged(SensorEvent event) {
        float y = event.values[0];
        float x = event.values[1];

        int side=0;
        if(x>sideLimit)
        {
            side=39;
        }else if(x<-sideLimit)side=37;
        //left 37
        //right 39
        if(inSideSensorMode&&preSide!=side)
        {
          if(preSide!=0)
          {
              c.send(""+-preSide);
              preSide=0;
          }if(side!=0)
            {
            c.send(""+side);
            preSide=side;
            }
        }
        if(!inUpSensorMode)
            return;

        //38 up
        //down 40
        int up=0;
        if(y>=accLimit)
        {
            up=38;
        }else if(y<breakLimit)up=40;

        if(preAcc != up)
        {
            if(preAcc!=0)
            {
                c.send(""+-preAcc);
                preAcc=0;
            }if(up!=0)
        {
            c.send(""+up);
            preAcc=up;
        }
        }




    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(!keys.containsKey(view.getId()))
            return false;
        int i=view.getId();
        if((i==R.id.btn_left||i==R.id.btn_right)&&inSideSensorMode)
            return false;
        if((i==R.id.btn_up||i==R.id.btn_down)&&inUpSensorMode)
            return false;
        if(motionEvent.getAction()==MotionEvent.ACTION_DOWN)
        {
            c.send(""+keys.get(i));
            return true;
        }else if(motionEvent.getAction()==MotionEvent.ACTION_UP){
            c.send(""+-keys.get(i));
            return true;
        } return false;
    }
}