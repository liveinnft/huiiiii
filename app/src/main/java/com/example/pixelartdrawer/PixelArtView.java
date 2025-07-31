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
    
    // Цвета для разноцветного рисования
    private int[] colors = {
        Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, 
        Color.MAGENTA, Color.CYAN, Color.rgb(255, 165, 0), Color.rgb(255, 192, 203),
        Color.LTGRAY, Color.DKGRAY, Color.WHITE
    };

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
        
        // Рисуем пиксельную версию изображения
        drawPixelArt(canvas);
        
        // Восстанавливаем состояние canvas
        canvas.restore();
    }

    private void drawPixelArt(Canvas canvas) {
        float baseSize = 200f;
        
        // Рисуем два соединенных круга внизу
        drawConnectedCircles(canvas, baseSize);
        
        // Рисуем палку, идущую от места соединения кругов
        drawStick(canvas, baseSize);
    }

    private void drawConnectedCircles(Canvas canvas, float baseSize) {
        float circleRadius = baseSize * 0.25f; // Увеличил с 0.15f до 0.25f
        float circleDistance = baseSize * 0.35f; // Увеличил с 0.25f до 0.35f - расстояние между центрами кругов
        
        // Добавляем случайные погрешности
        float noiseX1 = (random.nextFloat() - 0.5f) * noiseLevel * circleRadius;
        float noiseY1 = (random.nextFloat() - 0.5f) * noiseLevel * circleRadius;
        float noiseX2 = (random.nextFloat() - 0.5f) * noiseLevel * circleRadius;
        float noiseY2 = (random.nextFloat() - 0.5f) * noiseLevel * circleRadius;
        
        // Выбираем случайные цвета для кругов
        int circleColor1 = colors[random.nextInt(colors.length)];
        int circleColor2 = colors[random.nextInt(colors.length)];
        
        // Левый круг
        paint.setColor(circleColor1);
        drawPixelatedCircle(canvas, -circleDistance/2 + noiseX1, baseSize * 0.3f + noiseY1, circleRadius, paint);
        
        // Правый круг
        paint.setColor(circleColor2);
        drawPixelatedCircle(canvas, circleDistance/2 + noiseX2, baseSize * 0.3f + noiseY2, circleRadius, paint);
    }

    private void drawStick(Canvas canvas, float baseSize) {
        float stickWidth = baseSize * 0.15f; // Увеличил с 0.08f до 0.15f
        float stickHeight = baseSize * 0.8f; // Увеличил с 0.6f до 0.8f
        
        // Добавляем случайные погрешности
        float noiseX = (random.nextFloat() - 0.5f) * noiseLevel * stickWidth;
        float noiseY = (random.nextFloat() - 0.5f) * noiseLevel * stickHeight;
        
        // Выбираем случайный цвет для палки
        int stickColor = colors[random.nextInt(colors.length)];
        paint.setColor(stickColor);
        
        // Рисуем палку как прямоугольник с закругленными краями
        float stickX = noiseX;
        float stickY = -baseSize * 0.2f + noiseY; // Начинается от места соединения кругов
        
        // Рисуем основную часть палки
        drawPixelatedRectangle(canvas, stickX, stickY, stickWidth, stickHeight, paint);
        
        // Рисуем закругленный верх палки
        float topRadius = stickWidth / 2f;
        drawPixelatedCircle(canvas, stickX, stickY - stickHeight/2, topRadius, paint);
    }

    private void drawPixelatedRectangle(Canvas canvas, float centerX, float centerY, 
                                     float width, float height, Paint paint) {
        int stepsX = (int) (width / pixelSize);
        int stepsY = (int) (height / pixelSize);
        
        for (int i = -stepsX/2; i <= stepsX/2; i++) {
            for (int j = -stepsY/2; j <= stepsY/2; j++) {
                float x = centerX + i * pixelSize;
                float y = centerY + j * pixelSize;
                
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
    }

    private void drawPixelatedCircle(Canvas canvas, float centerX, float centerY, float radius, Paint paint) {
        int steps = (int) (radius * 2 / pixelSize);
        
        for (int i = -steps; i <= steps; i++) {
            for (int j = -steps; j <= steps; j++) {
                float x = centerX + i * pixelSize;
                float y = centerY + j * pixelSize;
                
                float distance = (float) Math.sqrt((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY));
                
                if (distance <= radius) {
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
        }
    }
}