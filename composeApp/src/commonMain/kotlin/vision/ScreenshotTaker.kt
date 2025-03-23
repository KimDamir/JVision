package vision

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap

expect fun takeScrenshot(): ImageBitmap
@Composable
expect fun listenForCall(action: ()->Unit)
@Composable
expect fun autoButton(modifier: Modifier)