package org.example.project

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import dataclasses.Query
import dataclasses.Word
import kotlinx.coroutines.flow.MutableSharedFlow
import org.jetbrains.compose.ui.tooling.preview.Preview
import ui.components.*
import ui.navigation.NavigationController
import ui.navigation.NavigationHost
import ui.navigation.composable
import ui.navigation.rememberNavController
import ui.theme.JvisionTheme
import util.authorizationNavigationActions


var queries = MutableSharedFlow<List<Query>>()
var wordExample = (Word(listOf("電車"), listOf("でんしゃ"), listOf("Train")))

var wordList = MutableSharedFlow<List<Word>>()

@Composable
fun App(isAuthorized:Boolean) {
    val startScreen =  if (!isAuthorized) Screen.AuthorizationScreen.name else Screen.HomeScreen.name
    val navigationController by rememberNavController(startScreen)
    val currentScreen by remember {
        navigationController.currentScreen
    }
    JvisionTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            CustomNavigationHost(navigationController)
        }
    }
}


@Preview
@Composable
fun CreateMainScreen(modifier: Modifier = Modifier, navigationController: NavigationController) {
    val word = remember {
        mutableStateOf(wordExample)
    }
    val words = wordList.collectAsState(listOf(wordExample, Word(listOf("結局"), listOf("けっきょく"), listOf("In the end")),
        Word(listOf(""), listOf(""), listOf("")), wordExample, wordExample, wordExample, wordExample))

        Column(
            modifier = modifier
        )
        {

            WordList(
                modifier = Modifier
                    .weight(1F),
                words.value,
                word
            )

            MainButtons(modifier = Modifier.weight(0.3F), navigationController)

            DescriptionSection(
                modifier = Modifier
                    .weight(1F)
                    .fillMaxSize(),
                word = word.value
            )
        }
    }


suspend fun setHistory(setQueries: List<Query>) {
    queries.emit(setQueries)
}

suspend fun changeWordList(newWordList: List<Word>) {
    wordList.emit(newWordList)
    wordExample = if (newWordList.isNotEmpty()) newWordList[0]
    else Word(listOf(""), listOf(""), listOf(""))
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
    navController: NavigationController
) {
    NavigationHost(navController) {
        composable(Screen.HomeScreen.name) {
            CreateMainScreen(navigationController = navController)
        }

        composable(Screen.AuthorizationScreen.name) {
            AuthorizationScreen(navController)
            authorizationNavigationActions()
        }

        composable(Screen.RegistrationScreen.name) {
            RegistrationScreen(navController)
        }

        composable(Screen.HistoryScreen.name) {
            History(queries = queries, navigationController = navController)
        }

    }.build()
}