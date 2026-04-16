package com.example.escapegame.theme

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlin.random.Random

// ── Matrix rain view ──────────────────────────────────────────────────────────

private class MatrixRainView(context: Context) : View(context) {

    private val chars = "01ABCDEF0110"
    private val fontSize = 22f * resources.displayMetrics.density

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = Typeface.MONOSPACE
        textSize = fontSize
    }

    private val fadePaint = Paint().apply {
        style = Paint.Style.FILL
        color = android.graphics.Color.argb(13, 5, 15, 10)
    }

    private var offscreenBitmap: Bitmap? = null
    private var offscreenCanvas: Canvas? = null
    private var drops = IntArray(0)

    private val handler = Handler(Looper.getMainLooper())
    private val animRunnable = object : Runnable {
        override fun run() {
            drawFrame()
            invalidate()
            handler.postDelayed(this, 50)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        offscreenBitmap?.recycle()
        if (w > 0 && h > 0) {
            offscreenBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            offscreenCanvas = Canvas(offscreenBitmap!!)
            offscreenCanvas!!.drawColor(android.graphics.Color.argb(255, 5, 15, 10))
            drops = IntArray((w / fontSize).toInt().coerceAtLeast(1)) { 1 }
            val prePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = Typeface.MONOSPACE
                textSize = fontSize
            }
            val cols = (w / fontSize).toInt()
            val rows = (h / fontSize).toInt()
            for (col in 0 until cols) {
                for (row in 0 until rows) {
                    if (Random.nextFloat() > 0.65f) {
                        val a = ((Random.nextFloat() * 0.15f + 0.03f) * 255).toInt()
                        prePaint.color = android.graphics.Color.argb(a, 22, 156, 78)
                        offscreenCanvas!!.drawText(
                            chars[Random.nextInt(chars.length)].toString(),
                            col * fontSize, (row + 1) * fontSize, prePaint
                        )
                    }
                }
            }
        }
    }

    private fun drawFrame() {
        val canvas = offscreenCanvas ?: return
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), fadePaint)
        for (i in drops.indices) {
            val char = chars[Random.nextInt(chars.length)].toString()
            val alpha = Random.nextFloat() * 0.5f + 0.1f
            textPaint.color = android.graphics.Color.argb((alpha * 255).toInt(), 22, 156, 78)
            canvas.drawText(char, i * fontSize, drops[i] * fontSize, textPaint)
            if (drops[i] * fontSize > height && Random.nextFloat() > 0.975f) {
                drops[i] = 0
            }
            drops[i]++
        }
    }

    override fun onDraw(canvas: Canvas) {
        offscreenBitmap?.let { canvas.drawBitmap(it, 0f, 0f, null) }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        handler.post(animRunnable)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        handler.removeCallbacks(animRunnable)
    }
}

// ── Composable background ─────────────────────────────────────────────────────

@Composable
fun MissionControlBackground(content: @Composable BoxScope.() -> Unit) {

    val scanTransition = rememberInfiniteTransition(label = "scan")
    val scanY by scanTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(8_000, easing = LinearEasing)),
        label = "scan_y"
    )

    val gridColor = Color(0xFF16A04E).copy(alpha = 0.03f)
    val scanColor = Color(0xFF16A04E).copy(alpha = 0.02f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050F0A))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF050F0A),
                            Color(0xFF050F0A),
                            Color(0xFF0A1A10).copy(alpha = 0.30f)
                        )
                    )
                )
        )

        AndroidView(
            factory = { context ->
                MatrixRainView(context).apply { alpha = 0.30f }
            },
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    val spacing = 60.dp.toPx()
                    var x = 0f
                    while (x <= size.width) {
                        drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
                        x += spacing
                    }
                    var y = 0f
                    while (y <= size.height) {
                        drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
                        y += spacing
                    }

                    val lineY = scanY * size.height
                    drawRect(
                        color = scanColor,
                        topLeft = Offset(0f, lineY),
                        size = androidx.compose.ui.geometry.Size(size.width, 2.dp.toPx())
                    )
                }
        )

        Box(modifier = Modifier.fillMaxSize(), content = content)
    }
}