package net.janusjanus.we4x4_v1;


/** this class Provided by firebase - GitHub * @author greg **/

import com.firebase.client.Firebase;

public class ChatApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}