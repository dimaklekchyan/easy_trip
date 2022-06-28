package ru.klekchyan.easytrip.base_ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ErrorPanel(
    text: String = "",
    onRetry: () -> Unit = {},
    retryText: String = ""
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Surface(
            elevation = 12.dp,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.requiredWidth(260.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.requiredHeight(16.dp))
                Text(
                    text = text,
                    style = TextStyle(fontSize = 16.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
                if (retryText.isNotEmpty()) {
                    Spacer(modifier = Modifier.requiredHeight(16.dp))
                    TextButton(onClick = onRetry) {
                        Text(text = retryText)
                    }
                }
            }
        }
    }
}