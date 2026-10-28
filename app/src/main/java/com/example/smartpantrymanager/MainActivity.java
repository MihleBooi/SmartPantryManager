package com.example.smartpantrymanager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PeriodicWorkRequest expiryWorkRequest =
                new PeriodicWorkRequest.Builder(
                        ExpiryNotificationWorker.class,
                        24,
                        TimeUnit.HOURS
                ).build();

        WorkManager.getInstance(getApplicationContext())
                .enqueueUniquePeriodicWork(
                        "expiry_notification_work",
                        ExistingPeriodicWorkPolicy.KEEP,
                        expiryWorkRequest
                );

        OneTimeWorkRequest testExpiryWork =
                new OneTimeWorkRequest.Builder(
                        ExpiryNotificationWorker.class
                ).build();

        WorkManager.getInstance(getApplicationContext())
                .enqueue(testExpiryWork);

        if (Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        100
                );
            }
        }

        Button openPantryButton = findViewById(R.id.btnOpenPantry);

        openPantryButton.setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    PantryActivity.class
            );
            startActivity(intent);
        });
    }
}
