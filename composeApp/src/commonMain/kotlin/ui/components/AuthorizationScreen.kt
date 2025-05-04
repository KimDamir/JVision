package ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import jvision.composeapp.generated.resources.Res
import jvision.composeapp.generated.resources.icon
import org.example.project.Screen
import org.jetbrains.compose.resources.painterResource
import ui.navigation.NavigationController

@Composable
fun AuthorizationScreen(navigationController: NavigationController) {

    val email = remember {
        mutableStateOf(TextFieldValue())
    }
    val password = remember {
        mutableStateOf(TextFieldValue())
    }
    val hasError = remember {
        mutableStateOf(false)
    }
    val focusRequester = remember {
        FocusRequester()
    }
    Column {
        Surface(modifier = Modifier.weight(1F).fillMaxWidth())
        {
            Box {
                Image(painter = painterResource(Res.drawable.icon), "Jvision Icon",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(170.dp)
                        .align(Alignment.Center))
            }

        }
        Surface(modifier = Modifier.weight(1.25F).fillMaxWidth())
        {
            Box(Modifier.fillMaxSize()) {
                Column(Modifier.fillMaxWidth().align(Alignment.Center)) {
                    Text("Sign in", modifier = Modifier.align(Alignment.Start).padding(horizontal = 65.dp))
                    TextField(value=email.value, onValueChange = {email.value = it},
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        placeholder = {Text("abc@email.com", color = MaterialTheme.colorScheme.outline)}
                    )

                    Spacer(modifier = Modifier.height(15.dp))
                    TextField(value = password.value , onValueChange = {password.value = it},
                        modifier = Modifier.align(Alignment.CenterHorizontally).background(color = Color.White)
                        ,
                        placeholder = {Text("Your password", color = MaterialTheme.colorScheme.outline)},
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = PasswordVisualTransformation()
                    )
                    if (hasError.value) {
                        Text("Wrong email or password", color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.CenterHorizontally))
                    }

                }
            }
        }
        Surface(modifier = Modifier.weight(1F).fillMaxWidth())
        {
            Column {
                loginButton(navigationController, email, password, hasError,
                    Modifier.align(Alignment.CenterHorizontally).width(250.dp).height(60.dp))

                Spacer(modifier = Modifier.height(15.dp))
                Button("Register", modifier = Modifier.align(Alignment.CenterHorizontally).width(250.dp).height(60.dp),
                onClick = {
                    navigationController.navigate(Screen.RegistrationScreen.name)
                })
            }
        }
    }
}
