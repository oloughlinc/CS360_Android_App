package edu.snhu.craigo_eventapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EventNotifierIntentService extends IntentService {

    EventTracker mEventTracker;
    Calendar calendar;


    private final String CHANNEL_ID_EVENT = "channel_event";

    public EventNotifierIntentService() {
        super("EventNotifierIntentService");
    }

    protected void onHandleIntent(Intent intent) {

        mEventTracker = EventTracker.getInstance(this);
        calendar = calendar.getInstance();


        createNotificationChannel();

        List<Event> notifiedEvents = new ArrayList<Event>();
        List<Event> eventList = mEventTracker.getEvents(EventOrder.byDate);

        while (true) { // could be while notifiedevents < eventlist sizes

            try {

                int notificationId = 0;

                for (Event event : eventList) {

                    // case: already notified this one
                    if (notifiedEvents.contains(event)) continue;

                    if (event.getDate().getTime() <= calendar.getTimeInMillis()) {

                        // send notification
                        sendNotification("Time for event: " + event.getTitle() + "!",
                                notificationId++);

                        // user has been notified
                        notifiedEvents.add(event);

                        // prevent sending more than one notification per second
                        Thread.sleep(1500);
                    }
                }

                Thread.sleep(3600000); // 1 hour

            } catch (InterruptedException e) {
                // on close service
                // return??
            }
        }
    }

    private void createNotificationChannel() {

        CharSequence name = getString(R.string.channel_name);
        String desc = getString(R.string.channel_desc);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID_EVENT, name, importance);
        channel.setDescription(desc);
        channel.setImportance(importance);

        // register with system
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private void sendNotification(String text, int notificationId) {

        // notification builder
        Notification notification = new Notification.Builder(this, CHANNEL_ID_EVENT)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(text)
                .build();

        // send
        // if notification id is the same, message replaces last
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, notification);

    }
}
