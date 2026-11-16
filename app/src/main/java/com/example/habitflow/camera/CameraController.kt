package com.example.habitflow.camera

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File

class CameraController(
    private val context: Context
) {

    private var cameraProvider:
            ProcessCameraProvider? = null

    private var imageCapture:
            ImageCapture? = null

    fun bind(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        lensFacing: Int,
        onError: (String) -> Unit
    ) {
        val providerFuture =
            ProcessCameraProvider
                .getInstance(context)

        providerFuture.addListener(
            {
                try {
                    val provider =
                        providerFuture.get()

                    cameraProvider = provider

                    val preview =
                        Preview.Builder()
                            .build()
                            .also { cameraPreview ->
                                cameraPreview
                                    .surfaceProvider =
                                    previewView
                                        .surfaceProvider
                            }

                    imageCapture =
                        ImageCapture.Builder()
                            .setCaptureMode(
                                ImageCapture
                                    .CAPTURE_MODE_MINIMIZE_LATENCY
                            )
                            .build()

                    val cameraSelector =
                        CameraSelector.Builder()
                            .requireLensFacing(
                                lensFacing
                            )
                            .build()

                    provider.unbindAll()

                    provider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                } catch (
                    exception: Exception
                ) {
                    onError(
                        exception.message
                            ?: "Unable to open camera."
                    )
                }
            },
            ContextCompat.getMainExecutor(
                context
            )
        )
    }

    fun takePhoto(
        onPhotoSaved: (Uri) -> Unit,
        onError: (String) -> Unit
    ) {
        val currentImageCapture =
            imageCapture

        if (currentImageCapture == null) {
            onError(
                "The camera is not ready."
            )
            return
        }

        val pictureDirectory =
            context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES
            ) ?: context.filesDir

        if (!pictureDirectory.exists()) {
            pictureDirectory.mkdirs()
        }

        val photoFile =
            File(
                pictureDirectory,
                "habit_${System.currentTimeMillis()}.jpg"
            )

        val outputOptions =
            ImageCapture.OutputFileOptions
                .Builder(photoFile)
                .build()

        currentImageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(
                context
            ),
            object :
                ImageCapture
                .OnImageSavedCallback {

                override fun onImageSaved(
                    outputFileResults:
                    ImageCapture
                    .OutputFileResults
                ) {
                    val savedUri =
                        outputFileResults.savedUri
                            ?: Uri.fromFile(
                                photoFile
                            )

                    onPhotoSaved(savedUri)
                }

                override fun onError(
                    exception:
                    ImageCaptureException
                ) {
                    onError(
                        exception.message
                            ?: "Unable to save photo."
                    )
                }
            }
        )
    }

    fun unbind() {
        cameraProvider?.unbindAll()
        cameraProvider = null
        imageCapture = null
    }
}