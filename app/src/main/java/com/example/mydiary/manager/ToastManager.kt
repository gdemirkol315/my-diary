package com.example.mydiary.manager

import com.example.mydiary.state.ToastData
import com.example.mydiary.state.ToastType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object ToastManager {
    private val _toastFlow = MutableSharedFlow<ToastData>()
    val toastFlow = _toastFlow.asSharedFlow()

    suspend fun showToast(message: String, type: ToastType = ToastType.Success) {
        _toastFlow.emit(ToastData(message, type))
    }
}


data class ToastData(
    val message: String,
    val type: ToastType
)

enum class ToastType {
    Success, Error, Info
}
