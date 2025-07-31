package com.example.pixelartdrawer

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class PixelArtView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        isAntiAlias = false // Для пиксельного эффекта
    }

    private val strokePaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 2f
        isAntiAlias = false
    }

    private var pixelSize = 8f
    private var centerX = 0f
    private var centerY = 0f
    private var scale = 1f
    private var rotation = 0f
    private var noiseLevel = 0.1f

    fun generateNewArt() {
        // Генерируем случайные параметры для вариаций
        pixelSize = Random.nextFloat() * 12f + 4f
        scale = Random.nextFloat() * 0.8f + 0.6f
        rotation = Random.nextFloat() * 0.2f - 0.1f
        noiseLevel = Random.nextFloat() * 0.3f + 0.05f
        
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Сохраняем состояние canvas
        canvas.save()
        
        // Применяем трансформации
        canvas.translate(centerX, centerY)
        canvas.scale(scale, scale)
        canvas.rotate(rotation * 360f)
        
        // Рисуем пиксельную версию картинки
        drawPixelArt(canvas)
        
        // Восстанавливаем состояние canvas
        canvas.restore()
    }

    private fun drawPixelArt(canvas: Canvas) {
        val baseSize = 200f
        val pixelBaseSize = pixelSize
        
        // Рисуем центральный элемент (лист/пламя)
        drawCentralElement(canvas, baseSize, pixelBaseSize)
        
        // Рисуем диагональные линии
        drawDiagonalLines(canvas, baseSize, pixelBaseSize)
        
        // Рисуем круговые элементы
        drawCircularElements(canvas, baseSize, pixelBaseSize)
    }

    private fun drawCentralElement(canvas: Canvas, baseSize: Float, pixelSize: Float) {
        val leafWidth = baseSize * 0.4f
        val leafHeight = baseSize * 0.6f
        
        // Добавляем случайные погрешности
        val noiseX = (Random.nextFloat() - 0.5f) * noiseLevel * leafWidth
        val noiseY = (Random.nextFloat() - 0.5f) * noiseLevel * leafHeight
        
        val path = Path()
        
        // Рисуем лист/пламя в пиксельном стиле
        val points = mutableListOf<PointF>()
        
        // Создаем точки для листа с пикселизацией
        for (i in 0..20) {
            val t = i / 20f
            val x = (t - 0.5f) * leafWidth + noiseX
            val y = if (t < 0.5f) {
                -leafHeight * 0.5f * (1f - 4f * (t - 0.5f) * (t - 0.5f)) + noiseY
            } else {
                -leafHeight * 0.3f * (1f - 4f * (t - 0.5f) * (t - 0.5f)) + noiseY
            }
            
            // Пикселизуем координаты
            val pixelX = (x / pixelSize).toInt() * pixelSize
            val pixelY = (y / pixelSize).toInt() * pixelSize
            
            points.add(PointF(pixelX, pixelY))
        }
        
        // Рисуем лист как набор прямоугольников
        for (i in 0 until points.size - 1) {
            val rect = RectF(
                points[i].x - pixelSize/2,
                points[i].y - pixelSize/2,
                points[i].x + pixelSize/2,
                points[i].y + pixelSize/2
            )
            canvas.drawRect(rect, paint)
        }
    }

    private fun drawDiagonalLines(canvas: Canvas, baseSize: Float, pixelSize: Float) {
        val lineLength = baseSize * 0.8f
        
        // Рисуем 4 диагональные линии
        val angles = listOf(45f, 135f, 225f, 315f)
        
        for (angle in angles) {
            val rad = Math.toRadians(angle.toDouble())
            val endX = (cos(rad) * lineLength).toFloat()
            val endY = (sin(rad) * lineLength).toFloat()
            
            // Добавляем случайные погрешности
            val noiseX = (Random.nextFloat() - 0.5f) * noiseLevel * lineLength
            val noiseY = (Random.nextFloat() - 0.5f) * noiseLevel * lineLength
            
            // Пикселизуем линию
            drawPixelatedLine(canvas, 0f, 0f, endX + noiseX, endY + noiseY, pixelSize)
        }
    }

    private fun drawPixelatedLine(canvas: Canvas, x1: Float, y1: Float, x2: Float, y2: Float, pixelSize: Float) {
        val dx = x2 - x1
        val dy = y2 - y1
        val steps = maxOf(kotlin.math.abs(dx / pixelSize), kotlin.math.abs(dy / pixelSize)).toInt()
        
        if (steps == 0) return
        
        val xStep = dx / steps
        val yStep = dy / steps
        
        for (i in 0..steps) {
            val x = x1 + i * xStep
            val y = y1 + i * yStep
            
            // Пикселизуем координаты
            val pixelX = (x / pixelSize).toInt() * pixelSize
            val pixelY = (y / pixelSize).toInt() * pixelSize
            
            val rect = RectF(
                pixelX - pixelSize/2,
                pixelY - pixelSize/2,
                pixelX + pixelSize/2,
                pixelY + pixelSize/2
            )
            canvas.drawRect(rect, paint)
        }
    }

    private fun drawCircularElements(canvas: Canvas, baseSize: Float, pixelSize: Float) {
        val circleRadius = baseSize * 0.15f
        val distance = baseSize * 0.4f
        
        // Позиции для 4 кругов
        val positions = listOf(
            PointF(distance, -distance),
            PointF(-distance, -distance),
            PointF(-distance, distance),
            PointF(distance, distance)
        )
        
        for (pos in positions) {
            // Добавляем случайные погрешности
            val noiseX = (Random.nextFloat() - 0.5f) * noiseLevel * distance
            val noiseY = (Random.nextFloat() - 0.5f) * noiseLevel * distance
            
            val centerX = pos.x + noiseX
            val centerY = pos.y + noiseY
            
            // Рисуем круг в пиксельном стиле
            drawPixelatedCircle(canvas, centerX, centerY, circleRadius, pixelSize)
            
            // Рисуем внутренний лист в круге
            drawInnerLeaf(canvas, centerX, centerY, circleRadius * 0.6f, pixelSize)
        }
    }

    private fun drawPixelatedCircle(canvas: Canvas, centerX: Float, centerY: Float, radius: Float, pixelSize: Float) {
        val steps = (radius * 2 / pixelSize).toInt()
        
        for (i in -steps..steps) {
            for (j in -steps..steps) {
                val x = centerX + i * pixelSize
                val y = centerY + j * pixelSize
                
                val distance = kotlin.math.sqrt((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY))
                
                if (distance <= radius && distance > radius - pixelSize) {
                    val rect = RectF(
                        x - pixelSize/2,
                        y - pixelSize/2,
                        x + pixelSize/2,
                        y + pixelSize/2
                    )
                    canvas.drawRect(rect, strokePaint)
                }
            }
        }
    }

    private fun drawInnerLeaf(canvas: Canvas, centerX: Float, centerY: Float, size: Float, pixelSize: Float) {
        val leafWidth = size * 0.6f
        val leafHeight = size * 0.8f
        
        // Добавляем случайные погрешности
        val noiseX = (Random.nextFloat() - 0.5f) * noiseLevel * leafWidth
        val noiseY = (Random.nextFloat() - 0.5f) * noiseLevel * leafHeight
        
        val points = mutableListOf<PointF>()
        
        // Создаем точки для внутреннего листа
        for (i in 0..15) {
            val t = i / 15f
            val x = centerX + (t - 0.5f) * leafWidth + noiseX
            val y = centerY + if (t < 0.5f) {
                -leafHeight * 0.5f * (1f - 4f * (t - 0.5f) * (t - 0.5f)) + noiseY
            } else {
                -leafHeight * 0.3f * (1f - 4f * (t - 0.5f) * (t - 0.5f)) + noiseY
            }
            
            // Пикселизуем координаты
            val pixelX = (x / pixelSize).toInt() * pixelSize
            val pixelY = (y / pixelSize).toInt() * pixelSize
            
            points.add(PointF(pixelX, pixelY))
        }
        
        // Рисуем внутренний лист
        for (i in 0 until points.size - 1) {
            val rect = RectF(
                points[i].x - pixelSize/2,
                points[i].y - pixelSize/2,
                points[i].x + pixelSize/2,
                points[i].y + pixelSize/2
            )
            canvas.drawRect(rect, paint)
        }
    }
}