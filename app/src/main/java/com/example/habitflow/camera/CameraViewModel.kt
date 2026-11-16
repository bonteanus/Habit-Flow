package com.example.habitflow.camera

import androidx.camera.core.CameraSelector
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class CameraViewModel : ViewModel() {

    private val _uiState =
        MutableStateFlow(CameraUiState())

    val uiState: StateFlow<CameraUiState> =
        _uiState

    fun onPermissionResult(
        isGranted: Boolean
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                hasCameraPermission =
                    isGranted
            )
        }
    }

    fun flipCamera() {
        _uiState.update { currentState ->

            val newLensFacing =
                if (
                    currentState.lensFacing ==
                    CameraSelector.LENS_FACING_BACK
                ) {
                    CameraSelector.LENS_FACING_FRONT
                } else {
                    CameraSelector.LENS_FACING_BACK
                }

            currentState.copy(
                lensFacing = newLensFacing
            )
        }
    }
}