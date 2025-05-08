package vision

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.theme.JvisionTheme
import util.drawOverOtherAppsEnabled

const val PERMISSION_REQUEST_CODE = 1

class PermissionActivity : AppCompatActivity() {

    private fun showDialog(titleText: String, messageText: String) {
        with(AlertDialog.Builder(this)) {
            title = titleText
            setMessage(messageText)
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

    private fun requestPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        try {
            startActivityForResult(intent, PERMISSION_REQUEST_CODE)
        } catch (e: Exception) {
            showDialog(
                "Permission Required",
                "Please enable 'Draw over other apps' permission for JVision to show you translations over" +
                        " other apps and take screenshots"
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JvisionTheme {
                Column(Modifier.background(MaterialTheme.colorScheme.surface)) {
                    Text(
                        text = "Permission Required",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
                    )

                    Text(
                        text = "Please enable 'Draw over other apps' permission for JVision to show you translations over" +
                                " other apps and take screenshots",
                        modifier = Modifier.padding(16.dp, 4.dp)
                    )

                    Button(
                        onClick = {
                            requestPermission()
                        },
                        modifier = Modifier.padding(16.dp, 8.dp)
                    ) {
                        Text(text = "Open settings")
                    }

                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Don't check for resultCode == Activity.RESULT_OK because the overlay activity
        // is closed with the back button and so the RESULT_CANCELLED is always received.
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (drawOverOtherAppsEnabled()) {
                // The permission has been granted.
                // Resend the last command - we have only one, so no additional logic needed.
                finish()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}