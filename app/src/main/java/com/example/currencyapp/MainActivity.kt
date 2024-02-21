package com.example.currencyapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val resultTitle: TextView = findViewById(R.id.result)
        val button: Button = findViewById(R.id.button)
        val viewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        button.setOnClickListener {
            viewModel.getData()
        }
        viewModel.uiState.observe(this) { uiState ->
            when (uiState) {
                is MyViewModel.UIState.Empty -> Unit
                is MyViewModel.UIState.Result -> {
                    resultTitle.text = uiState.title
                }
                is MyViewModel.UIState.Processing -> resultTitle.text = "Processing..."
                is MyViewModel.UIState.Error -> {
                    resultTitle.text = ""
                    Toast.makeText(this, uiState.description, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}