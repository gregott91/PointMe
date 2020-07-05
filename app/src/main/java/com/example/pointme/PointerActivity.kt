package com.example.pointme

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class PointerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pointer)
        setSupportActionBar(findViewById(R.id.toolbar))
    }
}