package com.empresa.ventaexpresstecnologia

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth

class MenuActivity : AppCompatActivity() {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val currentUser = auth.currentUser ?: run {
            goToLogin()
            return
        }

        val nombreEmpleado = currentUser.displayName?.takeIf { it.isNotBlank() }
            ?: currentUser.email
            ?: getString(R.string.empleado_sin_correo)
        findViewById<TextView>(R.id.usuarioTextView).text =
            getString(R.string.bienvenida_usuario, nombreEmpleado)

        findViewById<Button>(R.id.productosButton).setOnClickListener {
            startActivity(Intent(this, ProductosActivity::class.java))
        }
        findViewById<Button>(R.id.clientesButton).setOnClickListener {
            startActivity(Intent(this, ClientesActivity::class.java))
        }
        findViewById<Button>(R.id.ventasButton).setOnClickListener {
            startActivity(Intent(this, VentasActivity::class.java))
        }
        findViewById<Button>(R.id.cerrarSesionButton).setOnClickListener {
            auth.signOut()
            LoginManager.getInstance().logOut()
            goToLogin()
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser == null) {
            goToLogin()
        }
    }

    private fun goToLogin() {
        startActivity(
            Intent(this, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
        finish()
    }
}