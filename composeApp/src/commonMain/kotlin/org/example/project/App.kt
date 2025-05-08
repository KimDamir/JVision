package org.example.project

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import const.viewmodel.JVisionViewModel
import dataclasses.Query
import dataclasses.Word
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import org.jetbrains.compose.ui.tooling.preview.Preview
import ui.components.*
import ui.navigation.NavigationController
import ui.navigation.NavigationHost
import ui.navigation.composable
import ui.navigation.rememberNavController
import ui.theme.JvisionTheme
import util.authorizationNavigationActions


@Composable
fun App(isAuthorized:Boolean, vm:JVisionViewModel = JVisionViewModel()) {
    val scope = rememberCoroutineScope()
    val startScreen =  if (!isAuthorized) Screen.AuthorizationScreen.name else Screen.HomeScreen.name
    val navigationController by rememberNavController(startScreen)
    val currentScreen by remember {
        navigationController.currentScreen
    }
    navigationController.coroutineScope = scope
    JvisionTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            CustomNavigationHost(navigationController, vm)
        }
    }
}


@Preview
@Composable
fun CreateMainScreen(modifier: Modifier = Modifier, navigationController: NavigationController, viewModel: JVisionViewModel,
                     words: List<Word>) {
    val word = if(words.isNotEmpty()) words[0] else Word(listOf(), listOf(), listOf())

        Column(
            modifier = modifier
        )
        {

            WordList(
                modifier = Modifier
                    .weight(1F),
                words,
                word,
                viewModel
            )

            MainButtons(modifier = Modifier.weight(0.3F), navigationController, viewModel)

            DescriptionSection(
                modifier = Modifier
                    .weight(1F)
                    .fillMaxSize(),
                word = word
            )
        }
    }

enum class Screen(
    val label: String,
) {
    HomeScreen(
        label = "Home",
    ),
    AuthorizationScreen(
        label = "Authorization",
    ),
    RegistrationScreen(
        label = "Registration",
    ),
    HistoryScreen(
        label = "History",
    )
}


@Composable
fun CustomNavigationHost(
    navController: NavigationController,
    viewModel: JVisionViewModel
) {
    val queries = viewModel.queries.collectAsState(listOf())
    val wordList = viewModel.wordList.collectAsState(listOf())
    NavigationHost(navController) {
        composable(Screen.HomeScreen.name) {
            CreateMainScreen(navigationController = navController, viewModel = viewModel, words = wordList.value)
        }

        composable(Screen.AuthorizationScreen.name) {
            AuthorizationScreen(navController)
            authorizationNavigationActions()
        }

        composable(Screen.RegistrationScreen.name) {
            RegistrationScreen(navController)
        }

        composable(Screen.HistoryScreen.name) {
            History(navigationController = navController, viewModel = viewModel, queries = queries.value)
        }

    }.build()
}