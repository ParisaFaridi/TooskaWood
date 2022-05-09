package com.example.pottery

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.pottery.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()
        binding.group.alpha=0f
        binding.group.animate().setDuration(1500).alpha(1f).withEndAction{
            binding.group.visibility= View.GONE
            supportActionBar?.show()
            binding.constarintLayout.setBackgroundColor(Color.TRANSPARENT)
            binding.navHostFragment.visibility= View.VISIBLE
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
        }
    }
}