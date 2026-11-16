package com.example.habitflow.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.habitflow.camera.CameraController
import com.example.habitflow.camera.CameraViewModel
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    cameraViewModel: CameraViewModel,
    onBack: () -> Unit,
    onPhotoCaptured: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context =
        LocalContext.current

    val lifecycleOwner =
        LocalLifecycleOwner.current

    val cameraState by
    cameraViewModel
        .uiState
        .collectAsStateWithLifecycle()

    val cameraController =
        remember {
            CameraController(
                context.applicationContext
            )
        }

    val previewView =
        remember {
            PreviewView(context).apply {
                scaleType =
                    PreviewView.ScaleType.FILL_CENTER

                implementationMode =
                    PreviewView
                        .ImplementationMode
                        .COMPATIBLE
            }
        }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts
                    .RequestPermission()
        ) { isGranted ->

            cameraViewModel
                .onPermissionResult(
                    isGranted
                )

            if (!isGranted) {
                Toast.makeText(
                    context,
                    "Camera permission was denied.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    LaunchedEffect(Unit) {
        val permissionAlreadyGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

        if (permissionAlreadyGranted) {
            cameraViewModel
                .onPermissionResult(true)
        } else {
            permissionLauncher.launch(
                Manifest.permission.CAMERA
            )
        }
    }

    LaunchedEffect(
        cameraState.hasCameraPermission,
        cameraState.lensFacing
    ) {
        if (
            cameraState.hasCameraPermission
        ) {
            cameraController.bind(
                lifecycleOwner =
                    lifecycleOwner,
                previewView =
                    previewView,
                lensFacing =
                    cameraState.lensFacing,
                onError = { message ->
                    Toast.makeText(
                        context,
                        message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraController.unbind()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text("Habit photo")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector =
                                Icons.Default.ArrowBack,
                            contentDescription =
                                "Go back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (
                cameraState
                    .hasCameraPermission
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement =
                        Arrangement.spacedBy(
                            12.dp
                        )
                ) {
                    Button(
                        onClick = {
                            cameraViewModel
                                .flipCamera()
                        },
                        modifier =
                            Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector =
                                Icons.Default.Cached,
                            contentDescription = null
                        )

                        Text(" Flip")
                    }

                    Button(
                        onClick = {
                            cameraController
                                .takePhoto(
                                    onPhotoSaved = {
                                            uri ->

                                        Toast.makeText(
                                            context,
                                            "Photo captured.",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        onPhotoCaptured(
                                            uri.toString()
                                        )
                                    },
                                    onError = {
                                            message ->

                                        Toast.makeText(
                                            context,
                                            message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                )
                        },
                        modifier =
                            Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector =
                                Icons.Default.CameraAlt,
                            contentDescription = null
                        )

                        Text(" Capture")
                    }
                }
            }
        }
    ) { innerPadding ->

        if (
            cameraState.hasCameraPermission
        ) {
            AndroidView(
                factory = {
                    previewView
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment =
                    Alignment.Center
            ) {
                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {
                    Text(
                        text =
                            "Camera permission is required to attach a habit photo.",
                        style =
                            MaterialTheme.typography.bodyLarge
                    )

                    Button(
                        onClick = {
                            permissionLauncher.launch(
                                Manifest.permission.CAMERA
                            )
                        },
                        modifier =
                            Modifier.padding(
                                top = 16.dp
                            )
                    ) {
                        Text(
                            "Allow camera"
                        )
                    }
                }
            }
        }
    }
}