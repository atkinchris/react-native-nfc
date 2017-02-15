package com.myapp.nfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.Vibrator;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.HashMap;
import java.util.Map;

public class NfcModule extends ReactContextBaseJavaModule implements ActivityEventListener, LifecycleEventListener {
    private static final String TAG_NOT_PRESENT = "TAG_NOT_PRESENT";
    private static final String TAG_EXCEPTION = "TAG_EXCEPTION";

    private static final String ON_TAG_PRESENT = "ON_TAG_PRESENT";
    private static final String ON_FAILURE = "ON_FAILURE";

    private ReactApplicationContext reactContext;
    private NfcAdapter mNfcAdapter;
    private MifareUltralight tag;

    public NfcModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;

        reactContext.addActivityEventListener(this);
        reactContext.addLifecycleEventListener(this);
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put(ON_TAG_PRESENT, ON_TAG_PRESENT);
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
        if (mNfcAdapter == null) {
            mNfcAdapter = NfcAdapter.getDefaultAdapter(this.reactContext);
        }

        setupForegroundDispatch(getCurrentActivity(), mNfcAdapter);
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

            tag = MifareUltralight.get(tagFromIntent);

            byte[] id = tagFromIntent.getId();

            emitEvent(ON_TAG_PRESENT, bytesToHex(id));
        } catch (Exception e) {
            tag = null;
            emitEvent(ON_FAILURE, e.toString());
        }
    }

    @ReactMethod
    public void getPage(int offset, final Promise promise) {
        if (tag == null) {
            promise.reject(TAG_NOT_PRESENT, "Tag not present");
        }

        try {
            tag.connect();
            byte[] data = tag.readPages(offset);
            tag.close();

            promise.resolve(bytesToHex(data));
        } catch (Exception e) {
            promise.reject(TAG_EXCEPTION, e);
        }
    }
}
