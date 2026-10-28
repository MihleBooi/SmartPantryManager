
package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchExpiryNotifications;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchExpiryNotifications =
                findViewById(R.id.switchExpiryNotifications);

        preferences = getSharedPreferences(
                "PantrySettings", MODE_PRIVATE);

        boolean enabled = preferences.getBoolean(
                "expiry_notifications", true);

        switchExpiryNotifications.setChecked(enabled);

        switchExpiryNotifications.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    preferences.edit()
                            .putBoolean("expiry_notifications", isChecked)
                            .apply();

                    WorkManager workManager =
                            WorkManager.getInstance(getApplicationContext());

                    if (isChecked) {
                        PeriodicWorkRequest expiryWorkRequest =
                                new PeriodicWorkRequest.Builder(
                                        ExpiryNotificationWorker.class,
                                        24,
                                        TimeUnit.HOURS
                                ).build();

                        workManager.enqueueUniquePeriodicWork(
                                "expiry_notification_work",
                                ExistingPeriodicWorkPolicy.KEEP,
                                expiryWorkRequest
                        );

                        Toast.makeText(
                                SettingsActivity.this,
                                "Expiry notifications enabled",
                                Toast.LENGTH_SHORT
                        ).show();

                    } else {
                        workManager.cancelUniqueWork(
                                "expiry_notification_work");

                        Toast.makeText(
                                SettingsActivity.this,
                                "Expiry notifications disabled",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }
}
