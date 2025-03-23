package ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dictionary.Word
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun WordList(modifier: Modifier = Modifier, words:List<Word>, isHistory: Boolean = false) {
    Column(modifier=modifier.fillMaxSize())
    {
        Row (Modifier.weight(1F), horizontalArrangement = Arrangement.SpaceEvenly) {
            Surface(Modifier.weight(1F).fillMaxHeight(), border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.secondary), color = MaterialTheme.colorScheme.primaryContainer) {
                Text(text = "Kanji", modifier=Modifier.padding(2.dp), fontStyle = MaterialTheme.typography.bodyLarge.fontStyle, textAlign = TextAlign.Center, color = Color.Black)
            }
            Surface(Modifier.weight(1F).fillMaxHeight(), border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.secondary), color = MaterialTheme.colorScheme.primaryContainer) {
                Text(text = "Kana", modifier=Modifier.padding(2.dp), fontStyle = MaterialTheme.typography.bodyLarge.fontStyle, textAlign = TextAlign.Center, color = Color.Black)
            }
            Surface(Modifier.weight(1F).fillMaxHeight(), border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.secondary), color = MaterialTheme.colorScheme.primaryContainer) {
                Text(text = "Description", modifier=Modifier.padding(2.dp), fontStyle = MaterialTheme.typography.bodyLarge.fontStyle, textAlign = TextAlign.Center, color = Color.Black)
            }
        }
        Column(modifier = Modifier
            .weight(4F)
            .verticalScroll(rememberScrollState())) {
            var color = Color.White
            var isChosen = false
            if (words.isNotEmpty()) {
                for (word in words) {
                    Row(Modifier.fillMaxWidth().height(35.dp).background(color).clickable {
                        if (!isChosen) {
                            isChosen = true
                            color = Color(0x6F7EC9)
                        }
                    }, horizontalArrangement = Arrangement.SpaceEvenly) {

                        Surface(
                            Modifier.weight(1F),
                            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text(
                                text = word.writing,
                                Modifier.padding(2.dp).fillMaxSize(),
                                fontStyle = MaterialTheme.typography.bodyMedium.fontStyle,
                                textAlign = TextAlign.Center,
                                color = Color.Black
                            )
                        }
                        Surface(
                            Modifier.weight(1F),
                            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text(
                                text = word.reading,
                                Modifier.padding(2.dp).fillMaxSize(),
                                fontStyle = MaterialTheme.typography.bodyMedium.fontStyle,
                                textAlign = TextAlign.Center,
                                color = Color.Black
                            )
                        }
                        Surface(
                            Modifier.weight(1F),
                            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text(
                                text = word.definition,
                                Modifier.padding(2.dp).fillMaxSize(),
                                fontStyle = MaterialTheme.typography.bodyMedium.fontStyle,
                                textAlign = TextAlign.Center,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }

    }
}