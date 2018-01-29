package rc.loveq.eye.utils.glide;

import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

/**
 * Created by rc on 2018/1/29.
 * Description:
 */

@GlideExtension
public class EyeGlideExtension {
    private EyeGlideExtension() {
    }

    @GlideOption
    public static void centerCropAndRoundedCorners(RequestOptions options, int roundingRadius) {
        options.transforms(new CenterCrop(), new RoundedCorners(roundingRadius));
    }
}
