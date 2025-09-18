package com.empresa.ventaexpresstecnologia

import android.app.AlertDialog
import android.os.Bundle
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.empresa.ventaexpresstecnologia.modelo.Cliente
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ClientesActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var adapter: ClienteAdapter
    private lateinit var auth: FirebaseAuth
    private val clientes = mutableListOf<Cliente>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clientes)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser ?: run {
            goToLogin()
            return
        }

        database = FirebaseDatabase.getInstance().reference
            .child("empleados")
            .child(currentUser.uid)
            .child("clientes")

        adapter = ClienteAdapter(clientes,
            onEdit = { cliente -> editarClienteDialog(cliente) },
            onDelete = { cliente -> eliminarCliente(cliente) }
        )
        val recycler = findViewById<RecyclerView>(R.id.clientesRecyclerView)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        findViewById<Button>(R.id.agregarClienteButton).setOnClickListener {
            agregarClienteDialog()
        }

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                clientes.clear()
                for (ds in snapshot.children) {
                    val cli = ds.getValue(Cliente::class.java)
                    cli?.let { clientes.add(it) }
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
        findViewById<Button>(R.id.volverMenuBtn).setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
            finish() // opcional: saca ClientesActivity del back stack
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

    private fun agregarClienteDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_cliente, null)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Agregar Cliente")
            .setView(view)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = view.findViewById<EditText>(R.id.nombreClienteEditText).text.toString()
                val correo = view.findViewById<EditText>(R.id.correoClienteEditText).text.toString()
                val telefono = view.findViewById<EditText>(R.id.telefonoClienteEditText).text.toString()
                val id = database.push().key ?: ""
                val cliente = Cliente(id, nombre, correo, telefono)
                database.child(id).setValue(cliente)
            }
            .setNegativeButton("Cancelar", null)
            .create()
        dialog.show()
    }

    private fun editarClienteDialog(cliente: Cliente) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_cliente, null)
        view.findViewById<EditText>(R.id.nombreClienteEditText).setText(cliente.nombre)
        view.findViewById<EditText>(R.id.correoClienteEditText).setText(cliente.correo)
        view.findViewById<EditText>(R.id.telefonoClienteEditText).setText(cliente.telefono)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Editar Cliente")
            .setView(view)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = view.findViewById<EditText>(R.id.nombreClienteEditText).text.toString()
                val correo = view.findViewById<EditText>(R.id.correoClienteEditText).text.toString()
                val telefono = view.findViewById<EditText>(R.id.telefonoClienteEditText).text.toString()
                val nuevoCliente = cliente.copy(nombre = nombre, correo = correo, telefono = telefono)
                database.child(cliente.id).setValue(nuevoCliente)
            }
            .setNegativeButton("Cancelar", null)
            .create()
        dialog.show()
    }

    private fun eliminarCliente(cliente: Cliente) {
        database.child(cliente.id).removeValue()
    }
}