package org.example.project

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dataclasses.Query
import dataclasses.Word
import org.jetbrains.compose.ui.tooling.preview.Preview
import ui.components.*
import ui.navigation.NavigationController
import ui.navigation.NavigationHost
import ui.navigation.composable
import ui.navigation.rememberNavController
import ui.theme.JvisionTheme
import vision.listenForCall
import vision.takeScrenshot

var queries:List<Query> = listOf()
var wordExample = (Word(listOf("電車"), listOf("でんしゃ"), listOf("Train")))

var wordList = listOf(wordExample, Word(listOf("結局"), listOf("けっきょく"), listOf("In the end")),
    Word(listOf(""), listOf(""), listOf("")), wordExample, wordExample, wordExample, wordExample)

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

        Column(
            modifier = modifier
        )
        {
            listenForCall ({
                takeScrenshot()
            })

            WordList(
                modifier = Modifier
                    .weight(1F),
                wordList,
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


fun setHistory(setQueries: List<Query>) {
    queries = setQueries
}

fun changeWordList(newWordList: List<Word>) {
    wordList = newWordList
    wordExample = wordList[0]
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
        }

        composable(Screen.RegistrationScreen.name) {
            RegistrationScreen(navController)
        }

        composable(Screen.HistoryScreen.name) {
            History(queries = queries, navigationController = navController)
        }

    }.build()
}