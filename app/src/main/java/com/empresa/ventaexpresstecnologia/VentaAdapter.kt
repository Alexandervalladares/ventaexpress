package com.empresa.ventaexpresstecnologia

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.empresa.ventaexpresstecnologia.modelo.Venta
class VentaAdapter(
    private val ventas: List<Venta>
) : RecyclerView.Adapter<VentaAdapter.VentaViewHolder>() {

    class VentaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clienteTextView: TextView = itemView.findViewById(R.id.clienteVentaTextView)
        val fechaTextView: TextView = itemView.findViewById(R.id.fechaVentaTextView)
        val productosTextView: TextView = itemView.findViewById(R.id.productosVentaTextView)
        val totalTextView: TextView = itemView.findViewById(R.id.totalVentaTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VentaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_venta, parent, false)
        return VentaViewHolder(view)
    }

    override fun getItemCount(): Int = ventas.size

    override fun onBindViewHolder(holder: VentaViewHolder, position: Int) {
        val venta = ventas[position]
        holder.clienteTextView.text = holder.itemView.context.getString(
            R.string.detalle_cliente_format,
            venta.cliente.nombre
        )
        holder.fechaTextView.text = holder.itemView.context.getString(
            R.string.detalle_fecha_format,
            venta.fecha
        )
        val detalleProductos = venta.productos.joinToString(separator = "\n") { productoVenta ->
            holder.itemView.context.getString(
                R.string.detalle_producto_venta_format,
                productoVenta.producto.nombre,
                productoVenta.cantidad,
                productoVenta.producto.precio * productoVenta.cantidad
            )
        }
        holder.productosTextView.text = detalleProductos
        holder.totalTextView.text = holder.itemView.context.getString(
            R.string.total_parcial_format,
            venta.total
        )
    }
}