package com.andjelkadzida.chatsome.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.andjelkadzida.chatsome.MessageActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;


public class FirebaseMessaging extends FirebaseMessagingService
{

    private static final String TAG = "FcmListenerService";

    @Override
    public void onNewToken(@NonNull @NotNull String s)
    {
        Log.e("NEW_TOKEN",s);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser!=null)
        {
            updateToken(s);
        }
    }

    private void updateToken(String refreshToken)
    {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(refreshToken);
        if(firebaseUser!=null)
        {
            databaseReference.child(firebaseUser.getUid()).setValue(token);
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage)
    {
        super.onMessageReceived(remoteMessage);

        Log.e(TAG, "From: " + remoteMessage.getFrom());

        String sent = remoteMessage.getData().get("sent");
        String user = remoteMessage.getData().get("user");

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser()!=null)
        {
            FirebaseUser firebaseUser = auth.getCurrentUser();

            if (firebaseUser != null && sent.equals(auth.getCurrentUser().getUid()))
            {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    {
                        sendOreoNotification(remoteMessage);
                    } else
                    {
                        sendNotification(remoteMessage);
                    }
            }
        }
    }

    private void sendOreoNotification(RemoteMessage remoteMessage)
    {
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String user = remoteMessage.getData().get("user");


        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int i = Integer.parseInt(user.replaceAll("[\\D]", ""));

        Intent intent = new Intent(this, MessageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userid", user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoNotification oreoNotification = new OreoNotification(this);
        Notification.Builder builder = oreoNotification.getOreoNotification(title, body, pendingIntent, sound, icon);

        int l = 0;

        if(i>0)
        {
            l = i;
        }

        oreoNotification.getNotificationManager().notify(l, builder.build());
    }

    private void sendNotification(RemoteMessage remoteMessage)
    {
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String user = remoteMessage.getData().get("user");


        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int i = Integer.parseInt(user.replaceAll("[\\D]", ""));

        Intent intent = new Intent(this, MessageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userid", user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(sound)
                .setContentIntent(pendingIntent);

        NotificationManager not = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int l = 0;

        if(i>0)
        {
            l = i;
        }

        not.notify(l, builder.build());

    }
}
