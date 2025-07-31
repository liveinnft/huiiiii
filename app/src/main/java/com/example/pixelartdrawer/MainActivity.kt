package com.example.pixelartdrawer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pixelartdrawer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Настраиваем кнопку для генерации новой картинки
        binding.drawButton.setOnClickListener {
            binding.pixelArtView.generateNewArt()
        }
        
        // Генерируем первую картинку при запуске
        binding.pixelArtView.generateNewArt()
    }
}