package com.example.focusonplus

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetector
import java.util.concurrent.Executors

private const val TAG = "CameraPreview"

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun CameraPreview(
    context: Context,
    detector: FaceDetector,
    onFaceDetected: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val executor = remember { Executors.newSingleThreadExecutor() }
    
    // Store preview and analysis use cases
    val previewUseCase = remember { mutableStateOf<Preview?>(null) }
    val analysisUseCase = remember { mutableStateOf<ImageAnalysis?>(null) }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
                implementationMode = PreviewView.ImplementationMode.PERFORMANCE
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                setBackgroundColor(Color.BLACK)
            }

            previewView
        },
    modifier = modifier,
    update = { view ->
        view.visibility = android.view.View.VISIBLE
        
        // Bind camera when view is ready
        if (previewUseCase.value == null) {
            view.post {
                cameraProviderFuture.addListener({
                    try {
                        val cameraProvider = cameraProviderFuture.get()
                        
                        if (cameraProvider == null) {
                            Log.e(TAG, "CameraProvider is null")
                            return@addListener
                        }

                        // Create preview use case with optimal settings for smooth preview
                        val preview = Preview.Builder()
                            .setTargetResolution(android.util.Size(1280, 720)) // HD resolution for smooth preview
                            .build()
                            .also {
                                it.setSurfaceProvider(view.surfaceProvider)
                            }
                        previewUseCase.value = preview

                        // Create analysis use case with reduced resolution and lower frequency for smooth preview
                        val analysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                            .setTargetResolution(android.util.Size(320, 240)) // Very low resolution for minimal impact on preview
                            .build()
                            .also { analyzer ->
                                var frameSkip = 0
                                analyzer.setAnalyzer(executor) { imageProxy ->
                                    // Skip more frames - only process every 5th frame to keep preview smooth
                                    frameSkip++
                                    if (frameSkip % 5 == 0) {
                                        processImage(detector, imageProxy, onFaceDetected)
                                    } else {
                                        imageProxy.close()
                                    }
                                }
                            }
                        analysisUseCase.value = analysis

                        // Try front camera first (MacBook webcam), then back camera
                        val selectors = listOf(
                            CameraSelector.DEFAULT_FRONT_CAMERA,
                            CameraSelector.DEFAULT_BACK_CAMERA
                        )
                        
                        var cameraBound = false
                        for (selector in selectors) {
                            try {
                                if (cameraProvider.hasCamera(selector)) {
                                    cameraProvider.unbindAll()
                                    cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        selector,
                                        preview,
                                        analysis
                                    )
                                    cameraBound = true
                                    val cameraType = if (selector == CameraSelector.DEFAULT_FRONT_CAMERA) "FRONT" else "BACK"
                                    Log.d(TAG, "✅ Camera bound successfully: $cameraType camera")
                                    break
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Failed to bind camera", e)
                            }
                        }
                        
                        if (!cameraBound) {
                            Log.e(TAG, "❌ No camera available")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Camera initialization failed", e)
                        e.printStackTrace()
                    }
                }, ContextCompat.getMainExecutor(view.context))
            }
        }
    }
)
    
    // Cleanup when composable is disposed
    DisposableEffect(Unit) {
        onDispose {
            try {
                val cameraProvider = cameraProviderFuture.get()
                cameraProvider?.unbindAll()
                executor.shutdown()
                Log.d(TAG, "Camera resources cleaned up")
            } catch (e: Exception) {
                Log.e(TAG, "Error cleaning up camera", e)
            }
        }
    }
}

// Simple debounce state to prevent rapid callbacks
private var lastDetectionTime = 0L
private var lastKnownState = false
private const val DEBOUNCE_DELAY_MS = 200L // Update state every 200ms max

@androidx.camera.core.ExperimentalGetImage
fun processImage(
    detector: FaceDetector,
    imageProxy: ImageProxy,
    onFaceDetected: (Boolean) -> Unit
) {
    try {
        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return
        }

        val image = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )

        detector.process(image)
            .addOnSuccessListener { faces ->
                try {
                    val detected = faces.isNotEmpty()
                    val currentTime = System.currentTimeMillis()
                    
                    // Always update if state changed, or if enough time has passed
                    if (detected != lastKnownState || (currentTime - lastDetectionTime) > DEBOUNCE_DELAY_MS) {
                        lastKnownState = detected
                        lastDetectionTime = currentTime
                        onFaceDetected(detected)
                    }
                } catch (e: Exception) {
                    // Ignore callback errors
                }
            }
            .addOnFailureListener {
                try {
                    val currentTime = System.currentTimeMillis()
                    // Always update failure state if enough time has passed
                    if ((currentTime - lastDetectionTime) > DEBOUNCE_DELAY_MS) {
                        lastKnownState = false
                        lastDetectionTime = currentTime
                        onFaceDetected(false)
                    }
                } catch (e: Exception) {
                    // Ignore callback errors
                }
            }
            .addOnCompleteListener {
                try {
                    imageProxy.close()
                } catch (e: Exception) {
                    // Ignore close errors
                }
            }
    } catch (e: Exception) {
        try {
            imageProxy.close()
        } catch (ex: Exception) {
            // Ignore close errors
        }
    }
}
