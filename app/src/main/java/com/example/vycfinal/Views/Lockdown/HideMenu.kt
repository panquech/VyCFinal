package com.example.vycfinal.Views.Lockdown

import android.content.ContentValues
import android.graphics.Bitmap
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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
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
import java.io.OutputStream
import android.graphics.BitmapFactory
import android.os.Environment
import android.provider.MediaStore


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HideMenu(navController: NavController, esteganografiaViewModel: EsteganografiaViewModel) {
    var cifrado by rememberSaveable { mutableStateOf("") }
    var uri by remember { mutableStateOf<Uri?>(null) }
    val singlePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { resultUri -> uri = resultUri }
    )
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Ocultar Mensaje") },
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
    )  { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { singlePhotoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                Text("Seleccionar Imagen")
            }
            AsyncImage(model = uri, contentDescription = null, modifier = Modifier.size(248.dp))
            OutlinedTextField(
                value = cifrado,
                onValueChange = { cifrado = it },
                label = { Text(text = "Texto: ") }
            )
            Button(onClick = {
                uri?.let { selectedUri ->
                    val inputStream = context.contentResolver.openInputStream(selectedUri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)

                    esteganografiaViewModel.hideTextInImage(bitmap, context, cifrado)
                } ?: run {
                    Toast.makeText(context, "Por favor selecciona una imagen.", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Ocultar Texto y Guardar")
            }
        }
    }
}