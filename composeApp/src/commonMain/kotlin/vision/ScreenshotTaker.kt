package vision

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import const.viewmodel.JVisionViewModel

expect fun takeScreenshot(vm: JVisionViewModel)
@Composable
expect fun listenForCall(action: ()->Unit)
@Composable
expect fun autoButton(modifier: Modifier, vm: JVisionViewModel)