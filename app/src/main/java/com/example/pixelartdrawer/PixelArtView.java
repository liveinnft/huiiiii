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
        
        // Рисуем пиксельную версию смайлика
        drawPixelArt(canvas);
        
        // Восстанавливаем состояние canvas
        canvas.restore();
    }

    private void drawPixelArt(Canvas canvas) {
        float baseSize = 200f;
        
        // Рисуем основную форму смайлика (круг)
        drawSmileyFace(canvas, baseSize);
        
        // Рисуем глаза
        drawEyes(canvas, baseSize);
        
        // Рисуем нос
        drawNose(canvas, baseSize);
        
        // Рисуем рот
        drawMouth(canvas, baseSize);
    }

    private void drawSmileyFace(Canvas canvas, float baseSize) {
        float faceRadius = baseSize * 0.4f;
        
        // Добавляем случайные погрешности
        float noiseX = (random.nextFloat() - 0.5f) * noiseLevel * faceRadius;
        float noiseY = (random.nextFloat() - 0.5f) * noiseLevel * faceRadius;
        
        // Выбираем случайный цвет для лица
        int faceColor = colors[random.nextInt(colors.length)];
        paint.setColor(faceColor);
        
        // Рисуем круглое лицо в пиксельном стиле
        drawPixelatedCircle(canvas, noiseX, noiseY, faceRadius, paint);
    }

    private void drawEyes(Canvas canvas, float baseSize) {
        float eyeRadius = baseSize * 0.08f;
        float eyeDistance = baseSize * 0.15f;
        
        // Добавляем случайные погрешности
        float noiseX1 = (random.nextFloat() - 0.5f) * noiseLevel * eyeRadius;
        float noiseY1 = (random.nextFloat() - 0.5f) * noiseLevel * eyeRadius;
        float noiseX2 = (random.nextFloat() - 0.5f) * noiseLevel * eyeRadius;
        float noiseY2 = (random.nextFloat() - 0.5f) * noiseLevel * eyeRadius;
        
        // Выбираем случайные цвета для глаз
        int eyeColor1 = colors[random.nextInt(colors.length)];
        int eyeColor2 = colors[random.nextInt(colors.length)];
        
        // Левый глаз
        paint.setColor(eyeColor1);
        drawPixelatedCircle(canvas, -eyeDistance/2 + noiseX1, -baseSize * 0.1f + noiseY1, eyeRadius, paint);
        
        // Правый глаз
        paint.setColor(eyeColor2);
        drawPixelatedCircle(canvas, eyeDistance/2 + noiseX2, -baseSize * 0.1f + noiseY2, eyeRadius, paint);
    }

    private void drawNose(Canvas canvas, float baseSize) {
        float noseWidth = baseSize * 0.06f;
        float noseHeight = baseSize * 0.04f;
        
        // Добавляем случайные погрешности
        float noiseX = (random.nextFloat() - 0.5f) * noiseLevel * noseWidth;
        float noiseY = (random.nextFloat() - 0.5f) * noiseLevel * noseHeight;
        
        // Выбираем случайный цвет для носа
        int noseColor = colors[random.nextInt(colors.length)];
        paint.setColor(noseColor);
        
        // Рисуем нос как маленький прямоугольник
        float noseX = noiseX;
        float noseY = baseSize * 0.05f + noiseY;
        
        int stepsX = (int) (noseWidth / pixelSize);
        int stepsY = (int) (noseHeight / pixelSize);
        
        for (int i = -stepsX/2; i <= stepsX/2; i++) {
            for (int j = -stepsY/2; j <= stepsY/2; j++) {
                float x = noseX + i * pixelSize;
                float y = noseY + j * pixelSize;
                
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

    private void drawMouth(Canvas canvas, float baseSize) {
        float mouthWidth = baseSize * 0.25f;
        float mouthHeight = baseSize * 0.12f;
        
        // Добавляем случайные погрешности
        float noiseX = (random.nextFloat() - 0.5f) * noiseLevel * mouthWidth;
        float noiseY = (random.nextFloat() - 0.5f) * noiseLevel * mouthHeight;
        
        // Выбираем случайный цвет для рта
        int mouthColor = colors[random.nextInt(colors.length)];
        paint.setColor(mouthColor);
        
        // Рисуем рот как дугу
        List<PointF> points = new ArrayList<>();
        
        // Создаем точки для дуги рта
        for (int i = 0; i <= 20; i++) {
            float t = i / 20f;
            float angle = (float) (Math.PI * 0.8f * (t - 0.5f));
            float x = noiseX + (float) Math.cos(angle) * mouthWidth / 2f;
            float y = baseSize * 0.2f + noiseY + (float) Math.sin(angle) * mouthHeight / 2f;
            
            // Пикселизуем координаты
            float pixelX = (float) Math.floor(x / pixelSize) * pixelSize;
            float pixelY = (float) Math.floor(y / pixelSize) * pixelSize;
            
            points.add(new PointF(pixelX, pixelY));
        }
        
        // Рисуем рот как набор прямоугольников
        for (int i = 0; i < points.size() - 1; i++) {
            PointF point = points.get(i);
            
            // Рисуем горизонтальную линию в каждой точке для толщины
            int lineSteps = (int) (pixelSize / 2);
            for (int j = -lineSteps; j <= lineSteps; j++) {
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