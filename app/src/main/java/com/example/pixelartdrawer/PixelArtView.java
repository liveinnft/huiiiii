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
        Color.MAGENTA, Color.CYAN, Color.ORANGE, Color.PINK,
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
        
        // Рисуем пиксельную версию картинки
        drawPixelArt(canvas);
        
        // Восстанавливаем состояние canvas
        canvas.restore();
    }

    private void drawPixelArt(Canvas canvas) {
        float baseSize = 200f;
        
        // Рисуем два соединенных круга внизу
        drawConnectedCircles(canvas, baseSize);
        
        // Рисуем закругленную палку сверху
        drawRoundedStick(canvas, baseSize);
    }

    private void drawConnectedCircles(Canvas canvas, float baseSize) {
        float circleRadius = baseSize * 0.25f;
        float circleDistance = baseSize * 0.15f; // Расстояние между центрами кругов
        float verticalStretch = 1.2f; // Вертикальное вытягивание
        
        // Позиции центров кругов
        float leftCircleX = -circleDistance / 2f;
        float rightCircleX = circleDistance / 2f;
        float circleY = baseSize * 0.3f; // Круги внизу
        
        // Добавляем случайные погрешности
        float noiseX1 = (random.nextFloat() - 0.5f) * noiseLevel * circleRadius;
        float noiseY1 = (random.nextFloat() - 0.5f) * noiseLevel * circleRadius;
        float noiseX2 = (random.nextFloat() - 0.5f) * noiseLevel * circleRadius;
        float noiseY2 = (random.nextFloat() - 0.5f) * noiseLevel * circleRadius;
        
        // Выбираем случайные цвета для кругов
        int color1 = colors[random.nextInt(colors.length)];
        int color2 = colors[random.nextInt(colors.length)];
        
        // Рисуем левый круг
        drawPixelatedEllipse(canvas, leftCircleX + noiseX1, circleY + noiseY1, 
                           circleRadius, circleRadius * verticalStretch, color1);
        
        // Рисуем правый круг
        drawPixelatedEllipse(canvas, rightCircleX + noiseX2, circleY + noiseY2, 
                           circleRadius, circleRadius * verticalStretch, color2);
        
        // Рисуем область соединения кругов
        drawConnectionArea(canvas, leftCircleX + noiseX1, rightCircleX + noiseX2, 
                         circleY + (noiseY1 + noiseY2) / 2f, circleRadius, verticalStretch);
    }

    private void drawPixelatedEllipse(Canvas canvas, float centerX, float centerY, 
                                    float radiusX, float radiusY, int color) {
        paint.setColor(color);
        
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

    private void drawConnectionArea(Canvas canvas, float leftX, float rightX, 
                                 float centerY, float radius, float stretch) {
        // Выбираем случайный цвет для области соединения
        int connectionColor = colors[random.nextInt(colors.length)];
        paint.setColor(connectionColor);
        
        float connectionWidth = (rightX - leftX) * 0.6f; // Ширина области соединения
        float connectionHeight = radius * stretch * 0.8f;
        
        int stepsX = (int) (connectionWidth / pixelSize);
        int stepsY = (int) (connectionHeight / pixelSize);
        
        for (int i = 0; i <= stepsX; i++) {
            for (int j = -stepsY; j <= stepsY; j++) {
                float x = leftX + i * pixelSize;
                float y = centerY + j * pixelSize;
                
                // Создаем плавное соединение между кругами
                float t = i / (float) stepsX;
                float blendRadius = radius * (0.8f + 0.2f * (float) Math.sin(t * Math.PI));
                
                float normalizedX = (x - (leftX + rightX) / 2f) / (connectionWidth / 2f);
                float normalizedY = (y - centerY) / (blendRadius * stretch);
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

    private void drawRoundedStick(Canvas canvas, float baseSize) {
        float stickWidth = baseSize * 0.15f;
        float stickHeight = baseSize * 0.8f;
        float stickStartY = -baseSize * 0.2f; // Начинается выше кругов
        
        // Добавляем случайные погрешности
        float noiseX = (random.nextFloat() - 0.5f) * noiseLevel * stickWidth;
        float noiseY = (random.nextFloat() - 0.5f) * noiseLevel * stickHeight;
        
        // Выбираем случайный цвет для палки
        int stickColor = colors[random.nextInt(colors.length)];
        paint.setColor(stickColor);
        
        // Рисуем закругленную палку как набор прямоугольников
        List<PointF> points = new ArrayList<>();
        
        // Создаем точки для закругленной палки
        for (int i = 0; i <= 40; i++) {
            float t = i / 40f;
            float x = noiseX;
            float y;
            
            if (t < 0.1f) {
                // Нижняя закругленная часть
                float localT = t / 0.1f;
                y = stickStartY + stickHeight * 0.1f * (1f - (float) Math.cos(localT * Math.PI / 2));
            } else if (t > 0.9f) {
                // Верхняя закругленная часть
                float localT = (t - 0.9f) / 0.1f;
                y = stickStartY + stickHeight * (0.9f + 0.1f * (float) Math.sin(localT * Math.PI / 2));
            } else {
                // Прямая часть
                float localT = (t - 0.1f) / 0.8f;
                y = stickStartY + stickHeight * (0.1f + 0.8f * localT);
            }
            
            // Пикселизуем координаты
            float pixelX = (float) Math.floor(x / pixelSize) * pixelSize;
            float pixelY = (float) Math.floor(y / pixelSize) * pixelSize;
            
            points.add(new PointF(pixelX, pixelY));
        }
        
        // Рисуем палку как набор прямоугольников
        for (int i = 0; i < points.size() - 1; i++) {
            PointF point = points.get(i);
            
            // Рисуем горизонтальную линию в каждой точке
            int lineSteps = (int) (stickWidth / pixelSize);
            for (int j = -lineSteps/2; j <= lineSteps/2; j++) {
                float lineX = point.x + j * pixelSize;
                float lineY = point.y;
                
                // Пикселизуем координаты линии
                float pixelLineX = (float) Math.floor(lineX / pixelSize) * pixelSize;
                float pixelLineY = (float) Math.floor(lineY / pixelSize) * pixelSize;
                
                RectF rect = new RectF(
                    pixelLineX - pixelSize/2,
                    pixelLineY - pixelSize/2,
                    pixelLineX + pixelSize/2,
                    pixelLineY + pixelSize/2
                );
                canvas.drawRect(rect, paint);
            }
        }
    }
}