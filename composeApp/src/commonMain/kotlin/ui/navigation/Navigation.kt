package ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope

class NavigationController(
    private val startDestination: String,
    private var backStackScreens: MutableSet<String> = mutableSetOf(),
) {
    lateinit var coroutineScope: CoroutineScope
    // Variable to store the state of the current screen
    var currentScreen: MutableState<String> = mutableStateOf(startDestination)

    // Function to handle the navigation between the screen
    fun navigate(route: String) {
        if (route != currentScreen.value) {
            if (backStackScreens.contains(currentScreen.value) && currentScreen.value != startDestination) {
                backStackScreens.remove(currentScreen.value)
            }

            if (route == startDestination) {
                backStackScreens = mutableSetOf()
            } else {
                backStackScreens.add(currentScreen.value)
            }

            currentScreen.value = route
        }
    }

    // Function to handle the back
    fun navigateBack() {
        if (backStackScreens.isNotEmpty()) {
            currentScreen.value = backStackScreens.last()
            backStackScreens.remove(currentScreen.value)
        }
    }
}


/**
 * Composable to remember the state of the navcontroller
 */
@Composable
fun rememberNavController(
    startDestination: String,
    backStackScreens: MutableSet<String> = mutableSetOf()
): MutableState<NavigationController> = remember {
    mutableStateOf(NavigationController(startDestination, backStackScreens))
}