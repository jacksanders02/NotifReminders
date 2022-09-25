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
    private File REMINDER_STORAGE;
    private final HashMap<Integer, String[]> reminderKeys = new HashMap<>();
    private final HashSet<Integer> takenNotifIDs = new HashSet<>();
    private int currentNotifID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        createNotificationChannel();
        setContentView(R.layout.activity_main);
        REMINDER_STORAGE = new File(this.getFilesDir(), "reminders.txt");

        try {
            FileInputStream fis = this.openFileInput("reminders.txt");
            InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(isr);
            String line;
            currentNotifID = 0;
            while ((line = reader.readLine()) != null) {
                addNotifToHash(line);
            }

            reader.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    protected void updateCurrentID() {
        currentNotifID = 0;
        while (takenNotifIDs.contains(currentNotifID)) {
            currentNotifID++;
        }
    }

    protected void addNotifToHash(String s) {
        String[] toHash = s.split(" : ");
        int newID = Integer.parseInt(toHash[0]);
        reminderKeys.put(newID, Arrays.copyOfRange(toHash, 1, 3));
        takenNotifIDs.add(newID);
        updateCurrentID();
    }

    protected Editable removeUnderline(Editable t) {
        for (UnderlineSpan s : t.getSpans(0, t.length(), UnderlineSpan.class)) {
            t.removeSpan(s);
        }
        return t;
    }

    public void pushNotification(View v) {
        TextInputEditText tTitle = findViewById(R.id.notification_title_input);
        TextInputEditText tContent = findViewById(R.id.notification_input);
        NotificationCompat.Builder b = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_reminder_notification)
                .setContentTitle("Reminder!")
                .setPriority(NotificationCompat.PRIORITY_MAX);

        Editable title = removeUnderline(tTitle.getText());
        Editable content = removeUnderline(tContent.getText());

        b.setContentText(removeUnderline(tTitle.getText()));

        if (!tContent.getText().toString().equals("")) {
            b.setContentText(title)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(content));

            if (tTitle.getText().toString().equals("")) {
                b.setContentText(content);
            }
        }

        NotificationManagerCompat nm = NotificationManagerCompat.from(this);

        nm.notify(currentNotifID, b.build());

        String describer = currentNotifID + " : " + String.valueOf(title) + " : " + String.valueOf(content);

        try {
            FileWriter fw = new FileWriter(REMINDER_STORAGE, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(describer + "\n");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.println(currentNotifID);

        addNotifToHash(describer);
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