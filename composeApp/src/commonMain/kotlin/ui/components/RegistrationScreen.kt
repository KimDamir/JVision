package ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import jvision.composeapp.generated.resources.Res
import jvision.composeapp.generated.resources.arrow
import org.example.project.Screen
import org.jetbrains.compose.resources.imageResource
import ui.navigation.NavigationController

@Composable
fun RegistrationScreen(navigationController: NavigationController) {
    val emailRegex = Regex("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:" +
            "[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")" +
            "@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|" +
            "1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:" +
            "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])")
    val username = remember {
        mutableStateOf(TextFieldValue())
    }
    val password = remember {
        mutableStateOf(TextFieldValue())
    }
    val email = remember {
        mutableStateOf(TextFieldValue())
    }
    val repeatedPassword = remember {
        mutableStateOf(TextFieldValue())
    }
    val hasEmailError = !emailRegex.matches(email.value.text)
    val hasPasswordError = password.value.text != repeatedPassword.value.text
    val hasUsernameError = username.value.text == ""
    Column {
        Surface(modifier = Modifier.weight(1F).fillMaxWidth())
        {
            Column {
                Image(imageResource(Res.drawable.arrow), "Arrow Icon",
                    modifier = Modifier.size(50.dp)
                        .clickable { navigationController.navigate(Screen.AuthorizationScreen.name) })
                Spacer(modifier = Modifier.height(30.dp))
                Text("Sign up", modifier = Modifier.align(Alignment.Start).padding(horizontal = 65.dp))
            }
        }
        Surface(modifier = Modifier.weight(3F).fillMaxWidth())
        {
            Column {
                TextField(value=email.value, onValueChange = {email.value = it},
                    modifier = Modifier.align(Alignment.CenterHorizontally).background(color = Color.White),
                    placeholder = {Text("abc@email.com", color = MaterialTheme.colorScheme.outline)})
                Spacer(modifier = Modifier.height(15.dp))
                TextField(value=username.value, onValueChange = {username.value = it},
                    modifier = Modifier.align(Alignment.CenterHorizontally).background(color = Color.White),
                    placeholder = {Text("Username", color = MaterialTheme.colorScheme.outline)})
                Spacer(modifier = Modifier.height(15.dp))
                TextField(value = password.value, onValueChange = {password.value = it},
                    modifier = Modifier.align(Alignment.CenterHorizontally).background(color = Color.White),
                    placeholder = {Text("Your password", color = MaterialTheme.colorScheme.outline)})
                Spacer(modifier = Modifier.height(15.dp))
                TextField(value=repeatedPassword.value, onValueChange = {repeatedPassword.value = it},
                    modifier = Modifier.align(Alignment.CenterHorizontally).background(color = Color.White),
                    placeholder = {Text("Repeat password", color = MaterialTheme.colorScheme.outline)})

                if (hasEmailError) {
                    Text("Email is not correct", color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                if(hasUsernameError) {
                    Text("Enter username", color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                if (hasPasswordError) {
                    Text("Passwords don't match", color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }
        Surface(modifier = Modifier.weight(1F).fillMaxWidth())
        {
            Column {
                if (!hasEmailError and !hasPasswordError and !hasUsernameError and (password.value.text != "")) {
                    registerButton(email, password, username, navigationController,
                        Modifier.align(Alignment.CenterHorizontally).width(250.dp).height(60.dp))
                }
            }
        }
    }
}