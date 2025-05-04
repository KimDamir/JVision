package ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import api.getHistory
import api.login
import api.register
import api.sendQuery
import com.dokar.sonner.*
import dataclasses.Query
import dataclasses.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.example.project.Screen
import org.example.project.changeWordList
import org.example.project.setHistory
import ui.navigation.NavigationController




@Composable
actual fun loginButton(
    navigationController: NavigationController,
    email: MutableState<TextFieldValue>,
    password: MutableState<TextFieldValue>,
    hasError: MutableState<Boolean>,
    modifier: Modifier
) {
    val toaster = rememberToasterState()
    val emailRegex = Regex("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:" +
            "[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")" +
            "@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|" +
            "1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:" +
            "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])")
    Button("Sign in", modifier = modifier,
        onClick = {
            if (!emailRegex.matches(email.value.text) || password.toString() == "") {
                hasError.value = true
            } else {
                val result = login(email.value.text, password.value.text)
                if (result) {
                    navigationController.navigate(Screen.HomeScreen.name)
                } else {
                    toaster.show(
                        "Пользователь не найден.",
                        ToastType.Error,
                        ToasterDefaults.DurationShort
                    )
                }
            }
        })
    Toaster(state = toaster, showCloseButton = true)
}

@Composable
actual fun registerButton(
    email:MutableState<TextFieldValue>,
    password: MutableState<TextFieldValue>,
    username:MutableState<TextFieldValue>,
    navigationController:NavigationController,
    modifier: Modifier
) {
    val toaster = rememberToasterState()
    Button("Sign up", onClick = {
        val success = register(User(email.value.text, username.value.text, password.value.text))
        if (success) {
            navigationController.navigate(Screen.AuthorizationScreen.name)
            toaster.show(
                "Пользователь успешно зарегистрирован.",
                ToastType.Success,
                ToasterDefaults.DurationShort
            )
        } else {
            toaster.show(
                "Ошибка при регистрации.",
                ToastType.Error,
                ToasterDefaults.DurationShort
            )
        }
    },  modifier = modifier)
    Toaster(state = toaster, showCloseButton = true)
}

@Composable
actual fun historyButton(
    navigationController: NavigationController,
    modifier: Modifier
) {
    val scope = rememberCoroutineScope()
    Button("History", onClick = {
        scope.launch { setHistory(getHistory()) }
        navigationController.navigate(Screen.HistoryScreen.name)
    }, modifier = modifier)
}

@Composable
actual fun customWordColumn(navigationController: NavigationController, modifier: Modifier, queries: State<List<Query>>) {
    LazyColumn (modifier) {
        var color = Color.White
        val queryValues = queries.value
        var isChosen = false
        if (queryValues.isNotEmpty()) {
            items (queryValues) { query: Query ->
                Row(Modifier.fillMaxWidth().height(35.dp).background(color).clickable {
                    if (!isChosen) {
                        isChosen = true
                        color = Color(0x6F7EC9)
                        runBlocking {
                            changeWordList(sendQuery(query.query_text))
                        }
                        navigationController.navigate(Screen.HomeScreen.name)
                    }
                }, horizontalArrangement = Arrangement.SpaceEvenly) {

                    Surface(
                        Modifier.weight(1F),
                        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text(
                            text = query.writing,
                            Modifier.padding(2.dp).fillMaxSize(),
                            fontStyle = MaterialTheme.typography.bodyMedium.fontStyle,
                            textAlign = TextAlign.Center,
                            color = Color.Black
                        )
                    }
                    Surface(
                        Modifier.weight(1F),
                        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text(
                            text = query.translation,
                            Modifier.padding(2.dp).fillMaxSize(),
                            fontStyle = MaterialTheme.typography.bodyMedium.fontStyle,
                            textAlign = TextAlign.Center,
                            color = Color.Black
                        )
                    }
                    Surface(
                        Modifier.weight(1F),
                        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text(
                            text = query.time.substring(0, 19),
                            Modifier.padding(2.dp).fillMaxSize(),
                            fontStyle = MaterialTheme.typography.bodyMedium.fontStyle,
                            textAlign = TextAlign.Center,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
actual fun HistoryDropdownMenu() {
    var expanded by remember { mutableStateOf(false) }
    var chosen by remember { mutableStateOf(Options.HOUR) }
    val options = Options.entries.toTypedArray()
    val scope = rememberCoroutineScope()
    Box ()
    {
        Text(chosen.text, modifier = Modifier.clickable {
            expanded = !expanded
        })
        DropdownMenu(
            expanded,
            onDismissRequest = {expanded = false}
        ) {
            for (option in options) {
                DropdownMenuItem(
                    text = {Text(option.text)},
                    onClick = {
                        chosen = option
                        scope.launch {
                            setHistory(getHistory(option.text))
                        }
                        expanded = false
                    })
            }
        }
    }
}