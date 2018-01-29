package rc.loveq.eye.utils.glide;

import android.app.ActivityManager;
import android.content.Context;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

import static android.content.Context.ACTIVITY_SERVICE;
import static com.bumptech.glide.load.DecodeFormat.PREFER_ARGB_8888;
import static com.bumptech.glide.load.DecodeFormat.PREFER_RGB_565;

/**
 * Author：Rc
 * 0n 2018/1/28 16:53
 */

@GlideModule
public class EyeGlideModule extends AppGlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        RequestOptions options = new RequestOptions();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        options.format(activityManager.isLowRamDevice() ? PREFER_RGB_565 : PREFER_ARGB_8888);
        options.disallowHardwareConfig();
        builder.setDefaultRequestOptions(options);
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
