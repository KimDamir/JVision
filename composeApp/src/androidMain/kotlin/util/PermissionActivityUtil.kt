package util

import android.content.Context
import android.content.Intent
import android.provider.Settings
import vision.PermissionActivity

fun Context.drawOverOtherAppsEnabled(): Boolean {
    return Settings.canDrawOverlays(this)
}


fun Context.startPermissionActivity() {
    startActivity(
        Intent(this, PermissionActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    )
}