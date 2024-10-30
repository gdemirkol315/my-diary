package com.example.mydiary.handler

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.example.mydiary.manager.ToastManager
import com.example.mydiary.utils.CustomToast
import kotlinx.coroutines.launch

@Composable
fun ToastHandler() {
    val scope = rememberCoroutineScope()
    val toastData by ToastManager.toastFlow.collectAsState(initial = null)

    toastData?.let { data ->
        CustomToast(
            message = data.message,
            type = data.type,
            onDismiss = {
                scope.launch {
                    ToastManager.clearToast()
                }
            }
        )
    }
}
