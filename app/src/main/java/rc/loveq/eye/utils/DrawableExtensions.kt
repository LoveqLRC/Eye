@file:JvmName("DrawableUtils")

package rc.loveq.eye.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat

/**
 * Created by rc on 2018/2/2.
 * Description:
 */

fun Drawable.toBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}

fun drawableToBitmap(context: Context, @DrawableRes drawableId: Int) =
        ContextCompat.getDrawable(context,drawableId)?.toBitmap()




