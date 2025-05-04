package vision

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

expect fun takeScreenshot()
@Composable
expect fun listenForCall(action: ()->Unit)
@Composable
expect fun autoButton(modifier: Modifier)