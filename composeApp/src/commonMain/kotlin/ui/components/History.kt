package ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dataclasses.Query
import jvision.composeapp.generated.resources.Res
import jvision.composeapp.generated.resources.arrow
import kotlinx.coroutines.flow.MutableSharedFlow
import org.example.project.Screen
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import ui.navigation.NavigationController

@Composable
@Preview
fun History(modifier: Modifier = Modifier, queries:MutableSharedFlow<List<Query>>, navigationController: NavigationController) {
    val queriesState = queries.collectAsState(listOf())
    Column(modifier=modifier.fillMaxSize())
    {
        Row (modifier=Modifier.fillMaxWidth()){
            Image(imageResource(Res.drawable.arrow), "Arrow Icon",
                modifier = Modifier.size(50.dp)
                    .clickable { navigationController.navigate(Screen.HomeScreen.name) })
            Spacer(Modifier.weight(1f))
            Text("Show queries from: ")
            HistoryDropdownMenu()
            Spacer(Modifier.width(15.dp))
        }

        Row (Modifier.weight(1F), horizontalArrangement = Arrangement.SpaceEvenly) {
            Surface(Modifier.weight(1F).fillMaxHeight(), border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.secondary), color = MaterialTheme.colorScheme.primaryContainer) {
                Text(text = "Kanji", modifier= Modifier.padding(2.dp), fontStyle = MaterialTheme.typography.bodyLarge.fontStyle, textAlign = TextAlign.Center, color = Color.Black)
            }
            Surface(Modifier.weight(1F).fillMaxHeight(), border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.secondary), color = MaterialTheme.colorScheme.primaryContainer) {
                Text(text = "Description", modifier= Modifier.padding(2.dp), fontStyle = MaterialTheme.typography.bodyLarge.fontStyle, textAlign = TextAlign.Center, color = Color.Black)
            }
            Surface(Modifier.weight(1F).fillMaxHeight(), border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.secondary), color = MaterialTheme.colorScheme.primaryContainer) {
                Text(text = "Date", modifier= Modifier.padding(2.dp), fontStyle = MaterialTheme.typography.bodyLarge.fontStyle, textAlign = TextAlign.Center, color = Color.Black)
            }
        }
        customWordColumn(navigationController, Modifier
            .weight(4F),
            queriesState)
    }
}