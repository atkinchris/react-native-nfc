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
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class NfcModule extends ReactContextBaseJavaModule implements ActivityEventListener, LifecycleEventListener {
    private static final String E_INVALID_TAG_INTENT = "E_INVALID_TAG_INTENT";
    private ReactApplicationContext reactContext;

    private Promise mPickerPromise;
    private NfcAdapter mNfcAdapter;

    public NfcModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;

        reactContext.addActivityEventListener(this);
        reactContext.addLifecycleEventListener(this);
    }

    @Override
    public String getName() {
        return "Nfc";
    }

    @ReactMethod
    public void getUID(Promise promise) {
        mPickerPromise = promise;
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

        if (mPickerPromise != null) {
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            if (tagFromIntent != null) {
                mPickerPromise.resolve(tagFromIntent.getId());
            } else {
                mPickerPromise.reject(E_INVALID_TAG_INTENT, "Could not read tag from intent");
            }
        }
    }
}
