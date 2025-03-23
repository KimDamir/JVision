package org.example.project

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dictionary.Word
import org.jetbrains.compose.ui.tooling.preview.Preview
import ui.components.DescriptionSection
import ui.components.MainButtons
import ui.components.WordList
import ui.theme.JvisionTheme
import vision.listenForCall
import vision.takeScrenshot

@Composable
fun App() {
    JvisionTheme {
        CreateMainScreen(modifier = Modifier)
    }
}


@Preview
@Composable
fun CreateMainScreen(modifier: Modifier = Modifier) {
    var hasScreenshot by remember {
        mutableStateOf(false)
    }
    var image by remember {
        mutableStateOf(ImageBitmap(128, 32))
    }
    val word = Word("電車", "でんしゃ", "Train")
    val wordList = listOf(word, Word("結局", "けっきょく", "In the end"),
        Word("", "", ""), word, word, word, word)
    var prediction by remember {
        mutableStateOf("Placeholder value")
    }

    if (!hasScreenshot or hasScreenshot) {
        Column(
            modifier = modifier
        )
        {
            listenForCall ({
                
                image = takeScrenshot()
                hasScreenshot = true
            })
            CreateWordSection(
                modifier = Modifier
                    .weight(1F), word = word
            )

            MainButtons(modifier = Modifier.weight(0.3F))

            WordList(
                modifier = Modifier
                    .weight(1F),
                wordList
            )

            DescriptionSection(
                modifier = Modifier
                    .weight(1F)
                    .fillMaxSize(),
                word = word
            )
        }
    } else {
        drawImage(modifier = Modifier.fillMaxSize(),
                image=image)
        }
}

@Composable
fun CreateWordSection(modifier: Modifier = Modifier, word: Word) {
    val text = word.writing
    Row (modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly) {
        for (i in 0..3) {
            Surface (modifier=Modifier
                .weight(1F)
                .fillMaxSize(),
                border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.tertiary)
            )
            {
                if (i < text.length)
                    Text(text=text[i].toString(), textAlign = TextAlign.Center)
                else
                    Text(text="")
            }
        }
    }
}



@Composable
fun drawImage(modifier: Modifier = Modifier, image: ImageBitmap) {
    androidx.compose.foundation.Canvas(modifier=modifier) {
        drawImage(image)
    }
}