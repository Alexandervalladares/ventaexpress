package com.empresa.ventaexpresstecnologia.modelo

data class Venta(
    val id: String = "",
    val cliente: Cliente = Cliente(),
    val productos: List<ProductoVenta> = emptyList(),
    val total: Double = 0.0,
    val fecha: String = ""
)