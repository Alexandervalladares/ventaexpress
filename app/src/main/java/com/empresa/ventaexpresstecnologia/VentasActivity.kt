package com.empresa.ventaexpresstecnologia

import android.content.Intent
import android.os.Bundle

import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.empresa.ventaexpresstecnologia.modelo.Cliente
import com.empresa.ventaexpresstecnologia.modelo.Producto
import com.empresa.ventaexpresstecnologia.modelo.ProductoVenta
import com.empresa.ventaexpresstecnologia.modelo.Venta
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat

import java.util.Date
import java.util.Locale

class VentasActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var clientesAdapter: ArrayAdapter<String>
    private lateinit var productosAdapter: ArrayAdapter<String>
    private lateinit var ventasAdapter: VentaAdapter

    private val clientes = mutableListOf<Cliente>()
    private val productos = mutableListOf<Producto>()
    private val selectedProductos = mutableListOf<ProductoVenta>()
    private val ventas = mutableListOf<Venta>()

    private lateinit var spinnerClientes: Spinner
    private lateinit var spinnerProductos: Spinner
    private lateinit var productosSeleccionadosTV: TextView
    private lateinit var totalGeneradoTextView: TextView
    private lateinit var cantidadEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ventas)


        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser ?: run {
            goToLogin()
            return
        }
        // (Opcional) Flecha en la ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Ventas"

        database = FirebaseDatabase.getInstance().reference
            .child("empleados")
            .child(currentUser.uid)

        database = FirebaseDatabase.getInstance().reference
            .child("empleados")
            .child(currentUser.uid)


        spinnerClientes = findViewById(R.id.spinnerClientes)
        spinnerProductos = findViewById(R.id.spinnerProductos)
        productosSeleccionadosTV = findViewById(R.id.productosSeleccionadosTV)
        totalGeneradoTextView = findViewById(R.id.totalGeneradoTextView)
        cantidadEditText = findViewById(R.id.cantidadEditText)
        val agregarProdBtn = findViewById<Button>(R.id.agregarProdBtn)
        val registrarVentaBtn = findViewById<Button>(R.id.registrarVentaBtn)
        val ventasRecyclerView = findViewById<RecyclerView>(R.id.ventasRecyclerView)

        clientesAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, mutableListOf())
        spinnerClientes.adapter = clientesAdapter

        productosAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, mutableListOf())
        spinnerProductos.adapter = productosAdapter

        ventasAdapter = VentaAdapter(ventas)
        ventasRecyclerView.layoutManager = LinearLayoutManager(this)
        ventasRecyclerView.adapter = ventasAdapter

        productosSeleccionadosTV.text = getString(R.string.sin_productos_seleccionados)
        totalGeneradoTextView.text = getString(R.string.total_generado_format, 0.0)


        escucharClientes()
        escucharProductos()
        escucharVentas()

            agregarProdBtn.setOnClickListener { agregarProductoSeleccionado() }
            registrarVentaBtn.setOnClickListener { registrarVenta() }
        }

                override fun onStart() {
            super.onStart()
            if (auth.currentUser == null) {
                goToLogin()
            }
                    findViewById<Button>(R.id.volverMenuBtn)?.setOnClickListener {
                        startActivity(Intent(this, MenuActivity::class.java))
                        finish()
                    }

                }

                private fun escucharClientes() {
            database.child("clientes").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    clientes.clear()
                    clientesAdapter.clear()
                    for (ds in snapshot.children) {
                        ds.getValue(Cliente::class.java)?.let { clientes.add(it) }
                    }

                    clientesAdapter.addAll(clientes.map { it.nombre })
                    clientesAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
            }

                    private fun escucharProductos() {
                database.child("productos").addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        productos.clear()
                        productosAdapter.clear()
                        for (ds in snapshot.children) {
                            ds.getValue(Producto::class.java)?.let { productos.add(it) }
                        }
                        productosAdapter.addAll(productos.map { it.nombre })
                        productosAdapter.notifyDataSetChanged()
                        validarSeleccionConStock()
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }


                    private fun escucharVentas() {
                        database.child("ventas").addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                ventas.clear()
                                for (ds in snapshot.children) {
                                    ds.getValue(Venta::class.java)?.let { ventas.add(it) }
                                }
                                ventas.sortByDescending { it.fecha }
                                ventasAdapter.notifyDataSetChanged()
                                val total = ventas.sumOf { it.total }
                                totalGeneradoTextView.text = getString(R.string.total_generado_format, total)
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }

                    private fun agregarProductoSeleccionado() {
                        val pos = spinnerProductos.selectedItemPosition
                        val cantidad = cantidadEditText.text.toString().toIntOrNull()
                        if (productos.isEmpty() || pos < 0) {
                            Toast.makeText(this, R.string.selecciona_producto, Toast.LENGTH_SHORT).show()
                            return
                        }
                        if (cantidad == null || cantidad <= 0) {
                            Toast.makeText(this, R.string.cantidad_invalida, Toast.LENGTH_SHORT).show()
                            return
                        }

                        val producto = productos[pos]
                        if (producto.stock <= 0) {
                            Toast.makeText(this, getString(R.string.stock_insuficiente, producto.nombre), Toast.LENGTH_SHORT).show()
                            return
                        }


                                val existenteIndex = selectedProductos.indexOfFirst { it.producto.id == producto.id }
                                val cantidadActual = if (existenteIndex >= 0) selectedProductos[existenteIndex].cantidad else 0
                                val nuevaCantidad = cantidadActual + cantidad
                                if (nuevaCantidad > producto.stock) {
                                    Toast.makeText(this, getString(R.string.stock_insuficiente, producto.nombre), Toast.LENGTH_SHORT).show()
                                    return
                                }

                                if (existenteIndex >= 0) {
                                    val existente = selectedProductos[existenteIndex]
                                    selectedProductos[existenteIndex] = existente.copy(cantidad = nuevaCantidad)
                                } else {
                                    selectedProductos.add(ProductoVenta(producto, cantidad))
                                }

                                cantidadEditText.text.clear()
                                actualizarResumenSeleccion()
                            }

                            private fun actualizarResumenSeleccion() {
                                if (selectedProductos.isEmpty()) {
                                    productosSeleccionadosTV.text = getString(R.string.sin_productos_seleccionados)
                                    return
                                }

                                val detalle = selectedProductos.joinToString(separator = "\n") { venta ->
                                    val subtotal = venta.producto.precio * venta.cantidad
                                    getString(R.string.detalle_producto_venta_format, venta.producto.nombre, venta.cantidad, subtotal)
                                }
                                val total = selectedProductos.sumOf { it.producto.precio * it.cantidad }
                                val texto = detalle + "\n\n" + getString(R.string.total_parcial_format, total)
                                productosSeleccionadosTV.text = texto
                            }

                            private fun registrarVenta() {
                                val clientePos = spinnerClientes.selectedItemPosition
                                if (clientes.isEmpty() || clientePos < 0) {
                                    Toast.makeText(this, R.string.selecciona_cliente, Toast.LENGTH_SHORT).show()
                                    return
                                }
                                if (selectedProductos.isEmpty()) {
                                    Toast.makeText(this, R.string.selecciona_productos_para_vender, Toast.LENGTH_SHORT).show()
                                    return
                                }

                                val productosSinStock = selectedProductos.firstOrNull { venta ->
                                    val productoActual = productos.firstOrNull { it.id == venta.producto.id }
                                    productoActual == null || venta.cantidad > productoActual.stock
                                }
                                if (productosSinStock != null) {
                                    Toast.makeText(this, getString(R.string.stock_insuficiente, productosSinStock.producto.nombre), Toast.LENGTH_SHORT).show()
                                    return
                                }

                                val cliente = clientes[clientePos]
                                val total = selectedProductos.sumOf { it.producto.precio * it.cantidad }
                                val ventasRef = database.child("ventas")
                                val ventaId = ventasRef.push().key
                                if (ventaId == null) {
                                    Toast.makeText(this, R.string.error_registrar_venta, Toast.LENGTH_SHORT).show()
                                    return
                                }
                                val fecha = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                                val productosParaGuardar = selectedProductos.map { productoVenta ->
                                    productoVenta.copy(producto = productoVenta.producto.copy())
                                }
                                val venta = Venta(ventaId, cliente, productosParaGuardar, total, fecha)
                                ventasRef.child(ventaId).setValue(venta).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        actualizarStock(productosParaGuardar)
                                        Toast.makeText(this, R.string.venta_registrada, Toast.LENGTH_SHORT).show()
                                        selectedProductos.clear()

                                        actualizarResumenSeleccion()
                                    } else {
                                        Toast.makeText(this, R.string.error_registrar_venta, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }

                            private fun actualizarStock(productosVendidos: List<ProductoVenta>) {
                                for (productoVenta in productosVendidos) {
                                    val productoActual = productos.firstOrNull { it.id == productoVenta.producto.id }
                                    val stockActual = productoActual?.stock ?: productoVenta.producto.stock
                                    val nuevoStock = (stockActual - productoVenta.cantidad).coerceAtLeast(0)
                                    database.child("productos").child(productoVenta.producto.id).child("stock").setValue(nuevoStock)
                                }
                            }

                            private fun validarSeleccionConStock() {
                                var cambio = false
                                val iterator = selectedProductos.listIterator()
                                while (iterator.hasNext()) {
                                    val productoVenta = iterator.next()
                                    val productoActual = productos.firstOrNull { it.id == productoVenta.producto.id }
                                    if (productoActual == null || productoVenta.cantidad > productoActual.stock) {
                                        iterator.remove()
                                        cambio = true
                                    } else if (productoVenta.producto.stock != productoActual.stock) {
                                        iterator.set(productoVenta.copy(producto = productoActual))
                                        cambio = true
                                    }
                                }
                                if (cambio) {
                                    actualizarResumenSeleccion()
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