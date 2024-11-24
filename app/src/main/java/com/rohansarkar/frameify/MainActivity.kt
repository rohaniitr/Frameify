package com.rohansarkar.frameify

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rohansarkar.frameify.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    /**
     * View binding for [MainActivity].
     */
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}