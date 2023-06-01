package com.example.vdk1;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.vdk1.MainActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;

public class ChayNgamService extends Service {
    FirebaseDatabase database ;
    DatabaseReference rootRef;
    public final String channelId = "fuck";
    public  int notificationId ;
    public static int serviceId;
    public boolean first_time;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        database= FirebaseDatabase.getInstance();
        rootRef= database.getReference();
        serviceId=startId;
        first_time=true;
        DoBackgroundTask doBackgroundTask = new DoBackgroundTask();
        doBackgroundTask.doInBackground(3);
        String channel_idd = createNotificationChannel(ChayNgamService.this);
        RemoteViews remoteViews = new RemoteViews(ChayNgamService.this.getPackageName(), R.layout.custom_notif);
        Notification notification = new NotificationCompat.Builder(this, channel_idd)
                .setContentTitle("")
                .setContentText("")
                .setSmallIcon(R.drawable.door)
                .setContent(remoteViews)
                .build();

        startForeground(1, notification);
        return START_STICKY;
    }
    private String createNotificationChannel(Context context)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Smart Door";
            String channelDescription = "";
            int channelImportance = NotificationManager.IMPORTANCE_LOW;
            boolean channelEnableVibrate = true;
            String channelId1= "ThongBaoChayNgam";
            NotificationChannel notificationChannel = new NotificationChannel(channelId1, channelName, channelImportance);
            notificationChannel.setDescription(channelDescription);
            //notificationChannel.enableVibration(false);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
            return channelId1;
        } else {
            // Returns null for pre-O (26) devices.
            return null;
        }
    }
    private class DoBackgroundTask extends AsyncTask<Integer, Integer, Integer>  {

        public void onProgressUpdate(Integer... progress) {

        }
        public  String createNotificationChannel(Context context,String formatedtime) {
            // NotificationChannels are required for Notifications on O (API 26) and above.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence channelName = "Location tracking";
                String channelDescription = formatedtime;
                int channelImportance = NotificationManager.IMPORTANCE_HIGH;
                boolean channelEnableVibrate = true;
                NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, channelImportance);
                notificationChannel.setDescription(channelDescription);
                notificationChannel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, null);
                notificationChannel.enableVibration(channelEnableVibrate);
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                assert notificationManager != null;
                notificationManager.createNotificationChannel(notificationChannel);

                return channelId;
            } else {
                // Returns null for pre-O (26) devices.
                return null;
            }
        }
        public void notifyy(String time,String la,String lo) throws ParseException {
            //Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.bin);

            Intent intent = new Intent(ChayNgamService.this, MainActivity.class);
            intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(ChayNgamService.this, 0, intent,  PendingIntent.FLAG_MUTABLE);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat output = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date d = sdf.parse(time);
            String formattedTime = output.format(d);
            String channel_id = createNotificationChannel(ChayNgamService.this,formattedTime );
            Bitmap bitmapp =bitmapp = BitmapFactory.decodeResource(getResources(),R.drawable.door);


            NotificationCompat.Builder builder = new NotificationCompat.Builder(ChayNgamService.this, channel_id)
                    //.setLargeIcon(bitmap) // Set icon cho thông báo
                    .setContentTitle(formattedTime) // Set tiêu đề cho thông báo
                    .setContentText("Vĩ độ: " +la +", Kinh độ: "+lo ) // Set nội dung cho thông báo
                    .setPriority(NotificationCompat.PRIORITY_MAX) // Set mức độ ưu tiên của thông báo
                    .setSmallIcon(R.drawable.door)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL);

            builder.setLargeIcon(bitmapp);
            Notification notification = builder.build();

            NotificationManager notificationManager =(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(time.hashCode(), notification);
        }
        void sendMessage(String ketqua)
        {
            Intent intent = new Intent();
            intent.putExtra("ketqua",ketqua);

            intent.setAction("test.Broadcast");
            Log.d("test", "sendMessage : "+ ketqua);
            sendBroadcast(intent);
        }
        @Override
        public Integer doInBackground(Integer ... intergers) {
            Log.d("test", "doInBackground for ChayNgamService: ");


            rootRef.child("location").limitToLast(1).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    HashMap ketquaa = (HashMap) snapshot.getValue();
                    String time =  snapshot.getKey();
                    String cleanTime = time.replaceAll("\"", "");
                    Log.d("test", ketquaa.toString());
                    Log.d("test", time);
                    //long status = (long) ketquaa.get("status");

                    String la=ketquaa.get("latitude").toString();
                    String lo=ketquaa.get("longtitude").toString();







                    if(!first_time &&!Bin.isActivityActive)
                    {
                        try {
                            notifyy(cleanTime,la,lo);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Log.d("test","đã notiffy()");
                    }
                    else
                    {
                        first_time=false;
                    }
                    //sendMessage("ketqua",ketquaa);


                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            return 3;

        }
}}