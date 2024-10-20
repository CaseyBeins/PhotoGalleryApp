package com.example.photogalleryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.photogalleryapp.ui.theme.PhotoGalleryAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PhotoGalleryAppTheme {
                PhotoGalleryScreen()
            }
        }
    }
}

@Composable
fun PhotoGalleryScreen() {
    val images = listOf(
        R.drawable.nature1,
        R.drawable.nature2,
        R.drawable.nature3,
        R.drawable.nature4,
        R.drawable.nature5
    )

    var selectedImage by remember { mutableStateOf<Int?>(null) }

    if (selectedImage != null) {
        FullScreenImageViewer(
            images = images,
            initialImageIndex = images.indexOf(selectedImage!!),
            onClose = { selectedImage = null }
        )
    } else {
        ThumbnailGrid(images = images) { clickedImage ->
            selectedImage = clickedImage
        }
    }
}

@Composable
fun ThumbnailGrid(
    images: List<Int>,
    onImageClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(images) { image ->
            Image(
                painter = painterResource(id = image),
                contentDescription = null,
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                modifier = Modifier
                    .aspectRatio(1f)
                    .clickable { onImageClick(image) }
                    .padding(4.dp)
            )
        }
    }
}

@Composable
fun FullScreenImageViewer(
    images: List<Int>,
    initialImageIndex: Int,
    onClose: () -> Unit
) {
    var currentIndex by remember { mutableStateOf(initialImageIndex) }
    var offsetX by remember { mutableStateOf(0f) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = images[currentIndex]),
            contentDescription = null,
            contentScale = androidx.compose.ui.layout.ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            if (offsetX > 200 && currentIndex > 0) {
                                // Swipe right to previous image
                                currentIndex--
                            } else if (offsetX < -200 && currentIndex < images.size - 1) {
                                // Swipe left to next image
                                currentIndex++
                            }
                            offsetX = 0f // Reset after swipe
                        },
                        onDrag = { change, dragAmount ->
                            offsetX += dragAmount.x
                            change.consume() // Consume the gesture event
                        }
                    )
                }
        )

        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
        }
    }
}
