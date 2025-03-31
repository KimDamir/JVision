package ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.Screen
import ui.navigation.NavigationController
import vision.autoButton

@Composable
fun MainButtons(modifier: Modifier = Modifier, navigationController: NavigationController) {
    Surface(modifier=modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.tertiaryContainer) {
        Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            Button("Logout", onClick = {
                navigationController.navigate(Screen.AuthorizationScreen.name)
            }, modifier = Modifier.size(150.dp, 40.dp).padding(2.dp).weight(1F))
            historyButton(navigationController,
                Modifier.size(150.dp, 40.dp).padding(2.dp).weight(1F))
            autoButton( modifier = Modifier.size(150.dp, 40.dp).padding(2.dp).weight(1F))
        }
    }


}
