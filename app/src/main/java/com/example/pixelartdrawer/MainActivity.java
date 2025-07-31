package com.example.pixelartdrawer;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pixelartdrawer.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    
    private ActivityMainBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Настраиваем кнопку для генерации новой картинки
        binding.drawButton.setOnClickListener(v -> {
            binding.pixelArtView.generateNewArt();
        });
        
        // Генерируем первую картинку при запуске
        binding.pixelArtView.generateNewArt();
    }
}