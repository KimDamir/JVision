package ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val AppTypography
@Composable
get() = Typography(
    bodyLarge = TextStyle(
        fontFamily = CarterFont,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = CarterFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
)

