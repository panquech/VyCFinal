package com.example.vycfinal.ViewModel

import android.content.Context
import android.widget.Toast;
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.vycfinal.Model.UserModel
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel(){
    private val auth: FirebaseAuth = Firebase.auth
    var showAlert by mutableStateOf(false)
    var errorP: Boolean = false
    var errorE: Boolean = false
    var errorCE: Boolean = false
    var errorCP: Boolean = false
    var errorCU: Boolean = false
    var errorCC: Boolean = false
    var firebaseUser : FirebaseUser?=null
    fun signInWithGoogleCredential(credential: AuthCredential, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                auth.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onSuccess()
                    } else {
                        Log.d("ERROR EN FIREBASE", "Usuario y contraseña incorrectos")
                        showAlert = true
                    }
                }
            } catch (e: Exception) {
                Log.d("Error en JetpackCompose", "ERROR: ${e.localizedMessage}")
            }
        }
    }

    fun login(email: String, password: String, context: Context, onSuccess: () -> Unit) {
        viewModelScope.launch {
            errorE=false
            errorP=false
            if (!email.matches("(\\w{1,17})+@(\\w{2,10})+.+\\w{1,3}".toRegex())) {
                Toast.makeText(context, "Email incorrecto", Toast.LENGTH_SHORT).show()
                errorE = true
            }
            if (!password.matches("(?=.*\\d)(?=.*[\\&\\\$\\+\\-\\:\\;\\.\\,\\!\\@])[\\w\\d\\&\\\$\\+\\-\\:\\;\\.\\,\\!\\@]{6,15}".toRegex())) {
                Toast.makeText(context, "Password incorrecto", Toast.LENGTH_LONG).show()
                errorP = true
            }
            if(!errorE && !errorP) {
                try {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                onSuccess()
                            } else {
                                Log.d("ERROR EN FIREBASE", "Usuario y contraseña incorrectos")
                                Toast.makeText(context, "Usuario y contraseña incorrectos", Toast.LENGTH_LONG).show()
                                showAlert = true
                            }
                        }
                } catch (e: Exception) {
                    Log.d("Error en JetpackCompose", "ERROR: ${e.localizedMessage}")
                }
            }
        }
    }

    fun autenticar(navController: NavController){
        firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null){
            navController.navigate("Inventory")
        }else{
            navController.navigate("Login")
        }
    }

    fun createUser(email: String, password: String, passC:String, username: String, context: Context, onSuccess: () -> Unit){
        viewModelScope.launch {
            errorCE=false
            errorCP=false
            errorCU=false
            errorCC=false
            if (!username.matches("^[A-Za-z]{3,9}\$".toRegex())){
                // Toast.makeText(context, "Usuario no valido", Toast.LENGTH_LONG).show()
                errorCU = true
            }
            if(!email.matches("(\\w{1,17})+@(\\w{2,10})+.+\\w{1,3}".toRegex())){
                // Toast.makeText(context, "Correo invalido", Toast.LENGTH_SHORT).show()
                errorCE = true
            }
            if(!password.matches("(?=.*\\d)(?=.*[\\&\\\$\\+\\-\\:\\;\\.\\,\\!\\@])[\\w\\d\\&\\\$\\+\\-\\:\\;\\.\\,\\!\\@]{6,15}".toRegex())){
                // Toast.makeText(context, "Password incorrecto", Toast.LENGTH_LONG).show()
                errorCP = true
            }
            if(password != passC){
                // Toast.makeText(context,"No coincide el password", Toast.LENGTH_LONG).show()
                errorCC = true
            }
            if(!errorCU && !errorCP && !errorCE && !errorCC){
                try {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                saveUser(username)
                                onSuccess()
                            } else {
                                /*Log.d("ERROR EN FIREBASE", "Error al crear el usuario")
                                Toast.makeText(context,"Error al crear el usuario",Toast.LENGTH_SHORT).show()
                                showAlert = true*/


                                try {
                                    throw task.exception ?: Exception("Error desconocido")
                                } catch (e: FirebaseAuthUserCollisionException) {
                                    Log.d("ERROR EN FIREBASE", "El correo ya ha sido registrado")
                                    Toast.makeText(context, "El correo ya ha sido registrado", Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Log.d("ERROR EN FIREBASE", "Error al crear el usuario: ${e.localizedMessage}")
                                    Toast.makeText(context, "Error al crear el usuario", Toast.LENGTH_SHORT).show()
                                    showAlert = true
                                }
                            }
                        }
                } catch (e: Exception) {
                    Log.d("Error en JetpackCompose", "ERROR: ${e.localizedMessage}")
                }
            }
        }
    }

    private fun saveUser(username: String){
        val id = auth.currentUser?.uid
        val email = auth.currentUser?.email

        viewModelScope.launch (Dispatchers.IO) {
            val user = UserModel(
                userId = id.toString(),
                email = email.toString(),
                username = username
            )

            FirebaseFirestore.getInstance().collection("Users")
                .add(user)
                .addOnSuccessListener {
                    Log.d("GUARDADO CORRECTAMENTE", "Guardo correctamente")
                }
                .addOnFailureListener{
                    Log.d("ERROR AL GUARDAR", "ERROR AL GUARDAR EN FIRESTORE")
                }
        }
    }

    fun closeAlert() {
        showAlert = false
    }
}
