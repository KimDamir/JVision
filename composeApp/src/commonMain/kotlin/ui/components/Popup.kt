package ui.components



import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dataclasses.Word

@Composable
fun Popup(wordList:List<Word>) {
    val isEmpty = wordList.isEmpty()
    var word by remember {
        mutableStateOf(if(!isEmpty) wordList[0] else Word(listOf(""), listOf(""), listOf("")))
    }
    Surface(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {
            DescriptionSection(
                modifier = Modifier.weight(1F).fillMaxSize(),
                word = word,
                isEmpty
            )
            if (wordList.size > 1) {
                Column(modifier = Modifier
                    .weight(1F)
                    .verticalScroll(rememberScrollState())) {
                    if (wordList.isNotEmpty()) {
                        for (wordEntry in wordList) {
                            Surface(
                                Modifier.fillMaxHeight()
                                    .clickable {
                                        word = wordEntry
                                    },
                                border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.secondary)
                            ) {
                                Text(
                                    text = wordEntry.writings[0],
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
}