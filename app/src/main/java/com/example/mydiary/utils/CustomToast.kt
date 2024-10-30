package com.example.mydiary.utils


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.example.mydiary.state.ToastType
import kotlinx.coroutines.delay


@Composable
fun CustomToast(
    message: String,
    type: ToastType = ToastType.Success,
    duration: Long = 2000L,
    onDismiss: () -> Unit
) {
    var isVisible by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = message) {
        delay(duration)
        isVisible = false
        onDismiss()
    }

    if (isVisible) {
        Popup(
            alignment = Alignment.TopCenter
        ) {
            Surface(
                modifier = Modifier.padding(16.dp),
                shadowElevation = 8.dp,
                color = when (type) {
                    ToastType.Success -> Color(0xFF4CAF50)
                    ToastType.Error -> Color(0xFFE53935)
                    ToastType.Info -> Color(0xFF2196F3)
                }
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = when (type) {
                            ToastType.Success -> Icons.Default.CheckCircle
                            ToastType.Error -> Icons.Default.Clear
                            ToastType.Info -> Icons.Default.CheckCircle
                        },
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(
                        text = message,
                        color = Color.White
                    )
                }
            }
        }
    }
}