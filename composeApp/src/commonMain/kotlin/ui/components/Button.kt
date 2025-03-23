package ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun Button(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(onClick, modifier = modifier, colors = ButtonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Color.White,
        disabledContainerColor = MaterialTheme.colorScheme.primary,
        disabledContentColor = Color.White
    ), border = BorderStroke(1.dp, color = Color.Black)
    )
    {
        Text(text = text, fontStyle = MaterialTheme.typography.bodyLarge.fontStyle, textAlign = TextAlign.Center, color = Color.White)
    }
}