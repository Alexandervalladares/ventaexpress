package com.empresa.ventaexpresstecnologia

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        findViewById<Button>(R.id.productosButton).setOnClickListener {
            startActivity(Intent(this, ProductosActivity::class.java))
        }
        findViewById<Button>(R.id.clientesButton).setOnClickListener {
            startActivity(Intent(this, ClientesActivity::class.java))
        }
        findViewById<Button>(R.id.ventasButton).setOnClickListener {
            startActivity(Intent(this, VentasActivity::class.java))
        }
    }
}