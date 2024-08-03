package com.example.diplom.WorkMenu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.diplom.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity_AllMenu : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_all_menu)
        val NavigationMenu = findViewById<BottomNavigationView>(R.id.navigationmenu)
        val controller = findNavController(R.id.fragmentContainerView)
        NavigationMenu.setupWithNavController(controller)

    }


}