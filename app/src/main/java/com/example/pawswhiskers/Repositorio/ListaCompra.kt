package com.example.pawswhiskers.Repositorio

import com.example.pawswhiskers.Modelo.Producto

class ListaCompra {

    companion object{

        private val listaProductos: ArrayList<Producto> = ArrayList()

        fun añadirProducto(producto: Producto) {
            ListaCompra.listaProductos.add(producto)

            mostrarProductos()
        }

        // Elimina un producto de la lista
        fun eliminarProducto(producto: Producto) {
            ListaCompra.listaProductos.remove(producto)
        }

        // Obtiene todos los productos de la lista
        fun obtenerProductos(): ArrayList<Producto> {
            return ListaCompra.listaProductos
        }

        //Vacia la lista de productos
        fun vaciarLista() {
            ListaCompra.listaProductos.clear()
        }

        fun mostrarProductos() {
            if (ListaCompra.listaProductos.isEmpty()) {
                println("No hay productos en el repositorio.")
            } else {
                println("\nProductos en el repositorio:")
                for ((index, producto) in ListaCompra.listaProductos.withIndex()) {
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