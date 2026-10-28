
package com.example.smartpantrymanager;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ExpiryNotificationWorker extends Worker {

    public ExpiryNotificationWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        android.util.Log.d("ExpiryTest", "Expiry notification worker started!");
        android.util.Log.d("ExpiryTest", "CHECKPOINT 1: Worker reached start");

        Context context = getApplicationContext();

        if (Build.VERSION.SDK_INT >= 33
                && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return Result.success();
        }

        createNotificationChannel(context);

        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        Cursor cursor = databaseHelper.getAllIngredients();

        try {
            while (cursor.moveToNext()) {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COL_ID));

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COL_NAME));

                String expiryDate = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COL_EXPIRY));
                android.util.Log.d("ExpiryTest",
                        "Ingredient: " + name + ", expiry: " + expiryDate);

                if (expiryDate == null || expiryDate.trim().isEmpty()) {
                    continue;
                }

                try {
                    SimpleDateFormat formatter =
                            new SimpleDateFormat(
                                    "yyyy-MM-dd", Locale.getDefault());

                    formatter.setLenient(false);

                    Date expiry = formatter.parse(expiryDate.trim());

                    if (expiry == null) {
                        continue;
                    }

                    Calendar expiryDay = Calendar.getInstance();
                    expiryDay.setTime(expiry);
                    setToStartOfDay(expiryDay);

                    Calendar today = Calendar.getInstance();
                    setToStartOfDay(today);

                    long difference =
                            expiryDay.getTimeInMillis()
                                    - today.getTimeInMillis();

                    long daysLeft =
                            difference / (24 * 60 * 60 * 1000L);
                    android.util.Log.d("ExpiryTest",
                            "Expiry date: " + expiryDate
                                    + ", daysLeft calculated: " + daysLeft);

                    if (daysLeft >= 0 && daysLeft <= 3) {
                        android.util.Log.d("ExpiryTest",
                                "Cabbage qualifies for notification. Days left: " + daysLeft);

                        String message;

                        if (daysLeft == 0) {
                            message = name + " expires today!";
                        } else {
                            message = name + " expires in "
                                    + daysLeft + " day(s).";
                        }

                        NotificationCompat.Builder builder =
                                new NotificationCompat.Builder(
                                        context, "expiry_channel")
                                        .setSmallIcon(
                                                android.R.drawable.ic_dialog_info)
                                        .setContentTitle(
                                                "Pantry Expiry Reminder")
                                        .setContentText(message)
                                        .setStyle(
                                                new NotificationCompat.BigTextStyle()
                                                        .bigText(message))
                                        .setPriority(
                                                NotificationCompat.PRIORITY_DEFAULT)
                                        .setAutoCancel(true);

                        NotificationManagerCompat
                                .from(context)
                                .notify(id, builder.build());
                    }

                } catch (java.text.ParseException e) {
                    // Skip expiry dates that use an invalid format.
                }
            }
        } finally {
            cursor.close();
            databaseHelper.close();
        }

        return Result.success();
    }

    private void createNotificationChannel(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(
                    "expiry_channel",
                    "Pantry Expiry Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT);

            channel.setDescription(
                    "Reminders for ingredients nearing their expiry date");

            NotificationManager manager =
                    context.getSystemService(NotificationManager.class);

            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void setToStartOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
}
