package com.example.parcialp2.pages

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.parcialp2.R
import com.example.parcialp2.ui.theme.ParcialP2Theme
import com.google.android.gms.location.LocationServices
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(){

    val context = LocalContext.current
    var camaraPermiso by remember { mutableStateOf(false) }
    var ubicacionPermiso by remember { mutableStateOf(false) }
    var grabarAudioPermiso by remember { mutableStateOf(false) }
    var grabando by remember { mutableStateOf(false) }


    //------------------------------------------------------------------CAMARA
    val camaraPermisoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        isGranted : Boolean ->
        camaraPermiso = isGranted
    }

    val tomarFotoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) {
        bitmap ->
        if(bitmap != null){
            Toast.makeText(context, "Foto tomada con exito", Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(context, "No se pudo tomar la foto", Toast.LENGTH_LONG).show()
        }
    }

    //------------------------------------------------------------------ UBICACION
    val ubicacionPermisoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        isGranted : Boolean ->
        ubicacionPermiso = isGranted
    }

    val locationClient = LocationServices.getFusedLocationProviderClient(context)

    //------------------------------------------------------------------ AUDIO

    var mediaRecorder : MediaRecorder? = remember { null }

    val grabarAudioPermisoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        isGranted ->
        grabarAudioPermiso = isGranted
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = {
                            (context as? Activity)?.finishAffinity()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close App"
                        )
                    }
                }
            )
        }
    ) {
        innerPadding ->
        Column(
            modifier = Modifier
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = 25.dp,
                    end = 25.dp,
                    bottom = innerPadding.calculateBottomPadding()
                )
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Column {
                ElevatedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if(camaraPermiso){
                            tomarFotoLauncher.launch(null)
                        }else{
                            camaraPermisoLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    elevation = ButtonDefaults
                        .elevatedButtonElevation(
                            defaultElevation = 10.dp
                        ),
                    colors = ButtonDefaults
                        .elevatedButtonColors(
                            containerColor = colorResource(R.color.btnContainer),
                            contentColor = colorResource(R.color.btnContent)
                        )
                ) {
                    Text(
                        text = if(camaraPermiso) "Tomar Foto" else "Acceso a camara",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = if(camaraPermiso) "Permiso concedido" else "Permiso denegado",
                    textAlign = TextAlign.Center,
                    fontSize = 18.5.sp,
                    fontWeight = FontWeight.W400
                )
            }
            Column {
                ElevatedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if(!ubicacionPermiso){
                            ubicacionPermisoLauncher
                                .launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }else{
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                try{
                                    locationClient.lastLocation.addOnSuccessListener { location ->
                                        if(location != null){
                                            val message =
                                                "Ubicacion ${location.latitude}, ${location.longitude}"
                                            Toast.makeText(context,message,Toast.LENGTH_LONG).show()
                                        }else{
                                            Toast
                                                .makeText(
                                                    context,
                                                    "No se pudo obtener la ubicacion",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                        }
                                    }
                                }catch (e : SecurityException){
                                    Toast.makeText(
                                        context,
                                        "Error de seguridad",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }else{
                                Toast.makeText(
                                    context,
                                    "No tiene permisos de ubicacion",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        }


                    },
                    elevation = ButtonDefaults
                        .elevatedButtonElevation(
                            defaultElevation = 10.dp
                        ),
                    colors = ButtonDefaults
                        .elevatedButtonColors(
                            containerColor = colorResource(R.color.btnContainer),
                            contentColor = colorResource(R.color.btnContent)
                        )
                ) {
                    Text(
                        text = if(ubicacionPermiso)
                            "Obtener ubicacion" else "Acceso a la ubicación",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = if(ubicacionPermiso) "Permiso concedido" else "Permiso denegado",
                    textAlign = TextAlign.Center,
                    fontSize = 18.5.sp,
                    fontWeight = FontWeight.W400
                )
            }
            Column {
                ElevatedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if(grabarAudioPermiso){
                            if(ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.RECORD_AUDIO
                            ) == PackageManager.PERMISSION_GRANTED){

                                if(grabando){
                                    mediaRecorder?.apply {
                                        stop()
                                        release()
                                    }
                                    mediaRecorder = null
                                    grabando = false
                                    Toast.makeText(
                                        context,
                                        "Grabacion detenida",
                                        Toast.LENGTH_LONG).show()
                                }else{
                                    mediaRecorder = MediaRecorder(context).apply {
                                        setAudioSource(MediaRecorder.AudioSource.MIC)
                                        setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                                        setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                                        setOutputFile("${context.filesDir}/${UUID.randomUUID()}.3gp")

                                        try{
                                            prepare()
                                            start()
                                            grabando = true
                                            Toast.makeText(
                                                context,
                                                "Grabacion iniciada",
                                                Toast.LENGTH_LONG).show()
                                        }catch (e : Exception){
                                            Toast.makeText(
                                                context,
                                                "Error al iniciar la grabacion",
                                                Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }

                            }else{
                                Toast.makeText(
                                    context,
                                    "No tiene permiso para grabar audio",
                                    Toast.LENGTH_LONG).show()
                            }
                        }else{
                            grabarAudioPermisoLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    },
                    elevation = ButtonDefaults
                        .elevatedButtonElevation(
                            defaultElevation = 10.dp
                        ),
                    colors = ButtonDefaults
                        .elevatedButtonColors(
                            containerColor = colorResource(R.color.btnContainer),
                            contentColor = colorResource(R.color.btnContent)
                        )
                ) {
                    Text(
                        text =
                        if(grabarAudioPermiso)
                            if(grabando) "Detener grabacion" else "Grabar audio"
                        else "Acceso a grabar audio",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = if(grabarAudioPermiso) "Permiso concedido" else "Permiso denegado",
                    textAlign = TextAlign.Center,
                    fontSize = 18.5.sp,
                    fontWeight = FontWeight.W400
                )
            }


        }
    }

}

@Preview(
    showBackground = true
)
@Composable
fun MainPageReview(){

    ParcialP2Theme(
        dynamicColor = false
    ) {
        MainPage()
    }

}
