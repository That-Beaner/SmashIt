package tk.smashr.smashit;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class VibrationUtils {
    /**
     * Vibrate the device
     *
     * @param vibrationLength How many milliseconds to vibrate for
     */
    public static void vibrate(int vibrationLength, Context context) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (v.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(vibrationLength, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                // If device is on API 25 and below, use the old, deprecated method
                v.vibrate(vibrationLength);
            }
        }
    }

    /**
     * Use this method to do a quick vibration of the device
     * This is to give the user acknowledgement that their tap has been processed.
     */
    public static void shortVibrate(Context context) {
        vibrate(5, context);
    }
}
