package ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import dataclasses.Query
import ui.navigation.NavigationController


@Composable
actual fun loginButton(
    navigationController: NavigationController,
    email: MutableState<TextFieldValue>,
    password: MutableState<TextFieldValue>,
    hasError: MutableState<Boolean>,
    modifier: Modifier
) {
}

@Composable
actual fun registerButton(
    email: MutableState<TextFieldValue>,
    password: MutableState<TextFieldValue>,
    username: MutableState<TextFieldValue>,
    navigationController: NavigationController,
    modifier: Modifier
) {
}

@Composable
actual fun historyButton(
    navigationController: NavigationController,
    modifier: Modifier
) {
}

@Composable
actual fun customWordColumn(
    navigationController: NavigationController,
    modifier: Modifier,
    queries: List<Query>
) {
}