package com.example.safar.Screens.Components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.cos
import kotlin.math.sin
import android.graphics.Paint
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.drawText
import kotlin.math.min


@Composable
fun Speedometer(
    speed: Int,
    maxSpeed: Int = 140,
    modifier: Modifier = Modifier,
    size: Dp = 300.dp,
    needleColor: Color = Color.Cyan,
    textColor: Color = Color.White
) {
    val angleStart = 135f
    val angleEnd = 405f
    val sweepAngle = angleEnd - angleStart


    val animatedSpeed by animateFloatAsState(
        targetValue = speed.toFloat(),
        animationSpec = tween(durationMillis = 1000),
        label = ""
    )

    Box(
        modifier = modifier
            .size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size / 2.2f
            val center = this.center

            // Draw ticks and numbers
            for (i in 0..14) {
                val angle = Math.toRadians((angleStart + (i * (sweepAngle / 14))).toDouble())
                val lineLength = if (i % 2 == 0) 20f else 10f

                val startX = center.x + cos(angle) * (radius - lineLength)
                val startY = center.y + sin(angle) * (radius - lineLength)
                val endX = center.x + cos(angle) * radius
                val endY = center.y + sin(angle) * radius

                drawLine(
                    color = Color.White,
                    start = Offset(startX.toFloat(), startY.toFloat()),
                    end = Offset(endX.toFloat(), endY.toFloat()),
                    strokeWidth = 3f
                )

                if (i % 2 == 0) {
                    val label = (i * 10).toString()
                    val textAngle = angle
                    val labelX = center.x + cos(textAngle) * (radius - 40f)
                    val labelY = center.y + sin(textAngle) * (radius - 40f)

                    drawContext.canvas.nativeCanvas.apply {
                        drawText(
                            label,
                            labelX.toFloat(),
                            labelY.toFloat(),
                            Paint().apply {
                                color = android.graphics.Color.WHITE
                                textAlign = Paint.Align.CENTER
                                textSize = 28f
                                isAntiAlias = true
                            }
                        )
                    }
                }
            }

            // Draw needle
            val needleAngle = angleStart + (animatedSpeed / maxSpeed) * sweepAngle
            val needleRadians = Math.toRadians(needleAngle.toDouble())
            val needleLength = radius - 40f

            val needleX = center.x + cos(needleRadians) * needleLength
            val needleY = center.y + sin(needleRadians) * needleLength

            drawLine(
                color = needleColor,
                start = center,
                end = Offset(needleX.toFloat(), needleY.toFloat()),
                strokeWidth = 6f,
                cap = StrokeCap.Round
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = speed.toString(),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                text = "km/h",
                fontSize = 16.sp,
                color = textColor.copy(alpha = 0.8f)
            )
        }
    }
}


@Composable
@Preview(showBackground = true, backgroundColor = 0xFF101010)
fun SpeedometerPreview() {
    Speedometer(speed = 28)
}