package com.empresa.ventaexpresstecnologia

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.empresa.ventaexpresstecnologia.modelo.Venta
import java.text.NumberFormat
import java.util.Locale

class VentaAdapter(
    private val ventas: List<Venta>
) : RecyclerView.Adapter<VentaAdapter.VentaViewHolder>() {

    private val currencyFmt = NumberFormat.getCurrencyInstance(Locale.getDefault())

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
        holder.clienteTextView.text = venta.cliente.nombre
        holder.fechaTextView.text = venta.fecha

        val detalleProductos = venta.productos.joinToString("\n") { productoVenta ->
            "• ${productoVenta.producto.nombre} x${productoVenta.cantidad} — " +
                    currencyFmt.format(productoVenta.producto.precio * productoVenta.cantidad)
        }
        holder.productosTextView.text = detalleProductos

        holder.totalTextView.text = "Total: ${currencyFmt.format(venta.total)}"
    }
}
