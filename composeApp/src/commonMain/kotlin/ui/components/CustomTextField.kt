package ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun CustomTextField(fieldValue: MutableState<String>, modifier: Modifier, placeholderValue: String = "") {
    val focusRequester = remember {
        FocusRequester()
    }
    TextField(value=fieldValue.value, onValueChange = {fieldValue.value = it},
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.None,
            autoCorrectEnabled = false,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        modifier = modifier.focusable(true)
            .focusRequester(focusRequester)
            .clickable {
                println("Clicked")
                focusRequester.requestFocus()
            }
            ,
        placeholder = {Text(placeholderValue, color = MaterialTheme.colorScheme.outline)}
    )
}