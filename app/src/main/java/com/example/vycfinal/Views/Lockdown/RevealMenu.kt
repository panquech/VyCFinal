package com.example.vycfinal.Views.Lockdown

import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.vycfinal.ViewModel.EsteganografiaViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RevealMenu(navController: NavController, esteganografiaViewModel: EsteganografiaViewModel) {
    var mensajeRevelado by rememberSaveable { mutableStateOf("") }
    var uri by remember { mutableStateOf<Uri?>(null) }
    val singlePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { resultUri -> uri = resultUri }
    )
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Extraer Mensaje") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("MainMenu") }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))
            Button(
                onClick = {
                    singlePhotoPicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp)
            ) {
                Text("Seleccionar Imagen")
            }
            Spacer(Modifier.height(16.dp))
            AsyncImage(model = uri, contentDescription = null, modifier = Modifier.size(248.dp))
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    uri?.let { selectedUri ->
                        val inputStream = context.contentResolver.openInputStream(selectedUri)
                        val bitmap = BitmapFactory.decodeStream(inputStream)

                        try {
                            // Revelar el mensaje utilizando el ViewModel
                            val mensaje = esteganografiaViewModel.revealTextFromImage(bitmap)
                            mensajeRevelado = mensaje

                            // Mostrar un mensaje si el texto se revela correctamente
                            if (mensajeRevelado.isNotEmpty()) {
                                Toast.makeText(
                                    context,
                                    "Mensaje revelado con éxito.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "No se encontró texto oculto en la imagen.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Error al revelar el mensaje: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } ?: run {
                        Toast.makeText(context, "Por favor selecciona una imagen.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp)
            ) {
                Text("Revelar Mensaje Oculto")
            }
            Spacer(Modifier.height(8.dp))
            if (mensajeRevelado.isNotEmpty()) {
                Text(
                    text = "Mensaje Revelado: $mensajeRevelado",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(Modifier.height(16.dp))

        }
    }
}