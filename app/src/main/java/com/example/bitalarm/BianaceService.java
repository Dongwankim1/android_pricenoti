package com.example.bitalarm;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.orhanobut.logger.Logger;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class BianaceService extends Service {
    private WebSocketClient socket;
    private NotificationManagerCompat notificationManager;
    private int notiCount =0;
    private String priceLimit,selectItem;
    public BianaceService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        socket.close();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notificationManager = NotificationManagerCompat.from(this);
        priceLimit = intent.getStringExtra("priceLimit");
        selectItem = intent.getStringExtra("selectItem");

        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_2_ID).setSmallIcon(R.drawable.ic_one).setContentTitle("Foreground Start")
                .setContentText("Foregroud Start").setPriority(NotificationCompat.PRIORITY_HIGH).setCategory(NotificationCompat.CATEGORY_MESSAGE).build();
        notificationManager.notify(1, notification);

        notiCount++;

        startForeground(2,notification);
        URI uri;
        try{
            String websocketEndPointUrl = "wss://stream.binance.com:9443/ws/btcbusd@kline_15m";
            Logger.d("WSURL"+websocketEndPointUrl);

            uri = new URI(websocketEndPointUrl);

            socket = new WebSocketClient(uri, new Draft_6455()) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Logger.d(handshakedata);
                }

                @Override
                public synchronized void onMessage(String message) {

                    try{
                        JSONObject jsonObject = new JSONObject(message);
                        JSONObject jsonObject1 = new JSONObject(jsonObject.getString("k"));
                        String pricedata = jsonObject1.getString("c");
                        String pricesplit = pricedata.split("\\.")[0];

                        int limit = Integer.parseInt(priceLimit);
                        int currentPrice = Integer.parseInt(pricesplit);
                        switch (selectItem){
                            case "이상":
                                if(currentPrice>=limit){
                                    getNotiData(jsonObject1,intent,socket);
                                }
                                break;
                            case "이하":
                                if(currentPrice<=limit){
                                    getNotiData(jsonObject1,intent,socket);
                                }
                                break;

                            default:
                                break;
                        }


                    }catch(JSONException e){
                        e.printStackTrace();
                    }



                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Logger.d(reason);
                }

                @Override
                public void onError(Exception ex) {
                    Logger.d(ex);
                }
            };

            socket.connect();

        }catch(URISyntaxException e){
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }
    public void getNotiData(JSONObject jsonObject1,Intent intent,WebSocketClient socket){
        try {
            if (notiCount >= 9) {
                this.stopSelf();
                socket.close();
            }
            String title = "현재 가격" + jsonObject1.getString("c");

            try{
                Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_1_ID).setSmallIcon(R.drawable.ic_one).setContentTitle(title)
                        .setContentText(title).setPriority(NotificationCompat.PRIORITY_HIGH).setCategory(NotificationCompat.CATEGORY_MESSAGE).build();
                notificationManager.notify(1, notification);

                notiCount++;

                startForeground(1,notification);
            }catch(Exception e){
                e.printStackTrace();
            }


        }catch (JSONException e){
            e.printStackTrace();

        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.


        return null;
    }
}