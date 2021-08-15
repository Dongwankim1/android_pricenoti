package com.example.bitalarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


import com.orhanobut.logger.Logger;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private WebSocketClient socket;
    private NotificationManagerCompat notificationManager;

    private EditText et_price;
    private Spinner spinner;
    private Button btnWatch;
    private String upDown;
    TextView text_select_label;
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
                //startService();
                /*
                String title = "aaaaaaa";
                Notification notification = new NotificationCompat.Builder(MainActivity.this, App.CHANNEL_1_ID).setSmallIcon(R.drawable.ic_one).setContentTitle(title)
                        .setContentText(title).setPriority(NotificationCompat.PRIORITY_HIGH).setCategory(NotificationCompat.CATEGORY_MESSAGE).build();
                notificationManager.notify(1,notification);

                 */
            }
        });

        //notificationManager = NotificationManagerCompat.from(this);




    }
    public void startService(View v){
        String price =  et_price.getText().toString();
        String selectItem = (String)spinner.getSelectedItem();
        Intent serviceIntent = new Intent(this,BianaceService.class);
        serviceIntent.putExtra("priceLimit",price);
        serviceIntent.putExtra("selectItem",selectItem);
        startService(serviceIntent);
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

