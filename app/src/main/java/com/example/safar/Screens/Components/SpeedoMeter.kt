package com.example.safar.Screens.Components

import SafarTheme
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Speedometer(
    modifier: Modifier = Modifier,
    currentSpeed: Int = 65,
    maxSpeed: Int = 180,
    arcColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
    speedColor: Color = MaterialTheme.colorScheme.primary,
    tickColor: Color = MaterialTheme.colorScheme.onSurface,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val startAngle = 135f
    val sweepAngle = 270f

    Box(
        modifier = Modifier
            .size(240.dp)
            .background(color = Color.LightGray, shape = CircleShape)
            .clip(CircleShape)
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2.2f
            val center = Offset(size.width / 2, size.height / 2)

            // Background arc
            drawArc(
                color = arcColor,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                style = Stroke(width = 70f, cap = StrokeCap.Round)
            )

            // Foreground arc (speed)
            val sweep = sweepAngle * (currentSpeed / maxSpeed.toFloat())
            drawArc(
                color = speedColor,
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                style = Stroke(width = 58f, cap = StrokeCap.Round)
            )

            // Draw ticks and numbers
            for (i in 0..maxSpeed step 10) {
                val angle = startAngle + (sweepAngle * i / maxSpeed.toFloat())
                val rad = Math.toRadians(angle.toDouble())

                val startX = center.x + (radius - 30) * cos(rad).toFloat()
                val startY = center.y + (radius - 30) * sin(rad).toFloat()
                val endX = center.x + radius * cos(rad).toFloat()
                val endY = center.y + radius * sin(rad).toFloat()

                drawLine(
                    color = tickColor,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = if (i % 20 == 0) 5f else 2f
                )

                // Draw number every 20 km/h
                if (i % 20 == 0) {
                    drawContext.canvas.nativeCanvas.apply {
                        val textPaint = Paint().apply {
                            color = textColor.toArgb()
                            textSize = 34f
                            textAlign = Paint.Align.CENTER
                            isAntiAlias = true
                        }

                        val textRadius = radius - 85
                        val textX = center.x + textRadius * cos(rad).toFloat()
                        val textY = center.y + textRadius * sin(rad).toFloat() + 12f

                        drawText("$i", textX, textY, textPaint)
                    }
                }
            }

            // Needle
            val needleAngle = startAngle + (sweepAngle * currentSpeed / maxSpeed.toFloat())
            val needleRad = Math.toRadians(needleAngle.toDouble())
            val needleEnd = Offset(
                x = center.x + (radius - radius / 3) * cos(needleRad).toFloat(),
                y = center.y + (radius - radius / 3) * sin(needleRad).toFloat()
            )

            drawLine(
                color = speedColor,
                start = center,
                end = needleEnd,
                strokeWidth = 24f,
                cap = StrokeCap.Round
            )
        }

        // Speed text (centered, slightly lower)
        Column(
            modifier = Modifier.padding(top = 110.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$currentSpeed",
                color = textColor,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "km/h",
                color = textColor,
                fontSize = 18.sp
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun SpeedometerPreview() {
//    SafarTheme {
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(24.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            Speedometer(currentSpeed = 65, maxSpeed = 180)
//        }
//    }
//}
