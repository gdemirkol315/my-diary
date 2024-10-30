package com.example.mydiary.manager

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object ToastManager {
    private val _toastFlow = MutableSharedFlow<ToastData?>()
    val toastFlow = _toastFlow.asSharedFlow()

    suspend fun showToast(message: String, type: ToastType = ToastType.Success) {
        _toastFlow.emit(ToastData(message, type))
    }

    suspend fun clearToast() {
        _toastFlow.emit(null)
    }
}

data class ToastData(
    val message: String,
    val type: ToastType
)

enum class ToastType {
    Success, Error, Info
}
