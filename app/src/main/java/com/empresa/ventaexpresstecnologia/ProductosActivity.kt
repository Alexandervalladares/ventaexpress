package com.empresa.ventaexpresstecnologia

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.empresa.ventaexpresstecnologia.modelo.Producto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProductosActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var adapter: ProductoAdapter
    private val productos = mutableListOf<Producto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos)

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        database = FirebaseDatabase.getInstance().reference.child("empleados").child(userId).child("productos")

        adapter = ProductoAdapter(productos,
            onEdit = { producto -> editarProductoDialog(producto) },
            onDelete = { producto -> eliminarProducto(producto) }
        )
        val recycler = findViewById<RecyclerView>(R.id.productosRecyclerView)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        findViewById<Button>(R.id.agregarProductoButton).setOnClickListener {
            agregarProductoDialog()
        }

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productos.clear()
                for (ds in snapshot.children) {
                    val prod = ds.getValue(Producto::class.java)
                    prod?.let { productos.add(it) }
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun agregarProductoDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_producto, null)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Agregar Producto")
            .setView(view)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = view.findViewById<EditText>(R.id.nombreEditText).text.toString()
                val descripcion = view.findViewById<EditText>(R.id.descripcionEditText).text.toString()
                val precio = view.findViewById<EditText>(R.id.precioEditText).text.toString().toDoubleOrNull() ?: 0.0
                val stock = view.findViewById<EditText>(R.id.stockEditText).text.toString().toIntOrNull() ?: 0
                val id = database.push().key ?: ""
                val producto = Producto(id, nombre, descripcion, precio, stock)
                database.child(id).setValue(producto)
            }
            .setNegativeButton("Cancelar", null)
            .create()
        dialog.show()
    }

    private fun editarProductoDialog(producto: Producto) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_producto, null)
        view.findViewById<EditText>(R.id.nombreEditText).setText(producto.nombre)
        view.findViewById<EditText>(R.id.descripcionEditText).setText(producto.descripcion)
        view.findViewById<EditText>(R.id.precioEditText).setText(producto.precio.toString())
        view.findViewById<EditText>(R.id.stockEditText).setText(producto.stock.toString())
        val dialog = AlertDialog.Builder(this)
            .setTitle("Editar Producto")
            .setView(view)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = view.findViewById<EditText>(R.id.nombreEditText).text.toString()
                val descripcion = view.findViewById<EditText>(R.id.descripcionEditText).text.toString()
                val precio = view.findViewById<EditText>(R.id.precioEditText).text.toString().toDoubleOrNull() ?: 0.0
                val stock = view.findViewById<EditText>(R.id.stockEditText).text.toString().toIntOrNull() ?: 0
                val nuevoProducto = producto.copy(nombre = nombre, descripcion = descripcion, precio = precio, stock = stock)
                database.child(producto.id).setValue(nuevoProducto)
            }
            .setNegativeButton("Cancelar", null)
            .create()
        dialog.show()
    }

    private fun eliminarProducto(producto: Producto) {
        database.child(producto.id).removeValue()
    }
}