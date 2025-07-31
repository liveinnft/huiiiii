package com.example.pixelartdrawer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PixelArtView extends View {

    private Paint paint;
    private Paint strokePaint;
    private float pixelSize = 8f;
    private float centerX = 0f;
    private float centerY = 0f;
    private float scale = 1f;
    private float rotation = 0f;
    private float noiseLevel = 0.1f;
    private Random random = new Random();

    public PixelArtView(Context context) {
        super(context);
        init();
    }

    public PixelArtView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PixelArtView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(false); // Для пиксельного эффекта

        strokePaint = new Paint();
        strokePaint.setColor(Color.WHITE);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(2f);
        strokePaint.setAntiAlias(false);
    }

    public void generateNewArt() {
        // Генерируем случайные параметры для вариаций
        pixelSize = random.nextFloat() * 12f + 4f;
        scale = random.nextFloat() * 0.8f + 0.6f;
        rotation = random.nextFloat() * 0.2f - 0.1f;
        noiseLevel = random.nextFloat() * 0.3f + 0.05f;
        
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2f;
        centerY = h / 2f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // Сохраняем состояние canvas
        canvas.save();
        
        // Применяем трансформации
        canvas.translate(centerX, centerY);
        canvas.scale(scale, scale);
        canvas.rotate(rotation * 360f);
        
        // Рисуем пиксельную версию картинки
        drawPixelArt(canvas);
        
        // Восстанавливаем состояние canvas
        canvas.restore();
    }

    private void drawPixelArt(Canvas canvas) {
        float baseSize = 200f;
        float pixelBaseSize = pixelSize;
        
        // Рисуем центральный элемент (лист/пламя)
        drawCentralElement(canvas, baseSize, pixelBaseSize);
        
        // Рисуем диагональные линии
        drawDiagonalLines(canvas, baseSize, pixelBaseSize);
        
        // Рисуем круговые элементы
        drawCircularElements(canvas, baseSize, pixelBaseSize);
    }

    private void drawCentralElement(Canvas canvas, float baseSize, float pixelSize) {
        float leafWidth = baseSize * 0.4f;
        float leafHeight = baseSize * 0.6f;
        
        // Добавляем случайные погрешности
        float noiseX = (random.nextFloat() - 0.5f) * noiseLevel * leafWidth;
        float noiseY = (random.nextFloat() - 0.5f) * noiseLevel * leafHeight;
        
        // Рисуем лист/пламя в пиксельном стиле
        List<PointF> points = new ArrayList<>();
        
        // Создаем точки для листа с пикселизацией
        for (int i = 0; i <= 20; i++) {
            float t = i / 20f;
            float x = (t - 0.5f) * leafWidth + noiseX;
            float y;
            if (t < 0.5f) {
                y = -leafHeight * 0.5f * (1f - 4f * (t - 0.5f) * (t - 0.5f)) + noiseY;
            } else {
                y = -leafHeight * 0.3f * (1f - 4f * (t - 0.5f) * (t - 0.5f)) + noiseY;
            }
            
            // Пикселизуем координаты
            float pixelX = (float) Math.floor(x / pixelSize) * pixelSize;
            float pixelY = (float) Math.floor(y / pixelSize) * pixelSize;
            
            points.add(new PointF(pixelX, pixelY));
        }
        
        // Рисуем лист как набор прямоугольников
        for (int i = 0; i < points.size() - 1; i++) {
            PointF point = points.get(i);
            RectF rect = new RectF(
                point.x - pixelSize/2,
                point.y - pixelSize/2,
                point.x + pixelSize/2,
                point.y + pixelSize/2
            );
            canvas.drawRect(rect, paint);
        }
    }

    private void drawDiagonalLines(Canvas canvas, float baseSize, float pixelSize) {
        float lineLength = baseSize * 0.8f;
        
        // Рисуем 4 диагональные линии
        float[] angles = {45f, 135f, 225f, 315f};
        
        for (float angle : angles) {
            double rad = Math.toRadians(angle);
            float endX = (float) (Math.cos(rad) * lineLength);
            float endY = (float) (Math.sin(rad) * lineLength);
            
            // Добавляем случайные погрешности
            float noiseX = (random.nextFloat() - 0.5f) * noiseLevel * lineLength;
            float noiseY = (random.nextFloat() - 0.5f) * noiseLevel * lineLength;
            
            // Пикселизуем линию
            drawPixelatedLine(canvas, 0f, 0f, endX + noiseX, endY + noiseY, pixelSize);
        }
    }

    private void drawPixelatedLine(Canvas canvas, float x1, float y1, float x2, float y2, float pixelSize) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        int steps = (int) Math.max(Math.abs(dx / pixelSize), Math.abs(dy / pixelSize));
        
        if (steps == 0) return;
        
        float xStep = dx / steps;
        float yStep = dy / steps;
        
        for (int i = 0; i <= steps; i++) {
            float x = x1 + i * xStep;
            float y = y1 + i * yStep;
            
            // Пикселизуем координаты
            float pixelX = (float) Math.floor(x / pixelSize) * pixelSize;
            float pixelY = (float) Math.floor(y / pixelSize) * pixelSize;
            
            RectF rect = new RectF(
                pixelX - pixelSize/2,
                pixelY - pixelSize/2,
                pixelX + pixelSize/2,
                pixelY + pixelSize/2
            );
            canvas.drawRect(rect, paint);
        }
    }

    private void drawCircularElements(Canvas canvas, float baseSize, float pixelSize) {
        float circleRadius = baseSize * 0.15f;
        float distance = baseSize * 0.4f;
        
        // Позиции для 4 кругов
        PointF[] positions = {
            new PointF(distance, -distance),
            new PointF(-distance, -distance),
            new PointF(-distance, distance),
            new PointF(distance, distance)
        };
        
        for (PointF pos : positions) {
            // Добавляем случайные погрешности
            float noiseX = (random.nextFloat() - 0.5f) * noiseLevel * distance;
            float noiseY = (random.nextFloat() - 0.5f) * noiseLevel * distance;
            
            float centerX = pos.x + noiseX;
            float centerY = pos.y + noiseY;
            
            // Рисуем круг в пиксельном стиле
            drawPixelatedCircle(canvas, centerX, centerY, circleRadius, pixelSize);
            
            // Рисуем внутренний лист в круге
            drawInnerLeaf(canvas, centerX, centerY, circleRadius * 0.6f, pixelSize);
        }
    }

    private void drawPixelatedCircle(Canvas canvas, float centerX, float centerY, float radius, float pixelSize) {
        int steps = (int) (radius * 2 / pixelSize);
        
        for (int i = -steps; i <= steps; i++) {
            for (int j = -steps; j <= steps; j++) {
                float x = centerX + i * pixelSize;
                float y = centerY + j * pixelSize;
                
                float distance = (float) Math.sqrt((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY));
                
                if (distance <= radius && distance > radius - pixelSize) {
                    RectF rect = new RectF(
                        x - pixelSize/2,
                        y - pixelSize/2,
                        x + pixelSize/2,
                        y + pixelSize/2
                    );
                    canvas.drawRect(rect, strokePaint);
                }
            }
        }
    }

    private void drawInnerLeaf(Canvas canvas, float centerX, float centerY, float size, float pixelSize) {
        float leafWidth = size * 0.6f;
        float leafHeight = size * 0.8f;
        
        // Добавляем случайные погрешности
        float noiseX = (random.nextFloat() - 0.5f) * noiseLevel * leafWidth;
        float noiseY = (random.nextFloat() - 0.5f) * noiseLevel * leafHeight;
        
        List<PointF> points = new ArrayList<>();
        
        // Создаем точки для внутреннего листа
        for (int i = 0; i <= 15; i++) {
            float t = i / 15f;
            float x = centerX + (t - 0.5f) * leafWidth + noiseX;
            float y;
            if (t < 0.5f) {
                y = centerY - leafHeight * 0.5f * (1f - 4f * (t - 0.5f) * (t - 0.5f)) + noiseY;
            } else {
                y = centerY - leafHeight * 0.3f * (1f - 4f * (t - 0.5f) * (t - 0.5f)) + noiseY;
            }
            
            // Пикселизуем координаты
            float pixelX = (float) Math.floor(x / pixelSize) * pixelSize;
            float pixelY = (float) Math.floor(y / pixelSize) * pixelSize;
            
            points.add(new PointF(pixelX, pixelY));
        }
        
        // Рисуем внутренний лист
        for (int i = 0; i < points.size() - 1; i++) {
            PointF point = points.get(i);
            RectF rect = new RectF(
                point.x - pixelSize/2,
                point.y - pixelSize/2,
                point.x + pixelSize/2,
                point.y + pixelSize/2
            );
            canvas.drawRect(rect, paint);
        }
    }
}