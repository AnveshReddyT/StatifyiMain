package statifyi.com.statifyi.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

import java.util.Date;

import statifyi.com.statifyi.utils.DataUtils;
import statifyi.com.statifyi.utils.NetworkUtils;

/**
 * Created by KT on 30/04/16.
 */
public class MqttService extends Service implements MqttCallback {

    public static final String BROKER_URL = "tcp://broker.mqttdashboard.com:1883";
    public static final String PUBLISH_EXTRA_TOPIC = "PUBLISH_EXTRA_TOPIC";
    public static final String PUBLISH_EXTRA_PAYLOAD = "PUBLISH_EXTRA_PAYLOAD";
    public static final String MQTT_PUBLISH_ACTION = "broadcast.mqtt.publish";
    private static final int KEEP_ALIVE = 60 * 2;
    private static String TAG = "MqttService";
    public String clientId;
    public String TOPIC;
    public MqttClient mqttClient;
    public String networkType;
    public MqttConnectOptions options;
    private ConnectivityManager mConnManager;
    private BroadcastReceiver publishToServerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String topic = intent.getStringExtra(PUBLISH_EXTRA_TOPIC);
            String payload = intent.getStringExtra(PUBLISH_EXTRA_PAYLOAD);
            try {
                doPublish(payload, topic);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    };
    private BroadcastReceiver connectionChangeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            networkType = NetworkUtils.getNetworkClass(context);

            boolean hasConnectivity = networkType != null;
            Log.v(TAG, "hasConn: " + hasConnectivity + " client status - " + (mqttClient == null || mqttClient.isConnected()));
            if (hasConnectivity && (mqttClient == null || !mqttClient.isConnected())) {
                doConnect();
            } else if (!hasConnectivity && mqttClient != null && mqttClient.isConnected()) {
                doDisconnect();
            }
        }
    };

    public static void publishToServer(Context mContext, JSONObject json, String topic) throws MqttException {
        Intent publishIntent = new Intent(MQTT_PUBLISH_ACTION);
        publishIntent.putExtra(PUBLISH_EXTRA_TOPIC, topic);
        publishIntent.putExtra(PUBLISH_EXTRA_PAYLOAD, json.toString());
        mContext.sendBroadcast(publishIntent);
    }

    @Override
    public void onCreate() {
        registerConnectionChangeReceiver();
        registerPublishReceiver();
        mConnManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        clientId = DataUtils.getMobileNumber(this);
        TOPIC = "statifyi/LWT/" + clientId;
        options = new MqttConnectOptions();
        options.setCleanSession(false);
        options.setKeepAliveInterval(KEEP_ALIVE);
    }

    private void registerConnectionChangeReceiver() {
        registerReceiver(connectionChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void registerPublishReceiver() {
        registerReceiver(publishToServerReceiver, new IntentFilter(MQTT_PUBLISH_ACTION));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged()");
        super.onConfigurationChanged(newConfig);

    }

    private void doConnect() {
        try {
            if (mqttClient == null) {
                mqttClient = new MqttClient(BROKER_URL, clientId, new MemoryPersistence());
                options.setWill(mqttClient.getTopic("statifyi/LWT/" + clientId), "I'm gone".getBytes(), 2, true);
            }
            mqttClient.connect(options);
            doSubscribe();
            doPublish(clientId + " | " + networkType + " | " + new Date(), "statifyi/LWT/" + clientId);
        } catch (MqttSecurityException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            handleMqttException(e);
        }
    }

    private void doSubscribe() {
        try {
            mqttClient.setCallback(this);
            mqttClient.subscribe(TOPIC, 2);
        } catch (MqttSecurityException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            handleMqttException(e);
        }
    }

    private void doPublish(String payload, String topic) throws MqttException {
        if (!mqttClient.isConnected()) {
            doConnect();
        }
        final MqttMessage message = new MqttMessage(payload.getBytes());
        final byte[] b = message.getPayload();
        Log.e(TAG, "publishing... " + message);
        mqttClient.publish(topic, b, 2, false);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mqttClient == null || !mqttClient.isConnected()) {
            doConnect();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(connectionChangeReceiver);
        unregisterReceiver(publishToServerReceiver);
        doDisconnect();
    }

    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
//        final JSONObject jsonObject = new JSONObject(new String(mqttMessage.getPayload()));
        Log.e(TAG, "mqtt message arrived" + new String(mqttMessage.getPayload()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MQTTBinder();
    }

    private void doDisconnect() {
        try {
            Log.d(TAG, "doDisconnect()");
            if (mqttClient != null) {
                mqttClient.disconnect();
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void handleMqttException(MqttException e) {
        switch (e.getReasonCode()) {
            case MqttException.REASON_CODE_BROKER_UNAVAILABLE:
            case MqttException.REASON_CODE_CLIENT_TIMEOUT:
            case MqttException.REASON_CODE_CONNECTION_LOST:
            case MqttException.REASON_CODE_SERVER_CONNECT_ERROR:
                Log.v(TAG, "c" + e.getMessage());
                e.printStackTrace();
                break;
            case MqttException.REASON_CODE_FAILED_AUTHENTICATION:
                Intent i = new Intent("RAISEALLARM");
                i.putExtra("ALLARM", e);
                Log.e(TAG, "b" + e.getMessage());
                e.printStackTrace();
                break;
            default:
                Log.e(TAG, "a" + e.getMessage());
                e.printStackTrace();
        }
    }

    public class MQTTBinder extends Binder {
        public MqttService getService() {
            return MqttService.this;
        }
    }
}
