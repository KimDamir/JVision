package ui.components



import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import dictionary.Word

@Composable
fun Popup(wordList:List<Word>) {
    val isEmpty = wordList.isEmpty()
    WordList(
        modifier = Modifier,
        wordList
    )
    DescriptionSection(
        modifier = Modifier
            .fillMaxSize(),
        word = if(!isEmpty) wordList[0] else Word("", "", ""),
        isEmpty
    )
}