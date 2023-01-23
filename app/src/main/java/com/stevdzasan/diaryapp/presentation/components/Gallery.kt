package com.stevdzasan.diaryapp.presentation.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.stevdzasan.diaryapp.model.GalleryImage
import com.stevdzasan.diaryapp.model.GalleryState
import com.stevdzasan.diaryapp.ui.theme.Elevation
import kotlin.math.max

@Composable
fun Gallery(
    modifier: Modifier = Modifier,
    images: List<Uri>,
    imageSize: Dp = 40.dp,
    spaceBetween: Dp = 10.dp,
    imageShape: CornerBasedShape = Shapes().small
) {
    BoxWithConstraints(modifier = modifier) {
        val numberOfVisibleImages = remember {
            derivedStateOf {
                max(
                    a = 0,
                    b = this.maxWidth.div(spaceBetween + imageSize).toInt().minus(1)
                )
            }
        }

        val remainingImages = remember {
            derivedStateOf {
                images.size - numberOfVisibleImages.value
            }
        }

        Row {
            images.take(numberOfVisibleImages.value).forEach { image ->
                AsyncImage(
                    modifier = Modifier
                        .clip(imageShape)
                        .size(imageSize),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(image)
                        .crossfade(true)
                        .build(),
                    contentScale = ContentScale.Crop,
                    contentDescription = "Gallery Image"
                )
                Spacer(modifier = Modifier.width(spaceBetween))
            }
            if (remainingImages.value > 0) {
                LastImageOverlay(
                    imageSize = imageSize,
                    imageShape = imageShape,
                    remainingImages = remainingImages.value
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryUploader(
    modifier: Modifier = Modifier,
    galleryState: GalleryState,
    imageSize: Dp = 60.dp,
    imageShape: CornerBasedShape = Shapes().medium,
    spaceBetween: Dp = 12.dp,
    onAddClicked: () -> Unit,
    onImageSelect: (Uri) -> Unit,
    onImageClicked: (GalleryImage) -> Unit,
) {
    val multiplePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 8),
    ) { images ->
        images.forEach {
            onImageSelect(it)
        }
    }

    BoxWithConstraints(modifier = modifier) {
        val numberOfVisibleImages = remember {
            derivedStateOf {
                max(
                    a = 0,
                    b = this.maxWidth.div(spaceBetween + imageSize).toInt().minus(2)
                )
            }
        }

        val remainingImages = remember {
            derivedStateOf {
                galleryState.images.size - numberOfVisibleImages.value
            }
        }

        Row {
            AddImageButton(
                imageSize = imageSize,
                imageShape = imageShape,
                onClick = {
                    onAddClicked()
                    multiplePhotoPicker.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                }
            )
            Spacer(modifier = Modifier.width(spaceBetween))
            galleryState.images.take(numberOfVisibleImages.value).forEach { galleryImage ->
                AsyncImage(
                    modifier = Modifier
                        .clip(imageShape)
                        .size(imageSize)
                        .clickable { onImageClicked(galleryImage) },
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(galleryImage.image)
                        .crossfade(true)
                        .build(),
                    contentScale = ContentScale.Crop,
                    contentDescription = "Gallery Image"
                )
                Spacer(modifier = Modifier.width(spaceBetween))
            }
            if (remainingImages.value > 0) {
                LastImageOverlay(
                    imageSize = imageSize,
                    imageShape = imageShape,
                    remainingImages = remainingImages.value
                )
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun AddImageButton(
    imageSize: Dp,
    imageShape: CornerBasedShape,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(imageSize)
            .clip(shape = imageShape),
        onClick = onClick,
        tonalElevation = Elevation.Level1
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Icon",
            )
        }
    }
}

@Composable
fun LastImageOverlay(
    imageSize: Dp,
    imageShape: CornerBasedShape,
    remainingImages: Int
) {
    Box(contentAlignment = Alignment.Center) {
        Surface(
            modifier = Modifier
                .clip(imageShape)
                .size(imageSize),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {}
        Text(
            text = "+$remainingImages",
            style = TextStyle(
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}