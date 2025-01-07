package com.example.vycfinal.Views.User

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vycfinal.Components.Alert
import com.example.vycfinal.R
import com.example.vycfinal.ViewModel.LoginViewModel


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RegisterView(navController: NavController, loginVM: LoginViewModel) {

    Scaffold (topBar = {
        CenterAlignedTopAppBar(title = { Text( text = "Registro")},
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary
            ),
            navigationIcon = {
                IconButton(onClick = {
                    navController.navigate("Login")
                }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
                }
            }
        )
    }
    ){
        Column (
            modifier = Modifier
                .padding(64.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            val context = LocalContext.current
            var email by rememberSaveable { mutableStateOf("") }
            var ErrorE by remember { mutableStateOf("") }
            var password by rememberSaveable { mutableStateOf("") }
            var ErrorP by remember { mutableStateOf("") }
            var passwordC by rememberSaveable{ mutableStateOf("") }
            var ErrorCC by remember { mutableStateOf("") }
            var passwordVisibility by rememberSaveable { mutableStateOf(false) }
            var username by rememberSaveable { mutableStateOf("") }
            var ErrorU by remember { mutableStateOf("") }

            val icon = if (passwordVisibility)
                ImageVector.vectorResource(R.drawable.visibility)
            else
                ImageVector.vectorResource(R.drawable.visibility_off)

            OutlinedTextField(
                value = username,
                onValueChange = {username = it},
                label = { Text(text = "Username")},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, end = 30.dp))
            Text ( text = ErrorU, color=MaterialTheme.colorScheme.error, style = TextStyle(fontSize = 12.sp), modifier = Modifier.fillMaxWidth().padding(start = 30.dp, end = 30.dp))
            Spacer(modifier = Modifier.height(5.dp))
            OutlinedTextField(
                value = email,
                onValueChange = {email = it},
                label = { Text(text = "Email")},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, end = 30.dp))
            Text ( text = ErrorE, color=MaterialTheme.colorScheme.error, style = TextStyle(fontSize = 12.sp), modifier = Modifier.fillMaxWidth().padding(start = 30.dp, end = 30.dp))
            Spacer(modifier = Modifier.height(5.dp))
            OutlinedTextField(
                value = password,
                onValueChange = {password = it},
                label = { Text(text = "Password")},
                trailingIcon = {
                    IconButton(onClick = {
                        passwordVisibility = !passwordVisibility
                    }) {
                        Icon(imageVector = icon, contentDescription = "Visibility Icon")
                    }
                },
                visualTransformation = if(passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, end = 30.dp))
            Text ( text = ErrorP, color=MaterialTheme.colorScheme.error, style = TextStyle(fontSize = 11.sp), modifier = Modifier.fillMaxWidth().padding(start = 30.dp, end = 30.dp))
            Spacer(modifier = Modifier.height(5.dp))
            OutlinedTextField(
                value = passwordC,
                onValueChange = {passwordC = it},
                label = { Text(text = "Confirma Password",style = TextStyle(fontSize = 14.sp))},
                trailingIcon = {
                    IconButton(onClick = {
                        passwordVisibility = !passwordVisibility
                    }) {
                        Icon(imageVector = icon, contentDescription = "Visibility Icon")
                    }
                },
                visualTransformation = if(passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, end = 30.dp))
            Text ( text = ErrorCC, color=MaterialTheme.colorScheme.error, style = TextStyle(fontSize = 12.sp), modifier = Modifier.fillMaxWidth().padding(start = 30.dp, end = 30.dp))

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    loginVM.createUser(email, password, passwordC,username,context){
                        navController.navigate("MainMenu")
                    }
                    if (loginVM.errorCU){
                        ErrorU = "Solo acepta letras y minimo 3 caracteres"
                    }else{
                        ErrorU = ""
                    }
                    if (loginVM.errorCE){
                        ErrorE = "Formato invalido ejemplo@domino.com"
                    }else{
                        ErrorE = ""
                    }
                    if (loginVM.errorCP){
                        ErrorP = "Minimo 6 caracteres y maximo 15 con un numero y un caracter especial"
                    }else{
                        ErrorP = ""
                    }
                    if (loginVM.errorCC){
                        ErrorCC = "Las contraseñas no coinciden"
                    }else{
                        ErrorCC = ""
                    }
                }, modifier = Modifier
                    .width(225.dp)
                    .height(50.dp)
                    .padding(start = 30.dp, end = 30.dp),
                enabled = email.isNotEmpty().and(password.isNotEmpty()).and(username.isNotEmpty())
            ) {
                Text(text = "Registrarse")
            }
        }
        if(loginVM.showAlert){
            Alert(
                title = "Alerta",
                message = "Usuario No creado",
                confirmText = "Aceptar",
                onConfirmClick = {loginVM.closeAlert()}
            ){

            }
        }
    }
}
