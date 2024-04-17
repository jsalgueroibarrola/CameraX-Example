package com.jakurudev.cameraxexample

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.FLASH_MODE_ON
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

enum class ZoomLevel(val factor: Float) {
    X1(0f),
    X2(0.6f),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraProviderTab() {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val previewView = remember { PreviewView(context) }
    var isFlashEnabled by remember { mutableStateOf(false) }
    var isUsingFrontCamera by remember { mutableStateOf(false) }
    var zoom by remember { mutableStateOf(ZoomLevel.X1) }

    val imageCapture = remember {
        ImageCapture.Builder()
            .build()
    }

    LaunchedEffect(isUsingFrontCamera, zoom) {
        val cameraProvider = context.getCameraProvider()
        val lensFacing =
            if (isUsingFrontCamera) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        val preview =
            Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }

        cameraProvider.let {
            it.unbindAll()
            val camera = it.bindToLifecycle(lifecycleOwner, cameraSelector, imageCapture, preview)
            camera.cameraControl.setLinearZoom(zoom.factor)
        }
    }

    Scaffold(bottomBar = {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(onClick = {
                takePicture(
                    context = context,
                    imageCapture = imageCapture,
                    flashMode = if (isFlashEnabled) FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF,
                    onPhotoTaken = { image -> ImagesSingleton.images.add(image) }
                )
            }) {
                Text(text = "Take picture")
            }
        }
    }) {
        Column {
            Row(modifier = Modifier.padding(8.dp)) {
                CameraUIControls(
                    isFlashEnabled,
                    isUsingFrontCamera,
                    zoom
                ) { newFlashEnabled, newFrontCamera, newZoom ->
                    isFlashEnabled = newFlashEnabled
                    isUsingFrontCamera = newFrontCamera
                    zoom = newZoom
                }
            }
            AndroidView(
                factory = { previewView }, modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            )
        }
    }
}

@Composable
private fun CameraUIControls(
    isFlashEnabled: Boolean,
    isUsingFrontCamera: Boolean,
    zoom: ZoomLevel,
    updateStates: (Boolean, Boolean, ZoomLevel) -> Unit
) {
    Row(modifier = Modifier.padding(8.dp)) {
        IconButton(onClick = { updateStates(!isFlashEnabled, isUsingFrontCamera, zoom) }) {
            Icon(
                imageVector = if (isFlashEnabled) Icons.Filled.FlashOn else Icons.Filled.FlashOff,
                contentDescription = "Toggle Flash"
            )
        }
        IconButton(onClick = { updateStates(isFlashEnabled, !isUsingFrontCamera, zoom) }) {
            Icon(imageVector = Icons.Filled.FlipCameraAndroid, contentDescription = "Switch Camera")
        }
        Button(onClick = {
            val newZoom = when (zoom) {
                ZoomLevel.X1 -> ZoomLevel.X2
                ZoomLevel.X2 -> ZoomLevel.X1
            }
            updateStates(isFlashEnabled, isUsingFrontCamera, newZoom)
        }) {
            Text(text = zoom.name)
        }
    }
}

private fun takePicture(
    context: Context,
    imageCapture: ImageCapture,
    flashMode: Int,
    onPhotoTaken: (Bitmap) -> Unit
) {
    imageCapture.flashMode = flashMode

    imageCapture.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)

                val matrix = Matrix().apply {
                    postRotate(image.imageInfo.rotationDegrees.toFloat())
                }
                val rotatedBitmap = Bitmap.createBitmap(
                    image.toBitmap(),
                    0,
                    0,
                    image.width,
                    image.height,
                    matrix,
                    true
                )
                onPhotoTaken(rotatedBitmap)
            }
        }
    )
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }