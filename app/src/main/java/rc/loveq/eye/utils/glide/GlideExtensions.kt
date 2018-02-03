@file:JvmName("GlideUtils")

package rc.loveq.eye.utils.glide

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import com.bumptech.glide.load.resource.gif.GifDrawable
import rc.loveq.eye.utils.layers

/**
 * Created by rc on 2018/2/3.
 * Description:
 */
fun Drawable.getBitmap(): Bitmap? {
    if (this is TransitionDrawable) {
        layers.forEach {
            var bitmap = it.getBitmap()
            if (bitmap != null) {
                return bitmap
            }
        }
    }
    if (this is BitmapDrawable) {
        return bitmap
    } else if (this is GifDrawable) {
        return firstFrame
    }

    return null
}
