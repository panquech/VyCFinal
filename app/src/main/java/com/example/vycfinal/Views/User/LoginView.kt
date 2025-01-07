package com.example.vycfinal.Views.User

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vycfinal.R
import com.example.vycfinal.ViewModel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun LoginView(navController: NavController, loginVM: LoginViewModel){
    var firebaseUser : FirebaseUser?=null
    var iniciadaSesion = true
    firebaseUser = FirebaseAuth.getInstance().currentUser
    if (firebaseUser != null){
        iniciadaSesion = false
    }
    BackHandler(enabled = true) {
        loginVM.autenticar(navController)
    }
    Scaffold (topBar = {
        CenterAlignedTopAppBar(title = { Text( text = "Inicio de sesión")},
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary
            ),
        )
    }
    ){



        var ErrorPass by remember { mutableStateOf("") }
        var ErrorEmail by remember { mutableStateOf("") }
        val token = "1038237121989-re0kvp1sl2itogigdo8qn2dkt4paa9rv.apps.googleusercontent.com"
        val context = LocalContext.current
        val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                loginVM.signInWithGoogleCredential(credential) {
                    if (task.isSuccessful) {
                        navController.navigate("MainMenu")
                    } else {
                        Toast.makeText(context, "Error on Sign In with Google", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Log.d("Login Google error", "error: $e")
            }
        }

        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(64.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var email by rememberSaveable { mutableStateOf("") }
            var password by rememberSaveable { mutableStateOf("") }
            var passwordVisibility by rememberSaveable { mutableStateOf(false) }
            val icon = if (passwordVisibility)
                ImageVector.vectorResource(R.drawable.visibility)
            else
                ImageVector.vectorResource(R.drawable.visibility_off)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                enabled = iniciadaSesion,
                label = { Text(text = "Email: ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Text ( text = ErrorEmail, color=MaterialTheme.colorScheme.error, style = TextStyle(fontSize = 13.sp))
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                enabled = iniciadaSesion,
                label = { Text(text = "Contraseña: ") },
                trailingIcon = {
                    IconButton(onClick = {
                        passwordVisibility = !passwordVisibility
                    }) {
                        Icon(imageVector = icon, contentDescription = "Visibility Icon")
                    }
                },
                visualTransformation = if(passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)

            )
            Text ( text = ErrorPass, color=MaterialTheme.colorScheme.error, style = TextStyle(fontSize = 13.sp))

            Spacer(Modifier.height(8.dp))
            Button(onClick = {
                loginVM.login(email, password, context){
                    navController.navigate("MainMenu")
                }
                if (loginVM.errorP){
                    ErrorPass = "Minimo 6 caracteres con un numero y un caracter"
                }else{
                    ErrorPass = ""
                }
                if (loginVM.errorE){
                    ErrorEmail = "Formato invalido ejemplo@domino.com"
                }else{
                    ErrorEmail = ""
                }
            },
                modifier = Modifier
                    .fillMaxWidth(),
                enabled = email.isNotEmpty().and(password.isNotEmpty())) {
                Text(text = "Iniciar sesión")
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = {
                val opciones = GoogleSignInOptions.Builder(
                    GoogleSignInOptions.DEFAULT_SIGN_IN
                )
                    .requestIdToken(token)
                    .requestEmail()
                    .build()
                val googleSignInCliente = GoogleSignIn.getClient(context, opciones)
                launcher.launch(googleSignInCliente.signInIntent)
            }, modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = R.drawable.img),
                    contentDescription = "Google Login",
                    alignment = AbsoluteAlignment.CenterLeft,
                    modifier = Modifier
                        .size(30.dp)
                )
                Text(text = "Google Login", textAlign = TextAlign.Center, modifier = Modifier .weight(1f))
            }
            Spacer(Modifier.height(16.dp))
            Text(text = "¿Aun no tienes cuenta?")
            TextButton(onClick = { navController.navigate("Register") }) {
                Text(text = "Registrate")
            }
        }
    }
}

