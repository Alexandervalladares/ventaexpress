package com.empresa.ventaexpresstecnologia

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.empresa.ventaexpresstecnologia.modelo.Cliente

class ClienteAdapter(
    private val clientes: List<Cliente>,
    private val onEdit: (Cliente) -> Unit,
    private val onDelete: (Cliente) -> Unit
) : RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder>() {

    class ClienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre = itemView.findViewById<TextView>(R.id.nombreClienteTextView)
        val correo = itemView.findViewById<TextView>(R.id.correoClienteTextView)
        val telefono = itemView.findViewById<TextView>(R.id.telefonoClienteTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cliente, parent, false)
        return ClienteViewHolder(v)
    }

    override fun getItemCount(): Int = clientes.size

    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        val c = clientes[position]
        holder.nombre.text = c.nombre
        holder.correo.text = c.correo
        holder.telefono.text = c.telefono

        holder.itemView.setOnLongClickListener {
            PopupMenu(holder.itemView.context, holder.itemView).apply {
                menu.add("Editar")
                menu.add("Eliminar")
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.title) {
                        "Editar" -> onEdit(c)
                        "Eliminar" -> onDelete(c)
                    }
                    true
                }
                show()
            }
            true
        }
    }
}