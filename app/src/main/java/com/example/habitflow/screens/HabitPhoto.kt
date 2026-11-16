package com.example.habitflow.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun HabitPhoto(
    imageUri: String?,
    modifier: Modifier = Modifier
) {
    val context =
        androidx.compose.ui.platform.LocalContext.current

    val bitmap = remember(imageUri) {
        imageUri?.let { uri ->
            loadBitmap(
                context = context,
                uriString = uri
            )
        }
    }

    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Habit photograph",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally,
                    verticalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector =
                            Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint =
                            MaterialTheme.colorScheme
                                .onSurfaceVariant
                    )

                    Text(
                        text = "No photo attached",
                        style =
                            MaterialTheme.typography.bodyMedium,
                        color =
                            MaterialTheme.colorScheme
                                .onSurfaceVariant,
                        modifier = Modifier.padding(
                            horizontal = 16.dp
                        )
                    )
                }
            }
        }
    }
}

private fun loadBitmap(
    context: Context,
    uriString: String
): Bitmap? {
    return try {
        val uri = Uri.parse(uriString)

        when (uri.scheme) {
            "file" -> {
                BitmapFactory.decodeFile(
                    uri.path
                )
            }

            "content" -> {
                context.contentResolver
                    .openInputStream(uri)
                    ?.use { inputStream ->
                        BitmapFactory.decodeStream(
                            inputStream
                        )
                    }
            }

            else -> {
                BitmapFactory.decodeFile(
                    uriString
                )
            }
        }
    } catch (exception: Exception) {
        null
    }
}