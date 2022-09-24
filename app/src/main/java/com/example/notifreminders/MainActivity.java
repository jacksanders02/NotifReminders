package com.example.notifreminders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    private final String CHANNEL_ID = "NotifReminderChannel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();
        setContentView(R.layout.activity_main);
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
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        b.setContentText(removeUnderline(tTitle.getText()));

        if (!tContent.getText().toString().equals("")) {
            b.setContentText(removeUnderline(tTitle.getText()))
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(removeUnderline(tContent.getText())));

            if (tTitle.getText().toString().equals("")) {
                b.setContentText(removeUnderline(tContent.getText()));
            }
        }

        NotificationManagerCompat nm = NotificationManagerCompat.from(this);

        nm.notify(1, b.build());
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