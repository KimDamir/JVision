package ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dataclasses.Word

@Composable
fun DescriptionSection (modifier: Modifier = Modifier, word: Word, isEmpty: Boolean = false) {
    Surface(modifier=modifier.verticalScroll(rememberScrollState()), border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.tertiary))
    {
        if (!isEmpty)
            Text(text = "Writing: ${word.writings} \nReading: ${word.readings} \nDefinition: ${word.translations}"
            )
    }
}