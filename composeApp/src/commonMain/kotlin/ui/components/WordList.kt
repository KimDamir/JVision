package ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import const.viewmodel.JVisionViewModel
import dataclasses.Word
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun WordList(modifier: Modifier = Modifier, words:List<Word>, chosenWord: Word, vm:JVisionViewModel) {
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

            if (words.isNotEmpty()) {
                for (word in words) {
                    val surfaceColor = if (chosenWord != word) MaterialTheme.colorScheme.surface
                    else MaterialTheme.colorScheme.surfaceDim
                    Row(Modifier.fillMaxWidth().height(35.dp).clickable {
                        if (chosenWord != word) {
                            vm.changeChosenWord(word)
                        }
                    }, horizontalArrangement = Arrangement.SpaceEvenly) {

                        Surface(
                            Modifier.weight(1F),
                            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.secondary),
                            color=surfaceColor
                        ) {
                            Text(
                                text = word.writings[0],
                                Modifier.padding(2.dp).fillMaxSize(),
                                fontStyle = MaterialTheme.typography.bodyMedium.fontStyle,
                                textAlign = TextAlign.Center,
                                color = Color.Black
                            )
                        }
                        Surface(
                            Modifier.weight(1F),
                            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.secondary),
                            color=surfaceColor
                        ) {
                            Text(
                                text = word.readings[0],
                                Modifier.padding(2.dp).fillMaxSize(),
                                fontStyle = MaterialTheme.typography.bodyMedium.fontStyle,
                                textAlign = TextAlign.Center,
                                color = Color.Black
                            )
                        }
                        Surface(
                            Modifier.weight(1F),
                            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.secondary),
                            color=surfaceColor
                        ) {
                            Text(
                                text = word.translations[0],
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