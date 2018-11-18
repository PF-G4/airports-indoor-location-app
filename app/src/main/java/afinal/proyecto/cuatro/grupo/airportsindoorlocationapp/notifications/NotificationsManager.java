package afinal.proyecto.cuatro.grupo.airportsindoorlocationapp.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.estimote.proximity_sdk.api.ProximityObserver;
import com.estimote.proximity_sdk.api.ProximityObserverBuilder;
import com.estimote.proximity_sdk.api.ProximityZone;
import com.estimote.proximity_sdk.api.ProximityZoneBuilder;
import com.estimote.proximity_sdk.api.ProximityZoneContext;

import org.json.JSONArray;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import afinal.proyecto.cuatro.grupo.airportsindoorlocationapp.MainActivity;
import afinal.proyecto.cuatro.grupo.airportsindoorlocationapp.R;
import afinal.proyecto.cuatro.grupo.airportsindoorlocationapp.application.MyApplication;
import afinal.proyecto.cuatro.grupo.airportsindoorlocationapp.newmap.ContentZone;
import afinal.proyecto.cuatro.grupo.airportsindoorlocationapp.newmap.ImageAdapter;
import afinal.proyecto.cuatro.grupo.airportsindoorlocationapp.util.Validaciones;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class NotificationsManager {

    public static Map<String, Boolean> mapNotificationStatusByTag = new HashMap<>();
    public static Map<String, Calendar> mapLastPushByTag = new HashMap<>();

    public NotificationsManager() {
        Log.i("*** NotificationManager", "NotificationManager instance created");
    }

    /* Make it SingletonObject */
    private static volatile NotificationsManager instance;

    public static NotificationsManager getInstance() {
        if (instance == null ) {
            instance = new NotificationsManager();
        }

        return instance;
    }

    /* Beacon recognition distance  */
    private static final Double DISTANCE = 3.0;


    /* General notification */
    private Context notificationContext;
    private NotificationManager notificationManager;

    public void NotificationsManager(Context notificationContext) {
        this.notificationContext = notificationContext;
        this.notificationManager = (NotificationManager) notificationContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /* NewMap Notification */
    private ImageAdapter imageAdapter;
    private Context newMapContext;

    public void NewMapManager(Context newMapContext, ImageAdapter imageAdapter) {
        this.newMapContext = newMapContext;
        this.imageAdapter = imageAdapter;
    }

    /* NewMap Destination Found */
    public void NewDestinationFound(ImageAdapter imageAdapter, JSONArray jsonObject) {

        Log.i("*** NotificationManager","NewDestinationFound - jsonObject: "+jsonObject);

        if (imageAdapter != null) {
            // imageAdapter.adjustMapWithDestination(position,jsonObject);
            // imageAdapter.notifyDataSetChanged();
        }
    }

    /* Build notification */
    private Notification buildNotification(String title, String text, String link) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel contentChannel = new NotificationChannel(
                    "content_channel",
                    "Things near you",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(contentChannel);
        }

        return new NotificationCompat.Builder(notificationContext, "content_channel")
            .setSmallIcon(R.drawable.beacon_candy_small)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(
                    getContentIntentByLink(link)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build();
    }

    private PendingIntent getContentIntentByLink(String link) {

        /** dejo solo 4 beacons asociados a una uri. Creo que no es necesario que todos los beacons
         * lancen notificaciones */
        Intent notificationIntent;
        if (!Validaciones.isNullOrEmpty(link)) {
            notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        } else {
            notificationIntent = new Intent(notificationContext, MainActivity.class);
        }

        return PendingIntent.getActivity(
                notificationContext,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /* Beacon observer main process */
    public void startMonitoring() {
        ProximityObserver notificationManagerObserver =
                new ProximityObserverBuilder(
                        notificationContext,
                        ((MyApplication) notificationContext).cloudCredentials)
                        .onError(new Function1<Throwable, Unit>() {
                            @Override
                            public Unit invoke(Throwable throwable) {
                                Toast.makeText(notificationContext.getApplicationContext(),
                                        "Proximity observer error: " + throwable,
                                        Toast.LENGTH_LONG).show();
                                Log.e(
                                        "*** NotificationManager",
                                        "Proximity observer error: " + throwable);
                                return null;
                            }
                        })
                        .withBalancedPowerMode()
                        .withEstimoteSecureMonitoringDisabled()
                        .withTelemetryReportingDisabled()
                        .build();

        ProximityZone newMapZoneCandy2 = createNewMapZoneFromBeacon("candy-2");
        ProximityZone newMapZoneLemon2 = createNewMapZoneFromBeacon("lemon-2");
        ProximityZone newMapZoneBeetroot2 = createNewMapZoneFromBeacon("beetroot-2");
        ProximityZone newMapZoneCoconut2 = createNewMapZoneFromBeacon("coconut-2");
        ProximityZone newMapZoneCandy1 = createNewMapZoneFromBeacon("candy-1");
        ProximityZone newMapZoneLemon1 = createNewMapZoneFromBeacon("lemon-1");
        ProximityZone newMapZoneBeetroot1 = createNewMapZoneFromBeacon("beetroot-1");
        ProximityZone newMapZoneCoconut1 = createNewMapZoneFromBeacon("coconut-1");
        ProximityZone liveNotificationZoneLemon1 = createLiveNotificationZoneFromBeacon("notification-lemon-1");
        ProximityZone liveNotificationZoneLemon2 = createLiveNotificationZoneFromBeacon("notification-lemon-2");
        ProximityZone liveNotificationZoneCandy1 = createLiveNotificationZoneFromBeacon("notification-candy-1");
        ProximityZone liveNotificationZoneCandy2 = createLiveNotificationZoneFromBeacon("notification-candy-2");
        ProximityZone liveNotificationZoneBeetroot1 = createLiveNotificationZoneFromBeacon("notification-lemon-1");
        ProximityZone liveNotificationZoneBeetroot2 = createLiveNotificationZoneFromBeacon("notification-lemon-2");
        ProximityZone liveNotificationZoneCoconut1 = createLiveNotificationZoneFromBeacon("notification-candy-1");
        ProximityZone liveNotificationZoneCoconut2 = createLiveNotificationZoneFromBeacon("notification-candy-2");

        notificationManagerObserver.startObserving(
                Arrays.asList(
                        newMapZoneCandy1,
                        newMapZoneLemon1,
                        newMapZoneBeetroot1,
                        newMapZoneCoconut1,
                        newMapZoneCandy2,
                        newMapZoneLemon2,
                        newMapZoneBeetroot2,
                        newMapZoneCoconut2,
                        liveNotificationZoneLemon1,
                        liveNotificationZoneLemon2,
                        liveNotificationZoneCandy1,
                        liveNotificationZoneCandy2,
                        liveNotificationZoneBeetroot1,
                        liveNotificationZoneBeetroot2,
                        liveNotificationZoneCoconut1,
                        liveNotificationZoneCoconut2));

        mapNotificationStatusByTag.put("notification-lemon-1", true); //FIXME(Jonas) si hay tiempo, guardar configuracion de notificaciones en base de datos.
        mapNotificationStatusByTag.put("notification-candy-1", true);
        mapNotificationStatusByTag.put("notification-candy-2", true);
        mapNotificationStatusByTag.put("notification-coconut-2", true);
    }

    /* create zone for map activity */
    private ProximityZone createNewMapZoneFromBeacon(final String tag){

        return new ProximityZoneBuilder()
                .forTag(tag)
                .inCustomRange(DISTANCE)
                .onEnter(new Function1<ProximityZoneContext, Unit>() {
                    @Override
                    public Unit invoke(ProximityZoneContext proximityContext) {

                        Log.i("*** NewMapZone","Beacon coming-in: "+tag);

                        ContentZone contentZone = new ContentZone(tag, true, proximityContext);

                        if (imageAdapter != null) {
                            imageAdapter.adjustMapWith(contentZone);
                            imageAdapter.notifyDataSetChanged();

                        }

                        return null;
                    }
                })
                .onExit(new Function1<ProximityZoneContext, Unit>() {
                    @Override
                    public Unit invoke(ProximityZoneContext proximityContext) {

                        Log.i("*** NewMapZone","Beacon coming-out: "+tag);

                        ContentZone contentZone = new ContentZone(tag, false, proximityContext);

                        if (imageAdapter != null) {
                            imageAdapter.adjustMapWith(contentZone);
                            imageAdapter.notifyDataSetChanged();
                        }

                        return null;
                    }
                })
                .build();
    }

    /* create zone for notification service */
    public ProximityZone createLiveNotificationZoneFromBeacon(final String tag){

        return new ProximityZoneBuilder()
            .forTag(tag)
            .inCustomRange(DISTANCE)
            .onEnter(new Function1<ProximityZoneContext, Unit>() {
                @Override
                public Unit invoke(ProximityZoneContext proximityContext) {

                    Log.i("*** LiveNotification","Beacon near to: " + tag);

                    NotificationZone notificationZone = new NotificationZone(tag, proximityContext);

                    Boolean enabled = mapNotificationStatusByTag.get(tag);
                    if (enabled) {
                        Calendar lastPush = mapLastPushByTag.get(tag);
                        long differenceMinutes = -1;
                        if (lastPush != null) {
                            differenceMinutes = differenceMinutes(lastPush, Calendar.getInstance());
                        }
                        if (differenceMinutes >= 5 || differenceMinutes == -1) { //si paso al menos 5 minutos desde el ultimo push notifications o si es el primer push, entonces se notifica
                            notificationManager.notify(notificationZone.getId(), buildNotification(
                                notificationZone.getTitle(),
                                notificationZone.getMessage(),
                                notificationZone.getLink()));
                            mapLastPushByTag.put(tag, Calendar.getInstance());
                            Log.i("*** LiveNotification","Notificated beacon: " + tag);
                        }
                    } else {
                        Log.i("*** LiveNotification","Notification disabled for beacon: " + tag);
                    }

                    return null;
                }
            })
            .build();
    }

    //FIXME codigo duplicado
    private long differenceMinutes(Calendar cal1, Calendar cal2) {
        return ((cal2.getTimeInMillis() - cal1.getTimeInMillis()) / 1000) / 60;
    }
}
