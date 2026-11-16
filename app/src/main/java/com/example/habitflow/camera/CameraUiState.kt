package com.example.habitflow.camera

import androidx.camera.core.CameraSelector

data class CameraUiState(
    val hasCameraPermission: Boolean = false,
    val lensFacing: Int =
        CameraSelector.LENS_FACING_BACK
)