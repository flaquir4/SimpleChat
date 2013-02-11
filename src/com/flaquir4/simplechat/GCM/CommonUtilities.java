package com.flaquir4.simplechat.GCM;

import android.content.Context;
import android.content.Intent;

public final class CommonUtilities {
	
	// give your server registration url here
    static final String SERVER_URL = "http://simplechat.p.ht/register.php"; 
    static final String Message_URL = "http://simplechat.p.ht/send_message.php";

    // Google project id
    public static final String SENDER_ID = "17032338256"; 

    /**
     * Tag used on log messages.
     */
    static final String TAG = "simpleChat";

    static final String DISPLAY_MESSAGE_ACTION =
            "com.flaquir4.simplechat.DISPLAY_MESSAGE";

    static final String EXTRA_MESSAGE = "message";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
   public static void displayMessage(Context context, String message, String name) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        intent.putExtra("name", name);
        context.sendBroadcast(intent);
    }
}
