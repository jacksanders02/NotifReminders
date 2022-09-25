package com.example.notifreminders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final String CHANNEL_ID = "NotifReminderChannel";
    private int currentNotifID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); // Hide app title bar
        createNotificationChannel();
        setContentView(R.layout.activity_main);
        updateCurrentID();
    }

    protected void updateCurrentID() {
        // Set ID for the next notification to the uptime of the system (makes collisions highly unlikely)
        currentNotifID = (int) SystemClock.uptimeMillis();
    }

    protected Editable removeUnderline(Editable t) {
        for (UnderlineSpan s : t.getSpans(0, t.length(), UnderlineSpan.class)) {
            t.removeSpan(s);
        }
        return t;
    }

    public void pushNotification(View v) {
        // Grab reminder title/content from app inputs
        TextInputEditText tTitle = findViewById(R.id.notification_title_input);
        TextInputEditText tContent = findViewById(R.id.notification_input);

        // Create notification builder
        NotificationCompat.Builder b = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_reminder_notification)
                .setContentTitle("Reminder!")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setGroup(String.valueOf(currentNotifID)); // Group ID prevents bundling of reminders

        // Remove Gboard's underline from text
        Editable title = removeUnderline(tTitle.getText());
        Editable content = removeUnderline(tContent.getText());

        b.setContentText(title);

        if (!tContent.getText().toString().equals("")) {
            b.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(content));

            if (tTitle.getText().toString().equals("")) {
                b.setContentText(content);
            }
        }

        NotificationManagerCompat nm = NotificationManagerCompat.from(this);

        nm.notify(currentNotifID, b.build());
        updateCurrentID();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}