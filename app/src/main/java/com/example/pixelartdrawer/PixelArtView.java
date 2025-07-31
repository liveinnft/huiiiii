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
        
        // Рисуем основную форму
        drawMainShape(canvas, baseSize);
        
        // Рисуем детали
        drawDetails(canvas, baseSize);
    }

    private void drawMainShape(Canvas canvas, float baseSize) {
        float mainWidth = baseSize * 0.3f;
        float mainHeight = baseSize * 0.8f;
        
        // Добавляем случайные погрешности
        float noiseX = (random.nextFloat() - 0.5f) * noiseLevel * mainWidth;
        float noiseY = (random.nextFloat() - 0.5f) * noiseLevel * mainHeight;
        
        // Выбираем случайный цвет
        int mainColor = colors[random.nextInt(colors.length)];
        paint.setColor(mainColor);
        
        // Рисуем основную форму как эллипс
        drawPixelatedEllipse(canvas, noiseX, noiseY, mainWidth, mainHeight, paint);
    }

    private void drawDetails(Canvas canvas, float baseSize) {
        float detailWidth = baseSize * 0.15f;
        float detailHeight = baseSize * 0.2f;
        
        // Добавляем случайные погрешности
        float noiseX = (random.nextFloat() - 0.5f) * noiseLevel * detailWidth;
        float noiseY = (random.nextFloat() - 0.5f) * noiseLevel * detailHeight;
        
        // Выбираем случайный цвет для деталей
        int detailColor = colors[random.nextInt(colors.length)];
        paint.setColor(detailColor);
        
        // Рисуем детали как маленькие круги
        float detailX = noiseX;
        float detailY = baseSize * 0.3f + noiseY;
        
        drawPixelatedCircle(canvas, detailX, detailY, detailWidth/2, paint);
    }

    private void drawPixelatedEllipse(Canvas canvas, float centerX, float centerY, 
                                    float radiusX, float radiusY, Paint paint) {
        int stepsX = (int) (radiusX * 2 / pixelSize);
        int stepsY = (int) (radiusY * 2 / pixelSize);
        
        for (int i = -stepsX; i <= stepsX; i++) {
            for (int j = -stepsY; j <= stepsY; j++) {
                float x = centerX + i * pixelSize;
                float y = centerY + j * pixelSize;
                
                // Проверяем, находится ли точка внутри эллипса
                float normalizedX = (x - centerX) / radiusX;
                float normalizedY = (y - centerY) / radiusY;
                float distance = normalizedX * normalizedX + normalizedY * normalizedY;
                
                if (distance <= 1.0f) {
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