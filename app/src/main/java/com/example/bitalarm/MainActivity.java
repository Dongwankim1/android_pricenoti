package com.example.bitalarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


import com.orhanobut.logger.Logger;


import org.java_websocket.client.WebSocketClient;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private WebSocketClient socket;
    private NotificationManagerCompat notificationManager;

    private EditText et_price;
    private Spinner spinner;
    private Button btnWatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = findViewById(R.id.main_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.res, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        et_price = findViewById(R.id.main_price);

        btnWatch  = findViewById(R.id.btnWatch);

        Boolean a = isMyServiceRunning(BianaceService.class);

        if(a){
            btnWatch.setText("중지");
            btnWatch.setBackgroundColor(Color.RED);
        }
        Logger.d("asdasdqwdqwd",a);

        btnWatch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(btnWatch.getText().equals("시작")){
                    MainActivity.this.startService(v);
                    btnWatch.setText("중지");
                    btnWatch.setBackgroundColor(Color.RED);
                }else{
                    MainActivity.this.stopService(v);
                    btnWatch.setText("시작");
                    btnWatch.setBackgroundColor(Color.BLUE);
                }

            }
        });





    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void startService(View v){
        String price =  et_price.getText().toString();
        String selectItem = (String)spinner.getSelectedItem();
        Intent serviceIntent = new Intent(this,BianaceService.class);
        serviceIntent.putExtra("priceLimit",price);
        serviceIntent.putExtra("selectItem",selectItem);
        //startService(serviceIntent);
        ContextCompat.startForegroundService(this,serviceIntent);
    }

    public void stopService(View v){
        Intent serviceIntent = new Intent(this,BianaceService.class);
        stopService(serviceIntent);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {




    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

