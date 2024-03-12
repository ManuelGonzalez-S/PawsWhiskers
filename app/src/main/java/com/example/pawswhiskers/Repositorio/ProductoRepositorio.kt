package com.example.pawswhiskers.Repositorio

import com.example.pawswhiskers.Modelo.Producto

class ProductoRepositorio {

    companion object {
        private val listaProductos: ArrayList<Producto> = ArrayList()

        // Agrega un producto a la lista
        fun agregarProducto(producto: Producto) {
            listaProductos.add(producto)
        }

        // Elimina un producto de la lista
        fun eliminarProducto(producto: Producto) {
            listaProductos.remove(producto)
        }

        // Obtiene todos los productos de la lista
        fun obtenerProductos(): ArrayList<Producto> {
            return listaProductos
        }

        // Limpia la lista de productos
        fun limpiarProductos() {
            listaProductos.clear()
        }

        fun mostrarProductos() {
            if (listaProductos.isEmpty()) {
                println("No hay productos en el repositorio.")
            } else {
                println("Productos en el repositorio:")
                for ((index, producto) in listaProductos.withIndex()) {
                    println("Producto ${index + 1}:")
                    println("Nombre: ${producto.nombre}")
                    println("Precio: ${producto.precio}")
                    println("Referencia de Foto: ${producto.refFoto}")
                    println()
                }
            }
        }

    }
}