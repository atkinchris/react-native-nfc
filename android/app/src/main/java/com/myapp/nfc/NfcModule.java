package com.myapp.nfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Vibrator;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.HashMap;
import java.util.Map;

public class NfcModule extends ReactContextBaseJavaModule implements ActivityEventListener, LifecycleEventListener {
    private static final String ON_SUCCESS = "ON_SUCCESS";
    private static final String ON_FAILURE = "ON_FAILURE";
    private ReactApplicationContext reactContext;

    private NfcAdapter mNfcAdapter;

    public NfcModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;

        reactContext.addActivityEventListener(this);
        reactContext.addLifecycleEventListener(this);
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put(ON_SUCCESS, ON_SUCCESS);
        constants.put(ON_FAILURE, ON_FAILURE);
        return constants;
    }

    @Override
    public String getName() {
        return "Nfc";
    }

    private static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);
        adapter.enableForegroundDispatch(activity, pendingIntent, null, null);
    }

    private static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    private void emitEvent(String eventName, Object data) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, data);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }

    @Override
    public void onHostResume() {
        if (mNfcAdapter != null) {
            setupForegroundDispatch(getCurrentActivity(), mNfcAdapter);
        } else {
            mNfcAdapter = NfcAdapter.getDefaultAdapter(this.reactContext);
        }
    }

    @Override
    public void onHostPause() {
        if (mNfcAdapter != null) {
            stopForegroundDispatch(getCurrentActivity(), mNfcAdapter);
        }
    }

    @Override
    public void onHostDestroy() {

    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onNewIntent(Intent intent) {
        Vibrator vib = (Vibrator) getCurrentActivity().getSystemService(Context.VIBRATOR_SERVICE);
        long[] duration = { 50, 100, 200, 300 };
        vib.vibrate(duration, -1);

        try {
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            byte[] id = tagFromIntent.getId();

            emitEvent(ON_SUCCESS, bytesToHex(id));
        } catch (Exception e) {
            emitEvent(ON_FAILURE, e.toString());
        }
    }
}
