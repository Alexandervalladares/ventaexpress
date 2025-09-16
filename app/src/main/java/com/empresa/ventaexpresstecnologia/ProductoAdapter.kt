package com.empresa.ventaexpresstecnologia

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.empresa.ventaexpresstecnologia.modelo.Producto

class ProductoAdapter(
    private val productos: List<Producto>,
    private val onEdit: (Producto) -> Unit,
    private val onDelete: (Producto) -> Unit
) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre = itemView.findViewById<TextView>(R.id.nombreProductoTextView)
        val descripcion = itemView.findViewById<TextView>(R.id.descripcionProductoTextView)
        val precio = itemView.findViewById<TextView>(R.id.precioProductoTextView)
        val stock = itemView.findViewById<TextView>(R.id.stockProductoTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(v)
    }

    override fun getItemCount(): Int = productos.size

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val p = productos[position]
        holder.nombre.text = p.nombre
        holder.descripcion.text = p.descripcion
        holder.precio.text = "Precio: $${p.precio}"
        holder.stock.text = "Stock: ${p.stock}"

        holder.itemView.setOnLongClickListener {
            PopupMenu(holder.itemView.context, holder.itemView).apply {
                menu.add("Editar")
                menu.add("Eliminar")
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.title) {
                        "Editar" -> onEdit(p)
                        "Eliminar" -> onDelete(p)
                    }
                    true
                }
                show()
            }
            true
        }
    }
}