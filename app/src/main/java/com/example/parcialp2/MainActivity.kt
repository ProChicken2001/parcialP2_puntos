package com.example.parcialp2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.parcialp2.pages.MainPage
import com.example.parcialp2.ui.theme.ParcialP2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ParcialP2Theme(
                dynamicColor = false
            ) {
                MainPage()
            }
        }
    }
}