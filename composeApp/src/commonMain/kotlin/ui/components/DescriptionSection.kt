package ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dictionary.Word

@Composable
fun DescriptionSection (modifier: Modifier = Modifier, word: Word, isEmpty: Boolean = false) {
    Surface(modifier=modifier, border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.tertiary))
    {
        if (!isEmpty)
            Text(text = "${word.writing}    ${word.reading} \n" +
                    word.definition
            )
    }
}