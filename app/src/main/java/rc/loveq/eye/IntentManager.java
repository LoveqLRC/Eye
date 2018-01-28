package rc.loveq.eye;

import android.content.Context;
import android.content.Intent;

/**
 * Author：Rc
 * 0n 2018/1/28 17:31
 */

public class IntentManager {
    public static void startVideoPlayerActivity(Context context, Class clazz) {
        Intent intent = new Intent(context, clazz);
        context.startActivity(intent);
    }
}
