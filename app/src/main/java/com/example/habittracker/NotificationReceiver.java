package com.example.habittracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String habitName = intent.getStringExtra("habitName");
        String userName = intent.getStringExtra("userName");

        // Configuración de la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "habitReminder")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Recordatorio de Hábito")
                .setContentText(userName + ", es hora de realizar tu hábito: " + habitName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Crear un PendingIntent que abrirá la MainActivity
        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        // Enviar la notificación
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1001, builder.build());
    }
}
