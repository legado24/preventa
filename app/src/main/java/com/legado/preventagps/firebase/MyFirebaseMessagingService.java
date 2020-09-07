package com.legado.preventagps.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.legado.preventagps.R;
import com.legado.preventagps.activities.vendedor.BonificacionItemActivity;
import com.legado.preventagps.activities.vendedor.BonificacionPaqueteActivity;
import com.legado.preventagps.activities.vendedor.NewIngresosAlmacenActivity;
import com.legado.preventagps.activities.vendedor.RepartoVentaActivity;
import com.legado.preventagps.util.SessionUsuario;


/**
 * Created by _Adrian_ on 26/7/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    private static int count = 0;

    SessionUsuario sessionUsuario;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("NEW_TOKEN",s);
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
       // startService(new Intent(getApplicationContext(), ServicioBackgroundNotification.class));
      //  super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        Log.d(TAG, "NDATA: " + remoteMessage.getData());
        //Calling method to generate notification
        mensaje(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody(),remoteMessage.getNotification().getClickAction());
          sendNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody(),remoteMessage.getNotification().getClickAction());
     }

    private void mensaje(String title,String body,String clickAction) {
        Intent intent=new Intent("MENSAJE");
        intent.putExtra("body",body);
        intent.putExtra("title",title);
        intent.putExtra("clickAction",clickAction);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }
    private static RemoteViews contentView;
    private static Notification notification;
    private static NotificationManager notificationManager;
    private static final int NotificationID = 1005;
    private static NotificationCompat.Builder mBuilder;

    private void sendNotification(String title, String messageBody,String clickAction) {
        System.out.println("OKOKOKOKOKO"+clickAction);

        Intent intent=null;
        if(clickAction.equals("INGRESOSCOMPRA")){
           intent = new Intent(this, NewIngresosAlmacenActivity.class);
        }else if(clickAction.equals("BONIFICACIONITEM")){
            intent = new Intent(this, BonificacionItemActivity.class);

        }else if(clickAction.equals("BONIFICACIONPAQUETE")){
            intent = new Intent(this, BonificacionPaqueteActivity.class);

        }else if(clickAction.equals("REPARTOVENTA")){
            intent = new Intent(this, RepartoVentaActivity.class);
//            intent.putExtra("nroPedido","PE05586238");
//            intent.putExtra("codEmpresa","01");

        }


//you can use your launcher Activity insted of SplashActivity, But if the Activity you used here is not launcher Activty than its not work when App is in background.
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//Add Any key-value to pass extras to intent
        intent.putExtra("pushnotification", "true");
        intent.putExtra("title",title);
        intent.putExtra("body",messageBody);

       PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//For Android Version Orio and greater than orio.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel("Sesame", "Sesame", importance);
            mChannel.setDescription(messageBody);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            mNotifyManager.createNotificationChannel(mChannel);
        }
//For Android Version lower than oreo.
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "Seasame");
        mBuilder.setContentTitle(title)
                .setContentText(messageBody)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                //.setColor(Color.parseColor("#FFD600"))
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
                .setContentIntent(pendingIntent)
                .setChannelId("Sesame")
                .setPriority(NotificationCompat.PRIORITY_HIGH );

        mNotifyManager.notify(count, mBuilder.build());
        count++;




/*

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(getApplicationContext(), "notify_001");

        contentView = new RemoteViews(getPackageName(), R.layout.layout_notificacion_rebote);

       // RemoteViews notificationLayoutExpanded = new RemoteViews(getPackageName(), R.layout.list_articulo);


        contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
       // Intent switchIntent = new Intent(this, BackgroundService.switchButtonListener.class);
       // PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, 1020, intent, 0);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        contentView.setOnClickPendingIntent(R.id.flashButton, pendingIntent);
        contentView.setTextViewText(R.id.title, title);
        contentView.setTextViewText(R.id.text, messageBody);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);

        //mBuilder .setStyle(new NotificationCompat.BigTextStyle().bigText("okkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk"));

        mBuilder.setAutoCancel(true);
        //mBuilder.setOngoing(true);
        mBuilder.setPriority(Notification.PRIORITY_HIGH);
       // mBuilder.setOnlyAlertOnce(true);
       // mBuilder.setStyle(new NotificationCompat.DecoratedCustomViewStyle());
        mBuilder.setCustomContentView(contentView);
        //mBuilder.setCustomBigContentView(notificationLayoutExpanded);

        mBuilder.build().flags = Notification.FLAG_NO_CLEAR | Notification.PRIORITY_HIGH;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "channel_id";
            NotificationChannel channel = new NotificationChannel(channelId, "channel name", NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        notification = mBuilder.build();
        notificationManager.notify(NotificationID, notification);*/
    }


}
