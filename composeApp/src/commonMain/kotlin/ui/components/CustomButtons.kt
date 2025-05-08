package ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import const.viewmodel.JVisionViewModel
import dataclasses.Query
import ui.navigation.NavigationController

enum class Options(val text: String) {
    HOUR("Last hour"), DAY("Last 24 hours"), WEEK("Last week")
}

@Composable
expect fun loginButton(
    navigationController: NavigationController,
    email:MutableState<TextFieldValue>,
    password: MutableState<TextFieldValue>,
    hasError: MutableState<Boolean>,
    modifier: Modifier)

@Composable
expect fun registerButton(
    email:MutableState<TextFieldValue>,
    password: MutableState<TextFieldValue>,
    username:MutableState<TextFieldValue>,
    navigationController:NavigationController,
    modifier: Modifier
)

@Composable
expect fun historyButton(
    navigationController: NavigationController,
    modifier: Modifier,
    vm: JVisionViewModel
)

@Composable
expect fun customWordColumn(
    navigationController: NavigationController,
    modifier: Modifier,
    vm: JVisionViewModel,
    queries: List<Query>
)

@Composable
expect fun HistoryDropdownMenu(vm: JVisionViewModel)