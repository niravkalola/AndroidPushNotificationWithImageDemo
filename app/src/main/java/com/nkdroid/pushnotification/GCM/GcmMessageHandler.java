package com.nkdroid.pushnotification.GCM;

/**
 * Created by nirav on 28-10-2014.
 */

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.nkdroid.pushnotification.MainActivity;
import com.nkdroid.pushnotification.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class GcmMessageHandler extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    Context context;
    Bitmap icon;
    PendingIntent contentIntent;

    public GcmMessageHandler() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        this.context = this;
        final Bundle extras = intent.getExtras();
        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle

            sendNotification(extras, this);

        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(final Bundle response, final Context context) {
        new generatePictureStyleNotification(this, response.getString("title"), response.getString("message"), response.getString("image_url")).execute();
    }


    public class generatePictureStyleNotification extends AsyncTask<String, Void, Bitmap> {

        private Context mContext;
        private String title, message, imageUrl;

        public generatePictureStyleNotification(Context context, String title, String message, String imageUrl) {
            super();
            this.mContext = context;
            this.title = title;
            this.message = message;
            this.imageUrl = imageUrl;

        }

        @Override
        protected Bitmap doInBackground(String... params) {

            InputStream in;
            try {
                URL url = new URL(this.imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);

            icon = BitmapFactory.decodeResource(getResources(),
                    R.mipmap.ic_launcher);

            mNotificationManager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);

            contentIntent = PendingIntent.getActivity(mContext, 0,
                    new Intent(mContext, MainActivity.class).putExtra("is_from_notification", true), 0);
            builder = new NotificationCompat.Builder(context)
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(icon)
                    .setContentTitle(title)
                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(result))
                    .setContentText(message
                    );

            builder.setContentIntent(contentIntent);
            mNotificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }
}