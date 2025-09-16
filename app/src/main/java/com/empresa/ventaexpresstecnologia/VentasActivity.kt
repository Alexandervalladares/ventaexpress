package com.empresa.ventaexpresstecnologia

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.empresa.ventaexpresstecnologia.modelo.*
import java.text.SimpleDateFormat
import java.util.*

class VentasActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var clientes: MutableList<Cliente>
    private lateinit var productos: MutableList<Producto>
    private var selectedProductos = mutableListOf<ProductoVenta>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ventas)

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        database = FirebaseDatabase.getInstance().reference.child("empleados").child(userId)

        clientes = mutableListOf()
        productos = mutableListOf()

        val spinnerClientes = findViewById<Spinner>(R.id.spinnerClientes)
        val spinnerProductos = findViewById<Spinner>(R.id.spinnerProductos)
        val cantidadEditText = findViewById<EditText>(R.id.cantidadEditText)
        val agregarProdBtn = findViewById<Button>(R.id.agregarProdBtn)
        val registrarVentaBtn = findViewById<Button>(R.id.registrarVentaBtn)
        val productosSeleccionadosTV = findViewById<TextView>(R.id.productosSeleccionadosTV)

        // Cargar clientes y productos
        database.child("clientes").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                clientes.clear()
                for (ds in snapshot.children) {
                    ds.getValue(Cliente::class.java)?.let { clientes.add(it) }
                }
                spinnerClientes.adapter = ArrayAdapter(this@VentasActivity, android.R.layout.simple_spinner_dropdown_item, clientes.map { it.nombre })
            }
            override fun onCancelled(error: DatabaseError) {}
        })
        database.child("productos").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productos.clear()
                for (ds in snapshot.children) {
                    ds.getValue(Producto::class.java)?.let { productos.add(it) }
                }
                spinnerProductos.adapter = ArrayAdapter(this@VentasActivity, android.R.layout.simple_spinner_dropdown_item, productos.map { it.nombre })
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        agregarProdBtn.setOnClickListener {
            val pos = spinnerProductos.selectedItemPosition
            val cantidad = cantidadEditText.text.toString().toIntOrNull() ?: 1
            if (pos >= 0 && productos.isNotEmpty() && cantidad > 0) {
                val producto = productos[pos]
                selectedProductos.add(ProductoVenta(producto, cantidad))
                productosSeleccionadosTV.text = selectedProductos.joinToString("\n") { "${it.producto.nombre} x${it.cantidad}" }
            }
        }

        registrarVentaBtn.setOnClickListener {
            val clientePos = spinnerClientes.selectedItemPosition
            if (clientePos >= 0 && clientes.isNotEmpty() && selectedProductos.isNotEmpty()) {
                val cliente = clientes[clientePos]
                val total = selectedProductos.sumOf { it.producto.precio * it.cantidad }
                val ventaId = database.child("ventas").push().key ?: ""
                val fecha = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                val venta = Venta(ventaId, cliente, selectedProductos, total, fecha)
                database.child("ventas").child(ventaId).setValue(venta)
                Toast.makeText(this, "Venta registrada", Toast.LENGTH_SHORT).show()
                selectedProductos.clear()
                productosSeleccionadosTV.text = ""
            } else {
                Toast.makeText(this, "Selecciona cliente y productos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}