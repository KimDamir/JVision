package ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import jvision.composeapp.generated.resources.CarterOne_Regular
import jvision.composeapp.generated.resources.Res


val CarterFont
    @Composable
    get() = FontFamily(
    org.jetbrains.compose.resources.Font(
        Res.font.CarterOne_Regular, weight = FontWeight.Normal
    )
)