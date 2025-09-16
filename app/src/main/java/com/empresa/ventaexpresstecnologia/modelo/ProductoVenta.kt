package com.empresa.ventaexpresstecnologia.modelo

data class ProductoVenta(
    val producto: Producto = Producto(),
    val cantidad: Int = 0
)