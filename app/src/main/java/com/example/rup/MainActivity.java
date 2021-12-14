package com.example.rup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.allyants.notifyme.NotifyMe;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    NotifyMe.Builder rupNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rupNotifications = new NotifyMe.Builder(getApplicationContext());
        setNotificationsContent("aaa","bbb");
        setRupNotificationsTime(21,19);
        notificationsBuild();
    }

    public void setRupNotificationsTime(int hours, int minutes){
        Calendar date = Calendar.getInstance();
//        int day = date.get(Calendar.DAY_OF_MONTH);
//        int month = date.get(Calendar.MONTH);
//        int year = date.get(Calendar.YEAR);
//
//        date.set(Calendar.YEAR,year);
//        date.set(Calendar.MONTH, month);
//        date.set(Calendar.DAY_OF_MONTH, day);
        date.set(Calendar.HOUR_OF_DAY,hours);
        date.set(Calendar.MINUTE, minutes);
        date.set(Calendar.SECOND,0);
        rupNotifications.time(date);
        rupNotifications.rrule("FREQ=DAILY;INTERVAL=1");
    }

    public void setNotificationsContent(String notificationTitle, String notificationText){
        rupNotifications.title(notificationTitle);
        rupNotifications.content(notificationText);
        rupNotifications.small_icon(R.drawable.ic_cloud_rup);
    }

    public void notificationsBuild(){
        rupNotifications.build();
    }
}